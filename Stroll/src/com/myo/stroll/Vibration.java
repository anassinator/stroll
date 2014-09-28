package com.myo.stroll;

import android.util.Log;
import com.thalmic.myo.Myo;

public class Vibration extends Thread{
	private Myo myMyo;
	private long period = 2000;
	public Boolean vibrating = false;
	
	public Vibration(Myo myo) {
		myMyo = myo;		
	}   
	
	public void setPeriod(long newPeriod) {
		period = newPeriod;
	}
	
	public void startVibrating() {
		vibrating = true;
	}
	
	public void stopVibrating() {
		vibrating = false;
	}
	
	public void run() {
		myMyo.vibrate(Myo.VibrationType.LONG);
		
		while (true){
			if (vibrating) {
				myMyo.vibrate(Myo.VibrationType.SHORT);
			}
			
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
