package com.thinkdifferent.movieapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thinkdifferent.movieapp.R;
import com.thinkdifferent.movieapp.model.Movie;
import com.thinkdifferent.movieapp.util.AppConstants;
import com.thinkdifferent.movieapp.view.activity.MovieGridActivity;

import java.util.List;

/**
 * movies adapter .
 */

public class MoviesGridAdapter extends BaseAdapter implements View.OnClickListener {

    // caller context.
    private Context context;

    // list Data Members.
    private List<Movie> movies;

    // variables.
    private int selectedItem;


    /**
     * class constructor.
     *
     * @param context caller context.
     * @param movies  list of movies.
     */
    public MoviesGridAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }


    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // check if view is empty , if empty initialize it.
        ImageView movieImageView;
        if (view == null) {
            // initialize view.
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_grid_item_layout, viewGroup, false);
            movieImageView = (ImageView) view.findViewById(R.id.iv_movie);
            view.setTag(R.id.iv_movie, movieImageView);
            view.setOnClickListener(this);
        } else {
            // get image view from view layout.
            movieImageView = (ImageView) view.getTag(R.id.iv_movie);
        }

        view.setTag(R.id.selected_item_position, position);

        // set data to views.
        //Loading image from below url into imageView
        String imageLink = AppConstants.IMAGE_PATH_LINK + movies.get(position).image;
        Glide.with(viewGroup.getContext())
                .load(imageLink)
                .into(movieImageView);
        return view;
    }


    /**
     * on movie item click in GridView.
     *
     * @param view the view that user clicked on.
     */
    @Override
    public void onClick(View view) {
        selectedItem = (int) view.getTag(R.id.selected_item_position);
        performClick(selectedItem);
    }


    /**
     * user click in item in GridView.
     *
     * @param position
     */
    public void performClick(int position) {
        MovieGridActivity movieGridActivity = (MovieGridActivity) context;
        Movie movie = movies.get(position);
        movieGridActivity.onMovieItemClick(movie);

    }


    /**
     * get selected item from user.
     *
     * @return selected item position.
     */
    public int getSelectedItem() {
        return selectedItem;
    }
}
