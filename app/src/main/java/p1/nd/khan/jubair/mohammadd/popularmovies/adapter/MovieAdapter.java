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

import com.squareup.picasso.Picasso;

import p1.nd.khan.jubair.mohammadd.popularmovies.MainActivityFragment;
import p1.nd.khan.jubair.mohammadd.popularmovies.R;

/**
 * Created by laptop on 11/11/2015.
 */
public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.movie_poster_image);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags,int loaderID) {
        super(context, c, flags);
        Log.d(LOG_TAG, "MovieAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_movie_poster;
        //Log.d(LOG_TAG, "In new View");
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        //Log.d(LOG_TAG, "In bind View");
        //int imageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.C_POSTER_PATH);
        String posterPath = cursor.getString(MainActivityFragment.C_POSTER_PATH);
        //Log.i(LOG_TAG, "Image reference extracted: " + posterPath);
        if (posterPath != null) {
            Picasso.with(context).load(context.getString(R.string.POSTER_IMAGE_URL) + "/" + posterPath).into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_movie));
        }
    }
}