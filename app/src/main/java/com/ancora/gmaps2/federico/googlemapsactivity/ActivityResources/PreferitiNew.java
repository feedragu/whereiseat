package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.DataBasePref;
import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializable;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

public class PreferitiNew extends AppCompatActivity implements Serializable, GestureDetector.OnGestureListener, LoginFrag.OnFragmentInteractionListener, MyService.Callbacks{


    public LinkedList<PlacesModel> address = new LinkedList<>();
    public LinkedList<PlacesModel> addressL = new LinkedList<>();
    public double latitude;
    public double longitude;
    private LatLng coord;
    private LinkedSerializable addressS;
    private int k=0;
    public GridView lv;
    private TextView txtNome;
    private GestureDetector gd;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private TabHostActivity parentActivity;
    private Button btnOrdine;
    private int l=0;
    private View mContainerViewS;
    ViewGroup.LayoutParams oldOnesS;
    private ViewGroup commentLayout;
    private View mainView;
    private LoginFrag loginFragment;
    private ColorDrawable cd;
    private MyService myService;
    private Runnable r= new Runnable() {
        @Override
        public void run() {
            parentActivity.mContainerViewS.setVisibility(View.INVISIBLE);
            parentActivity.current=3;
            DataBasePref pref = new DataBasePref(PreferitiNew.this);
            address=pref.caricaLista();
            pref.close();
            PhotoAdapter ph = new PhotoAdapter(PreferitiNew.this, R.layout.list_grid, address);
            lv.setAdapter(ph);
        }
    };
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService=binder.getServiceInstance();
            myService.registerClient(PreferitiNew.this);



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(PreferitiNew.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };
    private boolean newL=false;
    private String key;
    private LinkedList<PlacesModel> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferiti);
        Intent i=getIntent();
        DataBasePref pref = new DataBasePref(PreferitiNew.this);
        address=pref.caricaLista();
        pref.close();
        addressS = (LinkedSerializable) i.getExtras().getSerializable("address2");
        addressL = addressS.getL();
        latitude=i.getExtras().getDouble("latitude");
        longitude=i.getExtras().getDouble("longitude");
        FrameLayout fm=(FrameLayout) findViewById(R.id.framePref);
        parentActivity = (TabHostActivity) this.getParent();
        parentActivity.current=3;
        key=parentActivity.key;
        commentLayout= (ViewGroup) findViewById(R.id.relativeAcc);

        loginFragment = (LoginFrag) getSupportFragmentManager().findFragmentById(R.id.Loginfragment);
        loginFragment.changeActivity();

        mainView=(ViewGroup) findViewById(R.id.view);

        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        parentActivity.mContainerViewS.setVisibility(View.INVISIBLE);
        lv = (GridView) findViewById(R.id.gridView);
        lv.setEmptyView(findViewById(android.R.id.empty));
        ViewGroup vg= (ViewGroup) findViewById(android.R.id.empty);
        vg.setOnTouchListener(gestureListener);
        PhotoAdapter ph =new PhotoAdapter(getApplicationContext(), R.layout.list_grid, address);
        lv.setAdapter(ph);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(PreferitiNew.this, MyService.class);
                getApplicationContext().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onResume() {
        super.onResume();
        r.run();

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void checkLog(String s, String s1) {
        CheckLog cl=new CheckLog(s, s1);
        cl.execute();
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
        public PhotoAdapter(Context context, int resource, LinkedList<PlacesModel> addr) {
            super(context, resource, addr);
            this.pm=addr;
            this.resource=resource;
            inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=inflater.inflate(resource, null);
            }

            final GestureDetector gestureDetectorn = new GestureDetector(getContext(), new MyGestureDetectorContext(position));
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetectorn.onTouchEvent(event);
                }
            };



            ImageView icon;
            TextView txtMss;
            TextView txtDist;
            RelativeLayout fm;
            fm=(RelativeLayout)convertView.findViewById(R.id.frameLG);
            fm.setOnTouchListener(gestureListener);
            fm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            icon= (ImageView) convertView.findViewById(R.id.photo);
            txtMss=(TextView) convertView.findViewById(R.id.txtNomG);
            txtDist=(TextView) convertView.findViewById(R.id.txtDistance);
            txtMss.setText(pm.get(position).getName());

            if(!pm.get(position).getPhotoRef().equals("null")) {
                    Picasso.with(this.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                            + pm.get(position).getPhotoRef() + "&key=" + parentActivity.key)
                    .resize(400, 400)
                    .into(icon);


            }
            else {
                Picasso.with(this.getContext())
                        .load(R.drawable.noimg)
                        .fit()
                        .into(icon);
            }

            Location loc1 = new Location("");
            loc1.setLatitude(latitude);
            loc1.setLongitude(longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(pm.get(position).getLat()));
            loc2.setLongitude(Double.parseDouble(pm.get(position).getLng()));
            int distanceInMeters = Math.round(loc1.distanceTo(loc2));
            txtDist.setText(distanceInMeters+" metri");
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

    public void showPopup(View button) {
        PopupMenu popup=new PopupMenu(this, button);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DataBasePref pref = new DataBasePref(PreferitiNew.this);
                address=pref.caricaListaN();
                pref.close();
                PhotoAdapter ph = new PhotoAdapter(PreferitiNew.this, R.layout.list_grid, address);
                lv.setAdapter(ph);
                return false;
            }
        });
        MenuInflater inflater=popup.getMenuInflater();
        inflater.inflate(R.menu.menu_ordine, popup.getMenu());
        popup.show();
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




    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    switchTabInActivity(2);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }


    class MyGestureDetectorContext extends GestureDetector.SimpleOnGestureListener {
        private int posP;

        public MyGestureDetectorContext(int position) {
            this.posP=position;

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    switchTabInActivity(1);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            /*Toast.makeText(getApplicationContext(), "premuto oggetto num: "+posP, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(PreferitiNew.this);
            dialog.setMessage("Do you want to delete "+address.get(posP).getName());
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    DataBasePref pref = new DataBasePref(PreferitiNew.this);
                    pref.eliminaContatto(address.get(posP).getPlaceId());
                    address = pref.caricaLista();
                    pref.close();
                    PhotoAdapter ph = new PhotoAdapter(getApplicationContext(), R.layout.list_grid, address);
                    lv.setAdapter(ph);

                }
            });
            dialog.setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();*/

            LatLng l1 = new LatLng(latitude, longitude);
            Intent i = new Intent(PreferitiNew.this, provaDetails.class);
            Bundle bundle = new Bundle();
            if (!newL) {
                bundle.putSerializable("place", address.get(posP));
                addressS=new LinkedSerializable(addressL);
                bundle.putSerializable("address", addressS);
                bundle.putParcelable("l1", l1);
                bundle.putString("key", key);
                bundle.putInt("pos", posP);
                bundle.putBoolean("pref", false);
                bundle.putBoolean("json", false);

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

            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(getApplicationContext(), "premuto oggetto num: "+posP, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(PreferitiNew.this);
            dialog.setMessage("Do you want to delete "+address.get(posP).getName());
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    DataBasePref pref = new DataBasePref(PreferitiNew.this);
                    pref.eliminaContatto(address.get(posP).getId());
                    address = pref.caricaLista();
                    pref.close();
                    PhotoAdapter ph = new PhotoAdapter(getApplicationContext(), R.layout.list_grid, address);
                    lv.setAdapter(ph);

                }
            });
            dialog.setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
            super.onLongPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
    public void switchTabInActivity(int indexTabToSwitchTo) {
        TabHostActivity parentActivity;
        parentActivity = (TabHostActivity) this.getParent();
        parentActivity.switchTab(indexTabToSwitchTo);
    }


}
