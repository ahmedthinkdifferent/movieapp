package com.thinkdifferent.movieapp.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.thinkdifferent.movieapp.R;
import com.thinkdifferent.movieapp.model.Movie;
import com.thinkdifferent.movieapp.view.fragment.MovieDetailFragment;


/**
 * activity to show movie details.
 */
public class MovieDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        // show action bar back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Movie movie = getIntent().getExtras().getParcelable("movie");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, MovieDetailFragment.newInstance(movie)).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
