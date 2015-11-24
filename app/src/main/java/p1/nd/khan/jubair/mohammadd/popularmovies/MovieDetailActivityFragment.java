package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import p1.nd.khan.jubair.mohammadd.popularmovies.adapter.MovieDetailsAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */


public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    public static final String LIST_VIEW_STATE = "list_view_state";
    public static final String FAVORITES = "favorites";
    private static final int CURSOR_DETAIL_LOADER_ID = 0;
    private int mMovieId = -1;
    private boolean mFavorite;
    private ListView mListView;
    private MovieDetailsAdapter mMovieDetailsAdapter;
    private Parcelable mRestoreListViewState;
    private CursorLoader mMovieDetailLoader;

    public MovieDetailActivityFragment() {
        // do nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (null != intent) {
            mMovieId = intent.getIntExtra(getString(R.string.MOVIE_PARCEL), -1);
            mFavorite = intent.getStringExtra(getString(R.string.pref_sort_order_key)).equals(getString(R.string.SORT_ORDER_FAVORITE));
        }
        if(!mFavorite) mFavorite=isMovieFavorite(mMovieId);
        if (!mFavorite) MovieSyncAdapter.syncMovieDetails(getActivity(), Integer.toString(mMovieId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mListView = (ListView) rootView.findViewById(R.id.detail_movie_container);
        mMovieDetailsAdapter = new MovieDetailsAdapter(getActivity(), null, 0, mFavorite);
        mListView.setAdapter(mMovieDetailsAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.MOVIE_PARCEL), mMovieId);
        outState.putBoolean(FAVORITES, mFavorite);
        outState.putParcelable(LIST_VIEW_STATE, mListView.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.MOVIE_PARCEL), mMovieId);
        getLoaderManager().initLoader(CURSOR_DETAIL_LOADER_ID, bundle, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CURSOR_DETAIL_LOADER_ID && args.containsKey(getString(R.string.MOVIE_PARCEL))) {
            Uri baseContentUri = mFavorite ? MovieContract.FAVORITES_BASE_CONTENT_URI : MovieContract.BASE_CONTENT_URI;
            Uri.Builder builder = baseContentUri.buildUpon()
                    .appendPath(MovieContract.MOVIES_PATH)
                    .appendPath(MovieContract.PATH_MOVIE_DETAILS);
            ContentUris.appendId(builder, mMovieId);
            mMovieDetailLoader = new CursorLoader(getActivity(), builder.build(), null, null, null, null);
        }
        return mMovieDetailLoader;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieDetailsAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieDetailsAdapter.swapCursor(data);
        if (mRestoreListViewState != null) {
            mListView.onRestoreInstanceState(mRestoreListViewState);
            mRestoreListViewState = null;
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

}
