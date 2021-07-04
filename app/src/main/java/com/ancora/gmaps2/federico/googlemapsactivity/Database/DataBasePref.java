package com.ancora.gmaps2.federico.googlemapsactivity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;

import java.text.MessageFormat;
import java.util.LinkedList;


/**
 * Created by Federico on 07/01/2016.
 */
public class DataBasePref extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "prefDatabase2.db";
    private static final int SCHEMA_VERSION = 1;
    public DataBasePref(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE {0} ({1} INTEGER PRIMARY KEY, {2} TEXT , {3} TEXT,{4} TEXT, {5} TEXT , {6} FLOAT, {7} TEXT, {8} TEXT);";
        db.execSQL(MessageFormat.format(sql, PreferitiI.TABLE_NAME, PreferitiI.ID, PreferitiI.PLACE_ID, PreferitiI.ADDRESS, PreferitiI.URL, PreferitiI.REST, PreferitiI.RATING, PreferitiI.LAT, PreferitiI.LONG));
    }



    public void inserisci(PlacesModel c) {
        SQLiteDatabase db=getReadableDatabase();
        Cursor cur=this.getId();
        int i=0;
        try{
            while(cur.moveToNext()) {
                i=cur.getInt(cur.getColumnIndex("id"));
                i++;
            }
        }finally {
            cur.close();
        }
        insericiPlace(db, c, i);
    }

    public boolean isPrsent(String place) {
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT count(*) FROM preferiti where placeid='"+place+"'";
        Cursor mcursor = null;
        try {
            mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            if (icount > 0)
                return true;
            else
                return false;
        }finally {
            mcursor.close();
        }
    }

    private void insericiPlace(SQLiteDatabase db, PlacesModel c, int i) {
        String s="INSERT INTO preferiti(id, placeId, address, nomeRest, rating, photoUrl, lat, long) values ('"+i+"','"+ c.getId() +"', '"+ c.getAddress() +"', '"+ c.getName()+"', '"+ c.getRating()+"','"+c.getPhotoRef()+"', '"+ c.getLat()+"', '"+ c.getLng()+"');";
        db.execSQL(s);
    }

    private Cursor getId() {
        String s="select max(id) as id from preferiti";
        return getReadableDatabase().rawQuery(s, null);
    }

    public void modifica (PlacesModel c) {
        SQLiteDatabase db=getReadableDatabase();
        modifica_cod(db, c);
    }


    private void modifica_cod(SQLiteDatabase db, PlacesModel c) {
        ContentValues cv=new ContentValues();
        String s="update preferiti set placeId " +c.getId()+"and set photoUrl "+c.getPhotoRef()+"and set nomeRest "+c.getName()
                +"where placeId=" + c.getPlaceId();
        db.execSQL(s);
    }

    public void modifica_cod_nuovo(SQLiteDatabase db, int pos) {
        Cursor c=this.getPref();
        c.moveToPosition(pos);
        int i=pos;
        while(c.moveToNext()) {
            i++;
            String s="update contatti set cod_contatto="+i+" where cod_contatto <"+pos;
            pos++;
            db.execSQL(s);
        }
    }

    public Cursor getPlaceId(String placeId) {
        String s="select placeId from preferiti where placeId="+ placeId;
        return(getReadableDatabase().rawQuery(s, null));
    }

    public Cursor getPhotoRef(String placeId) {
        String s="select photoRef from preferiti where placeId="+ placeId;
        return(getReadableDatabase().rawQuery(s, null));
    }

    public Cursor getNomeRest(String placeId) {
        String s="select nomeRest from preferiti where placeId="+ placeId;
        return(getReadableDatabase().rawQuery(s, null));
    }

    public Cursor getRating(String placeId) {
        String s="select rating from preferiti where placeId="+ placeId;
        return(getReadableDatabase().rawQuery(s, null));
    }

    public void eliminaContatto(String placeId) {
        SQLiteDatabase db=getReadableDatabase();
        elimina_cod(db, placeId);
    }

    private void elimina_cod(SQLiteDatabase db, String placeId) {
        String s="delete from preferiti where placeId='"+ placeId+"'";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getPref() {
        String s="select * from preferiti";
        return(getReadableDatabase().rawQuery(s, null));
    }

    public Cursor getPrefN() {
        String s="select * from preferiti order by nomeRest";
        return(getReadableDatabase().rawQuery(s, null));
    }

    public Cursor getPrefP() {
        String s="select * from preferiti";
        return(getReadableDatabase().rawQuery(s, null));
    }

    public LinkedList<PlacesModel> caricaLista() {
        Cursor c=this.getPref();
        LinkedList<PlacesModel> lista=new LinkedList<>();
            try {
                while (c.moveToNext()) {
                    lista.add(new PlacesModel(c.getString(c.getColumnIndex("placeId")), c.getString(c.getColumnIndex("address")), c.getString(c.getColumnIndex("photoUrl")),
                            c.getString(c.getColumnIndex("nomeRest")), c.getFloat(c.getColumnIndex("rating")), c.getString(c.getColumnIndex("lat")), c.getString(c.getColumnIndex("long"))));
                }
            } finally {
                c.close();
            }
        return lista;

    }

    public LinkedList<PlacesModel> caricaListaN() {
        Cursor c=this.getPrefN();
        LinkedList<PlacesModel> lista=new LinkedList<>();
        try {
            while (c.moveToNext()) {
                lista.add(new PlacesModel(c.getString(c.getColumnIndex("placeId")), c.getString(c.getColumnIndex("address")), c.getString(c.getColumnIndex("photoUrl")),
                        c.getString(c.getColumnIndex("nomeRest")), c.getFloat(c.getColumnIndex("rating")), c.getString(c.getColumnIndex("lat")), c.getString(c.getColumnIndex("long"))));
            }
        } finally {
            c.close();
        }
        return lista;

    }

    public LinkedList<PlacesModel> caricaListaPlace() {
        Cursor c=this.getPref();
        LinkedList<PlacesModel> lista=new LinkedList<>();
        try {
            while (c.moveToNext()) {
                lista.add(new PlacesModel(c.getString(c.getColumnIndex("placeId")), c.getString(c.getColumnIndex("address")), c.getString(c.getColumnIndex("photoUrl")),
                        c.getString(c.getColumnIndex("nomeRest")), c.getFloat(c.getColumnIndex("rating")), c.getString(c.getColumnIndex("lat")), c.getString(c.getColumnIndex("long"))));
            }
        } finally {
            c.close();
        }
        return lista;

    }


}
