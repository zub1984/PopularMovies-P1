package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.UriMatcher;

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
