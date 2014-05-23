package com.cg.sjb_screens;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
		
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		UserIdentifier newUser = new UserIdentifier(mngr.getDeviceId(), "cris");
		UserIdentifierService newUserIdService = new UserIdentifierService(newUser);
		try {
			Toast.makeText(getApplicationContext(), newUserIdService.addUserId(), Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
