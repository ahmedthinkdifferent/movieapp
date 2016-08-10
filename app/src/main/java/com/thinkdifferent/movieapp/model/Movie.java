package com.thinkdifferent.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * class hold movie data.
 */

public class Movie implements Parcelable {

    public String title, image, overview, rate, releaseDate;
    public List<Trailer> trailers;
    public List<Review> reviews;

    /**
     * class constructor.
     * @param title movie title
     * @param image movie image path.
     * @param overview movie overview.
     * @param rate movie total rate
     * @param releaseDate movie release date.
     */
    public Movie(String title, String image, String overview, String rate, String releaseDate) {
        this.title = title;
        this.image = image;
        this.overview = overview;
        this.rate = rate;
        this.releaseDate = releaseDate;
    }


    protected Movie(Parcel in) {
        title = in.readString();
        image = in.readString();
        overview = in.readString();
        rate = in.readString();
        releaseDate = in.readString();
        trailers = in.createTypedArrayList(Trailer.CREATOR);
        reviews = in.createTypedArrayList(Review.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(overview);
        dest.writeString(rate);
        dest.writeString(releaseDate);
        dest.writeTypedList(trailers);
        dest.writeTypedList(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
