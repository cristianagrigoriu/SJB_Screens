package com.cg.sjb_screens;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ActiveTHActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
																   GooglePlayServicesClient.OnConnectionFailedListener,
																   android.view.View.OnClickListener{

	private GoogleMap googleMap;
	private LocationClient mLocationClient;
	MarkerOptions marker;
	
	Button nextClue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active_th);

		nextClue = (Button)findViewById(R.id.nextClue);		
		nextClue.setOnClickListener((OnClickListener) this);
		
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		
		marker = null;
		
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            
            try {
                initializeMap();
     
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        	Toast.makeText(this, "Google Play services is not available.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	  public void onClick(View v) {
		  switch (v.getId()) {
	      	case R.id.nextClue:
	      		//Toast.makeText(getApplicationContext(), "DA", Toast.LENGTH_SHORT).show();
	      	// 1. Instantiate an AlertDialog.Builder with its constructor
	      		AlertDialog.Builder builder = new AlertDialog.Builder(this);

	      		// 2. Chain together various setter methods to set the dialog characteristics
	      		builder.setMessage("Loc de intalnire pentru: Caragiale, Goldoni, Shakespeare...")
	      		       .setTitle("Clue")
	      		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			                @Override
			                public void onClick(DialogInterface dialog, int id) {
			                    
			                }
	      		       });

	      		// 3. Get the AlertDialog from create()
	      		AlertDialog dialog = builder.create();
	      		dialog.show();
	    	default:
	    		break;
	     }
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.active_th, menu);
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
	
	@Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		Log.d("Succes", "connected");

    }
	
	@Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }
	
	@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
  
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        /*connect the client*/
        mLocationClient.connect();
        initializeMap();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        mLocationClient.connect();
        initializeMap();
    }
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		/*load map*/
		try {
            initializeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		/*show current location*/
		try {
            setMarkerAtCurrentLocation();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void initializeMap() {
        if (googleMap == null) {
        	
         // Get a handle to the Map Fragment
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            
            /*map type*/
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            
            /*a button to get you back home*/
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            
            this.setMarkerAtCurrentLocation();
            
            /*one can see the current location*/
            //googleMap.setMyLocationEnabled(true);
 
            // check if map is created successfully or not
            if (googleMap == null)
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }
    }
	
	public void setMarkerAtCurrentLocation() {		
    	Location mCurrentLocation = mLocationClient.getLastLocation();
    	
    	double latitude = mCurrentLocation.getLatitude();
    	double longitude = mCurrentLocation.getLongitude();
    	
    	/*move camera to current position*/
    	CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(15).build();
 
    	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    	
    	/*create marker*/
    	if (marker == null) {
	    	marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").visible(true);
	    	
	    	/*change to blue colour*/
	    	marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
	    	
	    	/*add marker to map*/
	    	googleMap.addMarker(marker);
    	}
    }

}
