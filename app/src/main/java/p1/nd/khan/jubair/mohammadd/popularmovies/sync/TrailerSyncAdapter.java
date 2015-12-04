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
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.TrailersEntry;
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
public class TrailerSyncAdapter {

    private static final String LOG_TAG = TrailerSyncAdapter.class.getSimpleName();
    private Context mContext;

    public TrailerSyncAdapter(Context context) {
        this.mContext = context;
    }

    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        String movieId = extras.getString(Constants.DETAIL_SYNC_MOVIE_ID);
        if (movieId != null && !movieId.trim().isEmpty() && Utility.isNetworkAvailable(mContext)) {
            getMovieTrailers(movieId);
        }
    }


   private void getMovieTrailers(final String movieId ){
        OkHttpClient client = new OkHttpClient();
        String builtUri = Uri.parse(MessageFormat.format(mContext.getString(R.string.MOVIE_TRAILER_URL), movieId))
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
                Log.e(LOG_TAG, "==onFailure[getMovieTrailers]:", e);
                return;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        insertTrailerContentValues(movieId, jsonData);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, "IOException | JSONException: Exception caught: ", e);
                    return;
                }
            }
        });
    }


    private void insertTrailerContentValues(String movieId, String reviewJsonStr)
            throws JSONException {
        int result = 0;
        JSONObject mdbMovieJson = new JSONObject(reviewJsonStr);
        JSONArray trailerArray = mdbMovieJson.getJSONArray(Constants.MDB_REQ_RESULTS);
        List<ContentValues> trailerList = new ArrayList<>();
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject mAttributes = trailerArray.getJSONObject(i);
            ContentValues mValues = new ContentValues();
            mValues.put(TrailersEntry.C_MOVIE_ID, movieId);
            mValues.put(TrailersEntry.C_TRAILER_ID, mAttributes.getString("id"));
            mValues.put(TrailersEntry.C_ISO_639_1, mAttributes.getString("iso_639_1"));
            mValues.put(TrailersEntry.C_KEY, mAttributes.getString("key"));
            mValues.put(TrailersEntry.C_NAME, mAttributes.getString("name"));
            mValues.put(TrailersEntry.C_SITE, mAttributes.getString("site"));
            mValues.put(TrailersEntry.C_SIZE, mAttributes.getString("size"));
            mValues.put(TrailersEntry.C_TYPE, mAttributes.getString("type"));
            trailerList.add(mValues);
        }
        if (trailerList.size() > 0) {
            result = mContext.getContentResolver().bulkInsert(
                    TrailersEntry.CONTENT_URI,
                    trailerList.toArray(new ContentValues[trailerList.size()]));
        }
        Log.v(LOG_TAG, "===[Trailer Inserted] : " + result);
    }
}
