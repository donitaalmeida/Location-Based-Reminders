package com.bignerdranch.android.locationbasedreminders;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private int counter;
    private Handler updateBarHandler;
    String[] fromDate;
    public static boolean validateDateField = false;
    private ProgressDialog pDialog;
    private double lat,lng;
    private Button mLocationButton;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE=105;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imageName;
    private Bitmap imageBitmap;

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

        selectedImage=(ImageView) findViewById(R.id.capture);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            selectedImage.setVisibility(View.VISIBLE);
            selectedImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                    }else{
                        dispatchTakePictureIntent();
                    }
                }
            });
        }
        contactsText = (ImageView) findViewById(R.id.imageIcon);
        contactsText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                }else{
                    displaycontacts(v);
                }
            }
        });
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
            Bundle p = getIntent().getExtras();
            String yourPrevious =p.getString("selectedcontact");
            selectedContact=(EditText) findViewById(R.id.contacts_text);
            if(yourPrevious!=null){
                selectedContact.setText(yourPrevious);
                selectedContact.setEnabled(false);
                selectedContact.setFocusable(false);
            }
            if(p.getString("placeName")!=null){
                placeName=p.getString("placeName");
                placeAddress=p.getString("placeAddress");
                lat=p.getDouble("lat");
                lng=p.getDouble("lng");
            }
            mTitleEditText.setText(title);
            mAddressEditText.setText(placeAddress);
            mNameEditText.setText(placeName);
            mDateEditText.setText(endDate);
        }
        updateBarHandler =new Handler();
        mLocationButton=(Button)findViewById(R.id.location_button);
        mLocationButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MapActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub,menu);
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("display","has camera app");
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }else{
            Log.d("display","no camera app");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            displayImage.setImageBitmap(imageBitmap);
            imageName="LBR"+new Date().getTime()+".png";
         /*   if(saveImage(name,imageBitmap)){
                Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            }
            displayImage.setImageBitmap(getImageFromDevice(name));*/
        }
    }

    private boolean saveImage(String imageName, Bitmap imageBitmap){
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File image = new File(sdCardDirectory, imageName);
        boolean success = false;
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(image);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
            Log.e("path",image.getCanonicalPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
    private Bitmap getImageFromDevice(String imageName){
        File f = new File(Environment.getExternalStorageDirectory()+"/"+imageName);
        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
        return bmp;
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
        DateFormat df = new SimpleDateFormat("MMMM dd, yyyy",Locale.US);
        df.setTimeZone(TimeZone.getDefault());
        mDateEditText.setText(df.format(myCalendar.getTime()));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.enable_service) {
            return true;
        }
       if(id==android.R.id.home) {

       }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelable("imageBitmap", imageBitmap);
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
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        imageBitmap = savedInstanceState.getParcelable("imageBitmap");
        displayImage.setImageBitmap(imageBitmap);
    }
    public void displaycontacts(View view) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Reading contacts...");
            pDialog.setCancelable(false);
            pDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  displaycontacts(contactsText);
                }
            }
            return;
            case PERMISSIONS_WRITE_EXTERNAL_STORAGE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
            }
        }
    }
    public void getData() {
        ContentResolver resolver = getContentResolver();//is used to provide retrieval of contacts
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);//provided null values because I want to retrieve all contacts
        nameNumberArray = new String[cursor.getCount()];
        String phoneNumber = new String();
        int i = 0;
        final int total=cursor.getCount();

        if (cursor.getCount() > 0) {
             while (cursor.moveToNext()) {
                 // Update the progress message
                 updateBarHandler.post(new Runnable() {
                     public void run() {
                         pDialog.setMessage("Reading contacts : " + counter++ + "/" + total);
                     }
                 });
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                 phoneCursor.close();
                 if(phoneNumber!=null){
                     nameNumberArray[i] = name + "  " + phoneNumber;
                 }

                i++;
            }
            cursor.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                    Bundle b = new Bundle();
                    b.putStringArray("nameContact", nameNumberArray);
                    Intent intent = new Intent(getApplicationContext(), DisplayContacts.class);
                    intent.putExtras(b);
                    loadingSection.setVisibility(View.GONE);
                    startActivity(intent);
                }
            }, 500);
        }
    }
    private void createReminder(){
        SQLiteDatabase db = openOrCreateDatabase("LocationBasedReminders", MODE_PRIVATE, null);
        Log.d("address",mAddressEditText.getText()+"");
        if(!mAddressEditText.getText().toString().equals("")&&!mTitleEditText.getText().toString().equals("")&&!mDateEditText.getText().toString().equals("")&&!mNameEditText.getText().toString().equals("")){
            if(imageName!=null){
                if(saveImage(imageName,imageBitmap)){
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                }
            }
            db.execSQL("CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN,contact VARCHAR,image VARCHAR );");
            db.execSQL("INSERT INTO Reminder(title, name, address, date, latitude, longitude, status, contact, image ) VALUES('" + mTitleEditText.getText().toString() + "', '" + mNameEditText.getText().toString() + "'," +
                    "'" + mAddressEditText.getText().toString() + "', '" + mDateEditText.getText().toString() + "','"+ lat + "','" + lng+ "','"+false+"','"+selectedContact.getText()+"','"+imageName+"');");
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
