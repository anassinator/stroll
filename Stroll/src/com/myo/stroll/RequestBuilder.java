package com.myo.stroll;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import android.util.Log;

public class RequestBuilder extends AsyncTask<String, Void, JSONObject> {
	// API call parameter list
    private static final String HEADER = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String ORIGIN = "origin=";
    private static final String DEST = "destination=";
    private static final String KEY = "key=";
    private static final String DEPARTURE_TIME = "departure_time=";
    private static final String MODE = "mode=";
    private static final String REGION = "region=";
    // Value of api parameters
    private static final String UNITS = "units";
    private static final String METRIC = "metric";
    private static final String ALTROUTE = "alternatives";
    private static final String TRUE = "true";
    private static final String CA = "ca";
    private static final String WALK = "walking";
    private static final String API_KEY = "AIzaSyA3bq0V_IMiwMGL4MR3M-1LmdbVB8kB-0k";
    
    private static String getCurrentTime() {
        return Integer.toString((int)(System.currentTimeMillis() / 1000));
    } 
    
    // Returns json string of direction api call results
    private String getDirectionString(String origin, String destination) {
    	Log.w("debugger", "Initialized");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        
        try {
        	// Use http get to get directions
        	String url = RequestBuilder.buildRequestURL(origin, destination);
        	Log.w("debugger", url);
            response = client.execute(new HttpGet(url));
            
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
        
        return null;
    }
    
    // Parses json string returned from directions api into json object
    private JSONObject getDirections(String origin, String destination) {
        String directionString = this.getDirectionString(origin, destination);
        
        if (directionString != null && !directionString.isEmpty()) {
            try {
            	Log.w("debugger", "Got direction string");
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
                + RequestBuilder.UNITS + RequestBuilder.METRIC + "&"
                + RequestBuilder.REGION + RequestBuilder.CA + "&"
                + RequestBuilder.DEPARTURE_TIME + RequestBuilder.getCurrentTime() + "&"
                + RequestBuilder.MODE + RequestBuilder.WALK;
    }

    @Override
    protected JSONObject doInBackground(String... place) {
    	
        if (place.length == 2) {
            Log.w("debugger", "Starting...");
            // place[0] - origin
            // place[1] - destination
            try {
    			return this.getDirections(URLEncoder.encode(place[0], "UTF-8"), URLEncoder.encode(place[1], "UTF-8"));
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}	
        }
        
        return null;
    }
}
