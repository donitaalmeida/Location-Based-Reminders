package com.bignerdranch.android.locationbasedreminders;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

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
        if(id==R.id.navigate)
        {
            startActivity(new Intent(this, AddReminder.class));
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
            MyFragment myFragment=MyFragment.getInstance(position);
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
    public static class MyFragment extends Fragment{
        private TextView textView;
        public static MyFragment getInstance(int position)
        {
            MyFragment myFragment=new MyFragment();
            Bundle args=new Bundle();
            args.putInt("position",position);
            myFragment.setArguments(args);
            return myFragment;
        }
        private  ArrayList<ReminderInfo>  getRecyclerViewData() {
            String title, name, address;
            float latitude, longitude;
            Date date;
            ArrayList<ReminderInfo> reminderList=new ArrayList<>();
            dbHelper=new ReminderDbAdapter(getActivity().getBaseContext());
            dbHelper.open();
            Cursor cursor = dbHelper.fetchAllReminders();
                while(cursor.moveToNext()) {
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                    longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                    date = new Date(cursor.getString(cursor.getColumnIndex("date")));
                    reminderList.add(new ReminderInfo(title, name, address, latitude, longitude, date));
                }
            return reminderList;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedINstanceState)
        {
            View layout=inflater.inflate(R.layout.my_fragment,container,false);


             Bundle bundle=getArguments();
            if(bundle!=null)
            {
                if(bundle.getInt("position")==0){
                    RecyclerView recyclerView=(RecyclerView) layout.findViewById(R.id.cardList);
                    recyclerView.setHasFixedSize(true);
                    ReminderInfoAdapter reminderInfoAdapter=new ReminderInfoAdapter(reminderList);
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(reminderInfoAdapter);
                    reminderList.clear();
                    reminderList.addAll(getRecyclerViewData());
                    reminderInfoAdapter.notifyDataSetChanged();

                }
            }
            return layout;
        }
    }


}
