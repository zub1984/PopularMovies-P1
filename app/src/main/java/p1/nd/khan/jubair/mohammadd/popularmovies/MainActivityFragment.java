package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import p1.nd.khan.jubair.mohammadd.popularmovies.adapter.MovieAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;
import p1.nd.khan.jubair.mohammadd.popularmovies.listener.EndlessScrollListener;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;

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

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,AbsListView.OnItemClickListener{

    private static final int CURSOR_LOADER_ID = 0;
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    OnMoviePosterSelectedListener mCallback;
    private MovieAdapter mMovieAdapter;
    private String mSortOrder;
    private int PAGE_NO;
    private String optionSelected;

    public static final String SORT_ORDER_POPULARITY = MovieEntry.C_VOTE_COUNT + " DESC," + MovieContract.MovieEntry._ID + " ASC";
    public static final String SORT_ORDER_RATING = MovieContract.MovieEntry.C_VOTE_AVERAGE + " DESC," + MovieContract.MovieEntry._ID + " ASC";

    private static final String[] MOVIE_LIST_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieEntry.C_MOVIE_ID,
            MovieEntry.C_POSTER_PATH
    };
    public static final int C_ID = 0;
    public static final int C_MOVIE_ID = 1;
    public static final int C_POSTER_PATH = 2;

    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    GridView mGridView;

    public MainActivityFragment() {
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
        mSortOrder = Utility.getPreferredSorting(getContext());

        if (savedInstanceState != null) {
            PAGE_NO = savedInstanceState.getInt("page_no");
        } else {
            PAGE_NO = 1;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.updatePreferredSorting(getActivity(), mSortOrder);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        outState.putInt("page_no", PAGE_NO);

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mCallback) {
            // increment the position to match Database Ids indexed starting at 1
            //int uriId = position + 1;
            //Log.v(LOG_TAG, "==onItemClick,uriId:" + uriId);
            // CursorAdapter returns a cursor at the correct position for getItem(), or null
            // if it cannot seek to that position.
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            if (cursor != null) {
                mCallback.onMoviePosterSelected(cursor.getInt(MainActivityFragment.C_MOVIE_ID));
            }
            mPosition =position;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                optionSelected = getString(R.string.SORT_ORDER_POPULARITY);
                break;
            case R.id.action_sort_by_rating:
                optionSelected = getString(R.string.SORT_ORDER_RATING);
                break;
        }
        item.setChecked(true);
        onSortingOptionChanged();
        return super.onOptionsItemSelected(item);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    private void onSortingOptionChanged() {
        if (optionSelected != null && !optionSelected.equals(mSortOrder)) {
            Log.v(LOG_TAG, "==onSortingOptionChanged,optionSelected:"+optionSelected+",mSortOrder:"+mSortOrder);
            PAGE_NO = 1;
            mSortOrder = optionSelected;
            getMovieFromNet(optionSelected, PAGE_NO);
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        }
    }

    // Append more data into the adapter
    private void customLoadMoreDataFromApi(int pOffset) {
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
        if (Utility.isNetworkAvailable(getActivity())){
            MovieSyncAdapter.syncImmediately(getActivity());
        }
        else Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);
        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.movie_list_gridview);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(this);

        // Attach the listener to the AdapterView onCreate
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        if (mSortOrder != null) {
            bundle.putString(getString(R.string.pref_sort_order_key), mSortOrder);
        }

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(PAGE_NO);
        Cursor cur = getActivity().getContentResolver().query(movieUri, MOVIE_LIST_COLUMNS, null, null, SORT_ORDER_POPULARITY);
        if (null!=cur && 0==cur.getCount()){
            //Log.d(LOG_TAG, "no data in cursor!");
            getMovieFromNet(mSortOrder, PAGE_NO);
        }
        getLoaderManager().initLoader(CURSOR_LOADER_ID, bundle, this);
        if (!cur.isClosed()) cur.close();

        super.onActivityCreated(savedInstanceState);
    }

    // Attach loader to our flavors database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(PAGE_NO);
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_LIST_COLUMNS,
                null,
                null,
                SORT_ORDER_POPULARITY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            Log.v(LOG_TAG, "===onLoadFinished ,mPosition:"+mPosition);
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }

    /* Container Activity must implement this interface
       http://developer.android.com/training/basics/fragments/communicating.html
       Communicating with Other Fragments*/
    public interface OnMoviePosterSelectedListener {
        void onMoviePosterSelected(int movieId);
    }
}
