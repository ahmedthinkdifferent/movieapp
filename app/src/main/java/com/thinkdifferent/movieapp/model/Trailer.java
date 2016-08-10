package com.thinkdifferent.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * class hold movie trailer info.
 */

public class Trailer implements Parcelable {

   public String name, key;

    /**
     * class constructor.
     * @param name trailer name.
     * @param key trailer youtube key for movie trailer.
     */
    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    protected Trailer(Parcel in) {
        name = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(key);
    }
}
