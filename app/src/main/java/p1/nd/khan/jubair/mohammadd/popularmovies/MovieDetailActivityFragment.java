package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class MovieDetailActivityFragment extends Fragment {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

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

    View rootView;
    //OnMoviePosterSelectedListener mCallback;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "=== onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "=== onCreateView");
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            MdbMovie movieDetails = intent.getParcelableExtra(getString(R.string.MOVIE_PARCEL));
            DrawMovieDetailsFragment(movieDetails);
        }
        return rootView;
    }

    public void DrawMovieDetailsFragment(MdbMovie movieDetails) {

        Log.v(LOG_TAG, "=== DrawMovieDetailsFragment:" + movieDetails.getTitle());
        ButterKnife.bind(this, rootView);
        final String tmdb_poster_url = "http://image.tmdb.org/t/p/w185";
        mBackdropPath.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(getActivity())
                .load(tmdb_poster_url + movieDetails.getBackdropPath())
                .into(mBackdropPath);

        Picasso.with(getActivity())
                .load(tmdb_poster_url + movieDetails.getPosterUrl())
                .into(mPosterUrl);

        mTitle.setText(movieDetails.getTitle());
        mOverview.setText(movieDetails.getOverview());
        mUserRating.setText(movieDetails.getUserRating());
        mReleaseDate.setText(movieDetails.getReleaseDate());
    }

}
