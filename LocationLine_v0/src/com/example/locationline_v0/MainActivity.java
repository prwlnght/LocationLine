package com.example.locationline_v0;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

	private LocationRequest mLocationRequest;
	private LocationResponseReceiver receiver;
	private Location mLastLocation;
	private String mLatitude;
	private String mLongitude;
	GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError;
	private boolean mRequestingLocationUpdates = true;
	private Location mCurrentLocation;
	
	
	public static final String ACTION_RESP = 
			"com.example.locationline_v0.MESSAGE_PROCESSED";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        IntentFilter filter = new IntentFilter(LocationResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new LocationResponseReceiver();
        registerReceiver(receiver, filter);
        
        
      //REMOVE if using a service to gather
        buildGoogleApiClient();
        createLocationRequest();
    }

    
    protected void createLocationRequest(){
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
	}
    
    protected synchronized void buildGoogleApiClient(){
    	mGoogleApiClient = new GoogleApiClient.Builder(this)
    		.addConnectionCallbacks(this)
    		.addOnConnectionFailedListener(this)
    		.addApi(LocationServices.API)
    		.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    public void beginGatheringHere(View v){
    	//REMOVE if using a service to gather
    	
    	
    	
    	TextView timeUpdate = (TextView) findViewById(R.id.textview_update_result);
		String text = "location gathering here ...";
		timeUpdate.setText(text);
		
		if(!mResolvingError){
    		mGoogleApiClient.connect();
    	}
    }
    
    
    public void beginGathering(View v){
    	//start a service to gather location data
    	Intent locationServiceIntent = new Intent(this, LocationService.class);
    	//TODO get this information from an edit text for number of hours. 
    	locationServiceIntent.putExtra("Duration", "24");
    	startService(locationServiceIntent);
    	//TODO disable gather button 
    }
    
    
    public class LocationResponseReceiver extends BroadcastReceiver{

    	public static final String ACTION_RESP = 
    			"com.example.locationline_v0.MESSAGE_PROCESSED";
		@Override
		public void onReceive(Context context, Intent intent) {
			TextView timeUpdate = (TextView) findViewById(R.id.textview_update_result);
			String text = intent.getStringExtra(LocationService.PARAM_OUT_MSG);
			timeUpdate.setText(text);
		}
    	
    }


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null){
			mLatitude = String.valueOf(mLastLocation.getLatitude());
			mLongitude  = String.valueOf(mLastLocation.getLongitude());
		}
		
		TextView timeUpdate = (TextView) findViewById(R.id.textview_update_result);
		String text = "" + mLatitude + "," + mLongitude;
		timeUpdate.setText(text);
		
		if(mRequestingLocationUpdates ){
			startLocationUpdates();
		}
		
	}
	
	private void startLocationUpdates() {
		Log.d("location related", "starting waiting for updates");
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);	
		Log.d("location related", "successfully started waiting for updates");
	}
	
	

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		
		if(mCurrentLocation != null){
			mLatitude = String.valueOf(mCurrentLocation.getLatitude());
			mLongitude  = String.valueOf(mCurrentLocation.getLongitude());
		}
		
		TextView timeUpdate = (TextView) findViewById(R.id.textview_update_result);
		String text = "" + mLatitude + "," + mLongitude;
		timeUpdate.setText(text);
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Toast.makeText(this, "ConnectionSuspended", Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	public void onStop(){
		mGoogleApiClient.disconnect();
		super.onStop();
	}
	
}
