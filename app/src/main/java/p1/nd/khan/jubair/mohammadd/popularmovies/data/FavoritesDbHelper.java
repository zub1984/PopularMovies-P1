package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.Context;

/**
 * Created by laptop on 11/17/2015.
 */
public class FavoritesDbHelper extends MovieDbHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "offline_movies.db";

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}