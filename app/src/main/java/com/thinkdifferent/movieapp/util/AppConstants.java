package com.thinkdifferent.movieapp.util;

/**
 * interface for application constants.
 */

public interface AppConstants {
    String API_KEY = "";
    String POPULAR_MOVIES_LINK = "http://api.themoviedb.org/3/movie/popular?api_key="+API_KEY;
    String TOP_RATED_MOVIES_LINK = "http://api.themoviedb.org/3/movie/top_rated?api_key="+API_KEY;
    String TRAILER_LINK = "http://api.themoviedb.org/3/movie/%s/videos?api_key=" + API_KEY;
    String REVIEWS_LINK = "http://api.themoviedb.org/3/movie/%s/reviews?api_key=" + API_KEY;
    String IMAGE_PATH_LINK = "http://image.tmdb.org/t/p/w185/";

}
