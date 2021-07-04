package com.ancora.gmaps2.federico.googlemapsactivity.Zoomato;


import android.database.Cursor;

public class CuisineModel
{
    String id;
    String name;
    public CuisineModel()
    {
        super();
    }

    public CuisineModel(Cursor cursor)
    {
        super();
        //this.id =cursor.getString(cursor.getColumnIndex(CuisineDbAdapter.CuisineId));
       // this.name =cursor.getString(cursor.getColumnIndex(CuisineDbAdapter.CuisineName));
    }

    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    @Override
    public String toString()
    {
        return "CuisinesClass [id=" + id + ", name=" + name + "]";
    }


}
