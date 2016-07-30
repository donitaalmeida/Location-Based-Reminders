package com.bignerdranch.android.locationbasedreminders;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shikh on 7/27/2016.
 */
public class AddReminder extends ActionBarActivity implements View.OnClickListener{
    private Button mSaveButton;
    private Button mCancelButton;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog datePicker1;
    private EditText mTitleEditText;
    public static String startDateString;
    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mDateEditText;
    private String[] contactList;
    private ImageView contactsText;
    private LinearLayout loadingSection=null;
    private String[] nameNumberArray;
    private EditText selectedContact;
    private ImageView selectedImage;
    private ImageView displayImage;
    static SharedPreferences sharedPreferences;
    private static final int CAMERA_REQUEST = 1888;

    String[] fromDate;
    public static boolean validateDateField = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE);
        displayImage=(ImageView) findViewById(R.id.pasteImage);
        loadingSection=(LinearLayout)findViewById(R.id.loading);
        selectedImage=(ImageView) findViewById(R.id.capture);
        contactsText = (ImageView) findViewById(R.id.imageIcon);
        mAddressEditText = (EditText) findViewById(R.id.place_address_edittext);
        mTitleEditText = (EditText) findViewById(R.id.title_edittext);
        mNameEditText = (EditText) findViewById(R.id.place_name_edittext);
        mDateEditText = (EditText) findViewById(R.id.end_date_edittext);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mDateEditText.setOnClickListener(this);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createReminder();
            }
        });
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });
        if(getIntent().getExtras()!=null)
        {
            sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE);
            String title = sharedPreferences.getString("Title", null);
            String placeAddress = sharedPreferences.getString("Place Address", null);
            String placeName=sharedPreferences.getString("Place name",null);
            String endDate=sharedPreferences.getString("End date",null);
            Log.d("Display","Title is "+title);
            Log.d("Display","Address is "+placeAddress);
            Log.d("Display","Name is "+placeName);
            Log.d("Display","End date is "+endDate);
            mTitleEditText.setText(title);
            mAddressEditText.setText(placeAddress);
            mNameEditText.setText(placeName);
            mDateEditText.setText(endDate);
            Bundle p = getIntent().getExtras();
            String yourPrevious =p.getString("selectedcontact");
            selectedContact=(EditText) findViewById(R.id.contacts_text);
            selectedContact.setText(yourPrevious);
            selectedContact.setEnabled(false);
            selectedContact.setFocusable(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub,menu);
        return true;
    }
    public void takeImageFromCamera(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                 startActivityForResult(cameraIntent, CAMERA_REQUEST);
             }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
               if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                        Bitmap mphoto = (Bitmap) data.getExtras().get("data");
                        displayImage.setImageBitmap(mphoto);
                    }
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

        validateDateField=true;
        mDateEditText.setText(myCalendar.getTime().toString());
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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        sharedPreferences=getSharedPreferences("shared",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Title",mTitleEditText.getText().toString());
        editor.putString("Place Address",mAddressEditText.getText().toString());
        editor.putString("Place name",mNameEditText.getText().toString());
        editor.putString("End date",mDateEditText.getText().toString());
        editor.commit();
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        /*savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");*/
    }
    public void displaycontacts(View view)
    {
        loadingSection.setVisibility(view.VISIBLE);
        contactList=getData();
        Bundle b=new Bundle();
        b.putStringArray("nameContact", contactList);
        Intent i=new Intent(this, DisplayContacts.class);
        i.putExtras(b);
        loadingSection.setVisibility(View.GONE);
        startActivity(i);
    }
    public String[] getData()
    {
        ContentResolver resolver=getContentResolver();//is used to provide retrieval of contacts
        Cursor cursor= resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);//provided null values because I want to retrieve all contacts
        nameNumberArray = new String[cursor.getCount()];
        String phoneNumber = new String();
        String email = new String();
        int i=0;


        while(cursor.moveToNext()) {
            String id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{id},null);
            while(phoneCursor.moveToNext()) {
                phoneNumber=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            Cursor emailCursor=resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+" = ?",new String[]{id},null);
            while(emailCursor.moveToNext()) {
                email=emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
            nameNumberArray[i] = name + "  " +phoneNumber;
            i++;
        }
        cursor.close();
        for(int j=0;j<nameNumberArray.length;j++)
        {
            Log.d("Display","Name and number is "+nameNumberArray[j]);
        }
        return nameNumberArray;
    }
    private void createReminder(){
        SQLiteDatabase db = openOrCreateDatabase("LocationBasedReminders", MODE_PRIVATE, null);
        Log.d("address",mAddressEditText.getText()+"");
        if(!mAddressEditText.getText().toString().equals("")&&!mTitleEditText.getText().toString().equals("")&&!mDateEditText.getText().toString().equals("")&&!mNameEditText.getText().toString().equals("")){
            db.execSQL("CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN );");
            db.execSQL("INSERT INTO Reminder(title, name, address, date, latitude, longitude, status ) VALUES('" + mTitleEditText.getText().toString() + "', '" + mNameEditText.getText().toString() + "'," +
                    "'" + mAddressEditText.getText().toString() + "', '" + mDateEditText.getText().toString() + "','"+ 0 + "','" + 0+ "','"+false+"');");
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
    @Override
    public void onClick(View v) {
        Date d =new Date();
        datePicker1 = new DatePickerDialog(this, fromListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker1.getDatePicker().setMinDate(d.getTime());
        datePicker1.show();
    }
}
