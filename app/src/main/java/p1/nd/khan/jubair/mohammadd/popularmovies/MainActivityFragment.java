package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AbsListView.OnItemClickListener {


    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayList<String> mMovieUrls;
    private CustomImageAdapter mMovieAdapter;
    private String mSortOrder;
    private SharedPreferences mPrefs;
    private ArrayList<MdbMovie> mdbMovies = new ArrayList<>();
    private boolean mRestoredState;

    OnMoviePosterSelectedListener mCallback;

    public MainActivityFragment() {
    }

    /* Container Activity must implement this interface
       http://developer.android.com/training/basics/fragments/communicating.html
       Communicating with Other Fragments*/
    public interface OnMoviePosterSelectedListener {
        void onMoviePosterSelected(MdbMovie mdbMovies);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMoviePosterSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement onMoviePosterSelected.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mdbMovies = (ArrayList<MdbMovie>) savedInstanceState.get("movie_stored");
            mRestoredState = true;
        }
        mPrefs = getActivity().getPreferences(0);
        mSortOrder = mPrefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.SORT_ORDER_POPULARITY));
        getMovieFromNet(mSortOrder);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("sort_mode", mSortOrder);
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        // Save the movie's current state
        outState.putParcelableArrayList("movie_stored", mdbMovies);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mCallback) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mCallback.onMoviePosterSelected(mdbMovies.get(position));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                item.setChecked(true);
                mSortOrder = getString(R.string.SORT_ORDER_POPULARITY);
                getMovieFromNet(mSortOrder);
                break;
            case R.id.action_sort_by_rating:
                item.setChecked(true);
                mSortOrder = getString(R.string.SORT_ORDER_RATING);
                getMovieFromNet(mSortOrder);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets movie details from MDB database from using internet call
     *
     * @param sortOrder to fetch the movie details from MDB.
     */
    private void getMovieFromNet(String sortOrder) {
        // onSaveInstanceState: state is stored, URL call not required.
        if (!mRestoredState) {
            if (Utility.isNetworkAvailable(getActivity())) {
                // Get Movie from Internet
                FetchMovieTask fetchMoviesTask = new FetchMovieTask();
                fetchMoviesTask.execute(sortOrder);
            } else {
                Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieUrls = new ArrayList<>();
        mMovieAdapter = new CustomImageAdapter(mMovieUrls);

        // if it is restored state load data from saved state (happens while switching from portrait mode to landscape mode)
        // mRestoredState : changed to static to read/store data independent of instance.
        if (mRestoredState) {
            //load poster image name from MdbMovie object
            for (MdbMovie movie : mdbMovies) mMovieUrls.add(movie.getPosterUrl());
            mMovieAdapter.notifyDataSetChanged();
        }

        // Get a reference to the ListView, and attach this adapter to it.
        GridView mGridView = (GridView) rootView.findViewById(R.id.movie_list_gridview);
        //set adaptor
        mGridView.setAdapter(mMovieAdapter);
        // Set OnItemClickListener so we can be notified on item clicks
        mGridView.setOnItemClickListener(this);
        return rootView;
    }

    // https://www.bignerdranch.com/blog/solving-the-android-image-loading-problem-volley-vs-picasso/
    private class CustomImageAdapter extends ArrayAdapter<String> {

        public CustomImageAdapter(ArrayList<String> urls) {
            super(getActivity(), 0, urls);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_movie_poster, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_poster_image);
            imageView.setAdjustViewBounds(true); //Adjust its bound to max while Preserve the aspect ratio of Image

            // Download Image from TMDB
            Picasso.with(getActivity())
                    .load(getString(R.string.POSTER_IMAGE_URL) + getItem(position))
                    .into(imageView);

            return convertView;
        }
    }

    // Fetch movie details from MDB
    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MdbMovie>> {

        /**
         * Take the String representing the complete response from MDB in JSON Format and
         * pull out the data we need.
         */

        private ArrayList<MdbMovie> getMovieDataFromJson(String mdbJsonStr)
                throws JSONException {
            // The name of the JSON objects that need to be extracted.

            JSONObject mdbMovieJson = new JSONObject(mdbJsonStr);
            JSONArray moviesArray = mdbMovieJson.getJSONArray(getString(R.string.MDB_REQ_RESULTS));

            // Must clear the list before adding new
            mdbMovies.clear();

            for (int i = 0; i < moviesArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject movieAttributes = moviesArray.getJSONObject(i);
                // Create a MdbMovie object then add to ArrayList of Movies
                // read movie attributes from JSONObject.
                //Log.v(LOG_TAG, "MDB Response: " + movieAttributes.getString(MDB_ORIGINAL_TITLE));
                MdbMovie movieObj = new MdbMovie(movieAttributes.getString(getString(R.string.MDB_BACKDROP_PATH)),
                        movieAttributes.getString(getString(R.string.MDB_MOVIE_ID)),
                        movieAttributes.getString(getString(R.string.MDB_ORIGINAL_TITLE)),
                        movieAttributes.getString(getString(R.string.MDB_OVERVIEW)),
                        movieAttributes.getString(getString(R.string.MDB_POSTER_IMAGE)),
                        movieAttributes.getString(getString(R.string.MDB_RELEASE_DATE)),
                        movieAttributes.getString(getString(R.string.MDB_VOTE_AVERAGE)),
                        movieAttributes.getString(getString(R.string.MDB_VOTE_COUNT)));

                mdbMovies.add(movieObj);
            }
            return mdbMovies;
        }

        @Override
        protected ArrayList<MdbMovie> doInBackground(String... params) {

            if (params == null) {
                Toast.makeText(getActivity(), "Sort order not defined!", Toast.LENGTH_SHORT).show();
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            int page = 1;

            try {
                // Construct the URL for the TMDB Movies query
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=PERSONAL_API_KEY&page=1

                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";
                final String PAGE = "page";

                Uri builtUri = Uri.parse(getString(R.string.MDB_POPULAR_MOVIE_URL)).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, getString(R.string.PERSONAL_API_KEY))
                        .appendQueryParameter(PAGE, Integer.toString(page))
                        .build();

                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                //Create the request to themoviedb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Error ", e);
                // sort order is not passed.
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // didn't got the response, there's no point in attempting to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            // Function to format the JSON response from MDB
            try {
                return getMovieDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, " MDB Response cannot be converted to JSONObject" + e.getMessage(), e);
            }
            return null;
        }

        //New data is back from the server.  Hooray!
        @Override
        protected void onPostExecute(ArrayList<MdbMovie> movies) {
            if (movies != null && movies.size() > 0) {
                mMovieUrls.clear();
                for (MdbMovie aMovie : movies) {
                    // Store movie poster URL into Adapter
                    mMovieUrls.add(aMovie.getPosterUrl());
                }
                mMovieAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "onPostExecute: No Response from MDB", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
