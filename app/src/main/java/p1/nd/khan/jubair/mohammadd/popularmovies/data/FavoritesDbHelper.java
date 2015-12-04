package p1.nd.khan.jubair.mohammadd.popularmovies.data;

import android.content.Context;

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

public class FavoritesDbHelper extends MovieDbHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "offline_movies.db";

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}