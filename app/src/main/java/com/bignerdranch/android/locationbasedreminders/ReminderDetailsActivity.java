package com.bignerdranch.android.locationbasedreminders;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderDetailsActivity extends AppCompatActivity {
    private TextView vTitle;
    private TextView vAddress;
    private TextView vName;
    private TextView vDate;
    private ImageButton vDeleteButton;
    private Button vDoneButton;
    private Button vUndoButton;
    private  ReminderDbAdapter dbHelper;
    private Button mClickButton;
    private ImageButton shareButton;
    private Intent serviceIntent;
    String title, name, address, image,contact;
    float latitude, longitude;
    String date;
    int id;
    boolean status;
    //private static final int CAMERA_REQUEST = 1888;
    //private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView displayImage;
    private Bitmap imageBitmap;
    Uri targetUri;

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

            targetUri=data.getData();
            String name="LBR"+new Date().getTime()+".png";
            if(saveImage(name,imageBitmap)){
                Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            }
            displayImage.setImageBitmap(getImageFromDevice(name));
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

    public void startService() {
        serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
    }

    public void stopService(){
            if(serviceIntent!=null){
                stopService(serviceIntent);
            }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayImage=(ImageView)findViewById(R.id.capturedImage);
        shareButton=(ImageButton)findViewById(R.id.shareButton);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareReminder();

            }
        });
        if(getIntent().getExtras()!=null) {
            id=getIntent().getExtras().getInt("id");
            vTitle = (TextView) findViewById(R.id.title);
            vAddress = (TextView) findViewById(R.id.address);
            vName = (TextView) findViewById(R.id.name);
            vDate = (TextView) findViewById(R.id.date);
            vDeleteButton = (ImageButton) findViewById(R.id.deleteButton);
            vDoneButton = (Button) findViewById(R.id.doneButton);
            vUndoButton = (Button) findViewById(R.id.undoButton);
            dbHelper = new ReminderDbAdapter(this);
            dbHelper.open();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Cursor cursor = dbHelper.fetchOneReminder(id);
            Log.e("cursor",cursor.moveToFirst()+"");
            if (cursor.moveToFirst()) {
                status = new Boolean(cursor.getString(cursor.getColumnIndex("status")));
                title = cursor.getString(cursor.getColumnIndex("title"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                address = cursor.getString(cursor.getColumnIndex("address"));
                latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                date = cursor.getString(cursor.getColumnIndex("date"));
                if(cursor.getColumnIndex("image")>=0){
                    image=cursor.getString(cursor.getColumnIndex("image"));
                }
                if(cursor.getColumnIndex("contact")>=0){
                    contact=cursor.getString(cursor.getColumnIndex("contact"));
                }
                Log.e("title",title);
            }
            dbHelper.close();
            cursor.close();
            vTitle.setText(title);
            vAddress.setText(address);
            vName.setText(name);
            vDate.setText(date);
            if(image!=null){
                displayImage.setImageBitmap(getImageFromDevice(image));
            }

            vDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteReminder(id);
                    Toast.makeText(getApplicationContext(), "Reminder deleted", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(v.getContext(), MainActivity.class));

                }
            });
            if (!status) {
                vDoneButton.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.INVISIBLE);
                vDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            stopService();
                            dbHelper.markAsDone(id);
                            startService();
                            Toast.makeText(v.getContext().getApplicationContext(), "Reminder Marked as done", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(v.getContext(), MainActivity.class));
                    }
                });
                vUndoButton.setVisibility(View.INVISIBLE);
            }else{
                vDoneButton.setVisibility(View.INVISIBLE);
                vUndoButton.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);
                vUndoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.undo(id);
                        Toast.makeText(v.getContext().getApplicationContext(), "Undo", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(v.getContext(), MainActivity.class));
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("imageBitmap", imageBitmap);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        imageBitmap = savedInstanceState.getParcelable("imageBitmap");
        displayImage.setImageBitmap(imageBitmap);
    }

    void shareReminder(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}

