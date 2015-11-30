package p1.nd.khan.jubair.mohammadd.popularmovies.utils;

import java.text.SimpleDateFormat;

import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;

/**
 * Created by laptop on 11/27/2015.
 */
public class Constants {

    public static final String MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG = "DETAIL_TAG";
    public static final String SELECTED_MOVIE_KEY = "selected_position";
    public static final String SELECTED_GRID_VIEW = "selected_grid_view";

    public static final int CURSOR_LOADER_ID = 0;
    public static final String DB_ORDER_POPULARITY = MovieContract.MovieEntry.C_VOTE_COUNT + " DESC," + MovieContract.MovieEntry._ID + " ASC";
    public static final String DB_ORDER_RATING = MovieContract.MovieEntry.C_VOTE_AVERAGE + " DESC," + MovieContract.MovieEntry._ID + " ASC";

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
