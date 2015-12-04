package p1.nd.khan.jubair.mohammadd.popularmovies.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Utility;

/*Copyright (C) 2015  Mohammad Jubair Khan (zub1984.kn@gmail.com) - Popular Movies Project of Udacity Nanodegree course.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class ReviewSyncAdapter {

    private static final String LOG_TAG = ReviewSyncAdapter.class.getSimpleName();
    private Context mContext;

    public ReviewSyncAdapter(Context context) {
        this.mContext = context;
    }

    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        String movieId = extras.getString(Constants.DETAIL_SYNC_MOVIE_ID);
        if (null!= movieId && Utility.isNetworkAvailable(mContext)) {
            getMovieReviews(movieId);
        }
    }

    private void getMovieReviews(final String movieId ){
        OkHttpClient client = new OkHttpClient();
        String builtUri = Uri.parse(MessageFormat.format(mContext.getString(R.string.MOVIE_REVIEW_URL), movieId))
                .buildUpon()
                .appendQueryParameter("api_key", mContext.getString(R.string.PERSONAL_API_KEY))
                .build().toString();
        Log.v(LOG_TAG, "URL:" + builtUri);

        Request request = new Request.Builder()
                .url(builtUri)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(LOG_TAG, "==onFailure[getMovieReviews]:", e);
                return;
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        insertReviewContentValues(movieId, jsonData);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, "IOException | JSONException: Exception caught: ", e);
                    return;
                }
            }
        });
    }


    private void insertReviewContentValues(String movieId, String reviewJsonStr)
            throws JSONException {
        int result = 0;
        JSONObject mdbMovieJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = mdbMovieJson.getJSONArray(Constants.MDB_REQ_RESULTS);
        List<ContentValues> reviewList = new ArrayList<>();
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject mAttributes = reviewArray.getJSONObject(i);
            ContentValues mValues = new ContentValues();
            mValues.put(ReviewsEntry.C_MOVIE_ID, movieId);
            mValues.put(ReviewsEntry.C_REVIEW_ID, mAttributes.getString("id"));
            mValues.put(ReviewsEntry.C_AUTHOR, mAttributes.getString("author"));
            mValues.put(ReviewsEntry.C_CONTENT, mAttributes.getString("content"));
            mValues.put(ReviewsEntry.C_URL, mAttributes.getString("url"));
            reviewList.add(mValues);
        }
        if (reviewList.size() > 0) {
            result = mContext.getContentResolver().bulkInsert(
                    ReviewsEntry.CONTENT_URI,
                    reviewList.toArray(new ContentValues[reviewList.size()]));
        }
        Log.v(LOG_TAG, "===[Review Inserted] : " + result);
    }
}
