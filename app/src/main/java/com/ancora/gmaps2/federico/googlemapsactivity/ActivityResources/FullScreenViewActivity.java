package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.models.FullScreenImageAdapter;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PhotoReferences;

import java.util.LinkedList;

public class FullScreenViewActivity extends Activity{

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        PhotoReferences p= (PhotoReferences) i.getExtras().getSerializable("photoRef");
        LinkedList<String> list=p.getList();
        String key=i.getExtras().getString("key");
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, list, key);

        viewPager.setAdapter(adapter);

        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // displaying selected image first
        viewPager.setCurrentItem(0);
    }
}
