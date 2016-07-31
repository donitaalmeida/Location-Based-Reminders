package com.bignerdranch.android.locationbasedreminders;

import java.util.Date;

/**
 * Created by donita on 12-07-2016.
 * object for holding info of places
 */
public class ReminderInfo implements Comparable<ReminderInfo> {
    protected int id;
    protected String title;
    protected String name;
    protected String address;
    protected double latitude;
    protected double longitude;
    protected Date date;
    protected boolean status;

    public ReminderInfo(int id,String title, String name, String address, float latitude, float longitude, Date date,boolean status) {
        this.id=id;
        this.title = title;
        this.name=name;
        this.address = address;
        this.latitude=latitude;
        this.longitude=longitude;
        this.date=date;
        this.status=status;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(ReminderInfo another) {
        if(this.date.compareTo(another.getDate())>0)
            return 1;
        else if(this.date.compareTo(another.getDate())<0)
            return -1;
        else
            return 0;
    }
}

