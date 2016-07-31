package com.bignerdranch.android.locationbasedreminders;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MyService extends Service {

    private Location mDestination;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            int count=0;
            float distance = mDestination.distanceTo(location);

            Notification mBuilder =
                    new NotificationCompat.Builder(MyService.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Distance "+count+"  ")
                            .setContentText(Float.toString(distance))
                            .build();
            NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(101,mBuilder);
            count++;
            /*NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(
                            Context.NOTIFICATION_SERVICE);
           Notification notification= mNotificationManager.notify(1, mBuilder.build());*/
            startForeground(101,mBuilder);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStart: " + intent);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mDestination = intent.getParcelableExtra("destination");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return START_NOT_STICKY;
            }
        }
        Location location = new Location("");
        location.setLatitude(37.3323918);
        location.setLongitude(-121.8914007);

        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);
        float distance = mDestination.distanceTo(location);

        Notification mBuilder =
                new NotificationCompat.Builder(MyService.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Distance is ")
                        .setContentText(Float.toString(distance))
                        .build();
            /*NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(
                            Context.NOTIFICATION_SERVICE);
           Notification notification= mNotificationManager.notify(1, mBuilder.build());*/
        startForeground(101,mBuilder);

        return START_STICKY;
    }
}