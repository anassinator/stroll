/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.myo.stroll;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;

import com.myo.stroll.R;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import com.google.android.gms.maps.model.LatLng;

public class StrollActivity extends Activity implements LocationListener{

    // This code will be returned in onActivityResult() when the enable Bluetooth activity exits.
    private static final int REQUEST_ENABLE_BT = 1;
    private Vibration vibration;
    
    // Layouts
    private FrameLayout pairStepLayout;
    private FrameLayout searchStepLayout;
    private FrameLayout progressStepLayout;
    
    private TextView percentage;
    private EditText searchField; 

    private float pitch;
    private float roll;
    private float yaw;
    private double latitude;
    private double longitude;
    private int current_step = 0;
    private LatLng current_location;
    private String current_pose;
    private Route route;
    
    Location location_variable;
    LocationManager locationManager ;
    String provider;

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        private Arm mArm = Arm.UNKNOWN;
        private XDirection mXDirection = XDirection.UNKNOWN;

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
        	Toast.makeText(getApplicationContext(), "Myo Connected", Toast.LENGTH_LONG).show();
            Log.w("debugger", "Wut");
            vibration = new Vibration(myo);
            vibration.start();
            LocationHandler.set_vibration(vibration);
            
            pairStepLayout.setVisibility(FrameLayout.GONE);
            searchStepLayout.setVisibility(FrameLayout.VISIBLE);
            progressStepLayout.setVisibility(FrameLayout.GONE);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
        	Toast.makeText(getApplicationContext(), "Myo Disconnected", Toast.LENGTH_LONG).show();
        	
        	pairStepLayout.setVisibility(FrameLayout.VISIBLE);
        	searchStepLayout.setVisibility(FrameLayout.GONE);
        	progressStepLayout.setVisibility(FrameLayout.GONE);
        }

        // onArmRecognized() is called whenever Myo has recognized a setup gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmRecognized(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            mArm = arm;
            mXDirection = xDirection;
            Toast.makeText(getApplicationContext(), "Arm Detected", Toast.LENGTH_LONG).show();
        }

        // onArmLost() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmLost(Myo myo, long timestamp) {
            mArm = Arm.UNKNOWN;
            mXDirection = XDirection.UNKNOWN;
            Toast.makeText(getApplicationContext(), "Arm Lost", Toast.LENGTH_LONG).show();
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (mXDirection == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

        	LocationHandler.update_orientation(rotation);
        	String rpy = String.format("R: %-3.0f\nP: %-3.0f\nY: %-3.0f\n%s\n%-3.0f\n%3.2f\n%3.2f", roll, pitch, yaw, current_pose, Coordinates.bearing, current_location.longitude, current_location.latitude);

        	Log.w("Orientation",rpy);
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
        	switch (pose) {
        		case WAVE_OUT:
        			current_pose = "WAVE OUT";
        			LocationHandler.search(true);
        			break;
        		case UNKNOWN:
        			current_pose = "UNKNOWN";
        			LocationHandler.search(false);
        			break;       			
        		case REST:
        			current_pose = "REST";
        			LocationHandler.search(true);
        			break;     
        		case THUMB_TO_PINKY:
        			current_pose = "PINKY";
        			LocationHandler.search(false);
        			break;     
        		case WAVE_IN:
        			current_pose = "WAVE IN";
        			LocationHandler.search(false);
        			break;     
        		case FIST:
        			current_pose = "FIST";
        			LocationHandler.search(false);
        			break;
        		case FINGERS_SPREAD:
        			current_pose = "FINGERS";
        			LocationHandler.search(false);
        			break;
        		default:
        	}
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_hello_world);
       
        pairStepLayout = (FrameLayout) findViewById(R.id.pair_step);
        searchStepLayout = (FrameLayout) findViewById(R.id.search_step);
        progressStepLayout = (FrameLayout) findViewById(R.id.progress_step);
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        
        if(provider!=null && !provider.equals("")){
        	 
            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);
            location_variable = location;
            locationManager.requestLocationUpdates(provider, 20000, 1, this);
 
            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
 
        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        
        searchField = (EditText) searchStepLayout.findViewById(R.id.search_field);
        percentage = (TextView) progressStepLayout.findViewById(R.id.percent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If Bluetooth is not enabled, request to turn it on.
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth, so exit.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (R.id.action_scan == id) {
//            onScanActionSelected();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void onScanActionSelected(View view) {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onLocationChanged(Location location) {
    	Log.w("debugger", "you moved");
    	longitude = location.getLongitude();
    	latitude = location.getLatitude();
    	current_location = new LatLng(latitude, longitude);
    	if (route != null) {
	    	if (LocationHandler.update_location(current_location, route.steps[current_step].end_location)) {
	    		current_step++;
	    	}
	    	double distance_left = Coordinates.distance;
	    	for (int i = current_step; i < route.steps.length; i++) {
	    		distance_left += route.steps[i].distance;
	    	}
	    	int percent_distance = (int)(distance_left / (double)route.distance * 100);
	    	percent_distance = Math.max(percent_distance, 0);
	    	
	    	percentage.setText(String.valueOf(100 - percent_distance));
    	}
    	Log.w("Location", Double.toString(longitude)+":"+Double.toString(latitude));
    }
    
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    
    public void onSearchButtonClicked(View view) {
        Log.w("debugger", "Waiting..");
        JSONObject directions;
		try {
			directions = new RequestBuilder().execute(String.valueOf(current_location.latitude) + "," + String.valueOf(current_location.longitude), searchField.getText().toString()).get();
				
            Log.w("debugger", "Yay! Got directions");
            Route[] routes = new RequestHandler(directions).routes;
            if (routes.length > 0) {
            	Toast.makeText(getApplicationContext(), "Directions found", Toast.LENGTH_LONG).show();
            	route = routes[0];
            } else {
            	Toast.makeText(getApplicationContext(), "Directions not found", Toast.LENGTH_LONG).show();
            	return;
            }
            onLocationChanged(location_variable);
            
            pairStepLayout.setVisibility(FrameLayout.GONE);
            searchStepLayout.setVisibility(FrameLayout.GONE);
            progressStepLayout.setVisibility(FrameLayout.VISIBLE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
