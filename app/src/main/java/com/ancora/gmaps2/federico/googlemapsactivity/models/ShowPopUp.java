package com.ancora.gmaps2.federico.googlemapsactivity.models;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.DataBasePref;
import com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources.GoogleActivityNavigation;
import com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources.PreferitiNew;
import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.LinkedList;

public class ShowPopUp extends AppCompatActivity implements Serializable{
    private PlacesModel pl;
    PopupWindow popUp;
    private Bitmap b;
    private LatLng l1;
    private LinkedSerializable l;
    private int pos;
    private LinkedList<PlacesModel> list;
    private ConnectAsyncTask c;
    private TextView name;
    private TextView addr;
    private TextView open;
    private PreferitiNew pn=new PreferitiNew();
    private DataBasePref sql =new DataBasePref(this);


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        popUp = new PopupWindow(this);
        setContentView(R.layout.popup);
        name=(TextView) findViewById(R.id.txtNome);
        addr=(TextView) findViewById(R.id.txtAddress);
        open=(TextView) findViewById(R.id.txtApert);
        ImageView iv=(ImageView) findViewById(R.id.image);
        l= (LinkedSerializable) i.getExtras().getSerializable("address");
        list=l.getL();
        pos=i.getExtras().getInt("pos");

        l1=i.getExtras().getParcelable("l1");
        if(list.get(pos).getUrl()!=null) {
            Picasso.with(this.getApplicationContext())
                    .load(list.get(pos).getUrl())
                    .fit()
                    .into(iv);
        }else {
            Picasso.with(this.getApplicationContext())
                    .load("https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png")
                    .fit()
                    .into(iv);
        }
        name.setText(list.get(pos).getName());
        addr.setText(list.get(pos).getAddress());
        if(list.get(pos).isOpen()) {
            open.setText("ristorante aperto");
        }else{
            open.setText("ristorante chiuso");
        }

        iv.setImageBitmap(b);
        DisplayMetrics dm=new DisplayMetrics();
        Button bP=(Button) findViewById(R.id.btnFav);
        bP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql.inserisci(list.get(pos));
                Toast.makeText(getApplicationContext(), list.get(pos).getName() +" aggiunto ai preferiti", Toast.LENGTH_LONG).show();
            }
        });
        Button b=(Button) findViewById(R.id.drawPath);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = new ConnectAsyncTask(makeURL(l1.latitude, l1.longitude, Double.parseDouble(list.get(pos).getLat()),
                        Double.parseDouble(list.get(pos).getLng())));
                c.execute();
            }
        });
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.85), (int)(height*.5));
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=car&alternatives=true");
        urlString.append("&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");
        return urlString.toString();
    }


    private class ConnectAsyncTask extends AsyncTask<Void, Void, Bundle> {
        private ProgressDialog progressDialog;
        String url;
        ConnectAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(ShowPopUp.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
        @Override
        protected Bundle doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrlPath(url);
            boolean path=true;
            Bundle bundle = new Bundle();
            LatLng coord=new LatLng(l1.latitude, l1.longitude);
            LatLng l2=new LatLng(Double.parseDouble(list.get(pos).getLat()), Double.parseDouble(list.get(pos).getLng()));
            Path jso=new Path(json);
            bundle.putSerializable("drawPath", jso);
            bundle.putParcelable("LatLong", coord);
            bundle.putParcelable("LatLong2", l2);
            bundle.putBoolean("path", path);
            bundle.putSerializable("address", l);
            return bundle;
        }
        @Override
        protected void onPostExecute(Bundle result) {
            super.onPostExecute(result);
            progressDialog.cancel();
            load(result);


        }
    }

    private void load(Bundle result) {
        Intent i = new Intent(ShowPopUp.this, GoogleActivityNavigation.class);
        i.putExtras(result);
        startActivity(i);
        finish();
    }


}