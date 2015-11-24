package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.TrailersEntry;

/**
 * Created by laptop on 11/9/2015.
 */
public class MovieProvider extends ContentProvider {
    private final String LOG_TAG = MovieProvider.class.getSimpleName();

    protected MovieDbHelper mOpenHelper;
    protected UriMatcher mUriMatcher;

    protected static final int MOVIE = 100;
    protected static final int MOVIES_ITEM = 101;
    protected static final int MOVIE_DETAILS = 103;

    protected static final int TRAILERS = 200;
    protected static final int TRAILER_ITEM = 201;

    protected static final int REVIEWS = 300;
    protected static final int REVIEW_ITEM = 301;


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return true;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MOVIES_ITEM:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case TRAILER_ITEM:
                return TrailersEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return TrailersEntry.CONTENT_TYPE;
            case REVIEW_ITEM:
                return ReviewsEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return ReviewsEntry.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MovieEntry.CONTENT_DETAILS_TYPE;
            default:
                //throw new UnsupportedOperationException("Unknown uri: " + uri);
                Log.e(LOG_TAG, "Unknown uri :" + uri);
                return "Unknown uri";
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = mUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {

            case MOVIES_ITEM:
                retCursor = db.query(MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE:
                retCursor = db.query(MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_DETAILS:
                long movieId = ContentUris.parseId(uri);
                String where = MovieEntry.C_MOVIE_ID + "=?";
                String[] argId = {String.valueOf(movieId)};

                String[] columns = new String[]{"*", "'" + MovieEntry.TABLE_NAME + "' as " + MovieContract.MOVIE_DETAIL_TABLE};
                Cursor[] cursors = new Cursor[3];

                cursors[0] = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        columns, where, argId, null, null, sortOrder);
                cursors[0].setNotificationUri(getContext().getContentResolver(), MovieEntry.CONTENT_URI);

                columns[1] = "'" + TrailersEntry.TABLE_NAME + "' as " + MovieContract.MOVIE_DETAIL_TABLE;
                cursors[1] = mOpenHelper.getReadableDatabase().query(TrailersEntry.TABLE_NAME,
                        columns, where, argId, null, null, sortOrder);
                cursors[1].setNotificationUri(getContext().getContentResolver(), TrailersEntry.CONTENT_URI);

                columns[1] = "'" + ReviewsEntry.TABLE_NAME + "' as " + MovieContract.MOVIE_DETAIL_TABLE;
                cursors[2] = mOpenHelper.getReadableDatabase().query(ReviewsEntry.TABLE_NAME,
                        columns, where, argId, null, null, sortOrder);
                cursors[2].setNotificationUri(getContext().getContentResolver(), ReviewsEntry.CONTENT_URI);

                retCursor = new MergeCursor(cursors);
                break;

            case TRAILERS:
                retCursor = db.query(TrailersEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case REVIEWS:
                retCursor = db.query(ReviewsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                //throw new UnsupportedOperationException("Unknown uri: " + uri);
                Log.e(LOG_TAG, "Unknown uri :" + uri);
                return null;
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE:
                long movieId = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (movieId > 0) {
                    returnUri = MovieEntry.buildMovieUri(movieId);
                } else {
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    Log.e(LOG_TAG, "Movie insert failed :" + uri);
                    returnUri=null;
                }
                break;
            case TRAILERS:
                long trailerId = db.insert(TrailersEntry.TABLE_NAME, null, values);
                if (trailerId > 0) {
                    returnUri = TrailersEntry.buildTrailerUri(trailerId);
                } else {
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    Log.e(LOG_TAG, "Trailer insert failed :" + uri);
                    returnUri=null;
                }
                break;
            case REVIEWS:
                long reviewId = db.insert(ReviewsEntry.TABLE_NAME, null, values);
                if (reviewId > 0) {
                    returnUri = ReviewsEntry.buildReviewsUri(reviewId);
                } else {
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    Log.e(LOG_TAG, "Review insert failed :" + uri);
                    returnUri=null;
                }
                break;
            default:
                //throw new UnsupportedOperationException("Unknown uri: " + uri);
                Log.e(LOG_TAG, "Unknown uri :" + uri);
                returnUri=null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                //throw new UnsupportedOperationException("Unknown uri: " + uri);
                Log.e(LOG_TAG, "Unknown uri :" + uri);
                rowsDeleted=0;
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsUpdated = db.update(TrailersEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(ReviewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                //throw new UnsupportedOperationException("Unknown uri: " + uri);
                Log.e(LOG_TAG,"Unknown uri :"+uri);
                rowsUpdated=0;
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int result = 0;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnMoviesCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnMoviesCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                result = returnMoviesCount;
                break;
            case TRAILERS:
                db.beginTransaction();
                int returnTrailersCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(TrailersEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnTrailersCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                result = returnTrailersCount;
                break;
            case REVIEWS:
                db.beginTransaction();
                int returnReviewsCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(ReviewsEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnReviewsCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                result = returnReviewsCount;
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }






    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    protected UriMatcher buildUriMatcher() {

        /*All paths added to the UriMatcher have a corresponding code to return when a match is
        found.  The code passed into the constructor represents the code to return for the root
        URI.  It's common to use NO_MATCH as the code for this case.*/

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.MOVIES_PATH, MOVIE);

        //content://p1.nd.khan.jubair.mohammadd.popularmovies.app/movies/131631
        matcher.addURI(authority, MovieContract.MOVIES_PATH + "/#", MOVIES_ITEM);

        //content://p1.nd.khan.jubair.mohammadd.popularmovies.app/movies/details/131631
        matcher.addURI(authority, MovieContract.MOVIES_PATH + "/" + MovieContract.PATH_MOVIE_DETAILS + "/#", MOVIE_DETAILS);

         /* trailers matcher */
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/#", TRAILER_ITEM);

        /* review matcher */
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", REVIEW_ITEM);

        return matcher;
    }
}

