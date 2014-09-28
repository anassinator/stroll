package com.myo.stroll;

import org.json.*;

public class RequestHandler {
    private JSONObject json;
	public Route[] routes;

    public RequestHandler(JSONObject json) {
        this.json = json;
        parse();
    }

    private void parse() {
    	try {
	        JSONArray json_routes = json.getJSONArray("routes");
	        routes = new Route[json_routes.length()];
        
	        for (int i = 0; i < json_routes.length(); i++) {
	        	try {
		            JSONObject route = json_routes.getJSONObject(i);
		            routes[i] = parse_route(route);
	        	} catch (JSONException e) {
	        		
	        	}
	        }
    	} catch (JSONException e) {
    		
    	}
    }
    
    private Route parse_route(JSONObject route) {
    	try {
	        JSONArray json_legs = route.getJSONArray("legs");
	        JSONArray json_steps = json_legs.getJSONObject(0).getJSONArray("steps");
	
	        Step[] steps = new Step[json_steps.length()];
	        for (int j = 0; j < json_steps.length(); j++) {
	            steps[j] = new Step(json_steps.getJSONObject(j));
	        }
	        
	        return new Route(steps);
    	} catch (JSONException e) {
        	return null;
    	}
    }
}
