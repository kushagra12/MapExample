package abhinav.hackdev.co.fetchmarker102;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by abhinav on 05/06/16.
 */
public class MarkerFetcher {

    private static final String TAG = "SPLTAG";
    private MarkerDataList markerDataList ;

    public MarkerFetcher() {
    }

    /*public MarkerDataList handleCall(LatLng value){
        MarkerDataList markerDataList ;
        String responseString = fetcherNetworkCall(value);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseString);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("markers")) ;
            Log.d(TAG, "fetcherNetworkCall: " + jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson() ;
        markerDataList = gson.fromJson(responseString, MarkerDataList.class) ;

        for (MarkerDataList.MarkerData markerData : markerDataList.getMarkerDataList()){
            Log.d(TAG, "fetcherNetworkCall: " + markerData.getLatVal() + " " +
                    markerData.getLongVal() + " " + markerData.getImgUrl() );
        }

        return markerDataList ;
    }*/

    public MarkerDataList fetcherNetworkCall(final LatLng value){

        okhttp3.Response response = null;
        String fetcherURL = "http://private-7e17da-markers.apiary-mock.com/api/fetchmarkers" ;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("latitude", String.valueOf(value.getLatitude()))
                .add("longitude", String.valueOf(value.getLongitude()))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(fetcherURL)
                .post(body)
                .build();

        try {
            response = client.newCall(request).execute();
            String res = response.body().string() ;


            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("markers")) ;

            Log.d(TAG, "fetcherNetworkCall 123: " + jsonArray.toString());

            Gson gson = new Gson() ;
            markerDataList = gson.fromJson(res, MarkerDataList.class) ;

        } catch (IOException e) {
            Log.d(TAG, "fetcherNetworkCall: Error" );
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "fetcherNetworkCall: Error JSON");
        }

        Log.d(TAG, "fetcherNetworkCall: " + markerDataList.getMarkerDataList().get(0).getUsername());

        return markerDataList ;

        /*try {
            assert response != null;
            return response.body().string() ;
        } catch (IOException e) {
            return "noData" ;
        }*/

    }
}
