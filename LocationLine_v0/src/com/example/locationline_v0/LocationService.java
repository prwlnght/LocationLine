package com.example.locationline_v0;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

	public static final String PARAM_OUT_MSG = "omsg";
	public static final String PARAM_IN_MSG = "imsg";
	private String resultText = "not initialized";
	GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private String mLatitude;
	private String mLongitude;
	private LocationRequest mLocationRequest;
	
	//Bool to track whether the app is already resolving an error 
	private boolean mResolvingError = false;
	

	private boolean mRequestingLocationUpdates = true;
	private Location mCurrentLocation;
	private String mLastUpdateTime;
	private boolean connected;
	private boolean continuous;
	private Timer timer;
	private TimerTask timerTask;
	final Handler handler = new Handler();
	
	public LocationService() {
		super();
		
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		
		connected = true;
		
	}

	
	
	private void startLocationUpdates() {
		
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);	
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		
		if(mCurrentLocation != null){
			mLatitude = String.valueOf(mCurrentLocation.getLatitude());
			mLongitude  = String.valueOf(mCurrentLocation.getLongitude());
		}
		
		sendLocationBroadCast();
	}
	

	@Override
	public void onCreate(){
		super.onCreate(); 
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener( this)
				.build();
		createLocationRequest();
	}


	public int onStartCommand(Intent intent, int flags, int startID){
		
		//set this acc to what is in intent 
		continuous = true;
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		if(!mResolvingError){
    		mGoogleApiClient.connect();
    	}
		sendLocationBroadCast();
		
		
		//time after every 5 seconds send again
		startTimer();
		
		return super.onStartCommand(intent, flags, startID);
	}
	
	
	public void startTimer(){
		timer = new Timer();
		
		//init the timerTask's job
		initializeTimerTask();
		
		//schedule the timer, after the 
		timer.schedule(timerTask, 500, 1000);
		
	}
	
	
	public void stopTimertask(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}
	
	
	public void initializeTimerTask(){
		timerTask = new TimerTask(){
			

			public void run(){
				handler.post(new Runnable(){

					@Override
					public void run() {
						sendLocationBroadCast();
						
					}
					//do nothing here
				});
			}
		};
	}
	
	
	/*
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		sendLocationBroadCast();
	}
	*/

	private void sendLocationBroadCast() {
		/**
		 * TODO 
		 * 1. Get current location 
		 * 2. Write to db
		 * 3. Wait a few seconds
		 * 4. Loop
		 */
		//getting the updated latitude longitude and sending the broadcast
		
		if(connected){
			mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		}
		if(mLastLocation != null){
			mLatitude = String.valueOf(mLastLocation.getLatitude());
			mLongitude  = String.valueOf(mLastLocation.getLongitude());
		}
		
		/* If connected/disconnected/suspended is not called .. wait.
		 * else write location to database. 
		 * */
		
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		String timeString = "uninit";
		if(mLastUpdateTime != null) timeString = mLastUpdateTime.toString();
		
		if(mLatitude != null) {
			resultText = "Location is:" + mLatitude + "," + mLongitude + "at " + timeString;
			writeLocationToDatabase(mLatitude, mLongitude, mLastUpdateTime);
		}
		else resultText = "location not found";
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainActivity.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, resultText);
		sendBroadcast(broadcastIntent);
		
		
	}

	
	private void writeLocationToDatabase(String mLatitude2, String mLongitude2,
			String mLastUpdateTime2) {
		// TODO write all this information in the database. 
		
		/*
		 * 1. Open of create a LocationLine_LocationDatabase
		 * 2. Write these values to the database, also include date 
		 * 3. 
		 */
		
	}

	protected void createLocationRequest(){
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_SHORT).show();
		
	
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}
