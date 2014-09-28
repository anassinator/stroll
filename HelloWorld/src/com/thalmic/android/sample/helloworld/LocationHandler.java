package com.thalmic.android.sample.helloworld;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thalmic.myo.Quaternion;

public class LocationHandler {
	private static final double CLOSE_ENOUGH = 20.00;
	private static double yaw = 0.00;
	private static Vibration vibration;
	private static Boolean searching = false;
	
	public static void set_vibration(Vibration vibration) {
		LocationHandler.vibration = vibration; 
	}

	public static void update_orientation(Quaternion quat) {
		yaw = (float) Math.toDegrees(Quaternion.yaw(quat));
		if (LocationHandler.searching) {
			vibration.start();
			LocationHandler.notify(LocationHandler.vibration);
		} else {
			vibration.stop();
		}
	}

	public static Boolean update_location(LatLng here, LatLng there) {
		Coordinates.update(here, there);

//		if (Coordinates.distance <= CLOSE_ENOUGH) {
//			vibration.start();
//			LocationHandler.notify(LocationHandler.vibration);
//			return true;
//		}
//
		return false;
	}

    public static void notify(Vibration vibration) {
    	long ms = 100 + (int) (10 * (Math.abs(LocationHandler.yaw - Coordinates.bearing) % 360));
    	Log.w("debugger", String.valueOf(ms) + "ms");
    	vibration.setPeriod(ms);
	}
 
	public static void search(Boolean state) {
		LocationHandler.searching = state;
	}
}
