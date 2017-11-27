package com.example.maryna.lab4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int RC_LOCATION_PERMISSION = 844;

    private static final long UPDATE_MIN_INTERVAL_SPEED = 300;
    private static final long UPDATE_MIN_INTERVAL_DISTANCE = 2500;

    private TextView speedTextView;
    private TextView distanceTextView;

    private Location lastLocation;
    private long lastTime;
    private long lastTimeDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedTextView = findViewById(R.id.speed);
        distanceTextView = findViewById(R.id.distance);

        speedTextView.setText(String.format("Speed: %.1f km/h", 0.0));
        distanceTextView.setText("Waiting...");

        setupLocationUpdates();
    }

    private void setupLocationUpdates() {
        // Check permissions to use GPS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates from GPS sensor
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //Subscribe to receive GPS data
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        long time = System.currentTimeMillis();
        if (lastLocation == null) lastLocation = location;
        if (time - lastTime < UPDATE_MIN_INTERVAL_SPEED) return; // For accuracy
        lastTime = time;

        SpeedCounter.getInstance().addNextPoint(System.currentTimeMillis(), location);

        double speed = SpeedCounter.getInstance().getSpeed();
        double speedKMH = speed * 3.6;

        speedTextView.setText(String.format("Speed: %.1f km/h", speedKMH));

        if (time - lastTimeDistance >= UPDATE_MIN_INTERVAL_DISTANCE) {
            lastTimeDistance = time;
            double distance = SpeedCounter.getInstance().updateDistance();
            distanceTextView.setText(String.format("Distance: %.0f m", distance));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}