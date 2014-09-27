package com.thalmic.android.sample.helloworld;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.StrictMode;

public class RequestBuilder extends AsyncTask<String, Void, JSONObject> {
    private static final String HEADER = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String ORIGIN = "origin=";
    private static final String DEST = "destination=";
    private static final String KEY = "key=";
    private static final String ALTROUTE = "alternatives";
    private static final String TRUE = "true";
    private static final String DEPARTURE_TIME = "departure_time=";
    private static final String MODE = "mode=";
    private static final String WALK = "walking";
    private static final String API_KEY = "AIzaSyA3bq0V_IMiwMGL4MR3M-1LmdbVB8kB-0k";
    
    public static JSONObject Directions = null;
    
    private static String getCurrentTime() {
        return Integer.toString((int)(System.currentTimeMillis() / 1000));
    } 
    
    private String getDirectionString(String origin, String destination) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        
        try {
            response = client.execute(new HttpGet(RequestBuilder.buildRequestURL(origin, destination)));
            
            StatusLine status = response.getStatusLine();
            
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                
                out.close();
                return out.toString();
            }
            
            response.getEntity().getContent().close();
        }
        catch (ClientProtocolException e) {
            return "Client Exception";
        }
        catch (IOException e) {
            return "IO Exception";
        }
        catch (Exception e) {
            return e.toString();
        }
        
        return "Empty";
    }
    
    private JSONObject getDirections(String origin, String destination) {
        String directionString = this.getDirectionString(origin, destination);
        
        if (directionString != null && !directionString.isEmpty()) {
            try {
                return new JSONObject(directionString);
            }
            catch(Exception e) {
                // whatever
            }
        }

        return null;
    }
    
    public static String buildRequestURL(String origin, String destination) {
        return RequestBuilder.HEADER + RequestBuilder.ORIGIN + origin + "&"
                + RequestBuilder.DEST + destination + "&"
                + RequestBuilder.KEY + RequestBuilder.API_KEY + "&"
                + RequestBuilder.ALTROUTE + RequestBuilder.TRUE + "&"
                + RequestBuilder.DEPARTURE_TIME + RequestBuilder.getCurrentTime() + "&"
                + RequestBuilder.MODE + RequestBuilder.WALK;
    }
    
    public static void resetDirections() {
    	RequestBuilder.Directions = null;
    }

    @Override
    protected JSONObject doInBackground(String... place) {
    	RequestBuilder.Directions = null;
    	
        if (place.length != 2)
            return null;
        return this.getDirections(place[0], place[1]);
    }
    
    @Override
    protected void onPostExecute(JSONObject result) {
        RequestBuilder.Directions = result;
    }
}
