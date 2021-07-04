package com.ancora.gmaps2.federico.googlemapsactivity.Database;

import android.provider.BaseColumns;

/**
 * Created by Federico on 22/04/2016.
 */
public interface Account extends BaseColumns {
    String TABLE_NAME = "registrazione";
    String COD="cod";
    String EMAIL="email";
    String USERNAME = "user";
    String PASS = "pass";
    String PHOTO_PATH="path";
    String LOG="logg";

}
