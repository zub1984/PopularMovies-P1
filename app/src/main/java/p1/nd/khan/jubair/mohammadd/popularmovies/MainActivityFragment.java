package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.adapter.MovieAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.listener.EndlessScrollListener;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Utility;

import static p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;


public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnItemClickListener {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    OnMoviePosterSelectedListener mCallback;
    private MovieAdapter mMovieAdapter;
    private String mSortOrder;
    private String optionSelected;

    private static final String[] MOVIE_LIST_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.C_MOVIE_ID,
            MovieEntry.C_POSTER_PATH,
            MovieEntry.C_VOTE_AVERAGE,
            MovieEntry.C_RELEASE_DATE
    };
   /* public static final int C_ID = 0;
    public static final int C_MOVIE_ID = 1;
    public static final int C_POSTER_PATH = 2;*/

    private int mPosition = GridView.INVALID_POSITION;
    private Parcelable mRestoreGridViewState;


    @Bind(R.id.movie_list_gridview)
    GridView mGridView;

    @Bind(R.id.main_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

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
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.updatePreferredSorting(getContext(), mSortOrder);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        int position = mGridView.getFirstVisiblePosition();
        outState.putInt(Constants.SELECTED_MOVIE_KEY, position);
        outState.putParcelable(Constants.SELECTED_GRID_VIEW, mGridView.onSaveInstanceState());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mCallback) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            if (null != cursor) {
                mCallback.onMoviePosterSelected(
                        cursor.getInt(cursor.getColumnIndex(MovieEntry.C_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieEntry.C_POSTER_PATH)),
                        getView(),
                        position,
                        mSortOrder);
            }
            mPosition = position;
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
            case R.id.action_show_favorites:
                optionSelected = getString(R.string.SORT_ORDER_FAVORITE);
                break;
        }

        item.setChecked(true);
        onSortingOptionChanged(optionSelected);
        return super.onOptionsItemSelected(item);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    private void onSortingOptionChanged(String optionSelected) {
        mSortOrder = optionSelected;
        Utility.updatePreferredSorting(getContext(), mSortOrder);
        if (!optionSelected.equalsIgnoreCase(getString(R.string.SORT_ORDER_FAVORITE))) {
            Log.v(LOG_TAG, "option changed, reload movie,mSortOrder:" + mSortOrder);
            MovieSyncAdapter.syncImmediately(getActivity(), mSortOrder);
        }
        reLoad();
        mSwipeRefreshLayout.setRefreshing(true);
        mMovieAdapter.notifyDataSetChanged();
    }

    private void reLoad() {
        getLoaderManager().restartLoader(Constants.CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView start- from MainActivityFragment!");
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        //<https://www.bignerdranch.com/blog/implementing-swipe-to-refresh/>
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setProgressViewOffset(true, getResources().getInteger(R.integer.progress_offset_start), getResources().getInteger(R.integer.progress_offset_end));

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0, Constants.CURSOR_LOADER_ID);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                MovieSyncAdapter.loadMoreData(getContext(), page);
                return true;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.SELECTED_MOVIE_KEY)) {
            mPosition = savedInstanceState.getInt(Constants.SELECTED_MOVIE_KEY);
            mRestoreGridViewState = savedInstanceState.getParcelable(Constants.SELECTED_GRID_VIEW);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = new Bundle();
        if (null!=mSortOrder) {
            bundle.putString(Constants.SORTING_KEY, mSortOrder);
        }
        getLoaderManager().initLoader(Constants.CURSOR_LOADER_ID, bundle, this);
    }

    // Attach loader to our database query run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Constants.CURSOR_LOADER_ID:
                String sortOrder = Utility.getPreferredSorting(getContext());
                if (sortOrder.equals(getString(R.string.SORT_ORDER_FAVORITE)))
                    return new CursorLoader(getActivity(), MovieEntry.FAVORITES_CONTENT_URI, MOVIE_LIST_COLUMNS, null, null, null);
                else
                    return new CursorLoader(getActivity(), MovieEntry.CONTENT_URI, MOVIE_LIST_COLUMNS, null, null,
                            sortOrder.equals(getString(R.string.SORT_ORDER_POPULARITY)) ? Constants.DB_ORDER_POPULARITY : Constants.DB_ORDER_RATING);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case Constants.CURSOR_LOADER_ID:
                mSwipeRefreshLayout.setRefreshing(false);
                mMovieAdapter.swapCursor(cursor);
                if (mPosition != GridView.INVALID_POSITION && mRestoreGridViewState != null && mGridView.getCount() >= mPosition) {
                    mGridView.onRestoreInstanceState(mRestoreGridViewState);
                    mPosition = GridView.INVALID_POSITION;
                    mRestoreGridViewState = null;
                }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        switch (cursorLoader.getId()) {
            case Constants.CURSOR_LOADER_ID:
                mSwipeRefreshLayout.setRefreshing(false);
                mMovieAdapter.swapCursor(null);
        }
    }

    /* Container Activity must implement this interface
       http://developer.android.com/training/basics/fragments/communicating.html
       Communicating with Other Fragments*/
    public interface OnMoviePosterSelectedListener {
        void onMoviePosterSelected(int movieId, String posterImage, View view, int position, String mSortOrder);
    }


    @Override
    public void onRefresh() {
        reLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
