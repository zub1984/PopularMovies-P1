package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.GridView;

import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;


public class MovieDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if (null == savedInstanceState ) {
            /* set the intent data received from MainActivity Class*/
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.MOVIE_ID_KEY, getIntent().getIntExtra(Constants.MOVIE_ID_KEY, GridView.INVALID_POSITION));
            arguments.putString(Constants.SORTING_KEY, getIntent().getStringExtra(Constants.SORTING_KEY));
            arguments.putString(Constants.POSTER_IMAGE_KEY,getIntent().getStringExtra(Constants.POSTER_IMAGE_KEY));

            MovieDetailActivityFragment fragment = MovieDetailActivityFragment.newInstance(arguments);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_movie_detail, fragment, Constants.MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        }
    }
}
