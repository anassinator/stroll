package com.myo.stroll;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates {
	public static double distance;
	public static double bearing; 
	
	public static void update(LatLng latLng1, LatLng latLng2) {
	    double earthRadius = 6371; //kilometers
	    double dLat = Math.toRadians(latLng2.latitude-latLng1.latitude);
	    double dLng = Math.toRadians(latLng2.longitude-latLng1.longitude);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(latLng1.latitude)) * Math.cos(Math.toRadians(latLng2.latitude)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    distance =  earthRadius * c;
		double deltaLong = Math.toRadians(latLng2.longitude - latLng1.longitude);
		double lat1 = Math.toRadians(latLng1.latitude);
		double lat2 = Math.toRadians(latLng2.latitude);

		double y = Math.sin(deltaLong) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);
		double result = Math.toDegrees(Math.atan2(y, x));
		bearing = (result + 360.0) % 360.0;
	}

}
