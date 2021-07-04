package com.ancora.gmaps2.federico.googlemapsactivity.models;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ancora.gmaps2.federico.googlemapsactivity.R;

public class ShowPopUpPlace extends AppCompatActivity{
    private PlacesModel pl;
    PopupWindow popUp;
    LinearLayout layout;
    ViewGroup.LayoutParams params;
    LinearLayout mainLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Intent i=getIntent();
        FrameLayout l=i.getExtras().getParcelable("layout");*/
        popUp = new PopupWindow(this);
        setContentView(R.layout.autocomplete_fragment);
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //popUp.showAtLocation(l, Gravity.TOP, 0, 0);
        int width= dm.widthPixels;
        int height=dm.heightPixels;
        //popUp.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getWindow().setLayout((int) (width * .4), (int) (height * .15));

    }


}