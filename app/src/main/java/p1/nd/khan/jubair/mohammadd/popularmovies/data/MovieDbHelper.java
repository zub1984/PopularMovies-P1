package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by laptop on 11/9/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.C_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.C_ORIGINAL_TITLE + " TEXT, " +
                MovieEntry.C_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.C_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.C_POSTER_PATH + " TEXT, " +
                MovieEntry.C_RELEASE_DATE + " TEXT, " +
                MovieEntry.C_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.C_VOTE_COUNT + " REAL NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
