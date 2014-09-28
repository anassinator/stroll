package com.myo.stroll;

public class Route {
	Step[] steps;
	int distance = 0;
	String end_address;
	
	public Route(Step[] steps, String end) {
		this.steps = steps;
		this.end_address = end;
		for (int i = 0; i < steps.length; i++) {
			this.distance += steps[i].distance;
		}
	}
}
