package p1.nd.khan.jubair.mohammadd.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

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

import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Utility;


/**
 * Created by laptop on 11/13/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    private Context mContext;

    private final TrailerSyncAdapter trailerSyncAdapter;
    private final ReviewSyncAdapter reviewSyncAdapter;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        trailerSyncAdapter = new TrailerSyncAdapter(context);
        reviewSyncAdapter = new ReviewSyncAdapter(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if (extras.containsKey(Constants.DETAIL_SYNC_MOVIE_ID)) {
            //Log.v(LOG_TAG, "fetch movies details data, onPerformSync!");
            trailerSyncAdapter.onPerformSync(account, extras, authority, provider, syncResult);
            reviewSyncAdapter.onPerformSync(account, extras, authority, provider, syncResult);
        } else {
            String bundleSortType= Utility.getPreferredSorting(mContext);
            //Log.v(LOG_TAG, "fetch movies data, onPerformSync!,bundleSortType:"+bundleSortType);
            if (! mContext.getString(R.string.SORT_ORDER_FAVORITE).equals(bundleSortType)) {
                getContext().getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
                int page = extras.getInt("page", 1);
                fetchMdbMovie(bundleSortType, page);
            }
        }
    }

    /**
     * Method to fetch the movie details from server.
     *
     * @param pSortOrder : user preferred order for movie display.
     * @param pPageNo    : page number to fetch the record.
     */

    private void fetchMdbMovie(String pSortOrder, int pPageNo) {
        OkHttpClient client = new OkHttpClient();
        String movieURL = UrlFormatter(pSortOrder, pPageNo);
        Log.v(LOG_TAG, "URL:" + movieURL);
        Request request = new Request.Builder()
                .url(movieURL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(LOG_TAG, "==onFailure[fetchMdbMovie]:", e);
                return;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        insertMovieContentValues(jsonData);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, "IOException | JSONException: Exception caught: ", e);
                    return;
                }
            }
        });
    }

    /**
     * Format and prepare the required URL for MDB request
     * Example : http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=PERSONAL_API_KEY&page=1
     *
     * @param pSortOrder : user preferred order for movie display.
     * @param pPageNo    : page number to fetch the record.
     */
    private String UrlFormatter(String pSortOrder, int pPageNo) {
        Uri builtUri = Uri.parse(mContext.getString(R.string.MDB_POPULAR_MOVIE_URL)).buildUpon()
                .appendQueryParameter(Constants.SORT_BY, pSortOrder)
                .appendQueryParameter(Constants.API_KEY, mContext.getString(R.string.PERSONAL_API_KEY))
                .appendQueryParameter(Constants.PAGE, Integer.toString(pPageNo))
                .appendQueryParameter(Constants.MDB_INCLUDE_ADULT, "false")
                .build();
        return builtUri.toString();
    }

    /**
     * Take the String representing the complete response from MDB in JSON format and
     * pull out the data we need for content provider.
     *
     * @param mdbJsonStr : JSON string response
     */
    private void insertMovieContentValues(String mdbJsonStr)
            throws JSONException {
        int result = 0;
        JSONObject mdbMovieJson = new JSONObject(mdbJsonStr);
        JSONArray moviesArray = mdbMovieJson.getJSONArray(Constants.MDB_REQ_RESULTS);
        List<ContentValues> moviesList = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject mAttributes = moviesArray.getJSONObject(i);
            ContentValues mValues = new ContentValues();

            mValues.put(MovieEntry.C_MOVIE_ID, mAttributes.getString(Constants.MDB_MOVIE_ID));
            mValues.put(MovieEntry.C_ORIGINAL_TITLE, mAttributes.getString(Constants.MDB_ORIGINAL_TITLE));
            mValues.put(MovieEntry.C_OVERVIEW, mAttributes.getString(Constants.MDB_OVERVIEW));
            mValues.put(MovieEntry.C_BACKDROP_PATH, mAttributes.getString(Constants.MDB_BACKDROP_PATH));
            mValues.put(MovieEntry.C_POSTER_PATH, mAttributes.getString(Constants.MDB_POSTER_IMAGE));
            mValues.put(MovieEntry.C_RELEASE_DATE, mAttributes.getString(Constants.MDB_RELEASE_DATE));
            mValues.put(MovieEntry.C_VOTE_AVERAGE, mAttributes.getString(Constants.MDB_VOTE_AVERAGE));
            mValues.put(MovieEntry.C_VOTE_COUNT, mAttributes.getString(Constants.MDB_VOTE_COUNT));
            moviesList.add(mValues);
        }
        // Call bulkInsert to add the Movie Entries to the database from here
        if (moviesList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    MovieEntry.CONTENT_URI,
                    moviesList.toArray(new ContentValues[moviesList.size()]));
        }
        Log.v(LOG_TAG, "===[No of Movie Inserted]: " + result);
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter immediately
     *
     * @param context  The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.v("LOG_TAG", "===[called syncImmediately ]: ");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter immediately
     *
     * @param context The context used to access the account service
     * @param movieId The Movie to be sync
     */
    public static void syncMovieDetails(Context context, String movieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (null != movieId) {
            bundle.putString(Constants.DETAIL_SYNC_MOVIE_ID, movieId);
        }
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context  The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Method to Sync data after account creation.
     *
     * @param newAccount instance of new account
     * @param context    The context used to access the account service.
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        MovieSyncAdapter.configurePeriodicSync(context, Constants.SYNC_INTERVAL, Constants.SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Method to load more data based on scrolling.
     *
     * @param context The context used to load more movie.
     * @param offset  page number to request/load the data.
     */
    public static void loadMoreData(Context context, int offset) {
        //Log.v("LOG_TAG","loadMoreData:"+offset);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt("page", offset);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

}
