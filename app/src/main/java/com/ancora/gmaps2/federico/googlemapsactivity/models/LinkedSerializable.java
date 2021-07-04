package com.ancora.gmaps2.federico.googlemapsactivity.models;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Federico on 18/01/2016.
 */
@SuppressWarnings("serial")
public class LinkedSerializable implements Serializable {
    LinkedList<PlacesModel> l;
    public LinkedSerializable(LinkedList<PlacesModel> l) {
        this.l=l;
    }

    public LinkedList<PlacesModel> getL() {
        return l;
    }

    public PlacesModel getPlaces(int i) {
        return l.get(i);
    }
}
