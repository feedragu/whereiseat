package com.ancora.gmaps2.federico.googlemapsactivity.models;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Federico on 24/05/2016.
 */
public class PhotoReferences implements Serializable {
    private LinkedList<String> list;

    public LinkedList<String> getList() {
        return list;
    }

    public void setList(LinkedList<String> list) {
        this.list = list;
    }

    public PhotoReferences(LinkedList<String> l) {
        this.list=l;

    }
}
