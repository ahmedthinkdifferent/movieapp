package com.thinkdifferent.movieapp.view.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.thinkdifferent.movieapp.R;
import com.thinkdifferent.movieapp.adapter.MoviesGridAdapter;
import com.thinkdifferent.movieapp.model.Movie;
import com.thinkdifferent.movieapp.model.MovieDatabase;
import com.thinkdifferent.movieapp.model.Review;
import com.thinkdifferent.movieapp.model.Trailer;
import com.thinkdifferent.movieapp.util.AppConstants;
import com.thinkdifferent.movieapp.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * fragment to show grid of movies .
 */
public class MovieGridFragment extends Fragment {

    // GUI References.
    private GridView moviesGrid;

    // Data Members.
    private List<Movie> movies;

    // variables.
    int selectedItemPosition = 0;


    /**
     * class empty constructor.
     */
    public MovieGridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_grid, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find views from view layout.
        moviesGrid = (GridView) view.findViewById(R.id.grid_movies);
        // check if bundle has old data or not.
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList("movies");
            selectedItemPosition = savedInstanceState.getInt("position");
        }

        if (movies == null) {
            // connect to server to get movies.
            String moviesLink = AppConstants.POPULAR_MOVIES_LINK;
            connectToServer(moviesLink);
        } else {
            // show movies in grid.
            showMoviesGrid(movies);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movies != null) {
            outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) movies);
            selectedItemPosition = ((MoviesGridAdapter) moviesGrid.getAdapter()).getSelectedItem();
            outState.putInt("position", selectedItemPosition);
        }
    }


    /**
     * get movies from internet.
     *
     * @param link movies link
     */
    public void connectToServer(String link) {

        if (Utility.isNetworkConnected(getActivity())) {
            // internet connected.
            getMoviesFromServer(link);
        } else {
            // internet not connected , show error message to user.
            showErrorView(link);
        }


    }


    /**
     * show list of user bookmarked movies.
     */
    public void showBookmarksMovies() {
        selectedItemPosition = 0;
        movies.clear();
        movies = new MovieDatabase(getContext()).getBookmarkedMovies();
        moviesGrid.setAdapter(new MoviesGridAdapter(getContext(), movies));
    }


    /**
     * show error layout to user.
     *
     * @param link movies link if user enable internet.
     */
    private void showErrorView(final String link) {
        final TextView errorTextView = (TextView) getView().findViewById(R.id.tv_error);
        errorTextView.setVisibility(View.VISIBLE);
        final Button tryAgainButton = (Button) getView().findViewById(R.id.btn_try);
        tryAgainButton.setVisibility(View.VISIBLE);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if internet connection is available.
                if (Utility.isNetworkConnected(getActivity())) {
                    errorTextView.setVisibility(View.INVISIBLE);
                    tryAgainButton.setVisibility(View.INVISIBLE);
                    getMoviesFromServer(link);
                }
            }
        });
    }


    /**
     * connect to server to get data.
     *
     * @param link movies link.
     */
    private void getMoviesFromServer(String link) {
        selectedItemPosition = 0;
        new MovieTask(getContext()).execute(link);

    }


    /**
     * set adapter to grid view and show movies images in grid.
     *
     * @param movies list of movies.
     */
    private void showMoviesGrid(List<Movie> movies) {
        moviesGrid.setNumColumns(getResources().getInteger(R.integer.row_data_count));
        moviesGrid.setAdapter(new MoviesGridAdapter(getActivity(), movies));
        if (!getResources().getBoolean(R.bool.is_device_small)) {
            moviesGrid.smoothScrollToPosition(selectedItemPosition);
            ((MoviesGridAdapter) moviesGrid.getAdapter()).performClick(selectedItemPosition);
        }

    }


    /**
     * async task responsible for get data from server.
     */
    private class MovieTask extends AsyncTask<String, Void, List<Movie>> {
        // GUI References.
        ProgressDialog progressDialog;

        // Members
        private Context context;

        // Data Variables.
        String moviesLink;


        /**
         * class constructor.
         *
         * @param context the caller context.
         */
        MovieTask(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog.
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            // get data from server.
            if (movies == null) {
                movies = new ArrayList<>();
            } else {
                movies.clear();
            }
            try {
                moviesLink = strings[0];
                URL moviesUrl = new URL(moviesLink);
                HttpURLConnection urlConnection = (HttpURLConnection) moviesUrl.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    // connect to server and get data.
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String data;
                    // loop inside data in response body and add it to response string.
                    while ((data = bufferedReader.readLine()) != null) {
                        response.append(data);
                    }
                    // close open connection and reader.
                    bufferedReader.close();
                    urlConnection.disconnect();

                    // parse data .
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray moviesArray = jsonObject.getJSONArray("results");
                    // loop inside movies array to get movies.

                    for (int i = 0; i < moviesArray.length(); i++) {
                        JSONObject movieObject = moviesArray.getJSONObject(i);
                        int movieID = movieObject.getInt("id");
                        Movie movie = new Movie(movieObject.getString("original_title"), movieObject.getString("poster_path"),
                                movieObject.getString("overview"), movieObject.getString("vote_average"), movieObject.getString("release_date"));

                        // connect to get movie trailers and reviews..
                        List<Trailer> trailers = getMovieTrailers(movieID);
                        List<Review> reviews = getMovieReviews(movieID);
                        movie.trailers = trailers;
                        movie.reviews = reviews;
                        // add movie item.
                        movies.add(movie);
                    }

                } else {
                    // cannot connect to server.
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {

            progressDialog.dismiss();
            // check if  have data or not from server.
            if (movies != null) {
                // have data, parse json data and fill it into list.
                MovieGridFragment.this.showMoviesGrid(movies);
            } else {
                // error in get data , show error message to user.
                MovieGridFragment.this.showErrorView(moviesLink);
            }


        }
    }


    /**
     * get movie trailers by movie id.
     *
     * @param movieID movie id number.
     * @return list of movie trailers names.
     */
    private List<Trailer> getMovieTrailers(int movieID) throws IOException, JSONException {
        List<Trailer> trailers = null;
        // initialize connection on server.
        URL url = new URL(String.format(AppConstants.TRAILER_LINK, movieID));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {
            // connected to link successfully.
            trailers = new ArrayList<>();
            String response = getStringResponse(urlConnection.getInputStream());
            urlConnection.disconnect();
            // parse response data.
            JSONObject jsonObject = new JSONObject(response);
            JSONArray trailersArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailerObject = trailersArray.getJSONObject(i);
                if (trailerObject.getString("type").equalsIgnoreCase("Trailer")) {
                    // add trailer info.
                    trailers.add(new Trailer(trailerObject.getString("name"), trailerObject.getString("key")));

                }

            }
        } else {
            urlConnection.disconnect();
        }
        return trailers;

    }


    /**
     * get list of movie reviews
     *
     * @param movieID movie id number.
     * @return list of movie reviews.
     */
    private List<Review> getMovieReviews(int movieID) throws IOException, JSONException {
        List<Review> reviews = null;
        // initialize connection on server.
        URL url = new URL(String.format(AppConstants.REVIEWS_LINK, movieID));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {
            // get data from response body.
            reviews = new ArrayList<>();
            String response = getStringResponse(urlConnection.getInputStream());
            urlConnection.disconnect();
            // parse data from response String body.
            JSONObject jsonObject = new JSONObject(response);
            JSONArray reviewsArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject reviewObject = reviewsArray.getJSONObject(i);
                reviews.add(new Review(reviewObject.getString("author"), reviewObject.getString("content")));
            }

        } else {
            urlConnection.disconnect();
        }


        return reviews;
    }


    /**
     * get string response from input stream url.
     *
     * @param inputStream input stream
     * @return string response from stream.
     */
    private String getStringResponse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String data;
        while ((data = bufferedReader.readLine()) != null) {
            stringBuilder.append(data);
        }

        bufferedReader.close();
        return stringBuilder.toString();
    }



}
