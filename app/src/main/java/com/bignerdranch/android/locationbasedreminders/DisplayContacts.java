package com.bignerdranch.android.locationbasedreminders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by shikh on 7/29/2016.
 */
public class DisplayContacts extends Activity implements AdapterView.OnItemClickListener{
    ListView l;
    String list[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Display","Inside onCreate method");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);
        l= (ListView) findViewById(R.id.contacts);

        Bundle b=this.getIntent().getExtras();
        String[] list=b.getStringArray("nameContact");
        Arrays.sort(list);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        l.setAdapter(adapter);
        l.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView temp=(TextView) view;//textview which represents what the user is clicked
        //Toast.makeText(this,temp.getText()+""+i,Toast.LENGTH_LONG).show();//Displaying the textview clicked
        //i is the position of the element clicked
        String text= (String) temp.getText();
        Bundle b1= new Bundle();
        b1.putString("selectedcontact",text);
        Intent newIntent = new Intent(this,AddReminder.class);
        newIntent.putExtras(b1);
        startActivity(newIntent);
    }
}

