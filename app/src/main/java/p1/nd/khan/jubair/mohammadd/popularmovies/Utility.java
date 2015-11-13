package p1.nd.khan.jubair.mohammadd.popularmovies;

/**
 * Created by laptop on 10/24/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utility {

    private static final SimpleDateFormat DATE_FORMAT_MDB = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_FORMAT_MONTH_YEAR = new SimpleDateFormat("MMMM yyyy");

    //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                result = DATE_FORMAT_MONTH_YEAR.format(DATE_FORMAT_MDB.parse(releaseDate));
            } catch (ParseException e) {

                result = "unknown_release_date";
            }
        } else {
            result = "unknown_release_date";
        }
        return result;
    }

    public static String getPreferredSorting(Activity activity) {
        return activity.getPreferences(0).getString(activity.getString(R.string.pref_sort_order_key), activity.getString(R.string.SORT_ORDER_POPULARITY));
    }

    public static void updatePreferredSorting(Activity activity, String sortOrder) {
        SharedPreferences.Editor editor = activity.getPreferences(0).edit();
        editor.putString(activity.getString(R.string.pref_sort_order_key), sortOrder);
        editor.apply();
    }

}

