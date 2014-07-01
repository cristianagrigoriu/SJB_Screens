package com.cg.sjb_screens;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngCreator;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class ViewJourneyActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
															 GooglePlayServicesClient.OnConnectionFailedListener,
															 android.view.View.OnClickListener{

	private GoogleMap googleMap;
	private LocationClient mLocationClient;
	
    private ArrayList<LatLng> markerPoints;
    
    Button postFB;
    
    StepsHandler stepsHandler = new StepsHandler(new Runnable() {
        @Override 
        public void run() {
        	Location mCurrentLocation = mLocationClient.getLastLocation();
        	
        	double currentLatitude = mCurrentLocation.getLatitude();
        	double currentLongitude = mCurrentLocation.getLongitude();
        	
        	Toast.makeText(ViewJourneyActivity.this, currentLatitude + " " + currentLongitude, Toast.LENGTH_SHORT).show();
        }
   });
    
    public class StepsHandler {
        // Create a Handler that uses the Main Looper to run in
        private Handler mHandler = new Handler(Looper.getMainLooper());

        private Runnable mStatusChecker;
        private int UPDATE_INTERVAL = 2000 * 5; //10 sec

        /**
         * Creates an UIUpdater object, that can be used to
         * perform UIUpdates on a specified time interval.
         * 
         * @param uiUpdater A runnable containing the update routine.
         */
        public StepsHandler(final Runnable stepsHandler) {
            mStatusChecker = new Runnable() {
                @Override
                public void run() {
                    // Run the passed runnable
                	stepsHandler.run();
                    // Re-run it after the update interval
                    mHandler.postDelayed(this, UPDATE_INTERVAL);
                }
            };
        }

        /**
         * The same as the default constructor, but specifying the
         * intended update interval.
         * 
         * @param uiUpdater A runnable containing the update routine.
         * @param interval  The interval over which the routine
         *                  should run (milliseconds).
         */
        public StepsHandler(Runnable stepsHandler, int interval){
        	this(stepsHandler);
        	UPDATE_INTERVAL = interval;
        }

        /**
         * Starts the periodical update routine (mStatusChecker 
         * adds the callback to the handler).
         */
        public synchronized void startUpdates(){
            mStatusChecker.run();
        }

        /**
         * Stops the periodical update routine from running,
         * by removing the callback.
         */
        public synchronized void stopUpdates(){
            mHandler.removeCallbacks(mStatusChecker);
        }
}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_journey);
		
		postFB = (Button)findViewById(R.id.postFB);		
		postFB.setOnClickListener((OnClickListener) this);
		
		// Initializing
        markerPoints = new ArrayList<LatLng>();
        
        mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
 
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
 
        if(googleMap!=null){
 
            // Enable MyLocation Button in the Map
        	googleMap.setMyLocationEnabled(true);
 
            // Setting onclick event listener for the map
        	/*googleMap.setOnMapClickListener(new OnMapClickListener() {
 
                @Override
                public void onMapClick(LatLng point) {
 
                    // Already two locations
                    if(markerPoints.size()>1){
                        markerPoints.clear();
                        googleMap.clear();
                    }
 
                    // Adding new item to the ArrayList
                    markerPoints.add(point);
 
                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();
 
                    // Setting the position of the marker
                    options.position(point);
 
                    /**
                    * For the start location, the color of marker is GREEN and
                    * for the end location, the color of marker is RED.
                    */
                    /*if(markerPoints.size()==1){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
 
                    // Add new marker to the Google Map Android API V2
                    googleMap.addMarker(options);
 
                    // Checks, whether start and end locations are captured
                    if(markerPoints.size() >= 2){
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);
 
                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);
 
                        DownloadTask downloadTask = new DownloadTask();
 
                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }
            });*/
        }
                
        
	}

	@Override
	  public void onClick(View v) {
		  switch (v.getId()) {
	      	case R.id.postFB:
	      		makeScreenshot();
	    	default:
	    		break;
	     }
	  }
	
	private void initializeMap() {
        if (googleMap == null) {
        	
         // Get a handle to the Map Fragment
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            
            /*map type*/
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
 
            // check if map is created successfully or not
            if (googleMap == null)
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }
    }
	
	private void drawRoad() {
		String[] originCoord = null;
        LatLng origin = null;
        
        if (getIntent().getExtras() != null) {
        	originCoord = getIntent().getExtras().getString("originCoord").split(" ");
        
        	if (originCoord.length > 1)
        	{
		        origin = new LatLng(Double.parseDouble(originCoord[0]), Double.parseDouble(originCoord[1]));
		        
		        Location mCurrentLocation = mLocationClient.getLastLocation();
		    	
		    	double currentLatitude = mCurrentLocation.getLatitude();
		    	double currentLongitude = mCurrentLocation.getLongitude();
		        
		    	LatLng dest = new LatLng(currentLatitude, currentLongitude);
		    	
		        String url = getDirectionsUrl(origin, dest);
		        
		        DownloadTask downloadTask = new DownloadTask();
		
		        // Start downloading json data from Google Directions API
		        downloadTask.execute(url);
        	}
        	else
        		Toast.makeText(this, "No clues available", Toast.LENGTH_SHORT).show();
        }
        else
        	Toast.makeText(this, "No road available", Toast.LENGTH_SHORT).show(); 
	}
	
	private void makeScreenshot() {
		// create bitmap screen capture
        Bitmap bitmap;
        View mCurrentUrlMask = getWindow().getDecorView();
		View v1 = mCurrentUrlMask.getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "sjb1.jpg";
        OutputStream fout = null;
        File imageFile = new File(mPath);

        try {
            fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    
 // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
 
            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }
	
	@Override
    protected void onStart() {
        super.onStart();
        /*connect the client*/
        mLocationClient.connect();
        initializeMap();
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        /*disconnect the client*/
        //mLocationClient.disconnect();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        mLocationClient.connect();
        initializeMap();
    }

	@Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.disconnect();
        stepsHandler.stopUpdates();
        //stopRepeatingTask();
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		//stepsHandler.startUpdates();
		drawRoad();
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
		stepsHandler.stopUpdates();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_journey, menu);
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
}
