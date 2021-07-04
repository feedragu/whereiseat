package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.JSONParser;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializable;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializableS;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

public class SplashActivityGoogle extends Activity implements Serializable, MyService.Callbacks {

    private UIHandler mHandler;
    private LinkedSerializable l;
    private LinkedSerializableS s;
    private TabHost tabHost;
    public LinkedList<PlacesModel> address = new LinkedList<>();
    private LinkedList<PlacesModel> address2 = new LinkedList<>();
    private double latitude;
    private double longitude;
    private LatLng coord;
    private LocationManager lm;
    private boolean firstTime = true;
    //private JSONTask json = new JSONTask();
    private NextPage nextPage = new NextPage();
    private NextPage2 nextPage2;
    private String next_page_token;
    private int k = 0;
    public String key = "AIzaSyDoykVxAjvUNzmZhxjQJRK0Kfl6TCuZSxs";
    private JSONParser jPar = new JSONParser();
    private LinkedList<String> jsonString = new LinkedList<>();
    MyService myService;
    private LinkedList<String> places = new LinkedList<>();
    private LinkedList<String> places2 = new LinkedList<>();
    private JSONParser jsonP = new JSONParser();
    private LinkedList<String> jsonParsed = new LinkedList<>();
    private getJson js = new getJson();
    int j = 0;

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance();
            myService.registerClient(SplashActivityGoogle.this);
            /*nextPage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                  + latitude + "," + longitude + "&type=restaurant&rankby=distance&key=" + key);*/
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };
    private int w = 0;
    private LocationListener locationListener;
    private boolean start=true;
    private NextPage3 nextPage3=new NextPage3();


    @Override
    public void updateClient(long data) {

    }

    @Override
    public void getString(String s) {

    }

    @Override
    public void getList(LinkedList<String> a) {

    }

    private class UIHandler extends Handler {
        private WeakReference<SplashActivityGoogle> activityRef;

        public UIHandler(final SplashActivityGoogle srcActivity) {
            this.activityRef = new WeakReference<SplashActivityGoogle>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SplashActivityGoogle src = this.activityRef.get();
            switch (msg.what) {
                case GO_AHEAD_WHAT:
                    src.goAhead();
                    break;
                case 0:
                    key = "AIzaSyBkUpQaDN-FVwsZ2RSGO9euQEb7s5ZI50k";
                    nextPage = new NextPage();
                    try {
                        loadList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    key = "AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os";
                    nextPage = new NextPage();
                    try {
                        loadList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    key = "AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I";
                    nextPage = new NextPage();
                    try {
                        loadList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    key = "AIzaSyDW_2d7DY8I5I3Bo2N6LJv-eYOD82TrjCc";
                    nextPage = new NextPage();
                    try {
                        loadList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    key = "AIzaSyBye7O1NjdiLdXzIKMfeNYNHegiM66aZ-U";
                    nextPage = new NextPage();
                    try {
                        loadList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    key = "AIzaSyDuNlbhCmUhL2gBgvPGTwe4gq2Y8N-qf_E";
                    nextPage = new NextPage();
                    try {
                        loadList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(
                            SplashActivityGoogle.this).create();
                    alertDialog.setTitle("Api esaurite");
                    alertDialog.setMessage("Richieste giornaliere esaurite");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.show();
                    break;

            }

        }
    }

    private static final long WAIT_INTERVAL = 10000L;
    private static final int GO_AHEAD_WHAT = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        mHandler = new UIHandler(this);
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled || !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS seems to be not enabled");
            dialog.setPositiveButton("enable GPS", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    firstTime = false;


                }
            });
            dialog.setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();

        } else {
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
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //lm.removeUpdates(locationListener);
            try {
                loadList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    protected void onStart() {
        Intent serviceIntent = new Intent(SplashActivityGoogle.this, MyService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (k == 0) {
            if (!firstTime) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            // This needs to stop getting the location data and save the battery power.
                            if (ActivityCompat.checkSelfPermission(SplashActivityGoogle.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashActivityGoogle.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            lm.removeUpdates(locationListener);

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            nextPage.execute();
                            k++;
                        }

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                lm.requestLocationUpdates("gps", 0, 0, locationListener);



            }
        }
    }

    public void loadList() throws InterruptedException {
        nextPage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude + "," + longitude + "&type=restaurantkeyword=fastfood&rankby=distance&key=" + key);//AIzaSyDoykVxAjvUNzmZhxjQJRK0Kfl6TCuZSxs"); //AIzaSyBkUpQaDN-FVwsZ2RSGO9euQEb7s5ZI50k

    }




    public class NextPage extends AsyncTask<String, String, Boolean> {
        private String finalJSOn;
        private JSONObject parentObject;
        private HttpsURLConnection connection;
        private BufferedReader br;


        @Override
        protected Boolean doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + latitude + "," + longitude + "&type=restaurant|cafe|bakery&rankby=distance&key=" + key);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            connection = null;
            br = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream;
                stream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                finalJSOn = buffer.toString();
                parentObject = new JSONObject(finalJSOn);

                try {
                    next_page_token = parentObject.getString("next_page_token");

                } catch (JSONException j) {
                    if (key.equals("AIzaSyDoykVxAjvUNzmZhxjQJRK0Kfl6TCuZSxs")) {
                        mHandler.sendEmptyMessage(0);
                        this.cancel(true);
                    } else if (key.equals("AIzaSyBkUpQaDN-FVwsZ2RSGO9euQEb7s5ZI50k")) {
                        mHandler.sendEmptyMessage(2);
                        this.cancel(true);
                    } else if (key.equals("AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os")) {
                        mHandler.sendEmptyMessage(3);
                        this.cancel(true);
                    } else if (key.equals("AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I")) {
                        mHandler.sendEmptyMessage(4);
                        this.cancel(true);
                    } else if (key.equals("AIzaSyDW_2d7DY8I5I3Bo2N6LJv-eYOD82TrjCc")) {
                        mHandler.sendEmptyMessage(5);
                        this.cancel(true);
                    }else if (key.equals("AIzaSyBye7O1NjdiLdXzIKMfeNYNHegiM66aZ-U")) {
                        mHandler.sendEmptyMessage(6);
                        this.cancel(true);
                    }  else if (key.equals("AIzaSyDuNlbhCmUhL2gBgvPGTwe4gq2Y8N-qf_E")) {
                        mHandler.sendEmptyMessage(7);
                        this.cancel(true);

                    }



                }
                try {
                    JSONArray parentArray = parentObject.getJSONArray("results");
                    //connection();
                    int k=0;
                    for (int i = 0; i < parentArray.length(); i++) {
                        Log.i("che sta a succede?", url.toString());
                        if(next_page_token!=null && i==8)
                            startSecond(next_page_token);
                        else
                            k=0;

                        PlacesModel placesModel = new PlacesModel();
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        if (finalObject.has("photos")) {
                            JSONArray photo = finalObject.getJSONArray("photos");
                            JSONObject photos = photo.getJSONObject(0);
                            placesModel.setPhotoRef(photos.getString("photo_reference"));
                            placesModel.setPhotoYes(true);
                        }
                        placesModel.setId(finalObject.getString("place_id"));
                        placesModel.setAddress(finalObject.getString("vicinity"));

                        placesModel.setName(finalObject.getString("name"));


                        JSONObject second = finalObject.getJSONObject("geometry");
                        JSONObject third = second.getJSONObject("location");
                        placesModel.setLat(third.getString("lat"));
                        placesModel.setLng(third.getString("lng"));

                        Location loc1 = new Location("");
                        loc1.setLatitude(latitude);
                        loc1.setLongitude(longitude);
                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(placesModel.getLat()));
                        loc2.setLongitude(Double.parseDouble(placesModel.getLng()));
                        int distanceInMeters = Math.round(loc1.distanceTo(loc2));
                        placesModel.setDistance(distanceInMeters);
                        if (finalObject.has("opening_hours")) {
                            JSONObject secondO = finalObject.getJSONObject("opening_hours");
                            placesModel.setOpen(secondO.getBoolean("open_now"));
                        }
                        address.add(placesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);

        }
    }


    private void connection() {
        if(w==0) {
            myService.connServer();
            w++;
        }

    }


    private void goAhead() {
        nextPage.cancel(true);
        nextPage2.cancel(true);
        nextPage3.cancel(true);
        Intent intent = new Intent(SplashActivityGoogle.this, TabHostActivity.class);
        Bundle bundle = new Bundle();
        l=new LinkedSerializable(address);
        bundle.putSerializable("address", l);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        bundle.putString("key", key);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
        Intent serviceIntent = new Intent(SplashActivityGoogle.this, MyService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public class NextPage2 extends AsyncTask<String, String, Boolean> {
        String next_next;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url2 = null;
            HttpsURLConnection connection = null;
            BufferedReader br = null;

            try {

                url2 = new URL( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + latitude + "," + longitude + "&type=restaurant|cafe|bakery&keyword=birra&rankby=distance&key=" + key +"&pagetoken="+params[0]); //+ "&pagetoken=" + next_page_token);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            connection = null;
            br = null;
            LinkedList<PlacesModel> array = new LinkedList<>();
            int k=0;
            try {
                if (url2 != null) {
                    connection = (HttpsURLConnection) url2.openConnection();
                }
                else {

                }
                connection.connect();
                InputStream stream = null;
                stream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line2;
                while ((line2 = br.readLine()) != null) {
                    buffer.append(line2);
                }
                String finalJSOn = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJSOn);

                try {
                    JSONArray parentArray = parentObject.getJSONArray("results");
                    //connection();
                    for (int i = 0; i < parentArray.length(); i++) {
                        k++;
                        Log.i("che sta a succede? 2", url2.toString());
                        PlacesModel placesModel = new PlacesModel();
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        if (finalObject.has("photos")) {
                            JSONArray photo = finalObject.getJSONArray("photos");
                            JSONObject photos = photo.getJSONObject(0);
                            placesModel.setPhotoRef(photos.getString("photo_reference"));
                            placesModel.setPhotoYes(true);
                        }
                        placesModel.setId(finalObject.getString("place_id"));
                        /*placesModel.setJsonD(jPar.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/details/json?" +
                                "placeid=" + placesModel.getId() + "&key=" + key));*/
                        placesModel.setAddress(finalObject.getString("vicinity"));
                        placesModel.setName(finalObject.getString("name"));

                        JSONObject second = finalObject.getJSONObject("geometry");
                        JSONObject third = second.getJSONObject("location");
                        placesModel.setLat(third.getString("lat"));
                        placesModel.setLng(third.getString("lng"));

                        Location loc1 = new Location("");
                        loc1.setLatitude(latitude);
                        loc1.setLongitude(longitude);
                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(placesModel.getLat()));
                        loc2.setLongitude(Double.parseDouble(placesModel.getLng()));
                        int distanceInMeters = Math.round(loc1.distanceTo(loc2));
                        placesModel.setDistance(distanceInMeters);
                        if (finalObject.has("opening_hours")) {
                            JSONObject secondO = finalObject.getJSONObject("opening_hours");
                            placesModel.setOpen(secondO.getBoolean("open_now"));
                        }
                        address.add(placesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(k>8) {
                    try {
                        next_next = parentObject.getString("next_page_token");
                    }catch(JSONException e) {

                        start=false;
                    }
                    if(start)
                        startThird(next_next);
                    return true;

                }else {
                    this.cancel(true);
                    startSecond(next_page_token);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);
            if(!start) {
                Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
                mHandler.sendMessage(goAheadMessage);
            }



        }
    }

    public class NextPage3 extends AsyncTask<String, String, Boolean> {
        String next_next;
        int num;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url2 = null;
            HttpsURLConnection connection = null;
            BufferedReader br = null;

            try {

                url2 = new URL( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + latitude + "," + longitude + "&type=restaurant|cafe|bakery&keyword=birra&rankby=distance&key=" + key +"&pagetoken="+params[0]); //+ "&pagetoken=" + next_page_token);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            connection = null;
            br = null;
            LinkedList<PlacesModel> array = new LinkedList<>();
            int k=0;
            try {
                if (url2 != null) {
                    connection = (HttpsURLConnection) url2.openConnection();
                }
                else {

                }
                connection.connect();
                InputStream stream = null;
                stream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line2;
                while ((line2 = br.readLine()) != null) {
                    buffer.append(line2);
                }
                String finalJSOn = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJSOn);

                try {
                    JSONArray parentArray = parentObject.getJSONArray("results");
                    //connection();
                    for (int i = 0; i < parentArray.length(); i++) {
                        k++;
                        Log.i("che sta a succede? 3", url2.toString());
                        PlacesModel placesModel = new PlacesModel();
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        if (finalObject.has("photos")) {
                            JSONArray photo = finalObject.getJSONArray("photos");
                            JSONObject photos = photo.getJSONObject(0);
                            placesModel.setPhotoRef(photos.getString("photo_reference"));
                            placesModel.setPhotoYes(true);
                        }
                        placesModel.setId(finalObject.getString("place_id"));
                        /*placesModel.setJsonD(jPar.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/details/json?" +
                                "placeid=" + placesModel.getId() + "&key=" + key));*/
                        placesModel.setAddress(finalObject.getString("vicinity"));
                        placesModel.setName(finalObject.getString("name"));

                        JSONObject second = finalObject.getJSONObject("geometry");
                        JSONObject third = second.getJSONObject("location");
                        placesModel.setLat(third.getString("lat"));
                        placesModel.setLng(third.getString("lng"));

                        Location loc1 = new Location("");
                        loc1.setLatitude(latitude);
                        loc1.setLongitude(longitude);
                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(placesModel.getLat()));
                        loc2.setLongitude(Double.parseDouble(placesModel.getLng()));
                        int distanceInMeters = Math.round(loc1.distanceTo(loc2));
                        placesModel.setDistance(distanceInMeters);
                        if (finalObject.has("opening_hours")) {
                            JSONObject secondO = finalObject.getJSONObject("opening_hours");
                            placesModel.setOpen(secondO.getBoolean("open_now"));
                        }
                        address.add(placesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(k>10) {
                    return true;
                }else {
                    this.cancel(true);
                    startThird(params[0]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean a) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            super.onPostExecute(a);
            Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
            mHandler.sendMessage(goAheadMessage);

        }
    }

    private void startThird(String next_next) {
        nextPage3=new NextPage3();
        nextPage3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, next_next);
    }


    public void startSecond(String page)  {
        nextPage2=new NextPage2();
        nextPage2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
    }

    public class getJson extends AsyncTask<String, String, Boolean> {
        private String s;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            s=jsonP.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + latitude + "," + longitude + "&type=restaurant&rankby=distance&key=" + key);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);


        }
    }





}