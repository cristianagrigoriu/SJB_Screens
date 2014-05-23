package com.cg.sjb_screens;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UserIdentifierService{
	UserIdentifier userId;
	
	public UserIdentifierService(UserIdentifier userIdentifier) {
		this.userId = userIdentifier;
	}
	
	public String addUserId() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://awesometreasurehunt.appspot.com/_ah/api/identifierapi/v1/addId/" + this.userId.getUserId() + "/" + this.userId.getUserName());
		
		/*List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("id", this.userId.getUserId()));
		pairs.add(new BasicNameValuePair("name", this.userId.getUserName()));
		post.setEntity(new UrlEncodedFormEntity(pairs));*/
		
		HttpResponse response = client.execute(post);
		return response.toString();
	}
}
