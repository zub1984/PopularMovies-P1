package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.GridView;

import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Constants;

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

public class MovieDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if (null == savedInstanceState ) {
            /* set the intent data received from MainActivity Class*/
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.MOVIE_ID_KEY, getIntent().getIntExtra(Constants.MOVIE_ID_KEY, GridView.INVALID_POSITION));
            arguments.putString(Constants.SORTING_KEY, getIntent().getStringExtra(Constants.SORTING_KEY));
            arguments.putString(Constants.POSTER_IMAGE_KEY,getIntent().getStringExtra(Constants.POSTER_IMAGE_KEY));

            MovieDetailActivityFragment fragment = MovieDetailActivityFragment.newInstance(arguments);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_movie_detail, fragment, Constants.MOVIE_DETAIL_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        }
    }
}
