package p1.nd.khan.jubair.mohammadd.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import p1.nd.khan.jubair.mohammadd.popularmovies.R;
import p1.nd.khan.jubair.mohammadd.popularmovies.data.MovieContract.MovieEntry;
import p1.nd.khan.jubair.mohammadd.popularmovies.utils.Utility;

/**
 * Created by laptop on 11/11/2015.
 */
public class MovieAdapter extends CursorAdapter {

    private final static int MOVIE_LIST = 0;

    public static class ViewHolder {

        @Bind(R.id.poster_item_release_date_text_view)
        TextView posterItemReleaseDate;
        @Bind(R.id.grid_item_rating_text_view)
        TextView gridItemRating;

        @Bind(R.id.poster_item_star_image_view)
        ImageView posterItemStar;

        @Bind(R.id.grid_image_title_container)
        RelativeLayout griImageTitleContainer;

        @Bind(R.id.movie_poster_image)
        ImageView posterImage;

        @Bind(R.id.grid_image_container)
        FrameLayout gridImageContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags, int loaderID) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case MOVIE_LIST:
                int layoutId = R.layout.list_item_movie_poster;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                break;
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.posterItemReleaseDate.setText(Utility.formatReleaseDate(cursor.getString(cursor.getColumnIndex(MovieEntry.C_RELEASE_DATE))));
        viewHolder.gridItemRating.setText(Utility.formatRating(context, cursor.getString(cursor.getColumnIndex(MovieEntry.C_VOTE_AVERAGE))));
        viewHolder.posterItemStar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_rate_black_18dp));

        String posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.C_POSTER_PATH));
        final int colorPrimaryLight = ContextCompat.getColor(context, (R.color.colorPrimaryTransparent));
        Picasso.with(viewHolder.posterImage.getContext()).load(context.getString(R.string.POSTER_IMAGE_URL) + "/" + posterPath)
                .placeholder(R.drawable.placeholder).
                into(viewHolder.posterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap posterBitmap = ((BitmapDrawable) viewHolder.posterImage.getDrawable()).getBitmap();
                        Palette.from(posterBitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                viewHolder.gridImageContainer.setBackgroundColor(ColorUtils.setAlphaComponent(palette.getMutedColor(colorPrimaryLight), 190));
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        //do nothing
                    }
                });
    }
}