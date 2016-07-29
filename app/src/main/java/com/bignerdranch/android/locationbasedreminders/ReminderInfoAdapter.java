package com.bignerdranch.android.locationbasedreminders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by donita on 12-07-2016.
 * adapter for recycler view for nearby place listing
 */
public class ReminderInfoAdapter extends RecyclerView.Adapter<ReminderInfoAdapter.PlaceViewHolder>{

    private List<ReminderInfo> placeList;

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
        ReminderInfo ci = placeList.get(i);
        placeViewHolder.vTitle.setText(ci.title);
        placeViewHolder.vAddress.setText(ci.address);
        placeViewHolder.vName.setText(ci.name);
        placeViewHolder.vDate.setText(ci.date.toString());
        //TODO Format Date
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new PlaceViewHolder(itemView);
    }

    public  static class PlaceViewHolder extends RecyclerView.ViewHolder {
        protected TextView vTitle;
        protected TextView vAddress;
        protected TextView vName;
        protected TextView vDate;

        public PlaceViewHolder(View v){
            super(v);
            vTitle=(TextView)v.findViewById(R.id.title);
            vAddress=(TextView)v.findViewById(R.id.address);
            vName=(TextView)v.findViewById(R.id.name);
            vDate=(TextView)v.findViewById(R.id.date);
        }
    }
}
