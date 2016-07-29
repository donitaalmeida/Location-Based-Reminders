package com.bignerdranch.android.locationbasedreminders;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by shikh on 7/27/2016.
 */
public class AddReminder extends ActionBarActivity{
    private Button mSaveButton;
    private Button mCancelButton;

    private EditText mTitleEditText;
    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);
        Toolbar toolbar=(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAddressEditText=(EditText)findViewById(R.id.place_address_edittext);
        mTitleEditText=(EditText)findViewById(R.id.title_edittext);
        mNameEditText=(EditText)findViewById(R.id.place_name_edittext);
        mDateEditText=(EditText)findViewById(R.id.end_date_edittext);
        mSaveButton=(Button)findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createReminder();
            }
        });
        mCancelButton=(Button)findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.action_settings) {
            return true;
        }
       if(id==android.R.id.home) {
           Intent intent = NavUtils.getParentActivityIntent(this);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
           NavUtils.navigateUpTo(this, intent);
       }
        return super.onOptionsItemSelected(item);
    }
    private void createReminder(){
        SQLiteDatabase db = openOrCreateDatabase("LocationBasedReminders", MODE_PRIVATE, null);
        Log.d("address",mAddressEditText.getText()+"");
        if(!mAddressEditText.getText().toString().equals("")&&!mTitleEditText.getText().toString().equals("")&&!mDateEditText.getText().toString().equals("")&&!mNameEditText.getText().toString().equals("")){
            db.execSQL("CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN );");
            db.execSQL("INSERT INTO Reminder(title, name, address, date, latitude, longitude, status ) VALUES('" + mTitleEditText.getText().toString() + "', '" + mNameEditText.getText().toString() + "'," +
                    "'" + mAddressEditText.getText().toString() + "', '" + new Date().toString() + "','"+ 0 + "','" + 0+ "','"+false+"');");
            Toast.makeText(getApplicationContext(), "Added to visit List", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));

        }
        else{
            LinearLayout linearLayout=(LinearLayout)findViewById(R.id.linear_layout);
            Snackbar.make(linearLayout, "Fields cannot be empty", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.RED)
                    .show();
        }

    }
}
