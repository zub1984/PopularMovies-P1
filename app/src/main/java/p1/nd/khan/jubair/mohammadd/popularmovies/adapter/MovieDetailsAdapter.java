package p1.nd.khan.jubair.mohammadd.popularmovies.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.ReviewsEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.TrailersEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Utility;

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
    private boolean mFavorite;
    private String firstTrailerKey;
    private int FIST_TRAILER = 0;


    @Bind(R.id.favorite_fab)
    FloatingActionButton mFab;

    @Bind(R.id.play_trailer)
    ImageView mTrailerIcon;

    public MovieDetailsAdapter(Context context, Cursor cursor, int flags, boolean favorite) {
        super(context, cursor, flags);
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
        @Bind(R.id.play_trailer)
        ImageView mPlayTrailer;

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
        @Bind(R.id.favorite_fab)
        FloatingActionButton mFavoriteFab;

        public MovieHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /* trailer holder class */
    public static class TrailerHolder {
        @Bind(R.id.Trailer_Container)
        RelativeLayout trailerContainer;
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
        switch (viewType) {
            case MOVIE:
                view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_detail, parent, false);
                MovieHolder movieHolder = new MovieHolder(view);
                view.setTag(movieHolder);
                mFab = movieHolder.mFavoriteFab;
                mTrailerIcon = movieHolder.mPlayTrailer;
                break;
            case TRAILER:
                view = LayoutInflater.from(context).inflate(R.layout.list_row_trailer, parent, false);
                TrailerHolder trailerHolder = new TrailerHolder(view);
                view.setTag(trailerHolder);
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
                DrawTrailerDetails(context, data, (TrailerHolder) view.getTag());
                break;
            case REVIEW:
                DrawReviewDetails(data, (ReviewHolder) view.getTag());
                break;
        }
    }

    //http://jakewharton.github.io/butterknife/

    /**
     * Method to draw trailer details.
     *
     * @param context     of the view.
     * @param data        cursor data to draw the view.
     * @param movieHolder attached with view
     */
    private void DrawMovieDetails(Context context, Cursor data, final MovieHolder movieHolder) {
        movieHolder.mBackdropPath.setScaleType(ImageView.ScaleType.FIT_XY);
        if (MOVIE == getCursorType(data)) {
            Picasso.with(context)
                    .load(context.getString(R.string.BACK_DROP_IMAGE_URL) + data.getString(data.getColumnIndex(MovieEntry.C_BACKDROP_PATH)))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(movieHolder.mBackdropPath);

            Picasso.with(context)
                    .load(context.getString(R.string.POSTER_IMAGE_URL) + data.getString(data.getColumnIndex(MovieEntry.C_POSTER_PATH)))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(movieHolder.mPosterUrl);

            /**********show the Trailer Icon if movie has trailer *************/
            playFirstTrailer(movieHolder);
            /**********show the Trailer Icon if movie has trailer *************/

            movieHolder.mRatingStar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_rate_black_18dp));
            final String movieId = data.getString(data.getColumnIndex(MovieEntry.C_MOVIE_ID));
            if (null != data.getString(data.getColumnIndex(MovieEntry.C_MOVIE_ID))) {
                displayFabIcon();
               // movieHolder.mFavoriteFab.setTag(R.id.FAVORITES_KEY, movieId);
                movieHolder.mFavoriteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processFavorites(movieHolder.mFavoriteFab, movieId);
                    }
                });
            }
            movieHolder.mTitle.setText(data.getString(data.getColumnIndex(MovieEntry.C_ORIGINAL_TITLE)));
            movieHolder.mOverview.setText(data.getString(data.getColumnIndex(MovieEntry.C_OVERVIEW)));
            movieHolder.mUserRating.setText(Utility.formatRating(context,data.getString(data.getColumnIndex(MovieEntry.C_VOTE_AVERAGE))));
            movieHolder.mReleaseDate.setText(Utility.formatReleaseDate(data.getString(data.getColumnIndex(MovieEntry.C_RELEASE_DATE))));
        }
    }


    /**
     * Method to play first trailer of the movie.
     *
     * @param movieHolder view holder of the movie.
     */
    private void playFirstTrailer(MovieHolder movieHolder) {
        if (null == firstTrailerKey) {
            movieHolder.mPlayTrailer.setVisibility(View.GONE);
        } else {
            movieHolder.mPlayTrailer.setVisibility(View.VISIBLE);
            movieHolder.mPlayTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playYouTube(firstTrailerKey);
                    Snackbar.make(v.getRootView(), "Play First Trailer!", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Method to draw trailer details.
     *
     * @param context       of the view.
     * @param data          cursor data to draw the view.
     * @param trailerHolder attached with view
     */
    private void DrawTrailerDetails(Context context, final Cursor data, TrailerHolder trailerHolder) {
        // DatabaseUtils.dumpCursor(data);
        if (TRAILER == getCursorType(data)) {
            final String tKey = data.getString(data.getColumnIndex(TrailersEntry.C_KEY));
            final String tName = data.getString(data.getColumnIndex(TrailersEntry.C_NAME));
            String type = data.getString(data.getColumnIndex(TrailersEntry.C_TYPE));
            String site = data.getString(data.getColumnIndex(TrailersEntry.C_SITE));
            trailerHolder.mName.setText(tName);
            trailerHolder.mType.setText(type);
            trailerHolder.mSize.setText(site);
            // set first trailer, so that it can be play directly from play trailer icon over backdrop poster.
            if (0 == FIST_TRAILER) {
                this.firstTrailerKey = tKey;
                FIST_TRAILER++;
            }
            if (null != tKey) {
                String url = MessageFormat.format(context.getString(R.string.youtube_thumbnail_url), tKey);
                Picasso.with(context).load(url).into(trailerHolder.mThumbnail);
                trailerHolder.mThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playYouTube(tKey);
                    }
                });

                trailerHolder.mShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareTrailer(tKey, tName);
                    }
                });
            }
        }
    }

    /**
     * Method to draw review details.
     *
     * @param data         cursor data to draw the view.
     * @param reviewHolder attached with view
     */
    private void DrawReviewDetails(Cursor data, ReviewHolder reviewHolder) {
        if (REVIEW == getCursorType(data)) {
            reviewHolder.mAuthor.setText(data.getString(data.getColumnIndex(ReviewsEntry.C_AUTHOR)));
            reviewHolder.mContent.setText(data.getString(data.getColumnIndex(ReviewsEntry.C_CONTENT)));
            //reviewHolder.mContent.setTag(R.id.REVIEW_URL, data.getString(data.getColumnIndex(ReviewsEntry.C_URL)));
        }
    }

    /**
     * Method to process favorites icon click events.
     *
     * @param view    MovieHolder View.
     * @param movieId to check for favorites.
     */
    private void processFavorites(View view, String movieId) {
        //Log.v(LOG_TAG,"movieId:"+movieId+",mFavorite:"+mFavorite);
        String resultMsg;
        if (mFavorite) {
            deleteFromFavorites(Integer.parseInt(movieId));
            resultMsg = mContext.getString(R.string.DEL_FROM_FAVORITES);
        } else {
            String movieTitle = addToFavorites(Integer.parseInt(movieId));
            resultMsg = MessageFormat.format(mContext.getString(R.string.ADD_TO_FAVORITES), movieTitle);
        }
        mFavorite = !mFavorite;
        displayFabIcon();
        Snackbar.make(view.getRootView(), resultMsg, Snackbar.LENGTH_SHORT).show();
    }


    private void displayFabIcon() {
        mFab.setImageResource(mFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_blank);
    }


    /**
     * Method to check if movie is favorite.
     *
     * @param movieId to check.
     * @return movie title after addition else null
     */
    private String addToFavorites(int movieId) {
        String title = null;
        String[] projection = new String[]{"*"};
        String selection = MovieEntry.C_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(movieId)};

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cMovies = null;
        Cursor cTrailers = null;
        Cursor cReviews = null;

        try {
            cMovies = contentResolver.query(MovieEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            if (null != cMovies && cMovies.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cMovies, contentValues);
                contentValues.remove("_id");
                title = contentValues.getAsString(MovieEntry.C_ORIGINAL_TITLE);
                contentResolver.insert(MovieEntry.FAVORITES_CONTENT_URI, contentValues);
            }
            cTrailers = contentResolver.query(TrailersEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            while (null != cTrailers && cTrailers.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cTrailers, contentValues);
                contentValues.remove("_id");
                contentResolver.insert(TrailersEntry.FAVORITES_CONTENT_URI, contentValues);
            }
            cReviews = contentResolver.query(ReviewsEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            while (null != cReviews && cReviews.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cReviews, contentValues);
                contentValues.remove("_id");
                contentResolver.insert(ReviewsEntry.FAVORITES_CONTENT_URI, contentValues);
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "SQLException!");
        } finally {
            if (null != cMovies) cMovies.close();
            if (null != cTrailers) cTrailers.close();
            if (null != cReviews) cReviews.close();
        }
        return title;
    }


    /**
     * Method to check if movie is favorite.
     *
     * @param movieId to check.
     * @return true if movie is deleted
     */
    private int deleteFromFavorites(int movieId) {
        String[] projection = new String[]{"*"};
        String selection = MovieEntry.C_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(movieId)};

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cMovies = null;
        Cursor cTrailers = null;
        Cursor cReviews = null;
        int result = 0;

        try {
            cMovies = contentResolver.query(MovieEntry.FAVORITES_CONTENT_URI, projection, selection, selectionArgs, null);
            if (null != cMovies && cMovies.moveToFirst()) {
                result = contentResolver.delete(MovieEntry.FAVORITES_CONTENT_URI, selection, selectionArgs);
            }
            cTrailers = contentResolver.query(TrailersEntry.FAVORITES_CONTENT_URI, projection, selection, selectionArgs, null);
            while (null != cTrailers && cTrailers.moveToNext()) {
                result = contentResolver.delete(TrailersEntry.FAVORITES_CONTENT_URI, selection, selectionArgs);
            }
            cReviews = contentResolver.query(ReviewsEntry.FAVORITES_CONTENT_URI, projection, selection, selectionArgs, null);
            while (null != cReviews && cReviews.moveToNext()) {
                result = contentResolver.delete(ReviewsEntry.FAVORITES_CONTENT_URI, selection, selectionArgs);
            }
        } finally {
            if (null != cMovies) cMovies.close();
            if (null != cTrailers) cTrailers.close();
            if (null != cReviews) cReviews.close();
        }
        return result;
    }

    private void playYouTube(String tKey) {
        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MessageFormat.format(mContext.getString(R.string.youtube_url), tKey)));
        youTubeIntent.putExtra("fullscreen_trailer", true);
        mContext.startActivity(youTubeIntent);
    }

    private void shareTrailer(String tKey, String tName) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        share.putExtra(Intent.EXTRA_SUBJECT, tName);
        share.putExtra(Intent.EXTRA_TEXT, MessageFormat.format(mContext.getString(R.string.youtube_url), tKey));
        mContext.startActivity(Intent.createChooser(share, "Share Movie Trailer"));
    }
}

