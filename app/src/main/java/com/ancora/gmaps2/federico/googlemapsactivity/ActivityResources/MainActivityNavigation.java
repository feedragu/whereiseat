package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.DataBasePref;
import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.JSONParser;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializable;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

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
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivityNavigation extends AppCompatActivity
        implements Serializable, MyService.Callbacks, LoginFrag.OnFragmentInteractionListener
        , CategoriesFragment.OnFragmentInteractionListener{
    private long FADE_DURATION=200;
    private UIHandler mHandler;
    private LinkedList<PlacesModel> address;
    public double latitude;
    public double longitude, latitude2, longitude2;
    public double newLat;
    public double newLng;
    private LinkedList<PlacesModel> address2= new LinkedList<>();
    private String next_page_token;
    private JSONParser jPar=new JSONParser();
    private NextPage nextPage = new NextPage();
    private LinkedSerializable addressS;
    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_MAX_OFF_PATH = 300;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private JsonPars json;
    private JsonParsNew jsonN=new JsonParsNew();
    private ListView lv;

    private float mY = Float.NaN;
    private boolean mStopScroll = false;
    private TabHostActivity parentActivity;
    private LinkedList<PlacesModel> newList;
    private LinkedList<String> details=new LinkedList<>();
    private ProgressDialog progressDialog;
    private boolean newL=false;
    public Socket client;
    public String key;
    MyService myService;
    String s;
    public int counter;
    int k=0;
    private View mainView;
    private JSONParser jp=new JSONParser();
    private LoginFrag loginFragment;
    private LinkedList<Boolean> listBl=new LinkedList<>();
    private View mContainerViewS;
    ViewGroup.LayoutParams oldOnesS;
    private ViewGroup commentLayout;
    private ColorDrawable cd;
    private DataBasePref pref=new DataBasePref(this);
    boolean clicked=false;
    private LinkedList<View.OnTouchListener> listG=new LinkedList<>();
    private CategoriesFragment catFrag;
    private Runnable create=new Runnable() {
        @Override
        public void run() {
            Intent serviceIntent = new Intent(MainActivityNavigation.this, MyService.class);
            getApplicationContext().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            Intent i=getIntent();
            addressS = (LinkedSerializable) i.getExtras().getSerializable("address");
            address = addressS.getL();
            latitude=i.getExtras().getDouble("latitude");
            longitude=i.getExtras().getDouble("longitude");
            for (int j=0; j<address.size(); j++) {
                final GestureDetector gestureDetectorn = new GestureDetector(getApplicationContext(), new MyGestureDetectorContext(j));
                gestureListener = new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetectorn.onTouchEvent(event);
                    }
                };
                listG.add(gestureListener);
            }

           /* for(int j=0; j<address.size(); j++) {
                Picasso.with(getApplicationContext()).
                        load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                                + address.get(j).getPhotoRef() + "&key=" + key)
                        .fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.i("success", "done");
                            }

                            @Override
                            public void onError() {
                                Log.i("success", "fail");
                            }
                        });
            }*/



            loginFragment = (LoginFrag) getSupportFragmentManager().findFragmentById(R.id.Loginfragment);
            catFrag = (CategoriesFragment) getSupportFragmentManager().findFragmentById(R.id.CatFragment);
            loginFragment.changeActivity();
            for(int j=0; j<address.size(); j++)  {
                details.add(address.get(j).getJsonD());
            }
            parentActivity.autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    newLat=place.getLatLng().latitude;
                    newLng=place.getLatLng().longitude;
                    nextPage = new NextPage();
                    nextPage.execute( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + place.getLatLng().latitude + "," + place.getLatLng().longitude + "&type=restaurant&rankby=distance&key="+key);
                    newL = true;

                }

                @Override
                public void onError(Status status) {

                }
            });
            pv=new PhotoAdapterGrid(getApplicationContext(),R.layout.categories_view, array, names);
            mContainerViewCat = findViewById(R.id.CatFragment);
            oldOnesCat = mContainerViewCat.getLayoutParams();
        }
    };
    private  Runnable r=new Runnable() {
        @Override
        public void run() {
            clicked=false;
            /*parentActivity.toolbar.setTitle(" Ristoranti");
            parentActivity.toolbar.setLogo(R.mipmap.ic_launcher);
            parentActivity.toolbar.getMenu().clear();*/
            parentActivity.mContainerViewS.setVisibility(View.VISIBLE);
            parentActivity.autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    newLat = place.getLatLng().latitude;
                    newLng = place.getLatLng().longitude;
                    nextPage.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + place.getLatLng().latitude + "," + place.getLatLng().longitude + "&type=restaurant&rankby" +
                            "=distance&key="+key);
                    newL = true;
                    nextPage = new NextPage();
                }

                @Override
                public void onError(Status status) {

                }
            });
            Collections.sort(address, new Comparator<PlacesModel>() {
                @Override
                public int compare(PlacesModel lhs, PlacesModel rhs) {
                    return Integer.valueOf((int) lhs.getDistance()).compareTo((int) rhs.getDistance());
                }
            });
            if(myService!=null) {
                listBl = myService.getListB(address);
                ph.setListB(listBl);
                ph.notifyDataSetChanged();
            }

            parentActivity.current=1;

        }
    };
    private PhotoAdapter ph;
    private ViewGroup categoriesLayout;
    private View mContainerViewCat;
    private ViewGroup.LayoutParams oldOnesCat;
    private int[] array=new int[]{R.drawable.caffe, R.drawable.bakery, R.drawable.pizzeria, R.drawable.kebab, R.drawable.dinner, R.drawable.beer, R.drawable.drink, R.drawable.messicano, R.drawable.sushi, R.drawable.near_me};
    public String[] names= new String[] {"Caffe","Pasticceria","Pizzerie","Kebab","Cena","Birra","Drink", "Messicano", "Sushi", "Vicino a me"};
    public String[] keywords= new String[] {"cafe","bakery","pizza","kebab","dinner","beer","cocktail"};
    private PhotoAdapterGrid pv;
    private LinkedList<Boolean> listB2;


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void checkLog(String text, String text1) {
        if(myService.isConn()) {
            CheckLog cl = new CheckLog(text, text1);
            cl.execute();
        }else {
            Toast.makeText(getApplicationContext(), "Server attualmente non disponibile", Toast.LENGTH_LONG).show();
        }
    }

    private class UIHandler extends Handler {
        private WeakReference<MainActivityNavigation> activityRef;
        public UIHandler(final MainActivityNavigation srcActivity) {
            this.activityRef= new WeakReference<MainActivityNavigation>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MainActivityNavigation src=this.activityRef.get();
            switch(msg.what) {
                case 0:
                    key="AIzaSyBkUpQaDN-FVwsZ2RSGO9euQEb7s5ZI50k";
                    nextPage = new NextPage();
                    nextPage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                + newLat+ "," + newLng+ "&type=restaurant&rankby=distance&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");

                    break;
                case 2:
                    key="AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os";
                    nextPage = new NextPage();
                    nextPage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + newLat+ "," + newLng+ "&type=restaurant&rankby=distance&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");
                    break;
                case 3:
                    key="AIzaSyBWbbdMKy-J-t2sSN0oVFuFzx8QYkeKK0I";
                    nextPage = new NextPage();
                    nextPage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + newLat+ "," + newLng+ "&type=restaurant&rankby=distance&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");
                    break;
                case 4:
                    final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(
                            MainActivityNavigation.this).create();
                    alertDialog.setTitle("Api esaurite");
                    alertDialog.setMessage("Richieste giornaliere esaurite");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.show();
                    break;
                case 6:
                    NextPage2 n=new NextPage2();
                    n.execute( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + newLat + "," + newLng + "&type=restaurant&rankby=distance&key=" + key+"&pagetoken="+next_page_token);
                    break;

            }
        }
    }

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            // We've binded to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(MainActivityNavigation.this); //Activity register in the service as client for callabcks!
            myService.setList(address.size());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    public void updateClient(long millis) {
    }

    @Override
    public void getString(String s) {
        this.s=s;
    }

    @Override
    public void getList(LinkedList<String> a) {

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mHandler = new UIHandler(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_navigation);
        parentActivity = (TabHostActivity) this.getParent();
        create.run();

        parentActivity.mContainerViewS.setVisibility(View.VISIBLE);
        parentActivity.current=1;
        key=parentActivity.key;
        counter=parentActivity.k;

        commentLayout= (ViewGroup) findViewById(R.id.relativeAcc);
        categoriesLayout= (ViewGroup) findViewById(R.id.relativeCat);

        mainView=(ViewGroup) findViewById(R.id.view);
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }
        };

        lv = (ListView) findViewById(R.id.listView);

        for(int l=0; l<address.size(); l++) {
            listBl.add(pref.isPrsent(address.get(l).getId()));
        }

        ph =new PhotoAdapter(getApplicationContext(), R.layout.list, address, listBl);
        lv.setAdapter(ph);
        lv.setDrawingCacheEnabled(true);
        catFrag.gv.setAdapter(pv);

    }

    public void modifyList(PlacesModel p, int pos) {
        address.remove(pos);
        address.add(pos, p);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(myService!=null) {
            r.run();
        }


        /*PhotoAdapter ph=new PhotoAdapter(getApplicationContext(), R.layout.list, address);
        lv.setAdapter(ph);*/

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void moveFragment() {
        cd= (ColorDrawable) mainView.getBackground();
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        commentLayout.setVisibility(View.VISIBLE);
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(300);
        TransitionManager.beginDelayedTransition(commentLayout, autoTransition);
        mContainerViewS = findViewById(R.id.Loginfragment);
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mainView.setBackground(dw);
        oldOnesS = mContainerViewS.getLayoutParams();
        RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mContainerViewS.setLayoutParams(map);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondMove();
            }
        });
        lv.setVisibility(View.GONE);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void moveFragmentCat() {
        categoriesLayout.setVisibility(View.VISIBLE);
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(250);
        TransitionManager.beginDelayedTransition(categoriesLayout, autoTransition);

        RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mContainerViewCat.setLayoutParams(map);
        /*Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 256);*/


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void secondMove() {
        mainView.setOnClickListener(null);
        mainView.setBackground(cd);
        commentLayout.setOnClickListener(null);

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(300);
        TransitionManager.beginDelayedTransition(commentLayout, autoTransition);
        mContainerViewS.setLayoutParams(oldOnesS);
        lv.setVisibility(View.VISIBLE);


        Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                commentLayout.setVisibility(View.INVISIBLE);
                lv.setVisibility(View.VISIBLE);
            }
        }, 350);


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void secondMoveCat() {
        catFrag.gv.setAdapter(null);
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(180);
        TransitionManager.beginDelayedTransition(categoriesLayout, autoTransition);
        mContainerViewCat.setLayoutParams(oldOnesCat);
        parentActivity.increaseCount();
        Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                catFrag.gv.setAdapter(pv);
            }
        }, 350);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public class PhotoAdapterGrid extends ArrayAdapter {
        private LinkedList<PlacesModel> pm;
        private int resource;
        private LayoutInflater inflater;
        int[] array;
        String[] names;

        public PhotoAdapterGrid(Context context, int resource, int[] array, String[] names) {
            super(context, resource);
            this.array=array;
            this.names=names;

        }

        @Override
        public int getCount() {
            return array.length;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.categories_view, parent, false);
            }

            ImageView icon;
            TextView txtType;
            icon = (ImageView) convertView.findViewById(R.id.imageCat);
            txtType = (TextView) convertView.findViewById(R.id.txtCat);

            Picasso.with(this.getContext())
                    .load(array[position])
                    .fit()
                    .into(icon);

            txtType.setText(names[position]);
            txtType.setTextColor(Color.BLACK);
            setScaleAnimation(convertView);
            return convertView;
        }
        private void setScaleAnimation(View view) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(FADE_DURATION);
            view.startAnimation(anim);
        }
    }



    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {


        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return true;


        }

    }



    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {

        if(mStopScroll) {
            motionEvent.setLocation(motionEvent.getX(), mY);
        }

        return super.dispatchTouchEvent(motionEvent);
    }




    public class PhotoAdapter extends ArrayAdapter implements GestureDetector.OnGestureListener{
        private LinkedList<PlacesModel> pm;
        private int resource;
        private LayoutInflater inflater;
        LinkedList<Boolean> listB=new LinkedList<>();


        public PhotoAdapter(Context context, int resource, LinkedList<PlacesModel> objects, LinkedList<Boolean> listB) {
            super(context, resource, objects);
            this.pm=objects;
            this.resource=resource;
            inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            this.listB=listB;

        }

        public void setListB(LinkedList<Boolean> list) {
            listB=list;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list, parent, false);
            }

            final ImageView icon= (ImageView) convertView.findViewById(R.id.photo);;
            Picasso.with(getApplicationContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=250&photoreference="
                            + pm.get(position).getPhotoRef() + "&key=" + key)
                    .fit()
                    .placeholder(R.drawable.images_new)
                    .noFade()
                    .into(icon);
            final TextView txtMss;
            RelativeLayout l=(RelativeLayout)convertView.findViewById(R.id.linearL);
            l.setOnTouchListener(listG.get(position));
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            final TextView txtAdr;
            final TextView txtDist;
            final ToggleButton toggleButton;
            TextView txtO;

            toggleButton = (ToggleButton) convertView.findViewById(R.id.favorite);

            txtMss = (TextView) convertView.findViewById(R.id.txtPuf);
            txtAdr = (TextView) convertView.findViewById(R.id.txtAdr);
            txtDist= (TextView) convertView.findViewById(R.id.txtDist);
            txtMss.setText(pm.get(position).getName());
            txtAdr.setText(pm.get(position).getAddress());
            if(pm.get(position).getDistance()<1000)
                txtDist.setText(String.valueOf(pm.get(position).getDistance())+" metri");
            else
                txtDist.setText(String.valueOf((float)pm.get(position).getDistance()/1000 )+ " kilometri");

            toggleButton.setChecked(listB.get(position));
            if (listB.get(position))
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_star_big_on));
            else
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_star_big_off));

            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listB.get(position) == false) {
                        listB.remove(position);
                        listB.add(position, true);
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_star_big_on));
                        Toast.makeText(getApplicationContext(), pm.get(position).getName() + " aggiunto ai preferiti", Toast.LENGTH_LONG).show();
                        DataBasePref pref = new DataBasePref(getApplicationContext());
                        pref.inserisci(pm.get(position));
                        pref.close();

                    } else {
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_star_big_off));
                        DataBasePref pref = new DataBasePref(getApplicationContext());
                        listB.remove(position);
                        listB.add(position, false);
                        pref.eliminaContatto(pm.get(position).getId());
                        Log.i("cazzo", pm.get(position).getId());
                        pref.close();
                        Toast.makeText(getApplicationContext(), pm.get(position).getName() + " rimosso dai preferiti", Toast.LENGTH_LONG).show();

                    }
                }
            });





            return convertView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }




    class MyGestureDetectorContext extends GestureDetector.SimpleOnGestureListener {
        private int posP;

        public MyGestureDetectorContext(int position) {
            this.posP = position;

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    switchTabInActivity(1);
                }
            } catch (Exception e) {

            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            synchronized (this) {
                if(!clicked) {
                    LatLng l1 = new LatLng(latitude, longitude);
                    Intent i = new Intent(MainActivityNavigation.this, provaDetails.class);
                    Bundle bundle = new Bundle();
                    if (!newL) {
                        bundle.putSerializable("place", address.get(posP));
                        bundle.putSerializable("address", addressS);
                        bundle.putParcelable("l1", l1);
                        bundle.putString("key", key);
                        bundle.putInt("pos", posP);
                        bundle.putBoolean("pref", false);
                        if (myService.isEmpty(posP) == true) {
                            bundle.putBoolean("json", true);
                        } else {
                            bundle.putBoolean("json", false);
                        }
                    } else {
                        bundle.putSerializable("place", newList.get(posP));
                        LinkedSerializable li = new LinkedSerializable(newList);
                        bundle.putBoolean("pref", false);
                        bundle.putSerializable("address", li);
                        bundle.putParcelable("l1", l1);
                        bundle.putString("key", key);
                        bundle.putInt("pos", posP);
                    }
                    i.putExtras(bundle);
                    startActivity(i);
                    clicked=true;
                }
            }

            return super.onSingleTapUp(e);
        }
    }


    public void switchTabInActivity(int indexTabToSwitchTo){
        TabHostActivity parentActivity;
        parentActivity = (TabHostActivity) this.getParent();
        parentActivity.switchTab(indexTabToSwitchTo);
    }

    public class SavePlace extends AsyncTask<PlacesModel, String, Boolean> {



        @Override
        protected Boolean doInBackground(PlacesModel... params) {
            parentActivity.setList(params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);
        }
    }

    public class JsonPars extends AsyncTask<String, String, Boolean> {
        int pos;

        public JsonPars(int position) {
            this.pos=position;
        }

        @Override
        protected Boolean doInBackground(String... params) {
                JSONObject json = null;
                int j=0;
                String jS;
            jS=jp.getJSONFromUrl(params[0]);
            try {
                json = new JSONObject(jS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonparent = null;
            try {
                jsonparent = json.getJSONObject("result");

                        if(jsonparent.has("opening_hours")) {
                            JSONObject jsonOpen = jsonparent.getJSONObject("opening_hours");
                            JSONArray opening = jsonOpen.getJSONArray("weekday_text");
                            for(int i=0; i<opening.length();i++) {
                                address.get(pos).addOpening(opening.getString(i));
                            }
                        }else {
                            address.get(pos).addOpening("Non disponibile");
                        }
                        if(jsonparent.has("formatted_phone_number")) {
                            String s = jsonparent.getString("formatted_phone_number");
                            address.get(pos).setPhone_number(s);
                            Log.i(address.get(pos).getName(), s);
                        }else {
                            address.get(pos).setPhone_number("");
                            Log.i(address.get(pos).getName(), "non disponibile");
                        }

            } catch (JSONException e) {
                e.printStackTrace();
            }




                return true;
            }

        @Override
        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);
        }
    }

    public class JsonParsNew extends AsyncTask<String, String, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {
            JSONObject json = null;
            int j=0;
            try {
                while(j<newList.size()) {
                    json = new JSONObject(newList.get(j).getJsonD());
                    JSONObject jsonparent = json.getJSONObject("result");
                    if(jsonparent.has("opening_hours")) {
                        JSONObject jsonOpen = jsonparent.getJSONObject("opening_hours");
                        JSONArray opening = jsonOpen.getJSONArray("weekday_text");
                        for(int i=0; i<opening.length(); i++) {
                            newList.get(j).addOpening(opening.getString(i));
                        }
                    }else {
                        newList.get(j).addOpening("Non disponibile");
                    }
                    if(jsonparent.has("formatted_phone_number")) {
                        String s = jsonparent.getString("formatted_phone_number");
                        newList.get(j).setPhone_number(s);
                        Log.i(newList.get(j).getName(), s);
                    }else {
                        newList.get(j).setPhone_number("");
                        Log.i(newList.get(j).getName(), "non disponibile");
                    }
                    j++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);
            jsonN=new JsonParsNew();
        }
    }

    public void startSearch(String url) {
        nextPage=new NextPage();
        nextPage.execute(url);
    }

    public class NextPage extends AsyncTask<String, String, Boolean> {

        private JSONObject parentObject;
        private boolean thereis=false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            newList=new LinkedList<>();
            progressDialog = new ProgressDialog(MainActivityNavigation.this);
            progressDialog.setMessage("Caricamento...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpsURLConnection connection = null;
            BufferedReader br = null;
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
                Log.i("url", url.toString());
                String finalJSOn = buffer.toString();
                parentObject = new JSONObject(finalJSOn);
                if (parentObject.has("next_page_token")) {
                    next_page_token = parentObject.getString("next_page_token");
                    thereis=true;
                }
                JSONArray parentArray = parentObject.getJSONArray("results");
                for (int i = 0; i < parentArray.length(); i++) {
                    PlacesModel placesModel = new PlacesModel();
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    if (finalObject.has("photos")) {
                        JSONArray photo = finalObject.getJSONArray("photos");
                        placesModel.setPhotoYes(true);
                        JSONObject photos = photo.getJSONObject(0);
                        placesModel.setPhotoRef(photos.getString("photo_reference"));
                        /*placesModel.setUrl("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                                + placesModel.getPhotoRef() + "&key=AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os");*/
                    }
                    placesModel.setId(finalObject.getString("place_id"));
                    /*placesModel.setJsonD(jPar.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/details/json?" +
                            "placeid=" + placesModel.getId() + "&key=AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os"));*/
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
                    newList.add(placesModel);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (thereis) {
                URL url2 = null;
                try {
                    url2 = new URL(params[0] + "&pagetoken=" + next_page_token);
                    Log.i("test", params[0] + "&pagetoken=" + next_page_token);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection connection2 = null;
                BufferedReader br2 = null;
                Log.i("photo references", "dentro 2");
                LinkedList<PlacesModel> array = new LinkedList<>();
                try {
                    connection2 = (HttpsURLConnection) url2.openConnection();
                    connection2.connect();
                    InputStream stream2 = null;
                    stream2 = connection2.getInputStream();
                    br = new BufferedReader(new InputStreamReader(stream2));
                    StringBuffer buffer = new StringBuffer();
                    String line2;
                    while ((line2 = br.readLine()) != null) {
                        buffer.append(line2);
                    }
                    String finalJSOn = buffer.toString();
                    JSONObject parentObject = new JSONObject(finalJSOn);
                    JSONArray parentArray = parentObject.getJSONArray("results");
                    for (int i = 0; i < parentArray.length(); i++) {
                        PlacesModel placesModel = new PlacesModel();
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        if (finalObject.has("photos")) {
                            JSONArray photo = finalObject.getJSONArray("photos");
                            placesModel.setPhotoYes(true);
                            JSONObject photos = photo.getJSONObject(0);
                            placesModel.setPhotoRef(photos.getString("photo_reference"));
                            /*placesModel.setUrl("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                                    + placesModel.getPhotoRef() + "&key=AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os");*/
                        }
                        placesModel.setId(finalObject.getString("place_id"));
                        /*placesModel.setJsonD(jPar.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/details/json?" +
                                "placeid=" + placesModel.getId() + "&key=AIzaSyBACBeomOgt5eOxMoaVxHTvBe7ya3Dh3os"));*/
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

                        newList.add(placesModel);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


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
            return true;

        }

        @Override
        protected void onPostExecute(Boolean a) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            newL = true;
            progressDialog.cancel();
            super.onPostExecute(a);
            Collections.sort(newList, new Comparator<PlacesModel>() {
                @Override
                public int compare(PlacesModel lhs, PlacesModel rhs) {
                    return Integer.valueOf((int) lhs.getDistance()).compareTo((int) rhs.getDistance());
                }
            });
            listB2 = myService.getListB(newList);
            progressDialog.cancel();
            PhotoAdapter pg =new PhotoAdapter(getApplicationContext(), R.layout.list, newList, listB2);
            lv.setAdapter(pg);
        }
    }

    public void setOld(){
        progressDialog = new ProgressDialog(MainActivityNavigation.this);
        progressDialog.setMessage("Caricamento...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        newL=false;
        Handler handler2=new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(ph);
                progressDialog.cancel();
            }
        }, 250);

    }

   /* public class NextPage extends AsyncTask<String, String, Boolean> {
        private String finalJSOn;
        private JSONObject parentObject;
        private HttpsURLConnection connection;
        private BufferedReader br;
        private double latitudeA;
        private double longitudeA;

        public NextPage(double latitude, double longitude) {
            this.latitudeA=latitude;
            this.longitudeA=longitude;
        }

        public NextPage() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            newList=new LinkedList<>();
            progressDialog = new ProgressDialog(MainActivityNavigation.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }


        @Override
        protected Boolean doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
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

                //try {
                    next_page_token = parentObject.getString("next_page_token");

                /*} catch (JSONException j) {
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
                    }
                }
                try {
                    JSONArray parentArray = parentObject.getJSONArray("results");
                    //connection();
                    int k=0;
                    for (int i = 0; i < parentArray.length(); i++) {
                        Log.i("Thread 1", "dentro 1");
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
                                "placeid=" + placesModel.getId() + "&key=" + key));//AIzaSyDoykVxAjvUNzmZhxjQJRK0Kfl6TCuZSxs");
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
                        newList.add(placesModel);
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
            mHandler.sendEmptyMessage(6);
            //startSecond("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
            //        + latitudeA + "," + longitudeA + "&type=restaurant&rankby=distance&key=" + key + "&pagetoken=" + next_page_token);

        }
    }*/





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
                Log.i("Thread 2", params[0]);
                url2 = new URL(params[0]); //+ "&pagetoken=" + next_page_token);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            connection = null;
            br = null;
            LinkedList<PlacesModel> array = new LinkedList<>();
            int k=0;
            try {
                connection = (HttpsURLConnection) url2.openConnection();
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
                SystemClock.sleep(500);
                try {
                    JSONArray parentArray = parentObject.getJSONArray("results");
                    //connection();
                    for (int i = 0; i < parentArray.length(); i++) {
                        k++;
                        Log.i("Thread 2", "dentro 2");
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
                        newList.add(placesModel);
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
                //if(k>10) {
                    next_next = parentObject.getString("next_page_token");
                    startThird("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + newLat+ "," + newLng+ "&type=restaurant&rankby=distance&key="+key+"&pagetoken="+next_next);
                    return true;

               /*}else {
                    this.cancel(true);
                    startSecond("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + newLat+ "," + newLng+ "&type=restaurant&rankby=distance&key="+key+"&pagetoken="+next_page_token);
                }*/

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
            mHandler.sendEmptyMessage(6);



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

                url2 = new URL(params[0]); //+ "&pagetoken=" + next_page_token);
                Log.i("Thread 3", url2.toString());
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
                        Log.i("Thread 3", "dentro 2");
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
                        newList.add(placesModel);
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
                if(k>5) {
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
            Collections.sort(newList, new Comparator<PlacesModel>() {
                @Override
                public int compare(PlacesModel lhs, PlacesModel rhs) {
                    return Integer.valueOf((int) lhs.getDistance()).compareTo((int) rhs.getDistance());
                }
            });
            listBl = myService.getListB(newList);
            progressDialog.cancel();
            PhotoAdapter pg =new PhotoAdapter(getApplicationContext(), R.layout.list, newList, listBl);
            lv.setAdapter(pg);



        }
    }

    private class CheckLog extends AsyncTask<Void, Void, Void> {

        private String user;
        private String pass;
        private int check;

        public CheckLog(String us, String pass) {
            this.user=us;
            this.pass=pass;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                check=myService.checkLog(user, pass);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(check==-1) {
                loginFragment.tUser.setError("Username non presente nell'archivio");
            } else if(check==-2) {
                loginFragment.tPas.setError("Password non corretta");
            }
            else{
                loginFragment.callPopUp();
                parentActivity.navTitle.setTitle("Gestisci account");
                String[] q = null;
                try {
                    q=myService.getUrlP(myService.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myService.resetId();
                Picasso.with(getApplicationContext())
                        .load(q[0])
                        .fit()
                        .into(parentActivity.imAcc);
                parentActivity.txAcc.setText(q[1]);
            }

        }

    }

        private void startThird(String next_next) {
            NextPage3 n=new NextPage3();
            n.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, next_next);
        }


        public void startSecond(String page)  {
            NextPage2 n=new NextPage2();
            n.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + newLat + "," + newLng + "&type=restaurant&rankby=distance&key=" + key + "&pagetoken=" + next_page_token);
        }
}









