package com.example.locationline_v0;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;



public class LocationGatheringService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

	
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	
	
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper){
			super(looper);
		}
	
		@Override
		public void handleMessage(Message msg){
			//do some work here i.e. send broadcast/write to teh database
			sendLocationBroadCast();
			//writeLocationToDatabase();
		}
		
		//stop the service using the startid -- not needed, the service will continue to run. 
	
	}
	
	
	@Override
	public void onCreate(){
		//start up the thread running the service. Note that we create a separate thread because the 
		//thread normally runs in the processe's main thread, which we don't want to block. We also make it
		//backgroud priority so CPU-intensive work will not disrupt our UI. 
		
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_FOREGROUND);
		thread.start();
		
		//Get the HandlerThread's looper and user it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		
	}
	
	
	
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	

	private void sendLocationBroadCast() {
		
		/*
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		String timeString = "uninit";
		if(mLastUpdateTime != null) timeString = mLastUpdateTime.toString();
		
		if(mLatitude != null) resultText = "Location is:" + mLatitude + "," + mLongitude + "at " + timeString;
		else resultText = "location not found";
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainActivity.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, resultText);
		sendBroadcast(broadcastIntent);
		*/
	}



}





/**
public class LocationGatheringService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

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
	
	
	public LocationGatheringService() {
		super("LocationService");
		
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null){
			mLatitude = String.valueOf(mLastLocation.getLatitude());
			mLongitude  = String.valueOf(mLastLocation.getLongitude());
		}
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
		 
	}


	public int onStartCommand(Intent intent, int flags, int startID){
		
		//set this acc to what is in intent 
		boolean continuous = true;
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		if(!mResolvingError){
    		mGoogleApiClient.connect();
    	}
		
		sendLocationBroadCast();
		
		if(continuous){
			startLocationUpdates();
		}
		return super.onStartCommand(intent, flags, startID);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
	

	private void sendLocationBroadCast() {
		
	
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		String timeString = "uninit";
		if(mLastUpdateTime != null) timeString = mLastUpdateTime.toString();
		
		if(mLatitude != null) resultText = "Location is:" + mLatitude + "," + mLongitude + "at " + timeString;
		else resultText = "location not found";
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainActivity.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, resultText);
		sendBroadcast(broadcastIntent);
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
*/