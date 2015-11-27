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

import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.Utility;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;


/**
 * Created by laptop on 11/13/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    private Context mContext;

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public static final String MOVIE_ID = "movieid";

    private final TrailerSyncAdapter trailerSyncAdapter;
    private final ReviewSyncAdapter reviewSyncAdapter;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext=context;
        trailerSyncAdapter = new TrailerSyncAdapter(context);
        reviewSyncAdapter=new ReviewSyncAdapter(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        if (extras.containsKey(MOVIE_ID)) {
            trailerSyncAdapter.onPerformSync(account, extras, authority, provider, syncResult);
            reviewSyncAdapter.onPerformSync(account, extras, authority, provider, syncResult);
        }
        else
        {
            String sortOrder = Utility.getPreferredSorting(getContext());
            String bundleSortType = extras.getString(Constants.SORTING_KEY, mContext.getString(R.string.SORT_ORDER_POPULARITY));

            if (!sortOrder.equals(bundleSortType)) {
                getContext().getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
            }

            if (Utility.isNetworkAvailable(getContext())) {
                int page = extras.getInt("page", 1);
                Log.v(LOG_TAG, "page:" + page);
                fetchMdbMovie(bundleSortType, page);
            } else
                Toast.makeText(getContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    void fetchMdbMovie(String pSortOrder,int pPageNo ){
        OkHttpClient client = new OkHttpClient();
        String movieURL=UrlFormatter(pSortOrder, pPageNo);
        Log.v(LOG_TAG, "URL:" + movieURL);
        Request request = new Request.Builder()
                .url(movieURL)
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
                        insertMovieContentValues(jsonData);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, "IOException | JSONException: Exception caught: ", e);
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
        final String SORT_BY = "sort_by";
        final String API_KEY = "api_key";
        final String PAGE = "page";
        Uri builtUri = Uri.parse(mContext.getString(R.string.MDB_POPULAR_MOVIE_URL)).buildUpon()
                .appendQueryParameter(SORT_BY, pSortOrder)
                .appendQueryParameter(API_KEY, mContext.getString(R.string.PERSONAL_API_KEY))
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
    private void insertMovieContentValues(String mdbJsonStr)
            throws JSONException {
        int result = 0;

        JSONObject mdbMovieJson = new JSONObject(mdbJsonStr);
        JSONArray moviesArray = mdbMovieJson.getJSONArray(mContext.getString(R.string.MDB_REQ_RESULTS));

        List<ContentValues> moviesList = new ArrayList<>();

        Log.v(LOG_TAG, "===[number of movies records]: " + moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject mAttributes = moviesArray.getJSONObject(i);
            ContentValues mValues = new ContentValues();

            mValues.put(MovieEntry.C_MOVIE_ID, mAttributes.getString(mContext.getString(R.string.MDB_MOVIE_ID)));
            mValues.put(MovieEntry.C_ORIGINAL_TITLE, mAttributes.getString(mContext.getString(R.string.MDB_ORIGINAL_TITLE)));
            mValues.put(MovieEntry.C_OVERVIEW, mAttributes.getString(mContext.getString(R.string.MDB_OVERVIEW)));
            mValues.put(MovieEntry.C_BACKDROP_PATH, mAttributes.getString(mContext.getString(R.string.MDB_BACKDROP_PATH)));
            mValues.put(MovieEntry.C_POSTER_PATH, mAttributes.getString(mContext.getString(R.string.MDB_POSTER_IMAGE)));
            mValues.put(MovieEntry.C_RELEASE_DATE, mAttributes.getString(mContext.getString(R.string.MDB_RELEASE_DATE)));
            mValues.put(MovieEntry.C_VOTE_AVERAGE, mAttributes.getString(mContext.getString(R.string.MDB_VOTE_AVERAGE)));
            mValues.put(MovieEntry.C_VOTE_COUNT, mAttributes.getString(mContext.getString(R.string.MDB_VOTE_COUNT)));
            moviesList.add(mValues);
        }
        // Call bulkInsert to add the Movie Entries to the database from here
        if (moviesList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    MovieEntry.CONTENT_URI,
                    moviesList.toArray(new ContentValues[moviesList.size()]));
        }
        Log.v(LOG_TAG, "===insertMovieContents: " + result);
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context,null);
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
     * @param context The context used to access the account service
     * @param sortType The sorting to be used.
     */
    public static void syncImmediately(Context context,String sortType) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (null!= sortType) {
            bundle.putString(Constants.SORTING_KEY, sortType);
        }
        ContentResolver.requestSync(getSyncAccount(context,sortType),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter immediately
     * @param context The context used to access the account service
     * @param movieId The Movie to be sync
     */
    public static void syncMovieDetails(Context context, String movieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (null!= movieId) {
            bundle.putString(MOVIE_ID, movieId);
        }
        ContentResolver.requestSync(getSyncAccount(context, movieId),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @param sortType The sorting to be used.
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context,String sortType) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context, sortType);
        }
        return newAccount;
    }

    /**
     * Method to Sync data after account creation.
     *
     * @param newAccount instance of new account
     * @param context The context used to access the account service.
     * @param sortType sorting order
     */
    private static void onAccountCreated(Account newAccount, Context context,String sortType) {
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context,sortType);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context, null);
    }

    /**
     * Method to load more data based on scrolling.
     *
     * @param context The context used to load more movie.
     * @param offset page number to request/load the data.
     */
    public static void loadMoreData(Context context,int offset){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt("page", offset);
        ContentResolver.requestSync(getSyncAccount(context, null), context.getString(R.string.content_authority), bundle);
    }

}
