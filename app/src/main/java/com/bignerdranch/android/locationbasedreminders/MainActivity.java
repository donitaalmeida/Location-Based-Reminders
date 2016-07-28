package com.bignerdranch.android.locationbasedreminders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private ViewPager vPager;
    private SlidingTabLayout slideTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        vPager=(ViewPager) findViewById(R.id.pager);
        vPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        slideTabs=(SlidingTabLayout)findViewById(R.id.tabs);
        slideTabs.setViewPager(vPager);
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
            return 3;
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
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedINstanceState)
        {
            View layout=inflater.inflate(R.layout.my_fragment,container,false);
            textView=(TextView)layout.findViewById(R.id.position);
            Bundle bundle=getArguments();
            if(bundle!=null)
            {
                textView.setText("The Page selected is "+bundle.getInt("position"));
            }
            return layout;
        }
    }
}
