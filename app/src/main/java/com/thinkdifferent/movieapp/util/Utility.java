package com.thinkdifferent.movieapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * utility class for all methods that is general for using in app.
 */

public class Utility {


    /**
     * check if device is connected to internet.
     *
     * @param context the activity or app context (caller for this method).
     * @return true if device is connected to internet otherwise false.
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }



    /**
     * show toast message .
     * @param context the caller context.
     * @param messageID message String resource.
     */
    public static void showToastMessage(Context context , int messageID){
        Toast.makeText(context, messageID, Toast.LENGTH_LONG).show();
    }


}
