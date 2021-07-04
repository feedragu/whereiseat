package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializable;
import com.ancora.gmaps2.federico.googlemapsactivity.models.MyMarker;
import com.ancora.gmaps2.federico.googlemapsactivity.models.Path;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

public class GoogleActivityNavigation extends AppCompatActivity
        implements OnMapReadyCallback, StreetViewPanorama.OnStreetViewPanoramaChangeListener, Serializable, OnStreetViewPanoramaReadyCallback, MyService.Callbacks {
    private GoogleMap mMap;
    private StreetViewPanorama street;
    private LocationListener mLocationListener;
    private LocationManager locationManager;
    private SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    private SupportMapFragment mapFragment;
    private View mContainerViewS;
    private View mContainerViewM;
    private ViewGroup googleLayout;
    private ArrayList<ViewGroup.LayoutParams> a;
    private boolean path;
    private String draw;
    private int k = 0;
    private int j = 0;
    private Marker myPos;
    MyService myService;
    private ArrayList<MyMarker> mMyMarkersArray = new ArrayList<MyMarker>();
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    private Timer timer;
    LatLng l;
    private Marker pos;
    private provaDetails parentActivity;


    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance();
            myService.registerClient(GoogleActivityNavigation.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(GoogleActivityNavigation.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };

    private GoogleApiClient client;
    private boolean not_first_time_showing_info_window = true;
    private int iniz = 0;
    private LinkedList<LatLng> listL;
    private LinkedList<Marker> listMark = new LinkedList<>();
    private int p = 0;
    private Handler handler;
    private boolean stop=false;
    public Thread r = new Thread() {
        @Override
        public void run() {
            send = true;
            listL = myService.sharePos(l);
            p = listMark.size();
            for (int j = 0; j < listL.size(); j++) {
                final MarkerOptions markerOption = new MarkerOptions().position(listL.get(j));
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                final int finalJ = j;
                pos = mMap.addMarker(markerOption);
                if (j < p) {
                    listMark.get(j).remove();
                    listMark.remove(j);
                    listMark.add(j, pos);

                } else {
                    listMark.add(pos);
                }
                Log.i("it is working?", String.valueOf(listMark.size()));


            }
            /*for (int i = 0; i < listMark.size(); i++) {
                animateMarker(listMark.get(i), listL.get(i), false);
                listMark.get(i).setPosition(listL.get(i));

            }*/
            Log.i("wut", String.valueOf(stop));
            if(stop==false) {
                handler.postDelayed(this, 10000);
            }
            send = false;
        }
    };
    private boolean send = false;
    private LinkedList<PlacesModel> address;
    private String key;
    private LinkedSerializable addressS;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(GoogleActivityNavigation.this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_google_activity_navigation);

        mMarkersHashMap = new HashMap<Marker, MyMarker>();

        streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        parentActivity = (provaDetails) getParent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(" Maps");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        googleLayout = (ViewGroup) findViewById(R.id.google_layout);

        Button bt = (Button) findViewById(R.id.btnAllarga);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (k == 0) {
                    a = moveFragments();
                    k++;
                } else {
                    mContainerViewM.setLayoutParams(a.get(1));
                    mContainerViewS.setLayoutParams(a.get(0));
                    mContainerViewS.setPadding(0, 0, 0, 0);
                    mContainerViewS.setVisibility(View.VISIBLE);
                    k = 0;
                }
            }
        });
        /*Button btr = (Button) findViewById(R.id.btnRitorna);
        btr.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //TransitionManager.beginDelayedTransition(googleLayout);
                mContainerViewM.setLayoutParams(a.get(1));
                mContainerViewS.setLayoutParams(a.get(0));
                mContainerViewS.setPadding(0, 0, 0, 0);
                mContainerViewS.setVisibility(View.VISIBLE);
            }
        });*/

        handler = new Handler();

        handler.postDelayed(r, 5000);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.i("latitude", String.valueOf(location.getLatitude()));
                Log.i("longitude", String.valueOf(location.getLongitude()));
                l = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, mMap.getCameraPosition().zoom));
                animateMarker(myPos, l, false);
                street.setPosition(l);
                synchronized (this) {
                    if (!send)
                        listL = myService.sharePos(l);
                }
                /*if(p==0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer = new Timer();
                            timer.scheduleAtFixedRate(timerTask, 0, 5007);
                        }
                    });
                    p++;
                }*/


            }

            @Override
            public void onStatusChanged(final String provider,
                                        final int status, final Bundle extras) {
            }

            @Override
            public void onProviderEnabled(final String provider) {
            }

            @Override
            public void onProviderDisabled(final String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }


        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            configureButton();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }

    }

    @Override
    protected void onResume() {
        if (r.isInterrupted()) {
            r.resume();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 3000, 1, mLocationListener);

        super.onResume();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void configureButton() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 3000, 1, mLocationListener);
    }

    @Override
    public void onBackPressed() {
        r.interrupt();
        finish();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(mLocationListener);
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.google_activity_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i("prova 2", id+"    "+android.R.id.home);

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Log.i("prova 3", id+"    "+android.R.id.home);
            stop=true;
            finish();
            if (ActivityCompat.checkSelfPermission(GoogleActivityNavigation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GoogleActivityNavigation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return true;
            }
            locationManager.removeUpdates(mLocationListener);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public ArrayList<ViewGroup.LayoutParams> moveFragments() {
        mContainerViewS = findViewById(R.id.streetviewpanorama);
        mContainerViewM = findViewById(R.id.map);
        ViewGroup.LayoutParams oldOnesS = mContainerViewS.getLayoutParams();
        ViewGroup.LayoutParams oldOnesM = mContainerViewM.getLayoutParams();
        ArrayList<ViewGroup.LayoutParams> a = new ArrayList();
        a.add(oldOnesS);
        a.add(oldOnesM);
        RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mContainerViewS.setVisibility(View.INVISIBLE);
        mContainerViewS.setPadding(0, 1100, 800, 0);
        mContainerViewM.setLayoutParams(map);
        return a;

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Intent i = getIntent();
        l = i.getParcelableExtra("LatLong");
        listL = myService.sharePos(l);
        key=i.getExtras().getString("key");
        for(int s=0; s<listL.size(); s++) {
            Log.i("lat", String.valueOf(listL.get(s).latitude));
        }
        for(int j=0; j<listL.size(); j++) {
                MarkerOptions markerOption = new MarkerOptions().position(listL.get(j));
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker pos = googleMap.addMarker(markerOption);
                listMark.add(pos);

        }
        path = i.getExtras().getBoolean("path");
        addressS = (LinkedSerializable) i.getExtras().getSerializable("address");
        address = null;
        if (addressS != null) {
            address = addressS.getL();
        }
        for(int k=0; k<address.size(); k++) {
            if (address.get(k).getPhotoRef()!=null) {
                mMyMarkersArray.add(new MyMarker(address.get(k).getName(), address.get(k).getPhotoRef(), Double.parseDouble(address.get(k).getLat()), Double.parseDouble(address.get(k).getLng())));
                Log.i("prova", address.get(k).getPhotoRef());
            }
            else {
                mMyMarkersArray.add(new MyMarker(address.get(k).getName(), "", Double.parseDouble(address.get(k).getLat()), Double.parseDouble(address.get(k).getLng())));

            }

        }


        if (path == true) {
            Path p= (Path) i.getExtras().getSerializable("drawPath");
            draw=p.getJsonPath();
            LatLng l2=i.getExtras().getParcelable("l2");
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            myPos=mMap.addMarker(new MarkerOptions().position(l).title("Tua posizione"));
            drawPath(draw);
            setUpMap();
            plotMarkers(mMyMarkersArray);

            /*for (int j = 0; j < address.size(); j++) {

                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
                Canvas canvas1 = new Canvas(bmp);*/

// paint defines the text color,
// stroke width, size
                /*Paint color = new Paint();
                color.setTextSize(15);
                color.setColor(Color.BLACK);*/

//modify canvas
                /*canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.arrow), 0,0, color);
                canvas1.drawText(address.get(j).getName(), 10, 100, color);*/
                   // LatLng pos = new LatLng(Double.parseDouble(address.get(j).getLat()), Double.parseDouble(address.get(j).getLng()));
                    /*MarkerOptions marker = new MarkerOptions().position(pos).title(address.get(j).getName());
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.cose2));*/
                /*mMap.addMarker(new MarkerOptions().position(pos)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                // Specifies the anchor to be at a particular point in the marker image.
                        .anchor(0.5f, 1));
            }*/
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 18));


        } else {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.addMarker(new MarkerOptions().position(l).title("Tua posizione"));
            for (int j = 0; j < address.size(); j++) {
                LatLng pos = new LatLng(Double.parseDouble(address.get(j).getLat()), Double.parseDouble(address.get(j).getLng()));
                MarkerOptions marker2 = new MarkerOptions().position(pos).title(address.get(j).getName());
                marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.cose2));
                mMap.addMarker(marker2);
            }
            parentActivity.progressDialog.cancel();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 18));
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        Intent i = getIntent();
        street = streetViewPanorama;
        LatLng l = i.getParcelableExtra("LatLong");
        streetViewPanorama.setPosition(l);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setStreetNamesEnabled(true);
    }


    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

    }

    public void drawPath(String  result) {

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))
                            .geodesic(true)
            );
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.ancora.gmaps2.federico.googlemapsactivity/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.ancora.gmaps2.federico.googlemapsactivity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public synchronized void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void setUpMap()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Check if we were successful in obtaining the map.

            if (mMap != null)
            {
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                    {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            }
            else
                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateClient(long data) {

    }

    @Override
    public void getString(String s) {

    }

    @Override
    public void getList(LinkedList<String> a) {

    }

    private void plotMarkers(ArrayList<MyMarker> markers)
    {
        if(markers.size() > 0)
        {
            for (int s=0; s<address.size(); s++)
            {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(markers.get(s).getmLatitude(), markers.get(s).getmLongitude()));
                Log.i("lat", String.valueOf(markers.get(s).getmLatitude()));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rest));

                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, markers.get(s));

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(s));
                final int finalS = s;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        int k=0;
                        for(int i=0; i<address.size(); i++) {
                            if(Double.parseDouble(address.get(i).getLat())==marker.getPosition().latitude  &&
                                    Double.parseDouble(address.get(i).getLng())==marker.getPosition().longitude)
                                k=i;
                        }
                        Intent i = new Intent(GoogleActivityNavigation.this, provaDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("place", address.get(k));
                        bundle.putSerializable("address", addressS);
                        bundle.putParcelable("l1", marker.getPosition());
                        bundle.putString("key", key);
                        bundle.putInt("pos", k);
                        bundle.putBoolean("pref", false);
                        if (myService.isEmpty(k) == true) {
                            bundle.putBoolean("json", true);
                        } else {
                            bundle.putBoolean("json", false);
                        }

                        i.putExtras(bundle);
                        startActivity(i);
                        r.interrupt();
                    }
                });
            }
        }
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        int s=0;
        public MarkerInfoWindowAdapter(int s)
        {
            this.s=s;
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker)
        {

            final View v = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            final TextView markerLabel = (TextView) v.findViewById(R.id.marker_label);
            if(!marker.getPosition().equals(l)) {
                final MyMarker myMarker = mMarkersHashMap.get(marker);

                final ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);


                Picasso.with(GoogleActivityNavigation.this)
                        .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                                + myMarker.getmIcon() + "&key=AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I")
                        .placeholder(R.drawable.image_nope)
                        .resize(440, 280)
                        .into(markerIcon, new InfoWindowRefresher(marker));


            /*if (not_first_time_showing_info_window) {
                Picasso.with(GoogleActivityNavigation.this).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                        + myMarker.getmIcon() + "&key=AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I").fit().into(markerIcon);
            } else { // if it's the first time, load the image with the callback set
                not_first_time_showing_info_window=true;
                Picasso.with(GoogleActivityNavigation.this).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                        + myMarker.getmIcon() + "&key=AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I").fit().into(markerIcon, new InfoWindowRefresher(marker));

            }*/


                markerLabel.setText(myMarker.getmLabel());
            }else {
                markerLabel.setText(marker.getTitle());
            }

            return v;
        }
    }

    private class InfoWindowRefresher implements Callback {
        private Marker marker;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.marker = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }

        @Override
        public void onError() {}
    }

    private int manageMarkerIcon(String markerIcon)
    {
        if (markerIcon.equals("icon1"))
            return R.drawable.noimg;
        else if(markerIcon.equals("icon2"))
            return R.drawable.noimg;
        else if(markerIcon.equals("icon3"))
            return R.drawable.noimg;
        else if(markerIcon.equals("icon4"))
            return R.drawable.noimg;
        else if(markerIcon.equals("icon5"))
            return R.drawable.noimg;
        else if(markerIcon.equals("icon6"))
            return R.drawable.noimg;
        else if(markerIcon.equals("icon7"))
            return R.drawable.noimg;
        else
            return R.drawable.noimg;
    }
}
