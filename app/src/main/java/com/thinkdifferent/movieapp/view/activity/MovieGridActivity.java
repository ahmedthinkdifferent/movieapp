package com.thinkdifferent.movieapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.thinkdifferent.movieapp.R;
import com.thinkdifferent.movieapp.listener.OnMovieItemCLickListener;
import com.thinkdifferent.movieapp.model.Movie;
import com.thinkdifferent.movieapp.model.MovieDatabase;
import com.thinkdifferent.movieapp.util.AppConstants;
import com.thinkdifferent.movieapp.util.Utility;
import com.thinkdifferent.movieapp.view.fragment.MovieDetailFragment;
import com.thinkdifferent.movieapp.view.fragment.MovieGridFragment;


/**
 * activity to show list of movies images to user in a GridView.
 */
public class MovieGridActivity extends AppCompatActivity implements OnMovieItemCLickListener {

   // variables.
    int selectedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt("item");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("item", selectedItem);
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch (selectedItem) {
            case 0:
                menu.findItem(R.id.popular).setChecked(true);
                break;
            case 1:
                menu.findItem(R.id.top_rated).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.favourites).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MovieGridFragment movieGridFragment = (MovieGridFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies_grid);

        switch (item.getItemId()) {
            case R.id.popular:
                // show popular movies.
                movieGridFragment.connectToServer(AppConstants.POPULAR_MOVIES_LINK);
                item.setChecked(true);
                selectedItem = 0;
                break;
            case R.id.top_rated:
                // show top rated movies.
                movieGridFragment.connectToServer(AppConstants.TOP_RATED_MOVIES_LINK);
                item.setChecked(true);
                selectedItem = 1;
                break;
            default:
                // show list of favourites movies from database.
                if (new MovieDatabase(this).hasBookmarks()) {
                    // has bookmarks movies in database.
                    item.setChecked(true);
                    selectedItem = 2;
                    movieGridFragment.showBookmarksMovies();
                } else {
                    // not found bookmarks is database, show toast message to user.
                    Utility.showToastMessage(this, R.string.not_found_bookmarks);
                }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onMovieItemClick(Movie movie) {

        // check if activity has 2 fragments shown in same time or not.
        if (getResources().getBoolean(R.bool.is_device_small)) {
            // device show only one fragment.
            startActivity(new Intent(MovieGridActivity.this, MovieDetailActivity.class).putExtra("movie", movie));
        } else {
            // device show 2 fragments in activity.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, MovieDetailFragment.newInstance(movie)).commit();
        }


    }

}
