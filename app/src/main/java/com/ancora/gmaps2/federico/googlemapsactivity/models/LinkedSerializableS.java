package com.ancora.gmaps2.federico.googlemapsactivity.models;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Federico on 18/01/2016.
 */
@SuppressWarnings("serial")
public class LinkedSerializableS implements Serializable {
    LinkedList<String> s;
    public LinkedSerializableS(LinkedList<String> l) {
        this.s=l;
    }

    public LinkedList<String> getS() {
        return s;
    }
}
