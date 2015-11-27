package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;


public class MovieDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.MOVIE_ID_KEY, getIntent().getIntExtra(Constants.MOVIE_ID_KEY, GridView.INVALID_POSITION));
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_detail, new MovieDetailActivityFragment(), "MDA_FRAGMENT")
                    .commit();*/
            getFragmentManager().beginTransaction().
                    add(R.id.fragment_movie_detail, fragment).
                    commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
