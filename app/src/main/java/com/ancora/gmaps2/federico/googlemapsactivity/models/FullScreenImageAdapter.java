package com.ancora.gmaps2.federico.googlemapsactivity.models;


import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private LinkedList<String> _imagePaths;
    private LayoutInflater inflater;
    private String key;

    // constructor
    public FullScreenImageAdapter(Activity activity, LinkedList<String> imagePaths, String key) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.key=key;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);


        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);*/
        Picasso.with(_activity)
                .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference="
                        + _imagePaths.get(position) + "&key=" + key)
                .placeholder(R.drawable.images_new)
                .noFade()
                .into(imgDisplay);

        // close button click event

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}