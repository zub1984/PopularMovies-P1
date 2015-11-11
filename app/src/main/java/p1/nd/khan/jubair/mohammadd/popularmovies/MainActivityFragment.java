package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.ContentValues;
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
import android.widget.GridView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import p1.nd.khan.jubair.mohammadd.popularmovies.adapter.MovieAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.listener.EndlessScrollListener;

import static p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;


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

public class MainActivityFragment extends Fragment implements AbsListView.OnItemClickListener{

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayList<String> mMovieUrls;
    private MovieAdapter mMovieAdapter;
    private String mSortOrder;
    private SharedPreferences mPrefs;
    private ArrayList<MdbMovie> mdbMovies = new ArrayList<>();
    private int PAGE_NO;
    private String optionSelected;

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
        mPrefs = getActivity().getPreferences(0);
        mSortOrder = mPrefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.SORT_ORDER_POPULARITY));

        if (savedInstanceState != null) {
            mdbMovies = savedInstanceState.getParcelableArrayList("movie_stored");
            PAGE_NO = savedInstanceState.getInt("page_no");
        } else {
            PAGE_NO = 1;
            getMovieFromNet(mSortOrder, PAGE_NO);
        }
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
        outState.putInt("page_no", PAGE_NO);
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
                optionSelected = getString(R.string.SORT_ORDER_POPULARITY);
                break;
            case R.id.action_sort_by_rating:
                item.setChecked(true);
                optionSelected = getString(R.string.SORT_ORDER_RATING);
                break;
        }
        // mRestoredState = false; //fix the issue of sorting on rotation
        reloadMovie(optionSelected);
        getMovieFromNet(optionSelected, PAGE_NO);
        return super.onOptionsItemSelected(item);
    }

    public void reloadMovie(String pOrder) {
        if (optionSelected != null && !optionSelected.equals(mSortOrder)) {
            PAGE_NO = 1;
            mSortOrder = pOrder;
            mdbMovies.clear();
            mMovieUrls.clear();
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int pOffset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        PAGE_NO = pOffset;
        getMovieFromNet(mSortOrder, pOffset);
    }

    /**
     * Gets movie details from MDB database from using internet call
     *
     * @param sortOrder to fetch the movie details from MDB.
     */
    private void getMovieFromNet(String sortOrder, int pPageNo) {
        if (Utility.isNetworkAvailable(getActivity()))
            OkHttpClientRequestHandler(sortOrder, pPageNo);
        else Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
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
                            // verify content provider
                            mdbMovies = convertContentValuesToUXFormat(insertMovieContentValues(jsonData));
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mdbMovies != null && mdbMovies.size() > 0) {
                                            mMovieUrls.clear();
                                            mMovieAdapter.notifyDataSetChanged();
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
        mMovieAdapter = new MovieAdapter(getContext(),mMovieUrls);

        // if it is restored state load data from saved state (happens while switching from portrait mode to landscape mode)
        if (mdbMovies.size() > 0) {
            //load poster image name from MdbMovie object
            for (MdbMovie movie : mdbMovies) mMovieUrls.add(movie.getPosterUrl());
            mMovieAdapter.notifyDataSetChanged();
        }

        // Get a reference to the ListView, and attach this adapter to it.
        GridView mGridView = (GridView) rootView.findViewById(R.id.movie_list_gridview);
        mGridView.setAdapter(mMovieAdapter);
        // Set OnItemClickListener so we can be notified on item clicks
        mGridView.setOnItemClickListener(this);

        // Attach the listener to the AdapterView onCreate
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        return rootView;
    }

    /**
     * Format and prepare the required URL for MDB request
     * Example : http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=PERSONAL_API_KEY&page=1
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
     * Take the String representing the complete response from MDB in JSON format and
     * pull out the data we need for content provider.
     *
     * @param mdbJsonStr : JSON string response
     */
    private List<ContentValues> insertMovieContentValues(String mdbJsonStr)
            throws JSONException {
        int result = 0;

        JSONObject mdbMovieJson = new JSONObject(mdbJsonStr);
        JSONArray moviesArray = mdbMovieJson.getJSONArray(getString(R.string.MDB_REQ_RESULTS));

        List<ContentValues> moviesList = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject mAttributes = moviesArray.getJSONObject(i);
            ContentValues mValues = new ContentValues();

            mValues.put(MovieEntry.C_MOVIE_ID, mAttributes.getString(getString(R.string.MDB_MOVIE_ID)));
            mValues.put(MovieEntry.C_ORIGINAL_TITLE, mAttributes.getString(getString(R.string.MDB_ORIGINAL_TITLE)));
            mValues.put(MovieEntry.C_OVERVIEW, mAttributes.getString(getString(R.string.MDB_OVERVIEW)));
            mValues.put(MovieEntry.C_BACKDROP_PATH, mAttributes.getString(getString(R.string.MDB_BACKDROP_PATH)));
            mValues.put(MovieEntry.C_POSTER_PATH, mAttributes.getString(getString(R.string.MDB_POSTER_IMAGE)));
            mValues.put(MovieEntry.C_RELEASE_DATE, mAttributes.getString(getString(R.string.MDB_RELEASE_DATE)));
            mValues.put(MovieEntry.C_VOTE_AVERAGE, mAttributes.getString(getString(R.string.MDB_VOTE_AVERAGE)));
            mValues.put(MovieEntry.C_VOTE_COUNT, mAttributes.getString(getString(R.string.MDB_VOTE_COUNT)));
            moviesList.add(mValues);
        }
        // Call bulkInsert to add the Movie Entries to the database from here
        if (moviesList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    MovieEntry.CONTENT_URI,
                    moviesList.toArray(new ContentValues[moviesList.size()]));
        }
        Log.v(LOG_TAG, "===insertMovieContents: " + result);
        return moviesList;
    }

    /*
       This code will allow to continue to return the data that
       the UX expects so that we can continue to test the application even once we begin using
       the database.
     */
    private ArrayList<MdbMovie> convertContentValuesToUXFormat(List<ContentValues> cvv) {
        // return strings to keep UI functional for now
        for (int i = 0; i < cvv.size(); i++) {
            ContentValues movieValues = cvv.get(i);
            MdbMovie movieObj;
            movieObj = new MdbMovie(
                    movieValues.getAsString(MovieEntry.C_BACKDROP_PATH),
                    movieValues.getAsString(MovieEntry.C_MOVIE_ID),
                    movieValues.getAsString(MovieEntry.C_ORIGINAL_TITLE),
                    movieValues.getAsString(MovieEntry.C_OVERVIEW),
                    movieValues.getAsString(MovieEntry.C_POSTER_PATH),
                    movieValues.getAsString(MovieEntry.C_RELEASE_DATE),
                    movieValues.getAsString(MovieEntry.C_VOTE_AVERAGE),
                    movieValues.getAsString(MovieEntry.C_VOTE_COUNT)
            );
            mdbMovies.add(movieObj);
        }
        return mdbMovies;
    }
}
