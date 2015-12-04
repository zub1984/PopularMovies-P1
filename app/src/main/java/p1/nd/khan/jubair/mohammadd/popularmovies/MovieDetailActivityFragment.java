package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.adapter.MovieDetailsAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;

/**
 * A placeholder fragment containing a simple view.
 */


public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    private int mMovieId;
    private boolean mFavorite;
    private ListView mListView;
    private MovieDetailsAdapter mMovieDetailsAdapter;
    private Parcelable mRestoreListViewState;
    private CursorLoader mMovieDetailLoader;
    private String mPosterImage;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Nullable
    @Bind(R.id.include_not_selected_movie_details)
    FrameLayout noSelectedView;

    public static MovieDetailActivityFragment newInstance(Bundle bundle) {
        MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MovieDetailActivityFragment() {
        // do nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getArguments()) {
            mMovieId = getArguments().getInt(Constants.MOVIE_ID_KEY);
            String sortKey = getArguments().getString(Constants.SORTING_KEY);
            mPosterImage = getArguments().getString(Constants.POSTER_IMAGE_KEY);
            mFavorite = getString(R.string.SORT_ORDER_FAVORITE).equals(sortKey);

            if (!mFavorite && 0 != mMovieId) mFavorite = isMovieFavorite(mMovieId);
            if (!mFavorite && 0 != mMovieId)
                MovieSyncAdapter.syncMovieDetails(getActivity(), Integer.toString(mMovieId));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        /*------------set the tool bar ------------------*/
        if (getActivity() instanceof MovieDetailActivity) {
            MovieDetailActivity detailActivity = (MovieDetailActivity) getActivity();
            detailActivity.setToolbar(mToolbar, true, false);
        }
         /*-----------set the tool bar ------------------*/

        mListView = (ListView) rootView.findViewById(R.id.fragment_detail_movie_container);
        mMovieDetailsAdapter = new MovieDetailsAdapter(getActivity(), null, 0, mFavorite);
        mListView.setAdapter(mMovieDetailsAdapter);
        setMovieNotSelected();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.MOVIE_ID_KEY, mMovieId);
        outState.putBoolean(Constants.FAVORITES, mFavorite);
        outState.putParcelable(Constants.LIST_VIEW_STATE, mListView.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //Log.v(LOG_TAG, "[onActivityCreated called]");
        if (0 != mMovieId) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.MOVIE_ID_KEY, mMovieId);
            getLoaderManager().initLoader(Constants.CURSOR_DETAIL_LOADER_ID, bundle, this);
        }
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Constants.CURSOR_DETAIL_LOADER_ID:
                if (0 != args.getInt(Constants.MOVIE_ID_KEY)) {
                    Uri baseContentUri = mFavorite ? MovieContract.FAVORITES_BASE_CONTENT_URI : MovieContract.BASE_CONTENT_URI;
                    Uri.Builder builder = baseContentUri.buildUpon()
                            .appendPath(MovieContract.MOVIES_PATH)
                            .appendPath(MovieContract.PATH_MOVIE_DETAILS);
                    ContentUris.appendId(builder, mMovieId);
                    mMovieDetailLoader = new CursorLoader(getActivity(), builder.build(), null, null, null, null);
                }
        }
        return mMovieDetailLoader;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case Constants.CURSOR_DETAIL_LOADER_ID:
                mMovieDetailsAdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case Constants.CURSOR_DETAIL_LOADER_ID:
                mMovieDetailsAdapter.swapCursor(data);
                if (mRestoreListViewState != null) {
                    mListView.onRestoreInstanceState(mRestoreListViewState);
                    mRestoreListViewState = null;
                }
        }
    }

    /**
     * Method to check if movie is favorite.
     *
     * @param movieId to check.
     * @return true or false
     */
    private boolean isMovieFavorite(int movieId) {
        String[] projection = new String[]{MovieEntry.C_MOVIE_ID};
        String selection = MovieEntry.C_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(movieId)};
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(MovieEntry.FAVORITES_CONTENT_URI, projection, selection, selectionArgs, null);
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Method to load not selected movie view, in two pane mode.
     */
    private void setMovieNotSelected() {
        if(null!=noSelectedView) noSelectedView.setVisibility(0 == mMovieId ? noSelectedView.VISIBLE : noSelectedView.GONE);
    }

}
