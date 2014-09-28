package com.thalmic.android.sample.helloworld;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.thalmic.myo.Myo;

public class Vibration{
	private Myo myMyo;
	private Timer vibrationTimer;
	private VibrationTask vibrationTask;
	private long period = 2000;
	public Boolean vibrating = false;
	
	public Vibration(Myo myo) {
		myMyo = myo;		
	}

	public void loop() {
		vibrationTimer.schedule(vibrationTask, 100, period);
	}
	
	public void start() {
		if (!vibrating) {
			vibrating = true;
			vibrationTimer = new Timer();
			vibrationTask = new VibrationTask();
			myMyo.vibrate(Myo.VibrationType.SHORT);
			loop();
		}
	}

	public void stop() {
		if (vibrating) {
			vibrationTimer.cancel();
			vibrationTask.cancel();
			vibrating = false;
		}
	}

	public void start(long time) {
		setPeriod(time);
		start();
	}

	public void setPeriod(long time) {
		period = Math.abs(time);
	}

	class VibrationTask extends TimerTask{
		public VibrationTask() {
		}
		
		@Override
		public void run(){
			myMyo.vibrate(Myo.VibrationType.SHORT);
			Log.w("debugger", "running like crazy");
		}
	}
	
	
	                                    

}
