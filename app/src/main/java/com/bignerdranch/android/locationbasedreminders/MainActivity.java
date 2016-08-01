package com.bignerdranch.android.locationbasedreminders;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
       int id=item.getItemId();
        if(id==R.id.action_settings)
        {
            return true;
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
    }



}
