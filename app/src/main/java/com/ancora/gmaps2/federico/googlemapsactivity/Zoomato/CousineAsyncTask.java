package com.ancora.gmaps2.federico.googlemapsactivity.Zoomato;

import android.os.AsyncTask;
import android.util.Log;

import com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources.SplashActivityGoogle;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CousineAsyncTask extends AsyncTask<String, Void, ArrayList<CuisineModel>>
{
    SplashActivityGoogle context;
    String cityId;
    private String cityName;

    public CousineAsyncTask(SplashActivityGoogle context,String cityId) {
        this.context=context;
        this.cityId=cityId;
    }
    @Override
    protected ArrayList<CuisineModel>doInBackground(String... Paramater)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>(3);
        params.add(new BasicNameValuePair("lat","18.52043"));
        params.add(new BasicNameValuePair("lon","73.856744"));
        params.add(new BasicNameValuePair("apikey", Constants.AUTH_KEY));

        String strJsonReponse = null;
        try {
            strJsonReponse = RestClient.getInstance(context).doApiCall(Constants.strLocation, "POST", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Data Response...",String.valueOf(strJsonReponse));

        JSONObject responseObj= null;
        try {
            responseObj = new JSONObject(strJsonReponse);
            JSONObject localityObj=responseObj.getJSONObject("locality");
            cityName = localityObj.getString("city_name");
            cityId = localityObj.getString("city_id");
            Log.d("city Id", cityId);
            Log.d("city Name", cityName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<CuisineModel>cuisineArray = null;
        try {
            strJsonReponse = RestClient.getInstance(context).doApiCall(Constants.strCuisines, "GET", params);
            Log.d("Cuisines Data Response",String.valueOf(strJsonReponse));
            CuisinesParser parser=new CuisinesParser();
            cuisineArray=parser.parseCuisines(strJsonReponse);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cuisineArray;
    }

    @Override
    protected void onPostExecute(ArrayList<CuisineModel> cuisineArray)
    {

    }


}