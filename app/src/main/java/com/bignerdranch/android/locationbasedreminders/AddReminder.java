package com.bignerdranch.android.locationbasedreminders;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by shikh on 7/27/2016.
 */
public class AddReminder extends ActionBarActivity implements View.OnClickListener,  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    ArrayList<PlaceInfo> temp=new ArrayList<>();
    private Button mSaveButton;
    private Button mCancelButton;
    private TextView mAddressText;
    private TextView mNameText;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog datePicker1;
    private EditText mTitleEditText;
    public static String startDateString;
    private EditText mNameEditText;
    private Location mLocation;
    private EditText mAddressEditText;
    private EditText mDateEditText;
    private String[] contactList;
    private ImageView contactsText;

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
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 125;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imageName;
    private Bitmap imageBitmap;
    private Button button1;
  //  private String placeType;
    private ImageButton imgButton;
    private String m_Text;
    //  private static double currentLatitude;
  //  private static double currentLongitude;
  //  private LatLng fromPosition;
  //  protected LocationManager locationManager;
  //  private RadioGroup mRadioGroup;
    private String mDropDownSelectedType;
 //   private GoogleApiClient mGoogleApiClient;
    private RadioButton mGeneralRadioButton;
    private RadioButton mSpecificRadioButton;
    private static final String GOOGLE_API_KEY = "AIzaSyDPCg0_ZB96tWAt5-7EaCsanrrLiLtwpaY";

    private Intent serviceIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
    }
    //--------------------------
    public void startService(View view, boolean power) {
        serviceIntent = new Intent(getApplicationContext(), LocationService.class);
        if(power){
            serviceIntent.putExtra("powerMode", "yes");
        }
        startService(serviceIntent);

    }


    public void stopService(View view){
        if(serviceIntent!=null){
            stopService(new Intent(this,LocationService.class));
        }
    }
    //-----------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        int id=item.getItemId();
        if(id==R.id.enable_service) {
            startService(getCurrentFocus(),false);
            // return true;
        }
        else if(id==R.id.diable_service){
            stopService(getCurrentFocus());
            // return true;
        }
        else if(id==R.id.power_saver){
            startService(getCurrentFocus(),true);
        }
        return super.onOptionsItemSelected(item);
    }

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
        mAddressText=(TextView)findViewById(R.id.place_address_textview);
        mNameText=(TextView)findViewById(R.id.place_name_textview);
        mLocationButton=(Button)findViewById(R.id.location_button);
        mLocationButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MapActivity.class));
            }
        });
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(AddReminder.this, button1);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                AddReminder.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        mDropDownSelectedType=item.getTitle().toString();
                        button1.setText(mDropDownSelectedType);

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method
     /*   if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                    .addApi(com.google.android.gms.location.places.Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }
        mGoogleApiClient.connect();
        mRadioGroup=(RadioGroup) findViewById(R.id.radioGenSpec);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.general){
                    mLocationButton.setVisibility(View.GONE);
                    mAddressEditText.setVisibility(View.GONE);
                    mNameEditText.setVisibility(View.GONE);
                    mNameText.setVisibility(View.GONE);
                    mAddressText.setVisibility(View.GONE);


                }else if(checkedId==R.id.specific){
                    mLocationButton.setVisibility(View.VISIBLE);
                    mAddressEditText.setVisibility(View.VISIBLE);
                    mNameEditText.setVisibility(View.VISIBLE);
                    mNameText.setVisibility(View.VISIBLE);
                    mAddressText.setVisibility(View.VISIBLE);
                }
            }
        });*/

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
    //    mGeneralRadioButton=(RadioButton)findViewById(R.id.general);
     //   mSpecificRadioButton=(RadioButton)findViewById(R.id.specific);
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

        imgButton =(ImageButton)findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"You download is resumed",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(AddReminder.this);
                builder.setTitle("Add a note");
                final EditText input = new EditText(AddReminder.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        Toast.makeText(getApplicationContext(),"Your note has been saved",Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        if(getIntent().getExtras()!=null)
        {
            sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE);
            String title = sharedPreferences.getString("Title", null);
            String placeAddress = sharedPreferences.getString("Place Address", null);
            String placeName=sharedPreferences.getString("Place name",null);
            String endDate=sharedPreferences.getString("End date",null);
         //   placeType=sharedPreferences.getString("placeType",null);
//            mGeneralRadioButton.setChecked(sharedPreferences.getBoolean("mGeneralRadioButton",false));
  //          mSpecificRadioButton.setChecked(sharedPreferences.getBoolean("mSpecificRadioButton",false));
            mDropDownSelectedType=sharedPreferences.getString(mDropDownSelectedType,null);
            if(mDropDownSelectedType!=null){
                button1.setText(mDropDownSelectedType);
            }
            m_Text=sharedPreferences.getString("m_Text",null);

            Log.d("Display","Title is "+title);
            Log.d("Display","Address is "+placeAddress);
            Log.d("Display","Name is "+placeName);
            Log.d("Display","End date is "+endDate);
            Bundle p = getIntent().getExtras();
            String yourPrevious =p.getString("selectedcontact");
            selectedContact=(EditText) findViewById(R.id.contacts_text);
            if(yourPrevious!=null){
                selectedContact.setText(yourPrevious+"");
                selectedContact.setEnabled(false);
                selectedContact.setFocusable(false);
            }
            if(p.getString("placeName")!=null){
                placeName=p.getString("placeName");
                placeAddress=p.getString("placeAddress");
               // placeType=p.getString("placeType");
                lat=p.getDouble("lat");
                lng=p.getDouble("lng");
            }
            mTitleEditText.setText(title);
            mAddressEditText.setText(placeAddress);
            mNameEditText.setText(placeName);
            mDateEditText.setText(endDate);
        }
        updateBarHandler =new Handler();

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
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelable("imageBitmap", imageBitmap);
        sharedPreferences=getSharedPreferences("shared",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Title",mTitleEditText.getText().toString());
        editor.putString("Place Address",mAddressEditText.getText().toString());
        editor.putString("Place name",mNameEditText.getText().toString());
        editor.putString("End date",mDateEditText.getText().toString());
      //  editor.putString("placeType",placeType);
        editor.putString("mDropDownSelectedType",button1.getText().toString());
        editor.putString("m_Text",m_Text);
    //    editor.putBoolean("mGeneralRadioButton",mGeneralRadioButton.isChecked());
     //   editor.putBoolean("mSpecificRadioButton",mSpecificRadioButton.isChecked());
        editor.commit();
        super.onSaveInstanceState(savedInstanceState);

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
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                    Bundle b = new Bundle();
                    b.putStringArray("nameContact", nameNumberArray);
                    Intent intent = new Intent(getApplicationContext(), DisplayContacts.class);
                    intent.putExtras(b);

                    startActivity(intent);
                }
            }, 500);
        }
    }
    private void createReminder() {
      /*  if (mRadioGroup.getCheckedRadioButtonId()==R.id.general) {
            if(mDropDownSelectedType==null){
                Toast.makeText(getApplicationContext(), "Enter a type of place", Toast.LENGTH_SHORT).show();
            }else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                //    myLocationListener=new MyLocationListener();
                //    locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                 //   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
                    if (mGoogleApiClient == null) {
                        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                                .addApi(com.google.android.gms.location.places.Places.PLACE_DETECTION_API)
                                .enableAutoManage(this, this)
                                .build();
                    }
                    mGoogleApiClient.connect();

                    mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLocation == null) {
                        mLocation = new Location("SJSU");
                        mLocation.setLatitude(37.3351895);
                        mLocation.setLongitude(-121.8821658);

                    } else {
                        Log.d("curlat",mLocation.getLatitude()+"");
                        Log.d("curlng",mLocation.getLongitude()+"");

                    }
                    createGeneral(mLocation);
                }
            }
        } else {*/
            createSpecific();
      //  }
    }
  /*  private void createGeneral(Location mLocation){
        nearBySearch(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()),mDropDownSelectedType);

    }*/

   private void createSpecific(){
       SQLiteDatabase db = openOrCreateDatabase("LocationBasedReminders", MODE_PRIVATE, null);
       Log.d("address", mAddressEditText.getText() + "");
       if (!mAddressEditText.getText().toString().equals("") && !mTitleEditText.getText().toString().equals("") && !mDateEditText.getText().toString().equals("") && !mNameEditText.getText().toString().equals("")) {
           if (imageName != null) {
               if (saveImage(imageName, imageBitmap)) {
                   Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
               }
           }

           String address=mAddressEditText.getText().toString().replace("'","\'");
           db.execSQL("CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                   "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN,contact VARCHAR,image VARCHAR, type VARCHAR , note VARCHAR);");
           db.execSQL("INSERT INTO Reminder(title, name, address, date, latitude, longitude, status, contact, image, type, note ) VALUES('" + mTitleEditText.getText().toString() + "', '" + mNameEditText.getText().toString() + "'," +
                   "'" + address + "', '" + mDateEditText.getText().toString() + "','" + lat + "','" + lng + "','" + false + "','" + selectedContact.getText() + "','" + imageName + "','"+mDropDownSelectedType+"','"+m_Text+"');");
           Toast.makeText(getApplicationContext(), "Added to visit List", Toast.LENGTH_SHORT).show();
           startActivity(new Intent(this, MainActivity.class));

       } else {
           LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


/*----------------------------------------
    public void nearBySearch(LatLng selectedLocation, String type)  {
        //Toast.makeText(this, "inside neAR by search",Toast.LENGTH_LONG).show();
        String type_general=type;
        StringBuilder placeStringBuilder = new StringBuilder(placeStringBuilder(type_general));
        MyTask myTask = new MyTask();
        Object toPass[] = new Object[1];
        toPass[0] = placeStringBuilder.toString();
        Log.d("Url recycler",placeStringBuilder.toString());
        myTask.execute(toPass);
    }

    public StringBuilder placeStringBuilder(String type) {
        //search_category = search_text.getText().toString().toLowerCase();

        StringBuilder searchString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");
        searchString.append("location=" + mLocation.getLatitude() + "," + mLocation.getLongitude());
        searchString.append("&radius=" + 12000);
        searchString.append("&keyword="+ type );
        searchString.append("&sensor=false");
        searchString.append("&key="+GOOGLE_API_KEY);
        Log.v("URL",searchString.toString());
        return searchString;
    }
    //async task to get nearby places
    public class MyTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        @Override
        protected String doInBackground(Object... inputObj) {
            try{
                String googlePlacesUrl = (String) inputObj[0];
                Http http = new Http();
                googlePlacesData = http.read(googlePlacesUrl);
                Log.d("googlePlacesData",googlePlacesData);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }
        @Override
        protected void onPostExecute(String result) {
            MyDisplayTask myDisplayTask=new MyDisplayTask();
            myDisplayTask.execute(result);
        }
    }
    //async task to display the places in the recycler view
    public class MyDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {
        JSONObject googlePlacesJson;
        @Override
        protected List<HashMap<String, String>> doInBackground(Object... inputObj) {
            List<HashMap<String, String>> googlePlacesList = null;
            Places placeJsonParser = new Places();
            try {
                googlePlacesJson = new JSONObject((String) inputObj[0]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return googlePlacesList;
        }
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            //placeList.clear();

            //RecyclerView xyz=(RecyclerView)findViewById(R.id.cardList);
            //PlaceAdapter abc=(PlaceAdapter)xyz.getAdapter();
            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> googlePlace = list.get(i);
                String placeName = googlePlace.get("place_name");
                float ratings=Float.parseFloat(googlePlace.get("ratings"));
                String address=googlePlace.get("vicinity");
                boolean open_now=Boolean.parseBoolean(googlePlace.get("open_now"));
                int price_level=Integer.parseInt(googlePlace.get("price_level"));
                double latitude=Double.parseDouble(googlePlace.get("lat"));
                double longitude=Double.parseDouble(googlePlace.get("lng"));
                temp.add(new PlaceInfo(placeName,address,ratings,open_now,price_level,latitude,longitude));
                Log.d("Display","PLACE NAME IS "+placeName);
            }
            Log.e("shikha",temp.size()+"length b4 sorting");
            Collections.sort(temp);

            Log.e("shikha",temp.size()+"length after sorting");
            if(temp!=null){

                Log.e("title",mTitleEditText.toString());
                Log.e("date",mDateEditText.toString());
                if (!mTitleEditText.getText().toString().equals("") &&! mDateEditText.getText().toString().equals("") ) {
                    if (imageName != null) {
                        if (saveImage(imageName, imageBitmap)) {
                            Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                    SQLiteDatabase db = openOrCreateDatabase("LocationBasedReminders", MODE_PRIVATE, null);

                    db.execSQL("CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN,contact VARCHAR,image VARCHAR );");
                    db.execSQL("CREATE TABLE IF NOT EXISTS Location( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "reminderid INTEGER, title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN );");
                    ContentValues values = new ContentValues();
                    values.put("title", mTitleEditText.getText().toString());
                    values.put("name","");
                    values.put("address","");
                    values.put("date",mDateEditText.getText().toString());
                    values.put("latitude",0);
                    values.put("longitude",0);
                    values.put("status",false);
                    if(selectedContact!=null){
                        values.put("contact",selectedContact.getText().toString());
                    }
                    if(imageName!=null){
                        values.put("image",imageName);
                    }
                    long id=db.insert("Reminder", null, values);
                    Log.e("id","inserted id is"+id);
                    //----------------------------------------------------------------------------------
                    int i=0;
                    for(PlaceInfo placeInfo:temp){
                        if(i>4){
                            break;
                        }
                        else{
                            values=new ContentValues();
                            values.put("title", mTitleEditText.getText().toString());
                            values.put("name",placeInfo.title);
                            values.put("address",placeInfo.address);
                            values.put("date",mDateEditText.getText().toString());
                            values.put("latitude",placeInfo.latitude);
                            values.put("longitude",placeInfo.longitude);
                            values.put("status",false);
                            values.put("reminderid",id);
                            db.insert("Location", null, values);
                            Log.d("info",placeInfo.fullString());
                        }
                        i++;
                    }

                    Toast.makeText(getApplicationContext(), "Added to visit List", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplication(), MainActivity.class));
                } else {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
                    Snackbar.make(linearLayout, "Fields cannot be empty(Generally)", Snackbar.LENGTH_SHORT)
                            .setActionTextColor(Color.RED)
                            .show();
                }

            }else{
                Log.e("error","google search returned no records");
            }

        }
    }*/

}
