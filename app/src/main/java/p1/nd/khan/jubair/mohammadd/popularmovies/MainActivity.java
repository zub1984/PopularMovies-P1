package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;

public class MainActivity extends BaseActivity implements MainActivityFragment.OnMoviePosterSelectedListener {

    private boolean mTwoPane;
    private int moviePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar(false, true);

        if (findViewById(R.id.fragment_movie_detail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(
                        R.id.fragment_movie_detail,
                        MovieDetailActivityFragment.newInstance(new Bundle()),
                        Constants.MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG)
                        .commit();
            } else {
                moviePosition = savedInstanceState.getInt(Constants.SELECTED_MOVIE_KEY);
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // The user selected the movie poster from the MainActivityFragment
    // Send movie details to MovieDetailActivityFragment for display.
    public void onMoviePosterSelected(int movieId, String mSortOrder) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        bundle.putString(Constants.SORTING_KEY, mSortOrder);
        if (mTwoPane) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MovieDetailActivityFragment fragment =MovieDetailActivityFragment.newInstance(bundle);
            fragmentTransaction.replace(R.id.fragment_movie_detail, fragment, Constants.MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG).commit();
        } else {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtras(bundle);
            startActivity(detailIntent);
        }

    }
}
