package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by laptop on 11/9/2015.
 * Defines table and column names for movie database
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "p1.nd.khan.jubair.mohammadd.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIES_PATH = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;

        // Table name
        public static final String TABLE_NAME = "movies";

        public static final String C_MOVIE_ID = "id";
        public static final String C_ORIGINAL_TITLE = "original_title";
        public static final String C_OVERVIEW = "overview";
        public static final String C_BACKDROP_PATH = "backdrop_path";
        public static final String C_POSTER_PATH = "poster_path";
        public static final String C_RELEASE_DATE = "release_date";
        public static final String C_VOTE_AVERAGE = "vote_average";
        public static final String C_VOTE_COUNT = "vote_count";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
