package com.myo.stroll;

public class Route {
	Step[] steps;
	int distance = 0;
	
	public Route(Step[] steps) {
		this.steps = steps;
		for (int i = 0; i < steps.length; i++) {
			this.distance += steps[i].distance;
		}
	}
}
