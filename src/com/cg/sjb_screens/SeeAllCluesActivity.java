package com.cg.sjb_screens;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.appspot.awesometreasurehunt.identifierapi.Identifierapi;
import com.appspot.awesometreasurehunt.identifierapi.model.Clue;
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SeeAllCluesActivity extends ActionBarActivity{
	TreasureHuntCollection myTHs;
	
	TreasureHunt currentTH;
	
	int clueNo;
	
	/*members for the sliding menu*/
    private ListView mDrawerList;
 
    private ArrayList<SeeAllTHItem> navDrawerItems;
    private SeeAllTHListAdapter adapter;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_all_clues);
 
        mDrawerList = (ListView) findViewById(R.id.clueList);
 
        navDrawerItems = new ArrayList<SeeAllTHItem>();
        
        String thId = getIntent().getExtras().getString("thID");
        
        currentTH = getTH(thId);
        
        if (currentTH.getAllClues() != null) {
	        for (Clue c : currentTH.getAllClues()) {
		        String instr1 = c.getInstructions().get(0);
		        String instr2 = c.getInstructions().get(1);
		        navDrawerItems.add(new SeeAllTHItem(instr1, instr2, R.drawable.ic_map)); 
	        }
		}
        else
        	Toast.makeText(this, "There are no clues in this Treasure Hunt", Toast.LENGTH_SHORT).show();
 
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
        
        setTitle(currentTH.getName());
	}
	
	private TreasureHunt getTH(String thId) {
		TreasureHunt result = new TreasureHunt();
		try {
			String[] params = {thId};
			result =  new getTHAsyncTask(SeeAllCluesActivity.this, new OnTaskCompleted() {
				
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
}
