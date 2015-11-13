package p1.nd.khan.jubair.mohammadd.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
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
import p1.nd.khan.jubair.mohammadd.popularmovies.Utility;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;


/**
 * Created by laptop on 11/13/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    private Context mContext;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext=context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        String pSortOrder= Utility.getPreferredSorting(getContext());
        int pPageNo=1;
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
    private List<ContentValues> insertMovieContentValues(String mdbJsonStr)
            throws JSONException {
        int result = 0;

        JSONObject mdbMovieJson = new JSONObject(mdbJsonStr);
        JSONArray moviesArray = mdbMovieJson.getJSONArray(mContext.getString(R.string.MDB_REQ_RESULTS));

        List<ContentValues> moviesList = new ArrayList<>();

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
        return moviesList;
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        }
        return newAccount;
    }

}
