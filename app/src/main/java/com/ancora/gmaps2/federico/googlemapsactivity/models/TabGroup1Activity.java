package com.ancora.gmaps2.federico.googlemapsactivity.models;

import android.content.Intent;
import android.os.Bundle;

import com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources.MainActivityNavigation;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

public class TabGroup1Activity extends TabGroupActivity{

    private final int REQUEST_PLACE_PICKER = 1;
    private Place pl;
    public LinkedList<PlacesModel> address = new LinkedList<>();
    private double latitude;
    private double longitude;
    private LatLng coord;
    public LinkedImageSerializable imageList;
    private LinkedSerializable addressS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        addressS = (LinkedSerializable) i.getExtras().getSerializable("address");
        address = addressS.getL();
        latitude=i.getExtras().getDouble("latitude");
        longitude=i.getExtras().getDouble("longitude");
        Intent intent=new Intent(this,MainActivityNavigation.class);
        Bundle bundle = new Bundle();
        addressS=new LinkedSerializable(address);
        bundle.putSerializable("address", addressS);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        intent.putExtras(bundle);
        startChildActivity("OptionsActivity", intent);
    }
}