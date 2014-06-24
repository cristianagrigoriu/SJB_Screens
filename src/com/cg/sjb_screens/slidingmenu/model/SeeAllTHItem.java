package com.cg.sjb_screens.slidingmenu.model;

public class SeeAllTHItem {
    
    private String title;
    private String details;
    private int icon;
    private String count = "0";
    private boolean isCounterVisible = false;
     
    public SeeAllTHItem(){}
 
    public SeeAllTHItem(String title, String details, int icon){
        this.title = title;
        this.details = details;
        this.icon = icon;
    }
     
    public SeeAllTHItem(String title, String details, int icon, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }
     
    public String getTitle(){
        return this.title;
    }
     
    public String getDetails() {
    	return this.details;
    }
    
    public int getIcon(){
        return this.icon;
    }
     
    public String getCount(){
        return this.count;
    }
     
    public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }
     
    public void setTitle(String title){
        this.title = title;
    }
    
    public void setDetails(String details){
        this.details = details;
    }
     
    public void setIcon(int icon){
        this.icon = icon;
    }
     
    public void setCount(String count){
        this.count = count;
    }
     
    public void setCounterVisibility(boolean isCounterVisible){
        this.isCounterVisible = isCounterVisible;
    }
}
