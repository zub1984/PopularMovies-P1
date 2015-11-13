package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;


public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(getString(R.string.MOVIE_PARCEL), getIntent().getIntExtra(getString(R.string.MOVIE_PARCEL), GridView.INVALID_POSITION));
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_detail, new MovieDetailActivityFragment(), "MDA_FRAGMENT")
                    .commit();
        }
    }
}
