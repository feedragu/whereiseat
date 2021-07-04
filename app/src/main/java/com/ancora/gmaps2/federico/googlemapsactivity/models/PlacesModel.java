package com.ancora.gmaps2.federico.googlemapsactivity.models;

import java.io.Serializable;
import java.util.LinkedList;

public class PlacesModel implements Serializable {
    private String lat;
    private String lng;
    private String id;
    private String name;
    private String photoRef;
    private String address;
    private boolean open;
    private LinkedList<String> list;

    public LinkedList<String> getList() {
        return list;
    }

    public void setList(LinkedList<String> list) {
        this.list = list;
    }

    private boolean photoYes=false;

    public String getWebsite() {
        return website;
    }

    private String website;

    public boolean isPhotoYes() {
        return photoYes;
    }

    public void setPhotoYes(boolean photoYes) {
        this.photoYes = photoYes;
    }

    private String placeId;
    private float rating;
    private String vicinity;
    private String width;
    private String phone_number;
    private LinkedList<String> opening;
    private String jsonPath;
    private boolean modified=false;

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public LinkedList<String> getOpening() {
        return opening;
    }

    public void setOpening(LinkedList<String> opening) {
        this.opening = opening;
    }

    public void addOpening(String opening) {
        this.opening.add(opening);
    }

    private String url;
    private int distance;
    private String JsonD;



    public PlacesModel(String string, String string1, String string2, String string3, float aFloat, String lat, String aLong) {
        this.id = string;
        this.address = string1;
        this.photoRef = string2;
        this.name = string3;
        this.rating = aFloat;
        this.lat=lat;
        this.lng=aLong;
        this.opening=new LinkedList<>();

    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setUrl(String setUrl) {
        this.url = setUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsonD() {
        return JsonD;
    }

    public void setJsonD(String jsonD) {
        JsonD = jsonD;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public PlacesModel() {
            this.opening=new LinkedList<>();
        }

    @Override
    public PlacesModel clone() throws CloneNotSupportedException {
        return (PlacesModel) super.clone();
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
