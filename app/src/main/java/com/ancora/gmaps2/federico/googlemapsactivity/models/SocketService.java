package com.ancora.gmaps2.federico.googlemapsactivity.models;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class SocketService extends Service {

    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Socket client;
    public DataInputStream dIn;
    public DataOutputStream dOut;
    private final IBinder mBinder = new LocalBinder();
    NotificationManager mNM;
    /** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    /** Holds last value set by a client. */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    public static final int MSG_SET_VALUE = 3;

    public class LocalBinder extends Binder {
        public SocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }

    public SocketService() {

    }

    @Override
    public void onCreate() {
        /*try {
            client = new Socket("ragusapc.duckdns.org", 5050);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public LinkedList<String> getList(String place) {
        LinkedList<String> a =new LinkedList<>();
        try {
            dOut=new DataOutputStream((client.getOutputStream()));
            dOut.writeUTF(String.valueOf(0));
            dOut.writeUTF(place);
            dIn = new DataInputStream(client.getInputStream());
            String s;
            StringTokenizer sT;

            s=dIn.readUTF();

            for (int h=0; h<60; h++) {
                Log.i("msg", s);
            }
            sT = new StringTokenizer(s, "|");
            while (sT.hasMoreTokens()) {
                a.addFirst(sT.nextToken());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public String getName() {
        return this.getName();
    }

    public float getRate (String place) throws IOException {
        float rateF=0;
        dOut=new DataOutputStream((client.getOutputStream()));
        dOut.writeUTF(String.valueOf(3));
        dOut.writeUTF(place);
        StringTokenizer sTR;
        String q=dIn.readUTF();
        sTR = new StringTokenizer(q, ",");
        while (sTR.hasMoreTokens()) {
            rateF=Float.parseFloat(sTR.nextToken());
        }

        return rateF;
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    for (int i=mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public String isAlive() {
        return "sono vivo";
    }
 }
