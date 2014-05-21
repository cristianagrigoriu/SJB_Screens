package com.cg.sjb_screens;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements android.view.View.OnClickListener {

	Button btnStartTH;
	Button btnSeeAllTH;
	Button btnContactFriends;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnStartTH = (Button)findViewById(R.id.startTH);		
		btnSeeAllTH = (Button)findViewById(R.id.seeAllTH);
		btnContactFriends = (Button)findViewById(R.id.contactFriends);
		
		btnStartTH.setOnClickListener((OnClickListener) this);
		btnSeeAllTH.setOnClickListener((OnClickListener) this);
		btnContactFriends.setOnClickListener((OnClickListener) this);
	}
	
	  @Override
	  public void onClick(View v) {
		  switch (v.getId()) {
	      	case R.id.startTH:
	      		Toast.makeText(getApplicationContext(), "Start a new Treasure Hunt", Toast.LENGTH_LONG).show();
	    		break;
	    	case R.id.seeAllTH:
	    		Toast.makeText(getApplicationContext(), "See all Treasure Hunts", Toast.LENGTH_LONG).show();
	    		break;
	    	case R.id.contactFriends:
	    		Toast.makeText(getApplicationContext(), "Contact Friends", Toast.LENGTH_LONG).show();
	    		break;
	    	default:
	    		break;
	     }
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
}
