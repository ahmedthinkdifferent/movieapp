package com.thinkdifferent.movieapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * class for movie database operations.
 */

public class MovieDatabase extends SQLiteOpenHelper {
    // constants.
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;


    // database connection.
    private SQLiteDatabase database;

    /**
     * class constructor.
     * used for create database if not exists.
     *
     * @param context the caller context.
     */
    public MovieDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create tables.
        final String MOVIES_TABLE_QUERY = "create table `movies` (`title` text, `image` text,`overview` text ,`rate`,`release_date`)";
        final String REVIEWS_TABLE_QUERY = "create table `reviews` (`movie_title` text , `author` text , `content` text)";
        final String TRAILERS_TABLE_QUERY = "create table `trailers` (`movie_title` text , `name` text,`key` text)";
        // execute sql queries.
        sqLiteDatabase.execSQL(MOVIES_TABLE_QUERY);
        sqLiteDatabase.execSQL(REVIEWS_TABLE_QUERY);
        sqLiteDatabase.execSQL(TRAILERS_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // update database if database version changed.
    }


    /**
     * add movie in user favourites.
     *
     * @param movie movie that wil be saved in database.
     * @return true if movie is bookmarked else return false.
     */
    public boolean bookmarkMovie(Movie movie) {
        openDB();
        ContentValues rowValues = new ContentValues();
        rowValues.put("title", movie.title);
        rowValues.put("image", movie.image);
        rowValues.put("overview", movie.overview);
        rowValues.put("rate", movie.rate);
        rowValues.put("release_date", movie.releaseDate);
        database.insert("movies", null, rowValues);
        if (movie.reviews != null) {
            // add reviews in reviews table.
            addReviews(movie.title, movie.reviews);
        }
        if (movie.trailers != null) {
            // add movie trailers in trailers table.
            addTrailers(movie.title, movie.trailers);
        }
        closeDB();
        return true;

    }


    /**
     * check if movie is bookmarked.
     *
     * @param movieTitle movie title.
     * @return true if movie is bookmarked otherwise false.
     */
    public boolean isMovieBookmarked(String movieTitle) {
        openDB();
        boolean isBookmarked = false;
        Cursor cursor = database.rawQuery("select`title` from `movies` where `title`='" + movieTitle.replaceAll("'", "_") + "'", null);
        if (cursor.getCount() > 0) {
            isBookmarked = true;
        }
        cursor.close();
        closeDB();
        return isBookmarked;
    }


    /**
     * get bookmarked movies.
     *
     * @return list of bookmarks movies.
     */
    public List<Movie> getBookmarkedMovies() {
        openDB();
        Cursor cursor = database.rawQuery("select * from `movies` ", null);
        List<Movie> movies = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Movie movie = new Movie(cursor.getString(0).replaceAll("_", "'"), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            movie.reviews = getMovieReviews(cursor.getString(0));
            movie.trailers = getMovieTrailers(cursor.getString(0));
            movies.add(movie);
            cursor.moveToNext();
        }

        cursor.close();
        closeDB();
        return movies;

    }


    /**
     * add movie reviews.
     *
     * @param reviews movie list reviews.
     */
    private void addReviews(String movieTitle, List<Review> reviews) {
        ContentValues rowValues = new ContentValues();
        for (Review review : reviews) {
            rowValues.put("movie_title", movieTitle);
            rowValues.put("author", review.author);
            rowValues.put("content", review.content);
            database.insert("reviews", null, rowValues);
            rowValues.clear();
        }
    }


    /**
     * add movie trailers
     *
     * @param trailers movie list trailers.
     */
    private void addTrailers(String movieTitle, List<Trailer> trailers) {
        ContentValues rowValues = new ContentValues();
        for (Trailer trailer : trailers) {
            rowValues.put("movie_title", movieTitle);
            rowValues.put("name", trailer.name);
            rowValues.put("key", trailer.key);
            database.insert("trailers", null, rowValues);
            rowValues.clear();
        }
    }


    /**
     * get movie reviews.
     *
     * @param movieTitle movie title to get movie reviews based on it.
     * @return list of reviews or null if not found.
     */
    private List<Review> getMovieReviews(String movieTitle) {
        openDB();
        Cursor cursor = database.rawQuery("select `author`,`content` from `reviews` where `movie_title`='" + movieTitle + "'", null);
        List<Review> reviews = null;
        if (cursor.getCount() > 0) {
            reviews = new ArrayList<>(cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                reviews.add(new Review(cursor.getString(0), cursor.getString(1)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        closeDB();
        return reviews;
    }


    /**
     * get list of movie trailers.
     *
     * @param movieTitle movie title to get movie trailers based on it.
     * @return list of movie trailers or null if not found.
     */
    private List<Trailer> getMovieTrailers(String movieTitle) {
        openDB();
        Cursor cursor = database.rawQuery("select `name`,`key` from `trailers` where `movie_title`='" + movieTitle + "'", null);
        List<Trailer> trailers = null;
        if (cursor.getCount() > 0) {
            trailers = new ArrayList<>(cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trailers.add(new Trailer(cursor.getString(0), cursor.getString(1)));
                cursor.moveToNext();
            }

        }
        cursor.close();
        closeDB();
        return trailers;
    }


    /**
     * check if user has bookmarks movies or not.
     *
     * @return true if has bookmarks in database else return false.
     */
    public boolean hasBookmarks() {
        boolean hasBookmarks = false;
        openDB();
        Cursor cursor = database.rawQuery("select `title` from `movies` limit 1", null);
        if (cursor.getCount() > 0) {
            hasBookmarks = true;
        }
        cursor.close();
        closeDB();
        return hasBookmarks;
    }

    /**
     * open database connection.
     */
    private void openDB() {
        database = getWritableDatabase();
    }

    /**
     * close database connection.
     */
    private void closeDB() {
        if (database != null) {
            database.close();
        }
    }
}
