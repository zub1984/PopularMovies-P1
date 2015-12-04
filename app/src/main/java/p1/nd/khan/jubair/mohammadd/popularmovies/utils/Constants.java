package p1.nd.khan.jubair.mohammadd.popularmovies.utils;

import java.text.SimpleDateFormat;

import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;

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
public class Constants {

    public static final String MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG = "DETAIL_TAG";
    public static final String SELECTED_MOVIE_KEY = "selected_position";
    public static final String SELECTED_GRID_VIEW = "selected_grid_view";

    public static final int CURSOR_LOADER_ID = 0;
    public static final String DB_ORDER_POPULARITY = MovieEntry.C_VOTE_COUNT + " DESC," + MovieEntry._ID + " ASC";
    public static final String DB_ORDER_RATING = MovieEntry.C_VOTE_AVERAGE + " DESC," + MovieEntry._ID + " ASC";


    /*-- themoviedb.org : Parameter details requires for PopularMovies App --*/
    public static final String MDB_REQ_RESULTS = "results";
    public static final String MDB_BACKDROP_PATH = "backdrop_path";
    public static final String MDB_MOVIE_ID = "id";
    public static final String MDB_ORIGINAL_TITLE = "original_title";
    public static final String MDB_OVERVIEW = "overview";
    public static final String MDB_POSTER_IMAGE = "poster_path";
    public static final String MDB_RELEASE_DATE = "release_date";
    public static final String MDB_VOTE_AVERAGE = "vote_average";
    public static final String MDB_VOTE_COUNT = "vote_count";
    public static final String MDB_INCLUDE_ADULT = "include_adult";

    public static final String SORT_BY = "sort_by";
    public static final String API_KEY = "api_key";
    public static final String PAGE = "page";

    public static final String LIST_VIEW_STATE = "list_view_state";
    public static final String FAVORITES = "favorites";
    public static final int CURSOR_DETAIL_LOADER_ID = 0;

    public static final String SORTING_KEY = "sort_mode";
    public static final String MOVIE_ID_KEY = "movie_id";
    public static final String POSTER_IMAGE_KEY = "p_image";
    public static final String POSTER_IMAGE_VIEW_KEY = "posterImage";


    public static final SimpleDateFormat DATE_FORMAT_MDB = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_MONTH_YEAR = new SimpleDateFormat("MMMM yyyy");
    public static final String PREFS_NAME = "moviePref";


    // Interval at which to sync with server, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public static final String DETAIL_SYNC_MOVIE_ID = "my_movie_id";


    public static final String NO_MOVIES = "No Movies!";
    public static final String ON_MOVIES_LOAD_SUCCESS = "Movies Loaded Successfully!";

}
