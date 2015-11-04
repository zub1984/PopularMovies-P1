package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
/*Copyright 2014 Square, Inc.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

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
                mRestoredState = false; //fix the issue of sorting on rotation
                getMovieFromNet(mSortOrder);
                break;
            case R.id.action_sort_by_rating:
                item.setChecked(true);
                mSortOrder = getString(R.string.SORT_ORDER_RATING);
                mRestoredState = false; //fix the issue of sorting on rotation
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
        // onSaveInstanceState: state is stored, URL call not required when mRestoredState=true.
        if (!mRestoredState) {
            if (Utility.isNetworkAvailable(getActivity())) {
                // Get Movie from Internet
                OkHttpClientRequestHandler(sortOrder, 1);
            } else {
                Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * OkHttpClientRequestHandler, to make ok http request to //http://api.themoviedb.org/3/discover/movie
     * http://square.github.io/okhttp/
     * http://stackoverflow.com/questions/16902716/comparison-of-android-networking-libraries-okhttp-retrofit-volley
     * Note :Planned to implement retrofit for stage:2
     *
     * @param pSortOrder : user preferred order for movie display.
     * @param pPageNo    : page number to fetch the record.
     */
    private void OkHttpClientRequestHandler(String pSortOrder, int pPageNo) {
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(UrlFormatter(pSortOrder, pPageNo))
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(LOG_TAG, "==onFailure:", e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            mdbMovies = getMovieDataFromJson(jsonData);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mdbMovies != null && mdbMovies.size() > 0) {
                                            mMovieUrls.clear();
                                            for (MdbMovie aMovie : mdbMovies) {
                                                // Store movie poster URL into Adapter
                                                mMovieUrls.add(aMovie.getPosterUrl());
                                            }
                                            mMovieAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to load poster image in list.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(LOG_TAG, "IOException | JSONException: Exception caught: ", e);
                    }
                }
            });
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

    // Construct the URL for the Movies query
    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=PERSONAL_API_KEY&page=1

    /**
     * Format and prepare the required URL for MDB request
     *
     * @param pSortOrder : user preferred order for movie display.
     * @param pPageNo    : page number to fetch the record.
     */
    private String UrlFormatter(String pSortOrder, int pPageNo) {
        final String SORT_BY = "sort_by";
        final String API_KEY = "api_key";
        final String PAGE = "page";
        Uri builtUri = Uri.parse(getString(R.string.MDB_POPULAR_MOVIE_URL)).buildUpon()
                .appendQueryParameter(SORT_BY, pSortOrder)
                .appendQueryParameter(API_KEY, getString(R.string.PERSONAL_API_KEY))
                .appendQueryParameter(PAGE, Integer.toString(pPageNo))
                .build();
        return builtUri.toString();
    }

    /**
     * Take the String representing the complete response from MDB in JSON Format and
     * pull out the data we need.
     *
     * @param mdbJsonStr : JSON string response
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
            // Read movie attributes from JSONObject and add to Movie List.
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
}
