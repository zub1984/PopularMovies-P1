package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

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

public class MovieDetailActivityFragment extends Fragment {

    //private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    @Bind(R.id.backdrop)
    ImageView mBackdropPath;
    @Bind(R.id.poster)
    ImageView mPosterUrl;

    @Bind(R.id.original_title)
    TextView mTitle;
    @Bind(R.id.overview)
    TextView mOverview;
    @Bind(R.id.release_date)
    TextView mReleaseDate;
    @Bind(R.id.rating)
    TextView mUserRating;

    @Bind(R.id.ratingStar)
    ImageView mRatingStar;

    View rootView;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            MdbMovie movieDetails = intent.getParcelableExtra(getString(R.string.MOVIE_PARCEL));
            DrawMovieDetailsFragment(movieDetails);
        }
        return rootView;
    }

    //http://jakewharton.github.io/butterknife/
    public void DrawMovieDetailsFragment(MdbMovie movieDetails) {
        ButterKnife.bind(this, rootView);
        mBackdropPath.setScaleType(ImageView.ScaleType.FIT_XY);

        if (movieDetails.getBackdropPath() != null && movieDetails.getBackdropPath().trim().length()>0) {
            Picasso.with(getActivity())
                    .load(getString(R.string.BACK_DROP_IMAGE_URL) + movieDetails.getBackdropPath())
                    .into(mBackdropPath);
        } else {
            mBackdropPath.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_movie));
        }

        if (movieDetails.getPosterUrl() != null && movieDetails.getPosterUrl().trim().length()>0) {
            Picasso.with(getActivity())
                    .load(getString(R.string.POSTER_IMAGE_URL) + movieDetails.getPosterUrl())
                    .into(mPosterUrl);
        } else {
            mPosterUrl.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_movie));
        }

        mRatingStar.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_rate_black_18dp));

        final String ratingStr = getString(R.string.rating_out_of_ten);

        mTitle.setText(movieDetails.getTitle());
        mOverview.setText(movieDetails.getOverview());
        mUserRating.setText(movieDetails.getUserRating() + ratingStr);
        mReleaseDate.setText(Utility.formatReleaseDate(movieDetails.getReleaseDate()));
    }

    /*Fragments have a different view lifecycle than activities. When binding a fragment in onCreateView, set
    the views to null in onDestroyView. Butter Knife has an unbind method to do this automatically.*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
