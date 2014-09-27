package com.thalmic.android.sample.helloworld;

import com.google.android.gms.maps.model.LatLng;
import com.thalmic.myo.Quaternion;

public class LocationHandler {
	private static final double CLOSE_ENOUGH = 20.00;
	private static double yaw = 0.00;
	private static Vibration vibration;
	
	public static void set_vibration(Vibration vibration) {
		LocationHandler.vibration = vibration; 
	}

	public static void update_orientation(Quaternion quat) {
		yaw = (float) Math.toDegrees(Quaternion.yaw(quat));
	}

	public static Boolean update_location(LatLng here, LatLng there) {
		Coordinates.update(here, there);

		if (Coordinates.distance <= CLOSE_ENOUGH) {
			LocationHandler.notify(vibration);
			return true;
		}

		return false;
	}

    public static void notify(Vibration vibration) {
    	long ms = 100 + (int) (10 * Math.abs(LocationHandler.yaw - Coordinates.bearing));
    	vibration.setPeriod(ms);
    }
 
	public static void search(Step step) {
		Coordinates.update(step.start_location, step.end_location);

		if (Coordinates.distance <= CLOSE_ENOUGH) {
			LocationHandler.notify(vibration);
		}
	}
}
