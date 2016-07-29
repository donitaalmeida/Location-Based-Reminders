package com.bignerdranch.android.locationbasedreminders;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shikh on 7/27/2016.
 */
public class AddReminder extends ActionBarActivity{
    private Button mSaveButton;
    private Button mCancelButton;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog datePicker1;
    private EditText mTitleEditText;
    public static String startDateString;
    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mDateEditText;
    String[] fromDate;
    public static boolean validateDateField = false;

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
                if(v.getId()==R.id.end_date_edittext)
                {
                    Date d =new Date();
                    datePicker1 = new DatePickerDialog(getApplicationContext(), fromListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePicker1.getDatePicker().setMinDate(d.getTime());//setting with today's date
                    datePicker1.show();
                }
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
    private DatePickerDialog.OnDateSetListener fromListener = new DatePickerDialog.OnDateSetListener(){
        public void onDateSet(DatePicker view, int from_year, int from_month,
                              int from_day) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, from_year);
            myCalendar.set(Calendar.MONTH, from_month);
            myCalendar.set(Calendar.DAY_OF_MONTH, from_day);
            updateLabelStart();
        }
    };
    private void updateLabelStart() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        //DateFormat sdf1 = new SimpleDateFormat(myFormat, Locale.US);
        //startDateString=sdf1.format(myCalendar.getTime());
        //Log.d("Display","Start date string is "+startDateString);
        //fromDate=startDateString.split("/");
        //Log.d("VIVZ","From hour is "+fromDate[0]);
        //Log.d("VIVZ","From MINUTE is "+fromDate[1]);
        validateDateField=true;
       // mDateEditText.setText(sdf1.format(myCalendar.getTime()));
       // mDateEditText.setText(myCalendar.getTime().toString());
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
