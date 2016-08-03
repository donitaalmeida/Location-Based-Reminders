package com.bignerdranch.android.locationbasedreminders;

/**
 * Created by shikh on 8/3/2016.
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by donita on 13-07-2016.
 * class to sort place info array list by user ratings field
 */
public class PlacesSorter {
    ArrayList<PlaceInfo> mPlaceInfos=new ArrayList<>();
    public PlacesSorter(ArrayList<PlaceInfo> PlaceInfos){
        mPlaceInfos=PlaceInfos;
    }
    public ArrayList<PlaceInfo> getSortedByRatings() {
        Collections.sort(mPlaceInfos);
        return mPlaceInfos;
    }
}
