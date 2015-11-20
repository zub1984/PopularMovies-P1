package p1.nd.khan.jubair.mohammadd.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.Utility;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.TrailersEntry;

/*Copyright 2013 Jake Wharton

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class MovieDetailsAdapter extends CursorAdapter {

    private final String LOG_TAG = MovieDetailsAdapter.class.getSimpleName();

    private static final int MOVIE = 0;
    private static final int TRAILER = 1;
    private static final int REVIEW = 2;

    private View.OnClickListener mOnClickListener;
    private boolean mFavorite;

    public MovieDetailsAdapter(Context context, Cursor cursor, int flags, View.OnClickListener onClickListener, boolean favorite) {
        super(context, cursor, flags);
        this.mOnClickListener = onClickListener;
        this.mFavorite = favorite;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getCursorType(cursor);
    }

    private int getCursorType(Cursor cursor) {
        int result = -1;
        String type = cursor.getString(cursor.getColumnIndex(MovieContract.MOVIE_DETAIL_TABLE));
        switch (type) {
            case MovieEntry.TABLE_NAME:
                result = MOVIE;
                break;
            case TrailersEntry.TABLE_NAME:
                result = TRAILER;
                break;
            case ReviewsEntry.TABLE_NAME:
                result = REVIEW;
                break;
        }
        return result;
    }

    /* movie details holder class */
    public static class MovieHolder {
        @Bind(R.id.backdrop)
        ImageView mBackdropPath;
        @Bind(R.id.poster)
        ImageView mPosterUrl;
        @Bind(R.id.ratingStar)
        ImageView mRatingStar;

        @Bind(R.id.original_title)
        TextView mTitle;
        @Bind(R.id.overview)
        TextView mOverview;
        @Bind(R.id.release_date)
        TextView mReleaseDate;
        @Bind(R.id.rating)
        TextView mUserRating;

        public MovieHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

   /* trailer holder class */
    public static class TrailerHolder {
        @Bind(R.id.type)
        TextView mType;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.size)
        TextView mSize;
        @Bind(R.id.thumbnail)
        ImageView mThumbnail;
        @Bind(R.id.share)
        ImageView mShare;

        public TrailerHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /* trailer holder class */
    public static class ReviewHolder {
        @Bind(R.id.author)
        TextView mAuthor;
        @Bind(R.id.content)
        TextView mContent;
        public ReviewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        int viewType = getItemViewType(cursor.getPosition());
        Log.v(LOG_TAG,"viewType"+viewType);
        switch (viewType) {
            case MOVIE:
                view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_detail, parent, false);
                MovieHolder movieHolder = new MovieHolder(view);
                view.setTag(movieHolder);
                break;
            case TRAILER:
                view = LayoutInflater.from(context).inflate(R.layout.list_row_trailer, parent, false);
                if(null!=view){
                    TrailerHolder trailerHolder = new TrailerHolder(view);
                    view.setTag(trailerHolder);
                }

                break;
            case REVIEW:
                view = LayoutInflater.from(context).inflate(R.layout.list_row_review, parent, false);
                ReviewHolder reviewHolder = new ReviewHolder(view);
                view.setTag(reviewHolder);
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor data) {
        int viewType = getItemViewType(data.getPosition());
        switch (viewType) {
            case MOVIE:
                DrawMovieDetails(context, data, (MovieHolder) view.getTag());
                break;
            case TRAILER:
                DrawTrailerDetails(context, data,(TrailerHolder)view.getTag());
                break;
            case REVIEW:
                DrawReviewDetails(data,(ReviewHolder) view.getTag());
                break;
        }
    }

    //http://jakewharton.github.io/butterknife/
    /**
     * Method to draw trailer details.
     *
     * @param context of the view.
     * @param data cursor data to draw the view.
     * @param movieHolder attached with view
     */
    private void DrawMovieDetails(Context context,Cursor data,MovieHolder movieHolder) {
        movieHolder.mBackdropPath.setScaleType(ImageView.ScaleType.FIT_XY);
        if (null!= data.getString(data.getColumnIndex(MovieEntry.C_BACKDROP_PATH))) {
            Picasso.with(context)
                    .load(context.getString(R.string.BACK_DROP_IMAGE_URL) + data.getString(data.getColumnIndex(MovieEntry.C_BACKDROP_PATH)))
                    .into(movieHolder.mBackdropPath);
        } else {
            movieHolder.mBackdropPath.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_movie));
        }

        if (null!= data.getString(data.getColumnIndex(MovieEntry.C_POSTER_PATH))) {
            Picasso.with(context)
                    .load(context.getString(R.string.POSTER_IMAGE_URL) + data.getString(data.getColumnIndex(MovieEntry.C_POSTER_PATH)))
                    .into(movieHolder.mPosterUrl);
        } else {
            movieHolder.mPosterUrl.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_movie));
        }
        View ratingStar= movieHolder.mRatingStar;
        movieHolder.mRatingStar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_rate_black_18dp));

        if (null != ratingStar) {
            ratingStar.setOnClickListener(mOnClickListener);
            ratingStar.setTag(R.id.FAVORITES_KEY, data.getString(data.getColumnIndex(MovieEntry.C_MOVIE_ID)));
        }

        movieHolder.mTitle.setText(data.getString(data.getColumnIndex(MovieEntry.C_ORIGINAL_TITLE)));
        movieHolder.mOverview.setText(data.getString(data.getColumnIndex(MovieEntry.C_OVERVIEW)));
        movieHolder.mUserRating.setText(data.getString(data.getColumnIndex(MovieEntry.C_VOTE_AVERAGE))+ context.getString(R.string.rating_out_of_ten));
        movieHolder.mReleaseDate.setText(Utility.formatReleaseDate(data.getString(data.getColumnIndex(MovieEntry.C_RELEASE_DATE))));
    }

    /**
     * Method to draw trailer details.
     *
     * @param context of the view.
     * @param data cursor data to draw the view.
     * @param trailerHolder attached with view
     */
    private void DrawTrailerDetails(Context context,Cursor data, TrailerHolder trailerHolder){
        String tName=data.getString(data.getColumnIndex(TrailersEntry.C_NAME));
        trailerHolder.mName.setText(tName);
        trailerHolder.mType.setText(data.getString(data.getColumnIndex(TrailersEntry.C_TYPE)));
        trailerHolder.mSize.setText(data.getString(data.getColumnIndex(TrailersEntry.C_SITE)));
        String tKey=data.getString(data.getColumnIndex(TrailersEntry.C_KEY));
        if(null!= tKey)
        {
            String url= MessageFormat.format(context.getString(R.string.youtube_thumbnail_url),tKey);
            Picasso.with(context).load(url).into(trailerHolder.mThumbnail);
            trailerHolder.mThumbnail.setOnClickListener(mOnClickListener);
            trailerHolder.mThumbnail.setTag(R.id.TRAILER_KEY, tKey);
        }

        View shareImage = trailerHolder.mShare;
        shareImage.setOnClickListener(mOnClickListener);
        shareImage.setTag(R.id.SHARE_KEY, tKey);
        shareImage.setTag(R.id.SHARE_NAME, tName);
    }

    /**
     * Method to draw review details.
     *
     * @param data cursor data to draw the view.
     * @param reviewHolder attached with view
     */
    private void DrawReviewDetails(Cursor data,ReviewHolder reviewHolder){
        reviewHolder.mAuthor.setText(data.getString(data.getColumnIndex(ReviewsEntry.C_AUTHOR)));
        reviewHolder.mContent.setText(data.getString(data.getColumnIndex(ReviewsEntry.C_CONTENT)));
        reviewHolder.mContent.setTag(R.id.REVIEW_URL,data.getString(data.getColumnIndex(ReviewsEntry.C_URL)));
    }

}

