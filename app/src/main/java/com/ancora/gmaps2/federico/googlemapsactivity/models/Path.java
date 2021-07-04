package com.ancora.gmaps2.federico.googlemapsactivity.models;

import java.io.Serializable;

/**
 * Created by Federico on 30/03/2016.
 */
public class Path implements Serializable {
    private String jsonPath;

    public Path (String s) {
        this.jsonPath=s;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }
}
