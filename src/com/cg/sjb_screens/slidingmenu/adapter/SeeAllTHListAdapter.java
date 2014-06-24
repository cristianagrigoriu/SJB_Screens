package com.cg.sjb_screens.slidingmenu.adapter;

import com.cg.sjb_screens.slidingmenu.model.*;
import com.cg.sjb_screens.R;

import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class SeeAllTHListAdapter extends BaseAdapter {
     
    private Context context;
    private ArrayList<SeeAllTHItem> thItems;
     
    public SeeAllTHListAdapter(Context context, ArrayList<SeeAllTHItem> thItems){
        this.context = context;
        this.thItems = thItems;
    }
 
    @Override
    public int getCount() {
        return thItems.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return thItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.th_list, null);
        }
          
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.thImage);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.thName);
        TextView txtCount = (TextView) convertView.findViewById(R.id.thDetails);
          
        imgIcon.setImageResource(thItems.get(position).getIcon());        
        txtTitle.setText(thItems.get(position).getTitle());
        txtCount.setText(thItems.get(position).getDetails());
         
        /*displaying count*/
        /*check whether it set visible or not*/
        //if(thItems.get(position).getCounterVisibility()){
          //  txtCount.setText(thItems.get(position).getCount());
        //}else{
            /*hide the counter view*/
          //  txtCount.setVisibility(View.GONE);
        //}
         
        return convertView;
    }
 
}

