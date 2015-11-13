package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnMoviePosterSelectedListener {

    //public final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityFragment mainActivityFragment =  (MainActivityFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main);


        if (findViewById(R.id.fragment_movie_detail) != null) {
            mTwoPane = true;
            mainActivityFragment.setActivateOnItemClick(true);
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // The user selected the movie poster from the MainActivityFragment
    // Send movie details to MovieDetailActivityFragment for display.
    public void onMoviePosterSelected(int movieId) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(getString(R.string.MOVIE_PARCEL), movieId);
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_movie_detail, new MovieDetailActivityFragment(), "MDA_FRAGMENT")
                    .commit();
        }else{
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(getString(R.string.MOVIE_PARCEL), movieId);
            startActivity(detailIntent);
        }

    }
}
