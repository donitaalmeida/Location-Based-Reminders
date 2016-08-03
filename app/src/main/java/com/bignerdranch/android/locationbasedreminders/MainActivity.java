package com.bignerdranch.android.locationbasedreminders;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private ViewPager vPager;
    private SlidingTabLayout slideTabs;
    private static ArrayList<ReminderInfo> reminderList = new ArrayList<>();
    private static ReminderDbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private Intent serviceIntent;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 125;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            toolbar=(Toolbar)findViewById(R.id.app_bar);
            setSupportActionBar(toolbar);
            vPager=(ViewPager) findViewById(R.id.pager);
            vPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
            slideTabs=(SlidingTabLayout)findViewById(R.id.tabs);
            if(slideTabs!=null){
                slideTabs.setViewPager(vPager);
            }
            FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
            if(floatingActionButton!=null){
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),AddReminder.class);
                        startActivity(intent);
                    }
                });
            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toolbar=(Toolbar)findViewById(R.id.app_bar);
                    setSupportActionBar(toolbar);
                    vPager=(ViewPager) findViewById(R.id.pager);
                    vPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                    slideTabs=(SlidingTabLayout)findViewById(R.id.tabs);
                    if(slideTabs!=null){
                        slideTabs.setViewPager(vPager);
                    }
                    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
                    if(floatingActionButton!=null){
                        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext(),AddReminder.class);
                                startActivity(intent);
                            }
                        });
                    }
                } else {

                    Snackbar.make(toolbar, "Location access is required to show coffee shops nearby.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                    }
                                }
                            }).setActionTextColor(Color.RED).show();
                }
                return;
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
    }
    public void startService(View view) {
        serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
    }

    public void stopService(View view){
        if(serviceIntent!=null){
            stopService(serviceIntent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        int id=item.getItemId();
        if(id==R.id.enable_service) {
            startService(getCurrentFocus());
            return true;
        }
        else if(id==R.id.diable_service){
            stopService(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }
    /*******************Adapter which displays several fragments**************/
    class PagerAdapter extends FragmentPagerAdapter {
        String[] tabs;
        public PagerAdapter(FragmentManager fm) {
            super(fm);
            tabs=getResources().getStringArray(R.array.tabs);
        }
        /****************Code for returning fragment out of the several fragments**************************/
        @Override
        public Fragment getItem(int position) {
            MyFragment myFragment=new MyFragment();
            Bundle args=new Bundle();
            args.putInt("position",position);
            myFragment.setArguments(args);
            Log.e("debug","get Item called");
            return myFragment;
        }
        public CharSequence getPageTitle(int position)
        {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void startservice(View view) {
        Location destination=new Location("");
        destination.setLatitude(37.33511);
        destination.setLongitude(-121.89102188);
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("destination", destination);
        startService(intent);
        stopService(intent);
    }



}
