package com.bignerdranch.android.locationbasedreminders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by donita on 30-07-2016.
 */
public  class MyFragment extends Fragment {
    private TextView textView;
    private  ReminderDbAdapter dbHelper;
    private  ArrayList<ReminderInfo> reminderList = new ArrayList<>();
    /*  public static MyFragment getInstance(int position)
      {
          MyFragment myFragment=new MyFragment();
          Bundle args=new Bundle();
          args.putInt("position",position);
          myFragment.setArguments(args);
          return myFragment;
      }*/
    public MyFragment(){

    }
    private ArrayList<ReminderInfo> getRecyclerViewData(int position) {
        String title, name, address;
        float latitude, longitude;
        Date date;
        int id;
        boolean status;
        ArrayList<ReminderInfo> reminderList=new ArrayList<>();
        dbHelper=new ReminderDbAdapter(getActivity().getBaseContext());
        dbHelper.open();
        Cursor cursor = dbHelper.fetchAllReminders();
        if (cursor.moveToFirst()){
            do{
                status=new Boolean(cursor.getString(cursor.getColumnIndex("status")));
                Log.d("status",status+"");
                if(!status&&position==0){
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                    longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                    date = new Date(cursor.getString(cursor.getColumnIndex("date")));
                    id=cursor.getInt(cursor.getColumnIndex("_id"));
                    reminderList.add(new ReminderInfo(id,title, name, address, latitude, longitude, date));
                }
                else if(status&&position==1){
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                    longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                    date = new Date(cursor.getString(cursor.getColumnIndex("date")));
                    id=cursor.getInt(cursor.getColumnIndex("_id"));
                    reminderList.add(new ReminderInfo(id,title, name, address, latitude, longitude, date));
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        return reminderList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedINstanceState)
    {
        Log.d("Create","in create view");
        View layout=inflater.inflate(R.layout.my_fragment,container,false);
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            RecyclerView recyclerView=(RecyclerView) layout.findViewById(R.id.cardList);
            recyclerView.setHasFixedSize(true);
            final ReminderInfoAdapter reminderInfoAdapter=new ReminderInfoAdapter(reminderList);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(reminderInfoAdapter);
            reminderList.clear();
            reminderInfoAdapter.notifyDataSetChanged();
            reminderList.addAll(getRecyclerViewData(bundle.getInt("position")));
               /* if(bundle.getInt("position")==0){
                    reminderList.addAll(getRecyclerViewData(true));
                }else if(bundle.getInt("position")==1){
                    reminderList.addAll(getRecyclerViewData(false));
                }*/
            reminderInfoAdapter.notifyDataSetChanged();
        }
        return layout;
    }


}
