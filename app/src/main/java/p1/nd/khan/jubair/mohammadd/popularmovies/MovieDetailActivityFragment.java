package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;

/**
 * A placeholder fragment containing a simple view.
 */

/*Copyright 2013 Jake Wharton

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    @Bind(R.id.backdrop)
    ImageView mBackdropPath;
    @Bind(R.id.poster)
    ImageView mPosterUrl;
    @Bind(R.id.ratingStar)
    ImageView mRatingStar;

    @Bind(R.id.original_title)
    TextView mTitle;
    @Bind(R.id.overview)
    TextView mOverview;
    @Bind(R.id.release_date)
    TextView mReleaseDate;
    @Bind(R.id.rating)
    TextView mUserRating;

    View rootView;

    private int mMovieId = -1;
    private static final int CURSOR_DETAIL_LOADER_ID = 0;

    // Specify the columns we need.
    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.C_MOVIE_ID,
            MovieEntry.C_ORIGINAL_TITLE,
            MovieEntry.C_OVERVIEW,
            MovieEntry.C_BACKDROP_PATH,
            MovieEntry.C_POSTER_PATH,
            MovieEntry.C_RELEASE_DATE,
            MovieEntry.C_VOTE_AVERAGE
           // MovieEntry.C_VOTE_COUNT
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these must change.
    private static final int C_ID = 0;
    private static final int C_MOVIE_ID = 1;
    private static final int C_ORIGINAL_TITLE = 2;
    private static final int C_OVERVIEW = 3;
    private static final int C_BACKDROP_PATH = 4;
    private static final int C_POSTER_PATH = 5;
    private static final int C_RELEASE_DATE = 6;
    private static final int C_VOTE_AVERAGE = 7;
   //private static final int C_VOTE_COUNT = 8;


    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = "(" + MovieEntry.C_MOVIE_ID + " = '" + mMovieId + "')";
        Uri provider = MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovieId)).build();
        return new CursorLoader(getActivity(),
                provider,
                MOVIE_DETAIL_COLUMNS,
                selection,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data && data.moveToFirst()) {
            DrawMovieDetailsFragment(data);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mMovieId = intent.getIntExtra(getString(R.string.MOVIE_PARCEL), -1);
                    }
        return rootView;
    }

    //http://jakewharton.github.io/butterknife/
    public void DrawMovieDetailsFragment(Cursor data) {
        ButterKnife.bind(this, rootView);
        mBackdropPath.setScaleType(ImageView.ScaleType.FIT_XY);

        if (data.getString(C_BACKDROP_PATH) != null ) {
            Picasso.with(getActivity())
                    .load(getString(R.string.BACK_DROP_IMAGE_URL) + data.getString(C_BACKDROP_PATH))
                    .into(mBackdropPath);
        } else {
            mBackdropPath.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_movie));
        }

        if (data.getString(C_POSTER_PATH)!= null) {
            Picasso.with(getActivity())
                    .load(getString(R.string.POSTER_IMAGE_URL) + data.getString(C_POSTER_PATH))
                    .into(mPosterUrl);
        } else {
            mPosterUrl.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_movie));
        }

        mRatingStar.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_rate_black_18dp));

        mTitle.setText(data.getString(C_ORIGINAL_TITLE));
        mOverview.setText(data.getString(C_OVERVIEW));
        mUserRating.setText(data.getString(C_VOTE_AVERAGE) + getString(R.string.rating_out_of_ten));
        mReleaseDate.setText(Utility.formatReleaseDate(data.getString(C_RELEASE_DATE)));
    }

    /*Fragments have a different view lifecycle than activities. When binding a fragment in onCreateView, set
    the views to null in onDestroyView. Butter Knife has an unbind method to do this automatically.*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
