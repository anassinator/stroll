package com.thalmic.android.sample.helloworld;

import java.io.ByteArrayOutputStream;
import org.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class RequestBuilder {
	private static final String HEADER = "https://maps.googleapis.com/maps/api/directions/json?";
	private static final String ORIGIN = "origin=";
	private static final String DEST = "destination=";
	private static final String KEY = "key=";
	private static final String DEPARTURE_TIME = "departure_time=";
	private static final String MODE = "mode=";
	private static final String WALK = "walking";
	private static final String API_KEY = "AIzaSyBDuJd3jPuUJTSI91B0yIrQFjuZ6QTbx_8";
	
	private static String getCurrentTime() {
		return Integer.toString((int)(System.currentTimeMillis() / 1000));
	} 
	
	private static String getDirectionString(String origin, String destination) {
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
		catch (Exception e) {
			// yolo
		}
		
		return null;
	}
	
	public static String buildRequestURL(String origin, String destination) {
		return RequestBuilder.HEADER + RequestBuilder.ORIGIN + origin + "&"
				+ RequestBuilder.DEST + destination + "&"
				+ RequestBuilder.KEY + RequestBuilder.API_KEY + "&"
				+ RequestBuilder.DEPARTURE_TIME + RequestBuilder.getCurrentTime() + "&"
				+ RequestBuilder.MODE + RequestBuilder.WALK;
	}
	
	public static JSONObject getDirections(String origin, String destination) {
		String directionString = RequestBuilder.getDirectionString(origin, destination);
		
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
}
