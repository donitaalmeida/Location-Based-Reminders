package com.bignerdranch.android.locationbasedreminders;


import android.content.Intent;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by donita on 12-07-2016.
 * adapter for recycler view for nearby place listing
 */
public class ReminderInfoAdapter extends RecyclerView.Adapter<ReminderInfoAdapter.PlaceViewHolder>{

    private List<ReminderInfo> placeList;
    ReminderInfo ci;

    public List<ReminderInfo> getPlaceList(){
        return placeList;
    }

    public ReminderInfoAdapter(List<ReminderInfo> placeList){
        this.placeList=placeList;
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder placeViewHolder, int i) {
        ci = placeList.get(i);;
        placeViewHolder.vTitle.setText(ci.title);
        placeViewHolder.vAddress.setText(ci.address);
        placeViewHolder.vName.setText(ci.name);

        if(ci.type.equals("Grocery")){
            placeViewHolder.vTypeImage.setImageResource(R.drawable.grocery);
        }else if(ci.type.equals("ATM")){
            placeViewHolder.vTypeImage.setImageResource(R.drawable.atm);
        }else if(ci.type.equals("Gas")){
            placeViewHolder.vTypeImage.setImageResource(R.drawable.gas);
        }else if(ci.type.equals("Medicine")){
            placeViewHolder.vTypeImage.setImageResource(R.drawable.medicine);
        }else{
            placeViewHolder.vTypeImage.setImageResource(R.drawable.pin_icon);
        }




        //TODO Format Date
        DateFormat df =new SimpleDateFormat("MMMM dd, yyyy",Locale.US);
        df.setTimeZone(TimeZone.getDefault());
        placeViewHolder.vDate.setText(df.format(ci.date));
        if(!ci.status){
            placeViewHolder.vDoneButton.setVisibility(View.VISIBLE);
            placeViewHolder.vUndoButton.setVisibility(View.INVISIBLE);
            placeViewHolder.vDoneButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ReminderDbAdapter dbAdapter=new ReminderDbAdapter(v.getContext());
                    dbAdapter.markAsDone(ci.id);
            //        v.getContext().stopService(new Intent(v.getContext(),LocationService.class));
             //       v.getContext().startService(new Intent(v.getContext(),LocationService.class));
                    Intent intent=new Intent(v.getContext(),MainActivity.class);
                    intent.putExtra("checkService","check");
                    Toast.makeText(v.getContext().getApplicationContext(), "Reminder Marked as done", Toast.LENGTH_LONG).show();
                    v.getContext().startActivity(intent);
                }
            });
        }
        else {
            placeViewHolder.vUndoButton.setVisibility(View.VISIBLE);
            placeViewHolder.vDoneButton.setVisibility(View.INVISIBLE);
            placeViewHolder.vUndoButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ReminderDbAdapter dbAdapter=new ReminderDbAdapter(v.getContext());
                    dbAdapter.undo(ci.id);
               //     v.getContext().stopService(new Intent(v.getContext(),LocationService.class));
               //     v.getContext().startService(new Intent(v.getContext(),LocationService.class));
                    Toast.makeText(v.getContext().getApplicationContext(), "Undo", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(v.getContext(),MainActivity.class);
                    intent.putExtra("checkService","check");
                    Toast.makeText(v.getContext().getApplicationContext(), "Undo Reminder", Toast.LENGTH_LONG).show();
                    v.getContext().startActivity(intent);
                }
            });
        }

        placeViewHolder.vDeleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ReminderDbAdapter dbAdapter=new ReminderDbAdapter(v.getContext());
                dbAdapter.deleteReminder(ci.id);
           //     v.getContext().stopService(new Intent(v.getContext(),LocationService.class));
            //    v.getContext().startService(new Intent(v.getContext(),LocationService.class));
                Toast.makeText(v.getContext().getApplicationContext(), "Reminder deleted", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(v.getContext(),MainActivity.class);
                intent.putExtra("checkService","check");
                v.getContext().startActivity(intent);

            }
        });
    }


    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext().getApplicationContext(),ReminderDetailsActivity.class);
                Log.d("ci",ci.id+"");
                intent.putExtra("id",ci.id);
                v.getContext().startActivity(intent);
            }
        });
        return new PlaceViewHolder(itemView);
    }

    public  static class PlaceViewHolder extends RecyclerView.ViewHolder {
        protected TextView vTitle;
        protected TextView vAddress;
        protected TextView vName;
        protected TextView vDate;
        protected ImageButton vDeleteButton;
        protected Button vDoneButton;
        protected Button vUndoButton;
        protected ImageView vTypeImage;


        public PlaceViewHolder(View v){
            super(v);
            vTitle=(TextView)v.findViewById(R.id.title);
            vAddress=(TextView)v.findViewById(R.id.address);
            vName=(TextView)v.findViewById(R.id.name);
            vDate=(TextView)v.findViewById(R.id.date);
            vDeleteButton=(ImageButton) v.findViewById(R.id.deleteButton);
            vDoneButton=(Button) v.findViewById(R.id.doneButton);
            vUndoButton=(Button)v.findViewById(R.id.undoButton);
            vTypeImage= (ImageView) v.findViewById(R.id.typeImage);

        }
    }
}
