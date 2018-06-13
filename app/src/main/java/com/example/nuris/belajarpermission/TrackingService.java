package com.example.nuris.belajarpermission;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;
import android.widget.Toast;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplication(),"Lokasi",Toast.LENGTH_LONG).show();
        buildNotification();
        loginToFirebase();
    }

//Create the persistent notification//

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.notiftes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(1, builder.build());
        }
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            unregisterReceiver(stopReceiver);

            stopSelf();
        }
    };

    private void loginToFirebase() {

        requestLocationUpdates();

    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(1000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Toast.makeText(getApplicationContext(),"Lokasi"+location.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }, null);
        }
    }
}