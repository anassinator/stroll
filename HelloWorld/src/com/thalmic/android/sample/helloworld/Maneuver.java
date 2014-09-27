package com.thalmic.android.sample.helloworld;

public class Maneuver {
	public Direction direction;
	
	public enum Direction {
		FORWARD, BACKWARD, RIGHT, LEFT
	}
	
	public Maneuver(String text) {
		try {
			if (text.equalsIgnoreCase("turn-left")) {
				this.direction = Direction.LEFT;
			} else if (text.equalsIgnoreCase("turn-right")) {
				this.direction = Direction.RIGHT;
			} else {
				this.direction = Direction.FORWARD;
			}
		} catch (NullPointerException e) {
			this.direction = Direction.FORWARD;
		}
	}
	
}
