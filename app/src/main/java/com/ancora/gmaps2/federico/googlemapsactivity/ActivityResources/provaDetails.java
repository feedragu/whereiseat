package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.CommentsAdapter;
import com.ancora.gmaps2.federico.googlemapsactivity.models.JSONParser;
import com.ancora.gmaps2.federico.googlemapsactivity.models.LinkedSerializable;
import com.ancora.gmaps2.federico.googlemapsactivity.models.Path;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PhotoReferences;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class provaDetails extends AppCompatActivity implements MyService.Callbacks{
    public LinkedList<PlacesModel> address;
    public Socket client;
    public TextView comment;
    public String commento;
    public int pos;
    public LinkedList<String> a = new LinkedList<>();
    public LinkedList<String> u = new LinkedList<>();
    public String place_id;
    private View fragContainer;
    private CommentFragment frag;
    private CommentsAdapter ph;
    private ImageView im;
    private String message;
    private TextView name, adr, id, mo, tu, wen, thur, frid, sat, sun;
    private ViewGroup commentLayout;
    private int k = 0;
    public Button rate;
    private RatingBar rateB;
    public MainActivityNavigation parent;
    private MyService myService;
    public SendMessage s = new SendMessage();
    // public SetComments sc = new SetComments();
    public float rateBar = 0f;
    public Toolbar toolbar;
    public TextView txt;
    private LatLng l1;
    //private ConnectAsyncTask c;
    private Button btnMap;
    private JSONParser jp = new JSONParser();
    private MainActivityNavigation parentActivity;
    private JsonPars json;
    public String key;
    private PlacesModel pl;
    private boolean jsonS;
    //private Boh b=new Boh();
    boolean pref;
    private int j = 0;
    public ProgressDialog progressDialog;
    public LinkedList<String> listP = new LinkedList<>();


    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance();
            myService.registerClient(provaDetails.this);
            conn = myService.isConn();
           /* if(conn)
                sc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(provaDetails.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };
    private LinkedSerializable l;
    private LinkedList<byte[]> photos;
    private PlacesModel p;
    private TextView web;
    private String jS;
    private boolean conn;
    private ConnectAsyncTask c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_comment);
        Log.i("line", "pallavolo inizio");
        parentActivity = (MainActivityNavigation) this.getParent();
        Intent i = getIntent();
        p = (PlacesModel) i.getExtras().getSerializable("place");
        key = i.getExtras().getString("key");
        l = (LinkedSerializable) i.getExtras().getSerializable("address");
        pos = i.getExtras().getInt("pos");
        pref = i.getExtras().getBoolean("pref");
        jsonS = i.getExtras().getBoolean("json");
        address = l.getL();
        binding();
        if(jsonS==false) {
            json=new JsonPars(pos);
            json.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/details/json?" +
                    "placeid=" + p.getId() +"&language=it&key=" + key);
        }
        l1 = i.getExtras().getParcelable("l1");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(" Dettagli Ristorante");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_order);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });
        exec();


    }

    private void caricaView(final PlacesModel a) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            id = (TextView) findViewById(R.id.txtPlace);
                            ImageView v = (ImageView) findViewById(R.id.imgGallery);
                            web = (TextView) findViewById(R.id.webSite);
                            mo = (TextView) findViewById(R.id.txtM);
                            tu = (TextView) findViewById(R.id.txtTu);
                            wen = (TextView) findViewById(R.id.txtW);
                            thur = (TextView) findViewById(R.id.txtTh);
                            frid = (TextView) findViewById(R.id.txtF);
                            sat = (TextView) findViewById(R.id.txtS);
                            sun = (TextView) findViewById(R.id.txtSu);
                            if (!a.getOpening().get(0).equals("Non disponibile")) {
                                mo.setText(a.getOpening().get(0));
                                tu.setText(a.getOpening().get(1));
                                wen.setText(a.getOpening().get(2));
                                thur.setText(a.getOpening().get(3));
                                frid.setText(a.getOpening().get(4));
                                sat.setText(a.getOpening().get(5));
                                sun.setText(a.getOpening().get(6));
                            } else {
                                wen.setText("Orari non disponibili");
                            }
                            if (!a.getPhone_number().equals(""))
                                id.setText(a.getPhone_number());
                            else
                                id.setText("numero non disponibile");
                            if (!a.getWebsite().equals("")) {
                                web.setText(a.getWebsite());
                            } else {
                                web.setText("Sito web non disponibile ");
                            }
                            final LinkedList<String> list = a.getList();
                            if (list.size() > 0) {
                                Picasso.with(getApplicationContext())
                                        .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="
                                                + list.get(1) + "&key=" + key)
                                        .placeholder(R.drawable.images_new)
                                        .noFade()
                                        .into(v);
                            } else {
                                Picasso.with(getApplicationContext())
                                        .load(R.drawable.noimm)
                                        .noFade()
                                        .into(v);
                            }
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PhotoReferences p = new PhotoReferences(list);
                                    Intent i = new Intent(provaDetails.this, FullScreenViewActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("photoRef", p);
                                    bundle.putString("key", key);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                }
                            });
                            Log.i("line", "pallavolo 5");
                        }
                    });

                }

            }
        };
        thread.start();

    }

    @Override
    protected void onResume() {
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(" Dettagli Ristorante");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        Log.i("line", "pallavolo 6");
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        super.onResume();
    }


        @Override
        protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
        }

        public void binding () {
            Intent intent = new Intent(provaDetails.this, MyService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }


        /*public void onAnswer(String s) {
            WriteComment rd=new WriteComment();
            message=s;
            rd.execute();
        }
*/
        @Override
        public void updateClient(long data) {
        }

        @Override
        public void getString(String s) {

        }

        @Override
        public void getList(LinkedList<String> a) {
            Toast.makeText(provaDetails.this, "getList", Toast.LENGTH_SHORT).show();
        }

        public void caricaLista() {
            try {
                rateBar=myService.getRateP(place_id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            rateB=(RatingBar) findViewById(R.id.rBar);
            rateB.setRating(rateBar);
            Log.i("line", "pallavolo 7");

        }

    public void exec() {
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {


            im = (ImageView) findViewById(R.id.imageIm);
            name = (TextView) findViewById(R.id.txtName);
            adr = (TextView) findViewById(R.id.txtAddr);

            frag = (CommentFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
            commentLayout = (ViewGroup) findViewById(R.id.relativeC);

            if (p.getId() != null) {
                place_id = p.getId();
            } else {
                place_id = p.getLat();
            }

            Log.i("line", "pallavolo3");

            fragContainer = findViewById(R.id.relative);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           /*txt=(TextView) findViewById(R.id.txtEnd);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    secondMove();
                }
            });*/
            name.setText(p.getName());
            adr.setText(p.getAddress());
            if (p.getPhotoRef() != null) {
                Picasso.with(getApplicationContext())
                        .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
                                + p.getPhotoRef() + "&key=" + key)
                        .fit()
                        .into(im);
            } else {
                Picasso.with(getApplicationContext())
                        .load(R.drawable.noimg)
                        .fit()
                        .into(im);
            }
            if (jsonS == true) {
                caricaView(myService.getJson(pos));
            }
            btnMap = (Button) findViewById(R.id.btnMap);
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(btnMap);
                }
            });

            rate = (Button) findViewById(R.id.btnRate);
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (conn == true) {
                        if (rateB.getRating() < 0.5) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(
                                    provaDetails.this).create();
                            alertDialog.setTitle("Voto non valido");
                            alertDialog.setMessage("Inserire almeno mezza stella di voto prima di inviarlo");
                            alertDialog.setIcon(R.mipmap.ic_launcher);
                            alertDialog.show();
                        } else {

                            /*Rate rd = new Rate();
                            rd.execute();*/

                        }
                    } else {
                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                provaDetails.this).create();
                        alertDialog.setTitle("Server non disponibile");
                        alertDialog.setMessage("Server al momento non raggiungibile");
                        alertDialog.setIcon(R.mipmap.ic_launcher);
                        alertDialog.show();
                    }

                }
            });
            // frag.gui();

        }


    }
/*
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void moveFrag(View v) {

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(250);
        TransitionManager.beginDelayedTransition(commentLayout, autoTransition);

        RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        fragContainer.setLayoutParams(map);
        txt.setVisibility(View.VISIBLE);
        k++;
        Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                frag.lv.setAdapter(ph);
                if (!a.isEmpty()) {
                    frag.lv.setAdapter(ph);
                } else {
                    frag.visible = false;
                }
            }
        }, 270);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void secondMove() {
        frag.lv.setAdapter(null);
        TransitionManager.beginDelayedTransition(commentLayout);
        txt.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(0,
                0);
        fragContainer.setLayoutParams(map);

        k=0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            unbindService(mConnection);
            myService.removeClient();
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public class PhotoAdapter extends ArrayAdapter implements View.OnClickListener {
        private LinkedList<String> pm, us;
        private int resource;
        private LayoutInflater inflater;

        public PhotoAdapter(Context context, int resource, LinkedList<String> objects, LinkedList<String> u) {
            super(context, resource, objects);
            this.pm = objects;
            this.us=u;
            this.resource = resource;
            inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }
            ImageView icon=(ImageView)convertView.findViewById(R.id.imageProf);
            TextView v = (TextView) convertView.findViewById(R.id.txtC);
            TextView us=(TextView) convertView.findViewById(R.id.txtUs);
            v.setText(pm.get(position));
            us.setText(u.get(position));
            Picasso.with(this.getContext())
                    .load(listP.get(position))
                    .transform(new CircleTransform())
                    .fit()
                    .into(icon);

            return convertView;
        }

        @Override
        public void onClick(View v) {

        }
    }
*/
    public void startMap(View v ) {
        c = new ConnectAsyncTask(makeURLCar(l1.latitude, l1.longitude, Double.parseDouble(p.getLat()),
                Double.parseDouble(p.getLng())));
        c.execute();
    }
/*
    private class WriteComment extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AccountUser ac=new AccountUser(provaDetails.this);

            Object[] ob= new Object[0];
            try {
                ob = myService.writeComment(message, 0.0, place_id, a, ac.getCodice());
            } catch (IOException e) {
                e.printStackTrace();
            }
            a= (LinkedList<String>) ob[0];
            u= (LinkedList<String>) ob[1];


        }


        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int k=myService.getSize();
            listP.clear();
            listP=myService.getPhoto();
            ph.add(a.getFirst(), u.getFirst(), listP, 0);
            Log.i("", a.getFirst());
            frag.checkVis(listP.isEmpty());
        }

    }

    private class SetComments extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Object[] ob=myService.listC(p.getId());
                a= (LinkedList<String>) ob[0];
                u=(LinkedList<String>) ob[1];
                listP=myService.getPhoto();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(jsonS==true) {
                caricaView(myService.getJson(pos));
            }
            //frag.attachAdapter(getApplicationContext(), R.layout.listcomment, a, u, listP);
            Log.i("prova", "dai cazzo");
            ph = new CommentsAdapter(getApplicationContext(), R.layout.listcomment, a, u, listP);



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            caricaLista();
            frag.checkVis(a.isEmpty());
        }

    }

    private class Rate extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myService.voteRest(rateB.getRating(),  place_id);

        }


        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            final Dialog dialog1 = new Dialog(provaDetails.this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setTitle("Grazie per il voto");
            dialog1.setContentView(R.layout.custom_alert);

            Button yes = (Button) dialog1.findViewById(R.id.btn_ok);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.cancel();
                    rateB.setEnabled(false);
                }
            });

            dialog1.show();
        }

    }

    private class Boh extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //frag.lv.setVisibility(View.VISIBLE);

        }

    }


*/


    public String makeURLWalk (double sourcelat, double sourcelog, double destlat, double destlog ){
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
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        urlString.append("&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");
        return urlString.toString();
    }

    public String makeURLTransit (double sourcelat, double sourcelog, double destlat, double destlog ){
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
        urlString.append("&sensor=false&mode=transit&alternatives=true");
        urlString.append("&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");
        return urlString.toString();
    }




    public String makeURLCar (double sourcelat, double sourcelog, double destlat, double destlog ){
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
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyDOAZPG4RH9okTaGiqRATtC2gU3GxX9wZ8");
        return urlString.toString();
    }


    private class ConnectAsyncTask extends AsyncTask<Void, Void, Bundle> {

        String url;
        ConnectAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(provaDetails.this);
            progressDialog.setMessage("Caricamento indicazioni stradali...");
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
            LatLng l2=new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLng()));
            Path jso=new Path(json);
            bundle.putSerializable("drawPath", jso);
            bundle.putParcelable("LatLong", coord);
            bundle.putParcelable("LatLong2", l2);
            bundle.putString("key", key);
            bundle.putBoolean("path", path);
            bundle.putSerializable("address", l);
            return bundle;
        }
        @Override
        protected void onPostExecute(Bundle result) {
            super.onPostExecute(result);
            load(result);


        }
    }

    private void load(Bundle result) {
        Intent i = new Intent(provaDetails.this, GoogleActivityNavigation.class);
        i.putExtras(result);
        startActivity(i);
        finish();
    }

    public void showPopup(View button) {
        PopupMenu popup=new PopupMenu(provaDetails.this, button);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("A piedi")) {
                    c = new ConnectAsyncTask(makeURLWalk(l1.latitude, l1.longitude, Double.parseDouble(p.getLat()),
                            Double.parseDouble(p.getLng())));
                    c.execute();
                }else if(item.getTitle().equals("Auto")) {
                    c = new ConnectAsyncTask(makeURLCar(l1.latitude, l1.longitude, Double.parseDouble(p.getLat()),
                            Double.parseDouble(p.getLng())));
                    c.execute();
                }
                else if(item.getTitle().equals("Mezzi pubblici")) {
                    c = new ConnectAsyncTask(makeURLTransit(l1.latitude, l1.longitude, Double.parseDouble(p.getLat()),
                            Double.parseDouble(p.getLng())));
                    c.execute();
                }
                return true;
            }
        });
        MenuInflater inflater=popup.getMenuInflater();
        inflater.inflate(R.menu.menu_mezzo, popup.getMenu());
        popup.show();
    }

    public class JsonPars extends AsyncTask<String, String, PlacesModel> {
        int pos;
        LinkedList<String> list=new LinkedList<>();
        PlacesModel pl=p;

        public JsonPars(int position) {
            this.pos=position;
        }

        @Override
        protected PlacesModel doInBackground(String... params) {

            JSONObject json = null;
            int j=0;

            jS=jp.getJSONFromUrl(params[0]);

            try {
                json = new JSONObject(jS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonparent = null;
            try {
                jsonparent = json.getJSONObject("result");
                if(jsonparent.has("website")) {
                    pl.setWebsite(jsonparent.getString("website"));
                }else {
                    pl.setWebsite("");
                }

                if(jsonparent.has("opening_hours")) {
                    JSONObject jsonOpen = jsonparent.getJSONObject("opening_hours");
                    JSONArray opening = jsonOpen.getJSONArray("weekday_text");
                    for(int i=0; i<opening.length(); i++) {
                        pl.addOpening(opening.getString(i));
                    }
                }else {
                    pl.addOpening("Non disponibile");
                }
                if(jsonparent.has("formatted_phone_number")) {
                    String s = jsonparent.getString("formatted_phone_number");
                    pl.setPhone_number(s);
                    Log.i(pl.getName(), s);
                }else {
                    pl.setPhone_number("");
                    Log.i(pl.getName(), "non disponibile");
                }
                if(jsonparent.has("photos")) {
                    JSONArray photo = jsonparent.getJSONArray("photos");
                    for (int h = 0; h < photo.length(); h++) {
                        JSONObject boh = (JSONObject) photo.getJSONObject(h);
                        list.add(boh.getString("photo_reference"));
                    }
                    pl.setList(list);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return pl;
        }

        @Override
        protected void onPostExecute(PlacesModel a) {
            super.onPostExecute(a);
            if(pref==false) {
                myService.setJson(a, pos);
            }
            id=(TextView)findViewById(R.id.txtPlace);
            ImageView v=(ImageView) findViewById(R.id.imgGallery);
            web=(TextView) findViewById(R.id.webSite);
            mo=(TextView)findViewById(R.id.txtM);
            tu=(TextView)findViewById(R.id.txtTu);
            wen=(TextView)findViewById(R.id.txtW);
            thur=(TextView)findViewById(R.id.txtTh);
            frid =(TextView)findViewById(R.id.txtF);
            sat  =(TextView)findViewById(R.id.txtS);
            sun=(TextView) findViewById(R.id.txtSu);
            //parentActivity.modifyList(p, pos);
            if(!a.getOpening().get(0).equals("Non disponibile")) {
                mo.setText(a.getOpening().get(0));
                tu.setText(a.getOpening().get(1));
                wen.setText(a.getOpening().get(2));
                thur.setText(a.getOpening().get(3));
                frid.setText(a.getOpening().get(4));
                sat.setText(a.getOpening().get(5));
                sun.setText(a.getOpening().get(6));
            } else {
                wen.setText("Orari non disponibili");
            }
            if (!a.getPhone_number().equals(""))
                id.setText(a.getPhone_number());
            else
                id.setText("numero non disponibile");
            if(!a.getWebsite().equals("")) {
                web.setText(a.getWebsite());
            }
            else {
                web.setText("Sito web non disponibile ");
            }

            if(list.size()>0) {
                Log.i("prova", String.valueOf(list.size()));
                if(list.size()>1) {
                    Picasso.with(getApplicationContext())
                            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="
                                    + list.get(1) + "&key=" + key)
                            .placeholder(R.drawable.images_new)
                            .noFade()
                            .into(v);
                }else {
                    Picasso.with(getApplicationContext())
                            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="
                                    + list.get(0) + "&key=" + key)
                            .placeholder(R.drawable.images_new)
                            .noFade()
                            .into(v);
                }
            }
            else {
                Picasso.with(getApplicationContext())
                        .load(R.drawable.noimm)
                        .noFade()
                        .into(v);
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoReferences p=new PhotoReferences(list);
                    Intent i=new Intent(provaDetails.this, FullScreenViewActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("photoRef", p);
                    bundle.putString("key", key);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
        }

    }

    /*

    public static byte[] compress(byte[] content){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        //System.out.printf("Compressiono %f\n", (1.0f * content.length/byteArrayOutputStream.size()));
        return byteArrayOutputStream.toByteArray();
    }
*/

}
