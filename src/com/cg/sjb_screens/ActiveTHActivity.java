package com.cg.sjb_screens;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.appspot.awesometreasurehunt.identifierapi.Identifierapi;
import com.appspot.awesometreasurehunt.identifierapi.model.Clue;
import com.appspot.awesometreasurehunt.identifierapi.model.TreasureHunt;
import com.appspot.awesometreasurehunt.identifierapi.model.TreasureHuntCollection;
import com.cg.sjb_screens.slidingmenu.adapter.NavDrawerListAdapter;
import com.cg.sjb_screens.slidingmenu.model.NavDrawerItem;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.CorrectionInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ActiveTHActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
																   GooglePlayServicesClient.OnConnectionFailedListener,
																   android.view.View.OnClickListener{

	private GoogleMap googleMap;
	private LocationClient mLocationClient;
	MarkerOptions marker;
	
	Button nextClue;
	
	TreasureHuntCollection myTreasureHunts = new TreasureHuntCollection();
	TreasureHunt currentTH; 
	
	/*members for the sliding menu*/
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    /*nav drawer title*/
    private CharSequence mDrawerTitle;
 
    /*used to store app title*/
    private CharSequence mTitle;
 
    /*slide menu items*/
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    
    boolean isClueFound = false;
    int clueNo;
	
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
        
        /*get list of treasure hunts for user*/
        String[] params = {getImei()};
  		
  		try {
			this.myTreasureHunts = new getTHAsyncTask(ActiveTHActivity.this, new OnTaskCompleted() {
				
				@Override
				public TreasureHuntCollection onTaskCompleted(TreasureHuntCollection myTreasureHunts) {
					return myTreasureHunts;
				}
			}).execute(params).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
  	
  		try {
			currentTH = getNextAvailableTH();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		
  		setUpSlidingMenu();
  		if (savedInstanceState == null) {
            // on first time display view for first nav item
            //displayView(0);
        }
  		
  		clueNo = -1;
	}
		
	private void setUpSlidingMenu() {
		mTitle = mDrawerTitle = getTitle();
		 
        /*load slide menu items*/
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        /*nav drawer icons from resources*/
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        /*adding nav drawer items to array*/
        /*View Journey*/
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        /*Get easier clue*/
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        /*See all clues*/
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        /*Progress*/
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
         
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, /*nav menu toggle icon*/
                R.string.app_name, /*nav drawer open - description for accessibility*/
                R.string.app_name /*nav drawer close - description for accessibility*/
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	}
	
	/**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
 
     /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Intent intent;
        switch (position) {
        case 0:
        	intent = new Intent(this, ViewJourneyActivity.class);
        	startActivity(intent);
            break;
        case 1:
        	/*intent = new Intent(this, FindRoadActivity.class);
        	startActivity(intent);*/
            break;
        case 2:
        	/*intent = new Intent(this, FindRoadActivity.class);
        	startActivity(intent);*/
            break;
        case 3:
        	/*intent = new Intent(this, FindRoadActivity.class);
        	startActivity(intent);*/
            break;
        default:
            break;
        }
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*toggle nav drawer on selecting action bar app icon/title*/
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_settings:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle("Explore!");
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	  public void onClick(View v) {
		  switch (v.getId()) {
	      	case R.id.nextClue:
	      		showClue();
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
	
	private class getTHAsyncTask extends AsyncTask<String, Void, TreasureHuntCollection>{
		  Context context;
		  private OnTaskCompleted listener;
		  TreasureHuntCollection response;

		  public getTHAsyncTask(Context context, OnTaskCompleted listener) {
		    this.context = context;
		    this.listener = listener;
		  }
		  
		  protected void onPreExecute(){ 
		     super.onPreExecute(); 
		  }

		  protected TreasureHuntCollection doInBackground(String... params) {
			  response = null;
		    try {
		    	Identifierapi.Builder builder = new Identifierapi.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		    	Identifierapi service =  builder.build();
				response = service.getTreasureHuntsForId(params[0]).execute();	
		    } catch (Exception e) {
		      Log.d("Could not Add Identifier", e.getMessage(), e);
		    }
		    
		    return response;
		  }

		  protected void onPostExecute(TreasureHuntCollection clues) {
			  super.onPostExecute(clues);
			  listener.onTaskCompleted(clues);
		  }
		}
	
	public interface OnTaskCompleted{
	    TreasureHuntCollection onTaskCompleted(TreasureHuntCollection myTreasureHunts);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private String getImei() {
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mngr.getDeviceId();
		return imei;
	}

	private TreasureHunt getNextAvailableTH() throws InterruptedException, ExecutionException {
		if (this.myTreasureHunts.getItems() != null)
			Log.d("marimea listei de treasure hunt-uri", this.myTreasureHunts.getItems().size() + "");
		if (this.myTreasureHunts.getItems() != null)
			for (TreasureHunt t : this.myTreasureHunts.getItems()) {
				if (!t.getThcompleted()) {
					String[] params = {t.getUniqueId()};
					return new getCurrentTHAsyncTask(ActiveTHActivity.this, new OnTaskCompletedTH() {
						
						@Override
						public TreasureHunt onTaskCompleted(TreasureHunt myTreasureHunts) {
							return myTreasureHunts;
						}
					}).execute(params).get();
					//return t;
				}
			}
		return null;
	}
	
	private void showClue() {
		double clueLatitude = -1, clueLongitude = -1;
		float[] distances = new float[1];
		Location mCurrentLocation = mLocationClient.getLastLocation();
		boolean isCorrectLocation = false;
    	
    	double currentLatitude = mCurrentLocation.getLatitude();
    	double currentLongitude = mCurrentLocation.getLongitude();
    	
    	Clue currentClue = getNextClueinTH();
    	
    	if (currentClue != null) {
	    	clueLatitude = currentClue.getCoordinates().get(0);
	    	clueLongitude = currentClue.getCoordinates().get(1);
    	}
		
    	Location.distanceBetween(currentLatitude, currentLongitude,
    	                		 clueLatitude, clueLongitude,
    	                		 distances);
    	
    	/*check if we are at the last clue*/
  		if (distances[0] <= 1000 || (clueLatitude == -1 && clueLongitude == -1))
  			isCorrectLocation = true;
  		
  		this.buildAlertBox(isCorrectLocation, isCurrentClueFirstClue(currentClue), currentClue);
	}
	
	private void buildAlertBox(boolean isCorrectPlace, boolean isFirstClue, Clue currentClue) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ActiveTHActivity.this);
		String instruction = getNextInstructioninTH(0);
		boolean lastClue = false;
		
    	/*we offer the first clue for free, only after they have to be in the right place*/
    	if (isCorrectPlace == true || isFirstClue == true) {
    		if (isCorrectPlace == true)
    			isClueFound = true;
    		/*0 means easy*/
			if (instruction == null) {
				instruction = "No clues available!";
			}
			else
				if (instruction.equals("")) {
					instruction = "You're reached the end, good for you!";
					lastClue = true;
					updateTHLocally();
					currentTH.setThcompleted(true);
					String[] params = {getImei(), currentTH.getUniqueId()};
					
					new setCurrentTHCompletedAsyncTask(ActiveTHActivity.this).execute(params);
					
					try {
						currentTH = getNextAvailableTH();
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			
			builder.setMessage(instruction)
			       .setTitle("Clue")
		       	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {}
		       	});
			
			if (isCorrectPlace == true && currentTH != null) {
				setClueFound(currentClue);
				String[] params = {getImei(), currentTH.getUniqueId(), Integer.toString(clueNo)};
				new setClueCompletedForTHAsyncTask(ActiveTHActivity.this).execute(params);
			}
    	}
    	else {
    		builder.setMessage("Sorry, you are not there yet, keep on exploring! " + instruction)
    		.setTitle("Clue")
		       	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {}
		       	});
    	}
    	
    	AlertDialog dialog = builder.create();
  		
		dialog.show();
		
		isClueFound = false;
	}
	
	private class setCurrentTHCompletedAsyncTask extends AsyncTask<String, Void, Void>{
		private Context context;
		
		public setCurrentTHCompletedAsyncTask(Context context) {
			this.context = context;
		}

		protected Void doInBackground(String... params) {
		    try {
		    	Identifierapi.Builder builder = new Identifierapi.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Identifierapi service =  builder.build();
				
				service.setTHCompletedForUser(params[0], params[1]).execute();
				
		    } catch (Exception e) {
		      Log.d("Could not Add Identifier", e.getMessage(), e);
		    }
		    return null;
		  }
	}
	
	private class setClueCompletedForTHAsyncTask extends AsyncTask<String, Void, Void>{
		private Context context;
		
		public setClueCompletedForTHAsyncTask(Context context) {
			this.context = context;
		}

		protected Void doInBackground(String... params) {
		    try {
		    	Identifierapi.Builder builder = new Identifierapi.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Identifierapi service =  builder.build();
				
				service.setClueCompletedForTHForUser(params[0], params[1], Integer.parseInt(params[2])).execute();
				
		    } catch (Exception e) {
		      Log.d("Could not Add Identifier", e.getMessage(), e);
		    }
		    return null;
		  }
	}
	
	private class getCurrentTHAsyncTask extends AsyncTask<String, Void, TreasureHunt>{
		private Context context;
		private OnTaskCompletedTH listener;
		
		public getCurrentTHAsyncTask(Context context, OnTaskCompletedTH listener) {
			this.context = context;
			this.listener = listener;
		}
		  
		protected void onPreExecute(){ 
		   super.onPreExecute(); 
		}

		protected TreasureHunt doInBackground(String... params) {
			TreasureHunt response = null;
		    try {
		    	Identifierapi.Builder builder = new Identifierapi.Builder(AndroidHttp.newCompatibleTransport(), 
		    															  new GsonFactory(), 
		    															  null);
				Identifierapi service =  builder.build();
				
				response = service.getTreasureHuntByID(params[0]).execute();
				
		    } catch (Exception e) {
		      Log.d("Could not get Treasure Hunt", e.getMessage(), e);
		    }
		    return response;
		  }

		  protected void onPostExecute(TreasureHunt currentTH) {
			  super.onPostExecute(currentTH);
			  listener.onTaskCompleted(currentTH);
		  }
	}
	
	public interface OnTaskCompletedTH{
	    TreasureHunt onTaskCompleted(TreasureHunt myTreasureHunts);
	}
	
	private boolean isCurrentClueFirstClue(Clue currentClue) {
		if (currentTH != null) {
			if (!currentTH.getAllClues().isEmpty())
				return currentTH.getAllClues().get(0).equals(currentClue);
		}
		return false;
	}
	
	private Clue getNextClueinTH() {
		int localClueNumber = -1;
		if (currentTH != null)
			if (!currentTH.getAllClues().isEmpty())
				for (Clue c : currentTH.getAllClues()) {
					localClueNumber++;
					if (!c.getIsFoundClue()) {
						clueNo = localClueNumber;
						return c;
					}
				}
		return null;
	}
	
	private String getNextInstructioninTH(int difficulty) {
		if (currentTH != null)
			if (!currentTH.getAllClues().isEmpty()) {
				for (Clue c : currentTH.getAllClues())
					if (!c.getIsFoundClue())
						return c.getInstructions().get(difficulty);
				/*it means we finished the TH*/
				return "";
			}
		return null;
	}
	
	private void setClueFound(Clue clueDone) {
		if (currentTH != null)
			if (!currentTH.getAllClues().isEmpty())
				for (Clue c : currentTH.getAllClues())
					if (c.equals(clueDone))
						c.setIsFoundClue(true);
	}
	
	private void updateTHLocally() {
		if (!myTreasureHunts.isEmpty())
			if (!currentTH.isEmpty())
				if (!myTreasureHunts.getItems().isEmpty())
					for (TreasureHunt t : myTreasureHunts.getItems())
						if (t.getUniqueId().equals(currentTH.getUniqueId()))
							t.setThcompleted(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
