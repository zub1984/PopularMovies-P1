package p1.nd.khan.jubair.mohammadd.popularmovies.utils;

/**
 * Created by laptop on 10/24/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;

import p1.nd.khan.jubair.mohammadd.popularmovies.R;

public class Utility {

    //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //http://stackoverflow.com/questions/3841317/how-to-see-if-wifi-is-connected-in-android
    public static boolean isWifiAvailable(Activity activity) {
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static String formatReleaseDate(String releaseDate) {
        String result;
        if (releaseDate != null && !releaseDate.trim().isEmpty()) {
            try {
                result = Constants.DATE_FORMAT_MONTH_YEAR.format(Constants.DATE_FORMAT_MDB.parse(releaseDate));
            } catch (ParseException e) {
                result = "unknown_release_date";
            }
        } else {
            result = "unknown_release_date";
        }
        return result;
    }

    public static String getPreferredSorting(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        return prefs.getString(Constants.SORTING_KEY, context.getString(R.string.SORT_ORDER_POPULARITY));
    }

    public static void updatePreferredSorting(Context context, String sortOrder) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        settings.edit().putString(Constants.SORTING_KEY, sortOrder).apply();
    }

    /**
     * Helper Method to format ratings display.
     *
     * @param rating ,the rating of movie
     */
    public static String formatRating(Context context,String rating) {
        return rating + context.getString(R.string.rating_out_of_ten);
    }

}

