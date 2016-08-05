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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by donita on 30-07-2016.
 */
public  class MyFragment extends Fragment {
    private TextView textView;
    private  ReminderDbAdapter dbHelper;
    private  ArrayList<ReminderInfo> reminderList = new ArrayList<>();


    private ArrayList<ReminderInfo> getRecyclerViewData(int position) {
        String title, name, address, type;
        float latitude, longitude;
        Date date;
        int id;
        boolean status;
        ArrayList<ReminderInfo> reminderList=new ArrayList<>();
        dbHelper=new ReminderDbAdapter(getActivity().getBaseContext());
        dbHelper.open();
        Cursor cursor = dbHelper.fetchAllReminders();
            if(cursor.moveToFirst()) {
                do {
                    status = new Boolean(cursor.getString(cursor.getColumnIndex("status")));
                    if (position == 0) {
                        status = !status;
                    }
                    if (status) {
                        title = cursor.getString(cursor.getColumnIndex("title"));
                        name = cursor.getString(cursor.getColumnIndex("name"));
                        address = cursor.getString(cursor.getColumnIndex("address"));
                        latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                        longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                        formatter.setTimeZone(TimeZone.getDefault());
                        type = cursor.getString(cursor.getColumnIndex("type"));
                        Log.e("donita", TimeZone.getDefault().getDisplayName());
                        try {
                            Log.e("date", "1" + cursor.getString(cursor.getColumnIndex("date")) + "1");

                            date = formatter.parse(cursor.getString(cursor.getColumnIndex("date")));

                        } catch (ParseException e) {
                            e.printStackTrace();
                            date = new Date();
                        }
                        id = cursor.getInt(cursor.getColumnIndex("_id"));
                        status = new Boolean(cursor.getString(cursor.getColumnIndex("status")));
                        reminderList.add(new ReminderInfo(id, title, name, address, latitude, longitude, date, status, type));
                    }

                } while (cursor.moveToNext());
            }
        cursor.close();
        dbHelper.close();
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
           // reminderList.clear();
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
