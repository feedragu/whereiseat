package com.ancora.gmaps2.federico.googlemapsactivity.Database;

import android.provider.BaseColumns;

/**
 * Created by Federico on 04/03/2016.
 */
public interface PreferitiI extends BaseColumns{
    String TABLE_NAME = "preferiti";
    String ID="id";
    String PLACE_ID = "placeId";
    String URL = "photoUrl";
    String REST="nomeRest";
    String RATING="rating";
    String ADDRESS="address";
    String LAT="lat";
    String LONG="long";
    String[] COLUMNS = new String[] { PLACE_ID, URL, REST, RATING };
}
