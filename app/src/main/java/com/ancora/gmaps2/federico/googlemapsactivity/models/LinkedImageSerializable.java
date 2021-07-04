package com.ancora.gmaps2.federico.googlemapsactivity.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Federico on 18/01/2016.
 */
@SuppressWarnings("serial")
public class LinkedImageSerializable implements Serializable {
    LinkedList<Bitmap> l;
    public LinkedImageSerializable(LinkedList<Bitmap> l) {
        this.l=l;
    }

    public LinkedList<Bitmap> getI() {
        return l;
    }

    public Bitmap getImage(int i) {
        return l.get(i);
    }
}