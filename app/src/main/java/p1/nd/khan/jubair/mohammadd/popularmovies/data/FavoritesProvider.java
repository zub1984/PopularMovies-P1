package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.UriMatcher;

/**
 * Created by laptop on 11/17/2015.
 */
public class FavoritesProvider extends MovieProvider {

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDbHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return true;
    }

    @Override
    protected UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.FAVORITES_CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.MOVIES_PATH, MOVIE);
        matcher.addURI(authority, MovieContract.MOVIES_PATH + "/#", MOVIES_ITEM);
        matcher.addURI(authority, MovieContract.MOVIES_PATH + "/" + MovieContract.PATH_MOVIE_DETAILS + "/#", MOVIE_DETAILS);

        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/#", TRAILER_ITEM);

        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", REVIEW_ITEM);

        return matcher;
    }
}
