package com.thinkdifferent.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * class hold movie reviews.
 */

public class Review implements Parcelable {

    String author, content;

    /**
     * class constructor.
     *
     * @param author  review author name.
     * @param content review content.
     */
    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }


    @Override
    public String toString() {
        return "<font color='blue'>author: </font>" + author + "<br/>" + "<font color='blue'> review:</font> " + content;
    }
}
