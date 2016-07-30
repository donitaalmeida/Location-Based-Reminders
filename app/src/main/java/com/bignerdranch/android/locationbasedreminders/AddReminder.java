package com.bignerdranch.android.locationbasedreminders;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private static final int CAMERA_REQUEST = 1888;
    private int counter;
    private Handler updateBarHandler;
    String[] fromDate;
    public static boolean validateDateField = false;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getIntent().getExtras()!=null)
        {
            Bundle p = getIntent().getExtras();
            String yourPrevious =p.getString("selectedcontact");
            selectedContact=(EditText) findViewById(R.id.contacts_text);
            selectedContact.setText(yourPrevious);
            selectedContact.setEnabled(false);
            selectedContact.setFocusable(false);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        updateBarHandler =new Handler();

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
    public void displaycontacts(View view) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                getData();


            }
        }).start();


    }
    public void getData() {
        ContentResolver resolver = getContentResolver();//is used to provide retrieval of contacts
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);//provided null values because I want to retrieve all contacts
        nameNumberArray = new String[cursor.getCount()];
        String phoneNumber = new String();
        String email = new String();
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
                Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                while (emailCursor.moveToNext()) {
                    email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                 emailCursor.close();
                nameNumberArray[i] = name + "  " + phoneNumber;
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
