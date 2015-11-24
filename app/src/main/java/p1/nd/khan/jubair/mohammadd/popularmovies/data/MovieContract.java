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
    public static final String PATH_MOVIE_DETAILS = "details";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";
    public static final String MOVIE_DETAIL_TABLE = "table_type";

    public static final String FAVORITES_CONTENT_AUTHORITY = "p1.nd.khan.jubair.mohammadd.popularmovies.app.favorites";
    public static final Uri FAVORITES_BASE_CONTENT_URI = Uri.parse("content://" + FAVORITES_CONTENT_AUTHORITY);



    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;
        public static final String CONTENT_DETAILS_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;
        public static final Uri FAVORITES_CONTENT_URI = FAVORITES_BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

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


    public static final class TrailersEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final Uri FAVORITES_CONTENT_URI = FAVORITES_BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        // Table name
        public static final String TABLE_NAME = "trailers";

        public static final String C_MOVIE_ID = "id";
        public static final String C_TRAILER_ID = "trailer_id";
        public static final String C_ISO_639_1 = "iso_693_1";
        public static final String C_KEY = "key";
        public static final String C_NAME = "name";
        public static final String C_SITE = "site";
        public static final String C_SIZE = "size";
        public static final String C_TYPE = "type";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final Uri FAVORITES_CONTENT_URI = FAVORITES_BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        // Table name
        public static final String TABLE_NAME = "reviews";

        public static final String C_MOVIE_ID = "id";
        public static final String C_REVIEW_ID = "review_id";
        public static final String C_AUTHOR = "author";
        public static final String C_CONTENT = "content";
        public static final String C_URL = "url";

        public static Uri buildReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
