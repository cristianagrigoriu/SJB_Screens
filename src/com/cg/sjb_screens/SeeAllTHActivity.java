package com.cg.sjb_screens;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.appspot.awesometreasurehunt.identifierapi.Identifierapi;
import com.appspot.awesometreasurehunt.identifierapi.model.TreasureHunt;
import com.appspot.awesometreasurehunt.identifierapi.model.TreasureHuntCollection;
import com.cg.sjb_screens.slidingmenu.adapter.SeeAllTHListAdapter;
import com.cg.sjb_screens.slidingmenu.model.SeeAllTHItem;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class SeeAllTHActivity extends ActionBarActivity{
	TreasureHuntCollection myTHs;
	
	/*members for the sliding menu*/
    private ListView mDrawerList;
 
    private ArrayList<SeeAllTHItem> navDrawerItems;
    private SeeAllTHListAdapter adapter;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_all_th);
 
        mDrawerList = (ListView) findViewById(R.id.thList);
 
        navDrawerItems = new ArrayList<SeeAllTHItem>();
 
        //aici populez lista
        String[] params = {getImei()};
        
        try {
			myTHs = new getTHForIdAsyncTask(SeeAllTHActivity.this, new OnTaskCompletedTH() {
				
				@Override
				public TreasureHuntCollection onTaskCompleted(TreasureHuntCollection myTreasureHunts) {
					return myTreasureHunts;
				}
			}).execute(params).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        for (int i=0; i<myTHs.getItems().size(); i++) {
        	TreasureHunt t = getTH(myTHs.getItems().get(i).getUniqueId());
        	String details = "No Clues: ";
        	if (t.getAllClues() == null)
        		details += "0";
        	else
        		details += t.getAllClues().size();
        	navDrawerItems.add(new SeeAllTHItem(t.getName(), details, R.drawable.ic_map));
        }
 
        // setting the nav drawer list adapter
        adapter = new SeeAllTHListAdapter(getApplicationContext(),
                navDrawerItems);
        if (mDrawerList == null)
        	Log.d("problem", "mDrawerList null");
        if (adapter == null)
        	Log.d("problem", "adapter null");
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}

	private TreasureHunt getTH(String thId) {
		TreasureHunt result = new TreasureHunt();
		try {
			String[] params = {thId};
			result =  new getTHAsyncTask(SeeAllTHActivity.this, new OnTaskCompleted() {
				
				@Override
				public TreasureHunt onTaskCompleted(TreasureHunt myTreasureHunts) {
					return myTreasureHunts;
				}
			}).execute(params).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_all_th, menu);
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
	
	private class getTHForIdAsyncTask extends AsyncTask<String, Void, TreasureHuntCollection>{
		private Context context;
		private OnTaskCompletedTH listener;
		
		public getTHForIdAsyncTask(Context context, OnTaskCompletedTH listener) {
			this.context = context;
			this.listener = listener;
		}
		  
		protected void onPreExecute(){ 
		   super.onPreExecute(); 
		}

		protected TreasureHuntCollection doInBackground(String... params) {
			TreasureHuntCollection response = null;
		    try {
		    	Identifierapi.Builder builder = new Identifierapi.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Identifierapi service =  builder.build();
				
				response = service.getTreasureHuntsForId(params[0]).execute();
				
		    } catch (Exception e) {
		      Log.d("Could not Add Identifier", e.getMessage(), e);
		    }
		    return response;
		  }

		  protected void onPostExecute(TreasureHuntCollection currentTH) {
			  super.onPostExecute(currentTH);
			  listener.onTaskCompleted(currentTH);
		  }
	}
	
	public interface OnTaskCompletedTH{
	    TreasureHuntCollection onTaskCompleted(TreasureHuntCollection myTreasureHunts);
	}
	
	private class getTHAsyncTask extends AsyncTask<String, Void, TreasureHunt>{
		private Context context;
		private OnTaskCompleted listener;
		
		public getTHAsyncTask(Context context, OnTaskCompleted listener) {
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
	
	public interface OnTaskCompleted{
	    TreasureHunt onTaskCompleted(TreasureHunt myTreasureHunts);
	}
	
	private String getImei() {
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mngr.getDeviceId();
		return imei;
	}
}
