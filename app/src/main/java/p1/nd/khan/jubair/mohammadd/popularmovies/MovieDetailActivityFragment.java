package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.text.MessageFormat;

import p1.nd.khan.jubair.mohammadd.popularmovies.adapter.MovieDetailsAdapter;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.TrailersEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */


public class MovieDetailActivityFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    public static final String LIST_VIEW_STATE = "list_view_state";
    public static final String FAVORITES = "favorites";
    private static final int CURSOR_DETAIL_LOADER_ID = 0;
    View rootView;
    private int mMovieId = -1;
    private boolean mFavorite = false;
    private ListView mListView;
    private MovieDetailsAdapter mMovieDetailsAdapter;
    private Parcelable mRestoreListViewState;
    private CursorLoader mMovieDetailLoader;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (null != intent) {
            mMovieId = intent.getIntExtra(getString(R.string.MOVIE_PARCEL), -1);
            mFavorite = intent.getStringExtra(getString(R.string.pref_sort_order_key)).
                    equalsIgnoreCase(getString(R.string.SORT_ORDER_FAVORITE));
        }

        if (!mFavorite) {
            mFavorite = isMovieFavorite(mMovieId);
        }
        if (!mFavorite) {
            MovieSyncAdapter.syncMovieDetails(getActivity(), Integer.toString(mMovieId));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mListView = (ListView) rootView.findViewById(R.id.detail_movie_container);
        boolean mFavorite = false;
        mMovieDetailsAdapter = new MovieDetailsAdapter(getActivity(), null, 0, this, mFavorite);
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
        Log.v(LOG_TAG, "onCreateLoader-1 - called,mMovieId:" + mMovieId + ",mFavorite:" + mFavorite);
        //content://p1.nd.khan.jubair.mohammadd.popularmovies.app/movies/details/19995
       if (id == CURSOR_DETAIL_LOADER_ID && args.containsKey(getString(R.string.MOVIE_PARCEL))) {
            Uri baseContentUri = mFavorite ? MovieContract.FAVORITES_BASE_CONTENT_URI : MovieContract.BASE_CONTENT_URI;
            Uri.Builder builder = baseContentUri.buildUpon()
                    .appendPath(MovieContract.MOVIES_PATH)
                    .appendPath(MovieContract.PATH_MOVIE_DETAILS);
            ContentUris.appendId(builder, mMovieId);

            mMovieDetailLoader= new CursorLoader(getActivity(), builder.build(), null, null, null, null);
       }
        return mMovieDetailLoader;

      /* Uri baseUri = MovieContract.BASE_CONTENT_URI;
        Uri.Builder builder = baseUri.buildUpon()
                .appendPath(MovieContract.MOVIES_PATH)
                .appendPath(MovieContract.PATH_MOVIE_DETAILS);
        Uri uri = ContentUris.appendId(builder, mMovieId).build();

        String selection = MovieEntry.C_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(mMovieId)};

        return new CursorLoader(
                getActivity(),
                uri,
                MOVIE_DETAIL_COLUMNS,
                selection,
                selectionArgs,
                null);*/
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

    @Override
    public void onClick(View view) {
        Log.v(LOG_TAG,"onClick - method");
        String tag;
        if ((null!= (tag = (String) view.getTag(R.id.FAVORITES_KEY)))) {
            Toast.makeText(getActivity(), "onClick!!", Toast.LENGTH_SHORT).show();
           /* String movieTitle = addToFavorites(Integer.parseInt(tag));
            if (null!=movieTitle) {
                String toastText = MessageFormat.format(getString(R.string.ADD_2_FAVORITES), movieTitle);
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
                *//*view.setVisibility(View.GONE);
                ((View)view.getTag(R.id.LIST_ITEM_STAR_BLACK)).setVisibility(View.VISIBLE);*//*
            }*/
        } else if ((tag = (String) view.getTag(R.id.SHARE_KEY)) != null) {
            shareTrailer(tag, (String) view.getTag(R.id.SHARE_NAME));
        } else if ((tag = (String) view.getTag(R.id.TRAILER_KEY)) != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MessageFormat.format(getString(R.string.youtube_url), tag))));
        }
    }

    private void shareTrailer(String tKey, String tName) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        share.putExtra(Intent.EXTRA_SUBJECT, tName);
        share.putExtra(Intent.EXTRA_TEXT, MessageFormat.format(getString(R.string.youtube_url), tKey));
        startActivity(Intent.createChooser(share, "Share Movie Trailer"));
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
     * Method to check if movie is favorite.
     *
     * @param movieId to check.
     * @return movie title after addition else null
     */
    private String addToFavorites(int movieId) {
        String title = null;

        String[] projection = new String[]{"*"};
        String selection = MovieEntry.C_MOVIE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cMovies = null;
        Cursor cTrailers = null;
        Cursor cReviews = null;

        try {
            cMovies = contentResolver.query(MovieEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            if (cMovies.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cMovies, contentValues);
                contentValues.remove("_id");
                title = contentValues.getAsString(MovieEntry.C_ORIGINAL_TITLE);
                contentResolver.insert(MovieEntry.FAVORITES_CONTENT_URI, contentValues);
            }

            cTrailers = contentResolver.query(TrailersEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            while (cTrailers.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cTrailers, contentValues);
                contentValues.remove("_id");
                contentResolver.insert(TrailersEntry.FAVORITES_CONTENT_URI, contentValues);
            }

            cReviews = contentResolver.query(ReviewsEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            while (cReviews.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cReviews, contentValues);
                contentValues.remove("_id");
                contentResolver.insert(ReviewsEntry.FAVORITES_CONTENT_URI, contentValues);
            }
        } finally {
            if (null != cMovies) cMovies.close();
            if (null != cTrailers) cTrailers.close();
            if (null != cReviews) cReviews.close();
        }
        return title;
    }
}
