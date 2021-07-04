package com.ancora.gmaps2.federico.googlemapsactivity.Zoomato;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;




public class CuisinesParser {

    ArrayList<CuisineModel> cuisinesArray;

    public ArrayList<CuisineModel> parseCuisines(String strJsonReponse)
    {

        cuisinesArray=new ArrayList<CuisineModel>();
        try
        {

            JSONObject responseCuisines = new JSONObject(strJsonReponse);
            JSONArray cuisinesObj = responseCuisines.getJSONArray("cuisines");

            for(int i=0;i < cuisinesObj.length();i++)
            {
                CuisineModel c=new CuisineModel();
                JSONObject jObj=cuisinesObj.getJSONObject(i);
                JSONObject cuisine=jObj.getJSONObject("cuisine");

                c.setId(cuisine.getString("cuisine_id").toString());
                Log.i("prova", c.getId());
                c.setName(cuisine.getString("cuisine_name").toString());

                cuisinesArray.add(c);

            }

        }
        catch (Exception e) {
            // TODO: handle exception
        }

        return cuisinesArray;
    }

}
