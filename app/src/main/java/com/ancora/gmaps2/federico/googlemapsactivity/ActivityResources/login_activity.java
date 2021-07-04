package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.AccountUser;
import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.Service.MyService;
import com.ancora.gmaps2.federico.googlemapsactivity.models.AccountReg;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public class login_activity extends AppCompatActivity implements MyService.Callbacks{

    public static final int THUMBNAIL_HEIGHT = 560;
    public static final int THUMBNAIL_WIDTH = 64;
    private Toolbar toolbar;
    private String imgDecodableString;
    private final int RESULT_LOAD_IMG=1;
    private ImageView imgView;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView imgView2;
    private Register r=new Register();
    private EditText email, user, pass, confPass;
    private TextInputLayout tmail, tuser, tpass, tpassa;
    byte[] bitmapdata;
    private MyService myService;

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService=binder.getServiceInstance();
            myService.registerClient(login_activity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(login_activity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent1 = new Intent(login_activity.this, MyService.class);
        bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(" Dettagli Ristorante");
        setSupportActionBar(toolbar);
        email=(EditText) findViewById(R.id.etEmail);
        user=(EditText) findViewById(R.id.etUserName);
        pass=(EditText) findViewById(R.id.etPass);
        confPass=(EditText) findViewById(R.id.etPassA);
        tmail=(TextInputLayout) findViewById(R.id.customMail);
        tuser=(TextInputLayout) findViewById(R.id.customUser);
        tpass=(TextInputLayout) findViewById(R.id.customPass);
        tpassa=(TextInputLayout) findViewById(R.id.customPassA);
        android.support.v7.app.ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        imgView = (ImageView) findViewById(R.id.imageView4);
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {


                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};


                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();


                bitmap = BitmapFactory.decodeFile(imgDecodableString);
                bitmap=Bitmap.createScaledBitmap(bitmap, THUMBNAIL_HEIGHT, THUMBNAIL_HEIGHT, false);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, blob);
                bitmapdata = blob.toByteArray();
                imgView.setImageBitmap(bitmap);
                /*byte[] newdata=myService.sendByte(bitmapdata, bitmapdata.length);

                Bitmap bitmap2 = BitmapFactory.decodeByteArray(newdata, 0, newdata.length);
                imgView2.setImageBitmap(bitmap2);*/
            } else {
                Toast.makeText(this, "Non hai selezionato un'immagine",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public void createAcc(View v) {
        r=new Register();
        r.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private class Register extends AsyncTask<Void, Void, Void> {
        private String userS, passS, mailS;
        int num;
        private ProgressDialog progressDialog;
        String s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(login_activity.this);
            progressDialog.setMessage("Creazione account...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            userS=user.getText().toString();
            passS=pass.getText().toString();
            mailS=email.getText().toString();

        }


        @Override
        protected Void doInBackground(Void... params) {
            num=myService.sendRegistration(userS, passS, mailS, bitmap);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(num!=-1 && num!=-2) {
                final AccountUser db=new AccountUser(login_activity.this);
                final AccountReg ar=new AccountReg(num, userS, passS, mailS, imgDecodableString, false);

                AlertDialog.Builder alert_box=new AlertDialog.Builder(login_activity.this);
                alert_box.setIcon(R.mipmap.ic_launcher);
                alert_box.setMessage("Vuoi rimanere collegato");
                alert_box.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), "Yes Button Clicked", Toast.LENGTH_LONG).show();
                        ar.setLog(true);
                        db.inserisci(ar);
                        finish();
                    }
                });
                alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), "No Button Clicked", Toast.LENGTH_LONG).show();
                        ar.setLog(false);
                        db.inserisci(ar);
                        finish();
                    }
                });
                alert_box.show();

            }
            if(num==-1) {
                tuser.setError("Nome già presente nell'archivio");
            }
            if(num==-2) {
                tpass.setError("password non disponibile");
            }
            progressDialog.cancel();

        }

    }

    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
}
