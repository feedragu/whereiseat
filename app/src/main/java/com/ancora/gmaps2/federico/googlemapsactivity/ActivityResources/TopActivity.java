package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class TopActivity extends AppCompatActivity implements Serializable, LoginFrag.OnFragmentInteractionListener, MyService.Callbacks {
    private TabHostActivity parentActivity;
    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_MAX_OFF_PATH = 300;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    public LinkedList<PlacesModel> addressTop;
    private double latitude;
    private double longitude;
    private String next_page_token;
    private JSONParser jPar=new JSONParser();
    private LinkedSerializable addressS;
    private boolean newL=false;
    private LinkedList<PlacesModel> newList;
    private ListView lv;
    private RatingBar rateBarL;
    private String key;
    LinkedList<PlacesModel> rate=new LinkedList<>();
    private View mContainerViewS;
    ViewGroup.LayoutParams oldOnesS;
    private ViewGroup commentLayout;
    private View mainView;
    private MyService myService;
    private  Intent i;
    private Runnable create=new Runnable() {
        @Override
        public void run() {
            Intent serviceIntent = new Intent(TopActivity.this, MyService.class);
            getApplicationContext().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

            addressS = (LinkedSerializable) i.getExtras().getSerializable("address3");
            addressTop = addressS.getL();
            key=parentActivity.key;


            loginFragment = (LoginFrag) getSupportFragmentManager().findFragmentById(R.id.Loginfragment);
            loginFragment.changeActivity();

            Collections.sort(addressTop, new Comparator<PlacesModel>() {
                @Override
                public int compare(PlacesModel lhs, PlacesModel rhs) {
                    return Float.valueOf(rhs.getRating()).compareTo(lhs.getRating());
                }
            });
            for(int j=0; j<10; j++) {
                rate.add(addressTop.get(j));
            }
            latitude=i.getExtras().getDouble("latitude");
            longitude=i.getExtras().getDouble("longitude");
        }
    };
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            // We've binded to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(TopActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };
    private LinkedList<Boolean> listBl=new LinkedList<>();
    private DataBasePref pref=new DataBasePref(this);
    private boolean clicked;
    private LoginFrag loginFragment;
    private ColorDrawable cd;
    private PhotoAdapter ph;
    private boolean conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i=getIntent();
        conn=i.getExtras().getBoolean("conn");
        if(conn==true) {
            setContentView(R.layout.activity_top);
            parentActivity = (TabHostActivity) this.getParent();
            create.run();
            commentLayout = (ViewGroup) findViewById(R.id.relativeAcc);
            mainView = (ViewGroup) findViewById(R.id.view);

            lv = (ListView) findViewById(R.id.listRat);
            for (int l = 0; l < addressTop.size(); l++) {
                listBl.add(pref.isPrsent(addressTop.get(l).getId()));
            }


            ph = new PhotoAdapter(getApplicationContext(), R.layout.list_rate, rate, listBl);
            lv.setAdapter(ph);
        }else {
            setContentView(R.layout.no_conn);
        }
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
        }, 301);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(conn==true) {
            clicked = false;
            if (myService != null) {
                listBl = myService.getListB(addressTop);
            }
            ph.setListB(listBl);
            ph.notifyDataSetChanged();
        }

    }

    public void switchTabInActivity(int indexTabToSwitchTo){
        TabHostActivity parentActivity;
        parentActivity = (TabHostActivity) this.getParent();
        parentActivity.switchTab(indexTabToSwitchTo);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    public void checkLog(String s, String s1) {
        if(myService.isConn()) {
            CheckLog cl = new CheckLog(s, s1);
            cl.execute();
        }else {
            Toast.makeText(getApplicationContext(), "Server attualmente non disponibile", Toast.LENGTH_LONG).show();
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
            }
        }

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


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_rate, parent, false);
            }
            final GestureDetector gestureDetectorn = new GestureDetector(getApplicationContext(), new MyGestureDetectorContext(position));
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetectorn.onTouchEvent(event);
                }
            };

            final ImageView icon;
            TextView txtMss;
            RelativeLayout l=(RelativeLayout)convertView.findViewById(R.id.linearL);
            l.setOnTouchListener(gestureListener);
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }

            });


            TextView txtAdr;
            TextView txtDist;
            TextView txtO;
            RatingBar rateBar;
            final ToggleButton toggleButton;

            icon= (ImageView) convertView.findViewById(R.id.photo);
            Picasso.with(getApplicationContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=250&photoreference="
                            + pm.get(position).getPhotoRef() + "&key=" + key)
                    .fit()
                    .placeholder(R.drawable.images_new)
                    .noFade()
                    .into(icon);

            toggleButton = (ToggleButton) convertView.findViewById(R.id.favorite);
            toggleButton.setChecked(this.listB.get(position));
            if (this.listB.get(position))
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_star_big_on));
            else
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_star_big_off));
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listB.get(position) == false) {
                        // pref.inserisci(pm.get(position));
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

            rateBarL=(RatingBar)convertView.findViewById(R.id.rateBarTop);
            txtMss = (TextView) convertView.findViewById(R.id.txtPuf);
            txtAdr = (TextView) convertView.findViewById(R.id.txtAdr);
            txtDist= (TextView) convertView.findViewById(R.id.txtDist);
            txtMss.setText(pm.get(position).getName());
            txtAdr.setText(pm.get(position).getAddress());
            rateBarL.setRating(pm.get(position).getRating());
            if(pm.get(position).getDistance()<1000)
                txtDist.setText(String.valueOf(pm.get(position).getDistance())+" metri");
            else
                txtDist.setText(String.valueOf(pm.get(position).getDistance()/1000 )+","+String.valueOf(pm.get(position).getDistance()).substring(0) + " kilometri");

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

        public void setListB(LinkedList<Boolean> listB) {
            this.listB = listB;
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
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        switchTabInActivity(2);
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        switchTabInActivity(0);
                    }
                } catch (Exception e) {

                }
                return false;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                synchronized (this) {
                    if (!clicked) {
                        LatLng l1 = new LatLng(latitude, longitude);
                        boolean path = false;

                        Intent i = new Intent(TopActivity.this, DetailsComment.class);
                        Bundle bundle = new Bundle();


                        if (!newL) {
                            bundle.putSerializable("place", rate.get(posP));
                            bundle.putSerializable("address", addressS);
                            bundle.putParcelable("l1", l1);
                            bundle.putString("key", key);
                            bundle.putInt("pos", posP);
                            bundle.putBoolean("pref", false);
                            bundle.putBoolean("json", false);

                        } else {
                            bundle.putSerializable("place", newList.get(posP));
                            LinkedSerializable li = new LinkedSerializable(newList);
                            bundle.putSerializable("address", li);
                            bundle.putBoolean("pref", false);
                            bundle.putParcelable("l1", l1);
                            bundle.putString("key", key);
                            bundle.putInt("pos", posP);
                        }
                        i.putExtras(bundle);
                        startActivity(i);
                        clicked = true;
                    }
                }

               /* LatLng l1 = new LatLng(latitude, longitude);
                boolean path = false;

                Intent i = new Intent(TopActivity.this, DetailsComment.class);
                Bundle bundle = new Bundle();

                if (!newL) {
                    bundle.putSerializable("place", addressTop.get(posP));
                    bundle.putSerializable("address", addressS);
                    bundle.putParcelable("l1", l1);
                    bundle.putString("key", key);
                    bundle.putInt("pos", posP);
                    if (myService.isEmpty(posP) == true) {
                        bundle.putBoolean("json", true);
                    } else {
                        bundle.putBoolean("json", false);
                    }
                } else {
                    bundle.putSerializable("place", newList.get(posP));
                    LinkedSerializable li = new LinkedSerializable(newList);
                    bundle.putSerializable("address", li);
                    bundle.putParcelable("l1", l1);
                    bundle.putString("key", key);
                    bundle.putInt("pos", posP);
                }
                i.putExtras(bundle);
                startActivity(i);
*/
                return super.onSingleTapUp(e);
            }
        }


    }


}


