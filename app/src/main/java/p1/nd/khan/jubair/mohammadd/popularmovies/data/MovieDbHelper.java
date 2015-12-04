package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.TrailersEntry;

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

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MovieDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version, null);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /* Table for Movie list */
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

        /* Table for Movie trailers */
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY," +
                TrailersEntry.C_TRAILER_ID + " TEXT UNIQUE NOT NULL, " +
                TrailersEntry.C_MOVIE_ID + " INTEGER NOT NULL, " +
                TrailersEntry.C_ISO_639_1 + " TEXT, " +
                TrailersEntry.C_KEY + " TEXT NOT NULL, " +
                TrailersEntry.C_NAME + " TEXT, " +
                TrailersEntry.C_SITE + " TEXT, " +
                TrailersEntry.C_SIZE + " INTEGER, " +
                TrailersEntry.C_TYPE + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);

         /* Table for Movie reviews */
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY," +
                ReviewsEntry.C_REVIEW_ID + " TEXT UNIQUE NOT NULL, " +
                ReviewsEntry.C_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewsEntry.C_AUTHOR + " TEXT, " +
                ReviewsEntry.C_CONTENT + " TEXT, " +
                ReviewsEntry.C_URL + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
