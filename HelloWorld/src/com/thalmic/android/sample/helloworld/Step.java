package com.thalmic.android.sample.helloworld;

import org.json.*;

public class Step {
	private JSONObject json;
	public int distance;
	public int duration;
	public Coordinate start_location;
	public Coordinate end_location;
	public Maneuver maneuver;
	
	public Step(JSONObject json) {
		this.json = json;
		this.distance = parse_value("distance");
		this.duration = parse_value("duration");
		this.start_location = parse_coordinates("start_location");
		this.end_location = parse_coordinates("end_location");
		this.maneuver = parse_maneuver();
	}
	
	private int parse_value(String name) {
    	try {
	        JSONObject json_obj = json.getJSONObject(name);
	        return json_obj.getInt("value");
    	} catch (JSONException e) {
    		return -1;
    	}
	}
	
	private Coordinate parse_coordinates(String name) {
    	try {
	        JSONObject json_obj = json.getJSONObject(name);
	        double lat = json_obj.getDouble("lat");
	        double lng = json_obj.getDouble("lng");
	        return new Coordinate(lat, lng);
    	} catch (JSONException e) {
        	return null;
    	}
	}
	
	private Maneuver parse_maneuver() {
		try {
			String maneuver_text = json.getString("maneuver");
			return new Maneuver(maneuver_text);
		} catch (JSONException e) {
			return new Maneuver("");
		}
	}
}
