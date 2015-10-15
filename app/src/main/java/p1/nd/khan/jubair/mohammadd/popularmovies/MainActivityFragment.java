package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayAdapter<String> mMovieListAdapter;
    private GridView gridView;

    // Default constructor
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String strData[] = {
                "Sun-Sunny-60/65",
                "Mon-rain-40/45",
                "Tue - HeavyRain - 65/60"
        };
        List<String> testData = new ArrayList<>(
                Arrays.asList(strData));

        mMovieListAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.list_item_movie_poster, R.id.list_item_movie_poster_textview, strData
        );

        gridView = (GridView) rootView.findViewById(R.id.list_item_movie_gridview);
        gridView.setAdapter(mMovieListAdapter);

        //return inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }
}
