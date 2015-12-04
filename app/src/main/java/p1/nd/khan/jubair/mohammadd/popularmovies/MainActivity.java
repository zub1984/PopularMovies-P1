package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Utility;

public class MainActivity extends BaseActivity implements MainActivityFragment.OnMoviePosterSelectedListener {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar(false, true);

        if (null != findViewById(R.id.fragment_movie_detail)) {
            mTwoPane = true;
            if (null == savedInstanceState) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(
                        R.id.fragment_movie_detail,
                        MovieDetailActivityFragment.newInstance(new Bundle()),
                        Constants.MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        if (Utility.isNetworkAvailable(this)) {
            MovieSyncAdapter.initializeSyncAdapter(this);
        } else {
            Log.v("LOG_TAG", " No Internet Connection!");
            Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        // fix the issue of menu option checked on start.
        String sOrder = Utility.getPreferredSorting(this);

        if (sOrder.equalsIgnoreCase(getString(R.string.SORT_ORDER_POPULARITY)))
            menu.findItem(R.id.action_sort_by_popular).setChecked(true);

        else if (sOrder.equalsIgnoreCase(getString(R.string.SORT_ORDER_RATING)))
            menu.findItem(R.id.action_sort_by_rating).setChecked(true);

        else menu.findItem(R.id.action_show_favorites).setChecked(true);

        return true;
    }


    /**
     * When the user selects the movie poster from the MainActivityFragment Send movie details to
     * MovieDetailActivityFragment for details display.
     *
     * @param movieId     clicked movie id.
     * @param posterImage poster image of clicked movie.
     * @param view        View holder of movie
     * @param position    position of the movie in grid view
     * @param mSortOrder  sorting order of the movie list
     */
    public void onMoviePosterSelected(int movieId, String posterImage, View view, int position, String mSortOrder) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        bundle.putString(Constants.SORTING_KEY, mSortOrder);
        bundle.putString(Constants.POSTER_IMAGE_KEY, posterImage);
        if (mTwoPane) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MovieDetailActivityFragment fragment = MovieDetailActivityFragment.newInstance(bundle);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.image_move));
                fragment.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
                fragmentTransaction.addSharedElement(view, Constants.POSTER_IMAGE_VIEW_KEY);
            }*/
            fragmentTransaction.replace(R.id.fragment_movie_detail, fragment, Constants.MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG).commit();
        } else {
            ActivityOptions activityOptions = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, view, Constants.POSTER_IMAGE_VIEW_KEY);
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtras(bundle);
            if (null != activityOptions)
                startActivity(detailIntent, activityOptions.toBundle());
            else
                startActivity(detailIntent);
        }

    }

}
