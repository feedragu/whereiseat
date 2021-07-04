package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.DataBasePref;
import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializable;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializableS;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.ancora.gmaps2.federico.googlemapsactivity.models.SocketService;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;

public class TabHostActivity extends TabActivity implements Serializable, TabHost.OnTabChangeListener,
        NavigationView.OnNavigationItemSelectedListener, MyService.Callbacks, LoginFrag.OnFragmentInteractionListener{

    private LinkedSerializable l;
    private TabHost tabHost;
    public LinkedList<PlacesModel> address = new LinkedList<>();
    private double latitude;
    private double longitude;
    private LatLng coord;
    private int m=0;
    private LinkedSerializable addressS;
    private LinkedList<String> jsonS;
    private TabHost mTabHost;
    public PlaceAutocompleteFragment autocompleteFragment;
    public LoginFrag loginFragment;
    public Toolbar toolbar;
    public View  mContainerViewS;
    private LinkedSerializableS s;
    private LinkedList<String> json=new LinkedList();
    private Intent intent1;
    private Intent intent2;
    private StartService ss=new StartService();
    public int current=1;
    private String keyString= String.valueOf(R.string.google_maps_key);//AIzaSyDoykVxAjvUNzmZhxjQJRK0Kfl6TCuZSxs
    private Intent intent3;
    public Socket client;
    public SocketService mService;
    boolean mBound = false;
    private Button login;
    MyService myService;
    public String key;
    private LinkedList<Float> listF;
    private SendMessage sd=new SendMessage();
    private LinkedList<PlacesModel> rateList=new LinkedList<>();
    int act=0;
    MainActivityNavigation currentActivity;
    PreferitiNew currentActivityP;
    TopActivity currentActivityT;
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance();
            myService.registerClient(TabHostActivity.this);
            myService.connServer();
            carica();
            if(myService.isConn())
                sd.execute();


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };
    private LinkedList<PlacesModel> listModel;
    private Menu menu;
    public MenuItem navTitle;
    public View hView;
    public ImageView imAcc;
    public TextView txAcc;
    private TextView btnCat;
    public int k=0;

    // AIzaSyBkUpQaDN-FVwsZ2RSGO9euQEb7s5ZI50k //AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I//AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8
    //AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);
        ss.execute();
        Intent i = getIntent();
        s= (LinkedSerializableS) i.getExtras().getSerializable("array");
        addressS = (LinkedSerializable) i.getExtras().getSerializable("address");
        address = addressS.getL();
        key=i.getExtras().getString("key");
        latitude = i.getExtras().getDouble("latitude");
        longitude = i.getExtras().getDouble("longitude");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(Color.argb(255, Color.WHITE, Color.WHITE, Color.WHITE));
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Inserire una via...");

        /*toolbar.setTitle(" Ristoranti");
        toolbar.setLogo(R.mipmap.ic_launcher);*/

        mContainerViewS = findViewById(R.id.place_autocomplete_fragment);
        mContainerViewS.setVisibility(View.VISIBLE);
        autocompleteFragment.setText("");

        mTabHost = getTabHost();
        mTabHost.setOnTabChangedListener(this);

        btnCat=(TextView) findViewById(R.id.btnCat);
        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m==0) {
                    if(act!=1) {
                        switchTab(0);
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentActivity.moveFragmentCat();
                                m++;
                            }
                        }, 270);
                    }else {
                        currentActivity.moveFragmentCat();
                        m++;
                    }

                }else {
                    currentActivity.secondMoveCat();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        /*drawer.findViewById(R.id.btnRegistrati).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TabHostActivity.this, login_activity.class));
            }
        });*/
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        hView =  navigationView.getHeaderView(0);
        imAcc=(ImageView) hView.findViewById(R.id.imAcc);
        txAcc=(TextView) hView.findViewById(R.id.barUser);
        navTitle=menu.findItem(R.id.nav_accedi);
        navigationView.setNavigationItemSelectedListener(this);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prova, menu);
        return true;
    }


    private void carica() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec spec;



        intent1 = new Intent(TabHostActivity.this, MainActivityNavigation.class);
        Bundle bundle = new Bundle();
        LinkedSerializable l1=new LinkedSerializable(address);
        bundle.putSerializable("address", l1);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        intent1.putExtras(bundle);

        intent2 = new Intent(TabHostActivity.this, PreferitiNew.class);
        Bundle bundle3 = new Bundle();
        LinkedSerializable l2=new LinkedSerializable(address);
        bundle3.putSerializable("address2", l2);
        bundle3.putDouble("latitude", latitude);
        bundle3.putDouble("longitude", longitude);
        intent2.putExtras(bundle3);

        intent3 = new Intent(TabHostActivity.this, TopActivity.class);
        Bundle bundle2 = new Bundle();
        LinkedSerializable l3=new LinkedSerializable(address);
        bundle2.putSerializable("address3", l3);
        bundle2.putBoolean("conn", myService.isConn());
        bundle2.putDouble("latitude", latitude);
        bundle2.putDouble("longitude", longitude);
        intent3.putExtras(bundle2);

        setupTab(new TextView(this), "Locali", intent1);
        setupTab(new TextView(this), "Top 10", intent3);
        setupTab(new TextView(this), "Preferiti", intent2);


    }

    private LinkedList loadList() {

        return rateList;
    }


    public void switchTab(int tab){
        tabHost.setCurrentTab(tab);
    }

    public Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(250);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(250);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


    @Override
    public void onTabChanged(String tabId) {


        if(tabId.equals("Preferiti")) {
            act=3;
            View currentView = getTabHost().getCurrentView();
            currentActivityP= (PreferitiNew) getCurrentActivity();
            currentView.setAnimation(inFromRightAnimation());
        }else if(tabId.equalsIgnoreCase("Top 10")){
            act=2;
            if(current == 1) {
                currentActivityT= (TopActivity) getCurrentActivity();
                View currentView = getTabHost().getCurrentView();
                currentView.setAnimation(inFromRightAnimation());
            }else  {
                currentActivityT= (TopActivity) getCurrentActivity();
                View currentView = getTabHost().getCurrentView();
                currentView.setAnimation(outToLeftAnimation());
            }
            }else if(tabId.equalsIgnoreCase("Locali")){
                act=1;
                currentActivity= (MainActivityNavigation) getCurrentActivity();
                View currentView = getTabHost().getCurrentView();
                if(k!=0) {
                    currentView.setAnimation(outToLeftAnimation());
                }else {
                    k++;
                }
            }


    }


    private void setupTab(final View view, final String tag, Intent intent1) {
        View tabview = createTabView(mTabHost.getContext(), tag);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent1);
        mTabHost.addTab(setContent);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        ImageView im=(ImageView) view.findViewById(R.id.imTab);
        if(text.equals("Preferiti")) {
            im.setImageResource(R.drawable.star);
        }else if(text.equalsIgnoreCase("Top 10")){
            im.setImageResource(R.drawable.top_10);
        }else {
            im.setImageResource(R.drawable.locali_icon);
        }

        tv.setText(text);
        return view;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String t= (String) item.getTitle();

        if (id == R.id.nav_accedi) {
            if(t.equals("Accedi")) {
                if (act == 1) {
                    currentActivity.moveFragment();
                } else if (act == 2) {
                    currentActivityT.moveFragment();
                } else if (act == 3) {
                    currentActivityP.moveFragment();
                }
            }
            else {

            }
        } else if (id == R.id.nav_reg) {
            startActivity(new Intent(TabHostActivity.this, login_activity.class));
        } else if (id == R.id.nav_sett) {

        } else if (id == R.id.nav_manage) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setList(PlacesModel pl) {
        DataBasePref pref=new DataBasePref(getApplicationContext());
        pref.inserisci(pl);
        pref.close();
    }

    public void increaseCount() {
        m=0;
    }


    private class SendMessage extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder sb=new StringBuilder();
            for(int i=0; i<address.size(); i++) {
                sb.append(address.get(i).getId() + "|");
            }
            try {
                listF=myService.getRate(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int i=0; i<address.size(); i++) {
                address.get(i).setRating(listF.get(i).floatValue());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }

    private class StartService extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            Intent serviceIntent = new Intent(TabHostActivity.this, MyService.class);
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }
}
