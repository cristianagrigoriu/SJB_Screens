package com.cg.sjb_screens;

public class UserIdentifier {
	String id;
	String name;
	
	public UserIdentifier() {}
	
	public UserIdentifier(String id) {
		this.id = id;
	}
	
	public UserIdentifier(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getUserId() {
		return this.id;
	}
	
	public String getUserName() {
		return this.name;
	}
}
