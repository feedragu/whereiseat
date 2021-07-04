package com.ancora.gmaps2.federico.googlemapsactivity.models;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

public class ShowPopUpImage extends AppCompatActivity implements Serializable {
    private PopupWindow popUp;
    private Bitmap b;
    private LatLng l1;
    private LinkedSerializable l;
    private int pos;
    private LinkedList<PlacesModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        popUp = new PopupWindow(this);
        DisplayMetrics dm=new DisplayMetrics();
        setContentView(R.layout.image_pop_up);
        ImageView iv=(ImageView) findViewById(R.id.imagePop);
        l= (LinkedSerializable) i.getExtras().getSerializable("address");
        list=l.getL();
        pos=i.getExtras().getInt("pos");
        Picasso.with(this.getApplicationContext())
                .load(list.get(pos).getUrl())
                .fit()
                .into(iv);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .4));

    }

    public Bitmap getBitmapfromUrl(String src) {
        try {
            URL url=new URL(src);
            HttpsURLConnection connection= (HttpsURLConnection) url.openConnection();
            InputStream input=connection.getInputStream();
            BufferedInputStream is = new BufferedInputStream(input);
            Bitmap myBitmap= BitmapFactory.decodeStream(is);
            return myBitmap;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
