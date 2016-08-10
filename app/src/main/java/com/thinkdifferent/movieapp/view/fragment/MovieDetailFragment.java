package com.thinkdifferent.movieapp.view.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thinkdifferent.movieapp.R;
import com.thinkdifferent.movieapp.model.Movie;
import com.thinkdifferent.movieapp.model.MovieDatabase;
import com.thinkdifferent.movieapp.model.Review;
import com.thinkdifferent.movieapp.model.Trailer;
import com.thinkdifferent.movieapp.util.AppConstants;
import com.thinkdifferent.movieapp.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {


    // GUI References.
    private TextView movieTitleTextView, movieDateTextView, movieRateTextView, movieOverviewTextView, movieReviewsTextView;
    private ImageView movieImageView, favouriteImageView;

    // members.
    private Movie movie;

    // variables.
    private boolean isMovieBookmarked;

    /**
     * get new instance from this fragment.
     *
     * @param movie movie object hold movie data.
     * @return new instance from fragment.
     */
    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setArguments(bundle);
        return movieDetailFragment;
    }


    /**
     * class empty constructor.
     */
    public MovieDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = getArguments().getParcelable("movie");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find views from layout view.
        movieTitleTextView = (TextView) view.findViewById(R.id.tv_movie_title);
        movieDateTextView = (TextView) view.findViewById(R.id.tv_movie_date);
        movieRateTextView = (TextView) view.findViewById(R.id.tv_movie_rate);
        movieOverviewTextView = (TextView) view.findViewById(R.id.tv_movie_overview);
        movieReviewsTextView = (TextView) view.findViewById(R.id.tv_movie_reviews);
        movieImageView = (ImageView) view.findViewById(R.id.iv_movie);
        favouriteImageView = (ImageView) view.findViewById(R.id.iv_favourite);
        if (new MovieDatabase(getContext()).isMovieBookmarked(movie.title)) {
            favouriteImageView.setImageResource(R.drawable.bookmarked);
            isMovieBookmarked = true;
        }
        favouriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMovieBookmarked) {
                    // movie is bookmarked , show toast message to user.
                    Utility.showToastMessage(getContext(), R.string.movie_already_bookmarked);
                } else {
                    // bookmark movie
                    isMovieBookmarked = new MovieDatabase(getContext()).bookmarkMovie(movie);
                    if (isMovieBookmarked) {
                        Utility.showToastMessage(getContext(), R.string.movie_bookmarked);
                        favouriteImageView.setImageResource(R.drawable.bookmarked);
                    }
                }
            }
        });

        // show data in views.
        setViewsData(movie);
    }


    /**
     * set data to views with movie info.
     *
     * @param movie the movie object hold movies data.
     */
    public void setViewsData(final Movie movie) {
        getView().findViewById(R.id.layout_movie_details).setVisibility(View.VISIBLE);
        movieTitleTextView.setText(movie.title);
        movieDateTextView.setText(movie.releaseDate);
        movieRateTextView.setText(movie.rate);
        movieOverviewTextView.setText(movie.overview);
        Glide.with(getContext()).load(AppConstants.IMAGE_PATH_LINK + movie.image).into(movieImageView);
        // show movie reviews if found.
        if (movie.reviews != null) {
            StringBuilder reviews = new StringBuilder("<strong><font color='blue'> Reviews </font></strong><br/>");
            for (Review review : movie.reviews) {
                reviews.append(review.toString());
            }
            movieReviewsTextView.setText(Html.fromHtml(reviews.toString()));

        }
        // show list of movie trailers if found.
        if (movie.trailers != null) {
            String[] trailersTitles = new String[movie.trailers.size()];
            int index = 0;
            for (Trailer trailer : movie.trailers) {
                trailersTitles[index] = trailer.name;
                index++;
            }

            // set trailers listview adapter.
            ListView trailersListView = (ListView) getView().findViewById(R.id.list_trailers);
            trailersListView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.listview_movie_trailers, R.id.tv_movie_trailer_title, trailersTitles));
            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    showMovieTrailer(movie.trailers.get(position).key);
                }
            });
        }

    }


    /**
     * show movie trailer in youtube or other app.
     *
     * @param key movie key in youtube .
     */
    private void showMovieTrailer(String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
        try {
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Utility.showToastMessage(getContext(), R.string.not_found_youtube);
        }

    }

}
