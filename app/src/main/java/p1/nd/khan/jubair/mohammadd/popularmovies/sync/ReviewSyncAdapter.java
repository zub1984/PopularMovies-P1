package p1.nd.khan.jubair.mohammadd.popularmovies.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.Utility;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;

/**
 * Created by laptop on 11/16/2015.
 */
public class ReviewSyncAdapter {

    private static final String LOG_TAG = ReviewSyncAdapter.class.getSimpleName();
    private Context mContext;

    public ReviewSyncAdapter(Context context) {
        this.mContext = context;
    }

    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        String movieId = extras.getString(MovieSyncAdapter.MOVIE_ID);
        if (movieId != null && !movieId.trim().isEmpty() && Utility.isNetworkAvailable(mContext)) {
            getReviewData(movieId);
        }
    }

    private boolean getReviewData(String movieId) {
        Log.d(LOG_TAG, "getReviewData movieId[" + movieId + "]");

        boolean result = false;

        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            Uri builtUri = Uri.parse(MessageFormat.format(mContext.getString(R.string.MOVIE_REVIEW_URL), movieId))
                    .buildUpon()
                    .appendQueryParameter("api_key", mContext.getString(R.string.PERSONAL_API_KEY))
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            parseResultValues(movieId, reader);

            result = true;

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return result;
    }

    private void parseResultValues(String movieId, JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            List<ContentValues> reviewList = new ArrayList();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results") && reader.peek() != JsonToken.NULL) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reviewList.add(parseContentValues(movieId, reader));
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            int inserted = insertDataIntoContentProvider(reviewList);
            Log.d(LOG_TAG, "Found [" + inserted + "] reviews");
        }
    }

    private int insertDataIntoContentProvider(List<ContentValues> moviesList) {
        int result = 0;
        if (moviesList.size() > 0) {
            result = mContext.getContentResolver().bulkInsert(
                    ReviewsEntry.CONTENT_URI,
                    moviesList.toArray(new ContentValues[moviesList.size()]));
        }
        return result;
    }

    @NonNull
    private ContentValues parseContentValues(String movieId, JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewsEntry.C_MOVIE_ID, movieId);
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                reviewValues.put(ReviewsEntry.C_REVIEW_ID, reader.nextString());
            } else if (name.equals("author")) {
                reviewValues.put(ReviewsEntry.C_AUTHOR, reader.nextString());
            } else if (name.equals("content")) {
                reviewValues.put(ReviewsEntry.C_CONTENT, reader.nextString());
            } else if (name.equals("url")) {
                reviewValues.put(ReviewsEntry.C_URL, reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return reviewValues;
    }

}
