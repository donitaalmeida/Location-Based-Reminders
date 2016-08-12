package com.bignerdranch.android.locationbasedreminders;

/**
 * Created by donita on 12-07-2016.
 * object for holding info of places
 */
public class PlaceInfo implements Comparable<PlaceInfo>{
    protected String title;
    protected String address;
    protected float ratings;
    protected boolean open_now;
    protected int price_level;
    protected double latitude;
    protected double longitude;

    public PlaceInfo(String title, String address, float ratings, boolean open_now, int price_level,double latitude,double longitude){
        this.title=title;
        this.address=address;
        this.ratings=ratings;
        this.open_now=open_now;
        this.price_level=price_level;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public float getRatings() {
        return ratings;
    }

    @Override
    public int compareTo(PlaceInfo another) {
        if(this.ratings<another.getRatings())
            return 1;
        else if(this.ratings>another.getRatings())
            return -1;
        else
            return 0;
    }

  /*  public String fullString(){
        return title+" "+address+" "+ratings+" "+latitude+" "+longitude;
    }*/
}

