package p1.nd.khan.jubair.mohammadd.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p1.nd.khan.jubair.mohammadd.popularmovies.R;

/**
 * Created by laptop on 11/11/2015.
 */
public class MovieAdapter extends ArrayAdapter<String> {
    private Context mContext;

    public MovieAdapter(Context context,ArrayList<String> urls) {
        super(context, 0, urls);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = R.layout.list_item_movie_poster;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_poster_image);
        imageView.setAdjustViewBounds(true); //Adjust its bound to max while Preserve the aspect ratio of Image

        // Download Image from TMDB
        //// https://www.bignerdranch.com/blog/solving-the-android-image-loading-problem-volley-vs-picasso/
        Picasso.with(mContext)
                .load(mContext.getString(R.string.POSTER_IMAGE_URL) + getItem(position))
                .into(imageView);

        return convertView;
    }
}