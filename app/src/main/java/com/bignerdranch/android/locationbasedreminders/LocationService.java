package com.bignerdranch.android.locationbasedreminders;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    public static final String TAG = "MyServiceTag";
    private static final int ONE_MINUTES = 1000 * 60 * 1;
    private static final int FIVE_MINUTES = 1000 * 60 * 5;

    public LocationManager locationManager;
    public MyLocationListener listener;

    private Location loc;
    public Location previousBestLocation = null;
    private Location mDestination;
    Intent intent;
  //  int counter = 0;
   // private boolean notify;
    ReminderDbAdapter mDbAdapter;
    private ArrayList<ReminderInfo> reminderList;
    @Override
    public void onCreate() {
        super.onCreate();
      //  notify=false;
        intent = new Intent(BROADCAST_ACTION);

        mDbAdapter=new ReminderDbAdapter(this.getBaseContext());
        reminderList=new ArrayList<>();
        mDbAdapter.open();
        addRemindersToService(mDbAdapter.fetchSpecificReminders());
       // addRemindersToService(mDbAdapter.fetchGeneralReminders());
        mDbAdapter.close();
       String powerMode=intent.getStringExtra("powerMode");
        if (powerMode!=null){
            Log.e("hi","in on create");
        }
    }
    public void addRemindersToService(Cursor cursor){
        String title, name, address,type;
        float latitude, longitude;
        Date date;
        int id;
        boolean status;
        if (cursor.moveToFirst()){
            do{
                status=new Boolean(cursor.getString(cursor.getColumnIndex("status")));
                if(!status){
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                    longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                    type=cursor.getString(cursor.getColumnIndex("type"));
                    DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                    formatter.setTimeZone(TimeZone.getDefault());
                    Log.e("donita",TimeZone.getDefault().getDisplayName());
                    try {
                        Log.e("date","1"+cursor.getString(cursor.getColumnIndex("date"))+"1");

                        date = formatter.parse(cursor.getString(cursor.getColumnIndex("date")));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        date=new Date();
                    }
                    id=cursor.getInt(cursor.getColumnIndex("_id"));
                    status=new Boolean(cursor.getString(cursor.getColumnIndex("status")));
                    reminderList.add(new ReminderInfo(id,title, name, address, latitude, longitude, date,status,type));
                    Toast.makeText(getApplicationContext(),title+" added",Toast.LENGTH_LONG).show();
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onStart(Intent intent, int startId) {

        int speed=ONE_MINUTES;
        String powerMode=intent.getStringExtra("powerMode");
        if (powerMode!=null){
            Log.e("hi","in on start");
            speed=FIVE_MINUTES;
        }

       // Toast.makeText(getApplicationContext(),"Reminders enabled",Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener(reminderList);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(isGpsEnabled()){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, speed, 0, listener);
        }else if(isNetworkEnabled()){
            Toast.makeText( getApplicationContext(), "Please enable GPS for better experience ", Toast.LENGTH_SHORT ).show();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, speed, 0, listener);
        }
        else{
            Toast.makeText( getApplicationContext(), "Service needs GPS to to enabled ", Toast.LENGTH_SHORT ).show();
            stopSelf();
        }

       // mDestination = intent.getParcelableExtra("destination");
        listener.startNotification("VisitTrack is active","",intent);
    }

    //check if gps is enabled
    private boolean isGpsEnabled() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }

    private boolean isNetworkEnabled() {
        NetworkConnectionDetector networkConnectionDetector=new NetworkConnectionDetector(getApplicationContext());
        if (networkConnectionDetector != null) {
            return networkConnectionDetector.isConnectingToInternet();
        } else {
            return false;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
    //    Toast.makeText( getApplicationContext(), "onDestroy ", Toast.LENGTH_SHORT ).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        listener.stopfore();
        locationManager.removeUpdates(listener);


    }

  /*  public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;


}*/
    public class MyLocationListener implements LocationListener
    {
        private Notification mBuilder;

        ArrayList<ReminderInfo> mReminderInfos;

        public MyLocationListener(ArrayList<ReminderInfo> ReminderInfos){
            this.mReminderInfos=ReminderInfos;
        }

        public void startNotification(String title, String description, Intent intent){
          //  RemoteViews rmv = new RemoteViews(getPackageName(),R.layout.notification);
        //    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

            mBuilder = new NotificationCompat.Builder(LocationService.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(description)
                        //    .setContent(rmv)
                            .build();
            NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.flags|=Notification.FLAG_AUTO_CANCEL;
          //  rmv.setOnClickPendingIntent(R.id.disableButton, pIntent);
            notificationManager.notify(101,mBuilder);
            startForeground(101,mBuilder);
            sendBroadcast(intent);
        }

        public void stopfore(){
            stopForeground(true);
        }
        void sendNotification(String title, String description, int id){
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(),ReminderDetailsActivity.class);
            intent.putExtra("id",id);
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

            Notification mBuilder2 = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(description)
                            .setContentIntent(pIntent)

                            .build();
            NotificationManager notificationManager=(NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.notify(102,mBuilder2);
        }


        public void onLocationChanged(final Location loc) {
            Toast.makeText( getApplicationContext(), "Updating", Toast.LENGTH_SHORT ).show();
          //  loc.getLatitude();
          //  loc.getLongitude();
         //   intent.putExtra("Latitude", loc.getLatitude());
         //   intent.putExtra("Longitude", loc.getLongitude());
         //   intent.putExtra("Provider", loc.getProvider());
            float distance;
            Log.d("Display","Location changed");
         //   int count=0;

            for(ReminderInfo reminderInfo: mReminderInfos){
                Location destination=new Location(reminderInfo.name);
                destination.setLatitude(reminderInfo.latitude);
                destination.setLongitude(reminderInfo.longitude);
                distance=destination.distanceTo(loc);
                if(distance < 1600&&!reminderInfo.status) {
                    sendNotification(reminderInfo.title,"you have to visit "+reminderInfo.name,reminderInfo.id);
                    reminderInfo.status=true;
                }
            }

        }
        public void onProviderDisabled(String provider) {
            Toast.makeText( getApplicationContext(), "Please enable Gps for better experience", Toast.LENGTH_SHORT ).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText( getApplicationContext(), "Gps has been Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
           // Toast.makeText( getApplicationContext(), "On status changed", Toast.LENGTH_SHORT).show();
        }
    }

}