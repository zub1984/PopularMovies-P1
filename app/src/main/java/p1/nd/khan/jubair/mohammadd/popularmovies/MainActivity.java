package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnMoviePosterSelectedListener {

    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main);

        if (findViewById(R.id.fragment_movie_detail) != null) {
            mTwoPane = true;
            mainActivityFragment.setActivateOnItemClick(true);
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // fix the issue of menu option checked on start.
        String sOrder=Utility.getPreferredSorting(this);

        if (sOrder.equalsIgnoreCase(getString(R.string.SORT_ORDER_POPULARITY)))
            menu.findItem(R.id.action_sort_by_popular).setChecked(true);

        else if(sOrder.equalsIgnoreCase(getString(R.string.SORT_ORDER_RATING)))
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
    public void onMoviePosterSelected(int movieId,String mSortOrder) {

        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.MOVIE_PARCEL), movieId);
        bundle.putString(getString(R.string.pref_sort_order_key), mSortOrder);

        if (mTwoPane) {
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_movie_detail, fragment).commit();
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_detail, new MovieDetailActivityFragment(), "MDA_FRAGMENT")
                    .commit();*/
        } else {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtras(bundle);
            startActivity(detailIntent);
        }

    }
}
