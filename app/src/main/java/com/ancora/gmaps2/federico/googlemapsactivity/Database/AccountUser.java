package com.ancora.gmaps2.federico.googlemapsactivity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ancora.gmaps2.federico.googlemapsactivity.models.AccountReg;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;

import java.text.MessageFormat;
import java.util.LinkedList;

/**
 * Created by Federico on 22/04/2016.
 */
public class AccountUser extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "accountDatabase2.db";
    private static final int SCHEMA_VERSION = 1;
    public AccountUser(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE {0} ({1} INTEGER PRIMARY KEY, {2} TEXT, {3} TEXT,{4} TEXT,{5} TEXT, {6} BOOLEAN);";
        db.execSQL(MessageFormat.format(sql, Account.TABLE_NAME, Account.COD, Account.USERNAME, Account.PASS, Account.EMAIL, Account.PHOTO_PATH, Account.LOG));
    }

    public boolean isMasterEmpty() {

        boolean flag;
        String quString = "select exists(select 1 from '" + Account.TABLE_NAME  + "');";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(quString, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        if (count ==1) {
            flag =  false;
        } else {
            flag = true;
        }
        cursor.close();
        db.close();

        return flag;
    }



    public void inserisci(AccountReg ar) {
        SQLiteDatabase db=getReadableDatabase();
        insericiPlace(db, ar);
    }

    private void insericiPlace(SQLiteDatabase db, AccountReg c) {
        String s="INSERT INTO '"+Account.TABLE_NAME+"'(cod, user, pass, email,  path, logg) values ('"+c.getCodice()+"','"+ c.getUsername() +"', '"+ c.getPassword() +"','"+ c.getEmail() +"', '"+ c.getPhotoPath()+"', '"+ c.isLog()+"');";
        db.execSQL(s);
    }

    private Cursor getId() {
        String s="select max(cod) as cod from '"+Account.TABLE_NAME+"'";
        return getReadableDatabase().rawQuery(s, null);
    }

    public void modifica (PlacesModel c) {
        SQLiteDatabase db=getReadableDatabase();
        modifica_cod(db, c);
    }

    public void deleteDB () {
        SQLiteDatabase db=getReadableDatabase();
        db.delete(Account.TABLE_NAME,null,null);
        db.close();
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

    public Cursor getCod() {
        String s="select cod from '"+Account.TABLE_NAME+"'";
        return(getReadableDatabase().rawQuery(s, null));
    }

    public int getCodice() {
        Cursor c=this.getCod();
        int k=0;
        while(c.moveToNext()) {
            k=c.getInt(c.getColumnIndex("cod"));
        }
        return k;
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
