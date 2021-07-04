package com.ancora.gmaps2.federico.googlemapsactivity.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.DataBasePref;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MyService extends Service {
    private Socket client;
    public DataInputStream dIn;
    public DataOutputStream dOut;
    public ObjectInputStream in;

    public int getId() {
        return id;
    }

    public void resetId() {
        id=-1;
    }

    public LinkedList<PlacesModel> jsonList=new LinkedList<>();
    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    private byte[] a;
    private LinkedList<byte[]> photos= new LinkedList<>();
    private int id=-1;
    private boolean conn=true;

    public String getS() {
        return s;
    }

    private String s;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int checkLog(String user, String pass) throws IOException {
        int end=0;
        dOut=new DataOutputStream(client.getOutputStream());
        dOut.writeInt(9);
        dIn = new DataInputStream(client.getInputStream());
        dOut.writeUTF(user);
        dOut.writeUTF(pass);
        end=dIn.readInt();
        id=dIn.readInt();
        return end;
    }

    public synchronized LinkedList<Boolean> getListB(LinkedList<PlacesModel> listP) {
        LinkedList<Boolean> list=new LinkedList<>();
        DataBasePref pref=new DataBasePref(this);
        for(int l=0; l<listP.size(); l++) {
            list.add(pref.isPrsent(listP.get(l).getId()));
            Log.i("", String.valueOf(list.get(l)));
        }
        pref.close();
        return list;
    }

    public class LocalBinder extends Binder {
        public MyService getServiceInstance(){
            return MyService.this;
        }
    }

    public boolean isConn() {
        return conn;
    }

    public void connServer() {
        try {
            client = new Socket();
            client.connect(new InetSocketAddress("2.233.66.131", 5050), 2000);

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Server non disponibile", Toast.LENGTH_LONG).show();
            conn=false;
        }
    }

    public LinkedList<String> getPhoto() {

        LinkedList<String> data = new LinkedList<>();
        try {
            dOut=new DataOutputStream(client.getOutputStream());
            dOut.writeInt(5);
            dIn = new DataInputStream(client.getInputStream());
            String s=dIn.readUTF();
            Log.i("prova",s);
            StringTokenizer sT;
            Scanner scanner = new Scanner(s);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sT = new StringTokenizer(line, "|");
                while (sT.hasMoreTokens()) {
                    data.addFirst(sT.nextToken());
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String[] getUrlP(int id) throws IOException {
        String[] s=new String[2];
        dOut=new DataOutputStream(client.getOutputStream());
        dOut.writeInt(10);
        dIn = new DataInputStream(client.getInputStream());
        dOut.writeInt(id);
        String url=dIn.readUTF();
        s[0]=url;
        s[1]=dIn.readUTF();
        return s;
    }

    public int getSize() {
        int len = 0;
        try {
            dOut=new DataOutputStream(client.getOutputStream());
            dOut.writeInt(7);
            dIn = new DataInputStream(client.getInputStream());
            len = dIn.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return len;
    }

    public void registerClient(Activity activity){
        this.activity = (Callbacks)activity;
    }

    public void removeClient() {
        this.activity=null;
    }

    public void startCounter(){
        Toast.makeText(getApplicationContext(), "Counter started", Toast.LENGTH_SHORT).show();
    }

    public Object[] listC(String place) throws IOException {
        Object[] o=new Object[2];
        LinkedList<String> a2 =new LinkedList<>();
        LinkedList<String> u2=new LinkedList<>();
        try {
            boolean noToken = false;
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeInt(0);
            dOut.writeUTF(place);
            dIn = new DataInputStream(client.getInputStream());
            String s;
            StringTokenizer sT, sv;
            s=dIn.readUTF();
            Log.i("funzia o no", s);
            Scanner scanner = new Scanner(s);
            StringBuilder sb=new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sT = new StringTokenizer(line, "|");
                if(sT.countTokens()==1) {
                    Log.i("funzia o no", String.valueOf(sT.countTokens()));
                    sb.append(sT.nextToken() + "\n");
                    Log.i("funzia o no", sb.toString());
                    noToken=true;
                }else {
                    while (sT.hasMoreTokens()) {
                        if(!noToken) {
                            a2.addFirst(sT.nextToken());
                            u2.addFirst(sT.nextToken());
                        }else {
                            sb.append(sT.nextToken());
                            Log.i("funzia o no 2", sb.toString());
                            a2.addFirst(sb.toString());
                            u2.addFirst(sT.nextToken());

                        }
                    }
                    sb=new StringBuilder();
                }

            }
            scanner.close();

            o[0]=(LinkedList) a2;
            o[1]=(LinkedList) u2;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return o;
    }

    public void setList(int size) {
        for(int i=0; i<size; i++) {
            jsonList.add(null);
        }
    }

    public PlacesModel getJson(int pos) {
        return jsonList.get(pos);
    }

    public void setJson(PlacesModel s, int pos) {
        jsonList.remove(pos);
        jsonList.add(pos, s);
    }

    public boolean isEmpty(int pos) {
        if(jsonList.get(pos)==null) {
            return false;
        }else {
            return true;
        }
    }

    public LinkedList<Float> getRate (String place) throws IOException {
        LinkedList<Float> rateF=new LinkedList<>();
        dOut=new DataOutputStream((client.getOutputStream()));
        dOut.writeInt(3);
        StringTokenizer sTR;
        dIn = new DataInputStream(client.getInputStream());
        dOut.writeUTF(place);
        String w=dIn.readUTF();
        StringTokenizer sT = new StringTokenizer(w, "|");
        while (sT.hasMoreTokens()) {
            rateF.add(Float.parseFloat(sT.nextToken()));
        }
        return rateF;
    }

    public float getRateP (String place) throws IOException {
        float f;
        dOut=new DataOutputStream((client.getOutputStream()));
        dOut.writeInt(8);
        dIn = new DataInputStream(client.getInputStream());
        dOut.writeUTF(place);
        f=dIn.readFloat();

        return f;
    }

    public int sendRegistration(String user, String pass, String mail, Bitmap bitmap) {
        int cod = 0;
        try {
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeInt(6);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            byte[] data;
            dIn = new DataInputStream(client.getInputStream());
            dOut.writeUTF(user);
            dOut.writeUTF(pass);
            bitmap=Bitmap.createScaledBitmap(bitmap, 360, 360, false);
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, blob);
            data = blob.toByteArray();
            dOut.writeInt(data.length);
            dOut.write(data, 0, data.length);
            dOut.writeUTF(mail);
            cod=dIn.readInt();
            s=dIn.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }



        return cod;
    }

    public synchronized LinkedList<LatLng> sharePos(LatLng l) {
        LinkedList<LatLng> latlng=new LinkedList<>();
        try {
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeInt(4);
            dOut.writeUTF(String.valueOf(l.latitude));
            dOut.writeUTF(String.valueOf(l.longitude));
            dIn = new DataInputStream(client.getInputStream());
            int pos=dIn.readInt();
            String position=dIn.readUTF();
            Scanner scanner = new Scanner(position);
            Log.i("latLng", position);
            StringTokenizer sL;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sL = new StringTokenizer(line, ",");
                while (sL.hasMoreTokens()) {
                    LatLng l2=new LatLng(Double.parseDouble(sL.nextToken()), Double.parseDouble(sL.nextToken()));
                    if(!l2.equals(l)) {
                        latlng.add(l2);
                    }
                    Log.i("latLng", "sta a fa qualcosa");
                }

            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latlng;

    }

    public void voteRest(float rating, String l) {
        try {
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeInt(2);
            dOut.writeFloat(rating);
            dOut.writeUTF(l);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*public LinkedList<String> writeComment(String msg, double v, String place_id, LinkedList<String> a, int codice) {
        try {
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeInt(1);
            dOut.writeUTF(msg);
            dOut.writeUTF(String.valueOf(v));
            dOut.writeUTF(place_id);
            dOut.writeInt(codice);
            dIn = new DataInputStream(client.getInputStream());
            String s;
            StringTokenizer sT;
            s=dIn.readUTF();
            sT = new StringTokenizer(s, "|");
            while (sT.hasMoreTokens()) {
                a.addFirst(sT.nextToken());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }*/
    public Object[] writeComment(String msg, double v, String place_id, LinkedList<String> a, int codice) throws IOException {
        Object[] o=new Object[2];
        LinkedList<String> c =new LinkedList<>();
        LinkedList<String> u=new LinkedList<>();
        try {
            boolean noToken=false;
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeInt(1);
            dOut.writeUTF(msg);
            dOut.writeUTF(String.valueOf(v));
            dOut.writeUTF(place_id);
            dOut.writeInt(codice);
            dIn = new DataInputStream(client.getInputStream());
            String s;
            StringTokenizer sT;
            s=dIn.readUTF();
            Scanner scanner = new Scanner(s);
            StringBuilder sb=new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sT = new StringTokenizer(line, "|");
                if(sT.countTokens()==1) {
                    Log.i("funzia o no", String.valueOf(sT.countTokens()));
                    sb.append(sT.nextToken() + "\n");
                    Log.i("funzia o no", sb.toString());
                    noToken=true;
                }else {
                    while (sT.hasMoreTokens()) {
                        if(!noToken) {
                            c.addFirst(sT.nextToken());
                            u.addFirst(sT.nextToken());
                        }else {
                            sb.append(sT.nextToken());
                            Log.i("funzia o no 2", sb.toString());
                            c.addFirst(sb.toString());
                            u.addFirst(sT.nextToken());

                        }
                    }
                    sb=new StringBuilder();
                }

            }
            scanner.close();

            o[0]=(LinkedList) c;
            o[1]=(LinkedList) u;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return o;
    }

    public LinkedList<byte[]> getPhotos() {
        return photos;
    }


    public interface Callbacks{
        void updateClient(long data);
        void getString(String s);
        void getList(LinkedList<String> a);
    }
}