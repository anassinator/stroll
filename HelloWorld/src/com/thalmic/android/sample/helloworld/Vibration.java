package com.thalmic.android.sample.helloworld;
import java.util.Timer;
import java.util.TimerTask;
import com.thalmic.myo.Myo;

public class Vibration{
	private Myo myMyo;
	private Timer vibrationTimer;
	private VibrationTask vibrationTask;
	private long period = 2000;
	
	public Vibration(Myo myo){
		myMyo = myo;		
	}
	public void loop(){
		vibrationTimer = new Timer();
		vibrationTask= new VibrationTask();
		vibrationTimer.schedule(vibrationTask, period);
	}
	
	public void start(){
		myMyo.vibrate(Myo.VibrationType.SHORT);
		loop();
	}
	public void cancel(){
		if(vibrationTimer !=null){
		vibrationTimer.cancel();
		vibrationTask.cancel();
		}
	}
	public void setPeriod(long time){
		period = time;
	}

	class VibrationTask extends TimerTask{
		public VibrationTask(){
		}
		
		@Override
		public void run(){
			myMyo.vibrate(Myo.VibrationType.SHORT);
			loop();
		}
	}
	
	
	                                    

}
