package com.example.maryna.lab4;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//Singleton class
public class SpeedCounter {
    private static final int HISTORY = 3;
    private static final double EQUATOR_LENGTH = 40000000;
    private static final double MERIDIAN_LENGTH = 20000000;

    private static SpeedCounter speedCounter;

    // These arrays contain last HISTORY + 1 point received from GPS
    private List<Location> coordinates;
    private List<Long> times;

    private Location lastDistanceMeanLocation;
    private double distance;

    public static SpeedCounter getInstance() {
        if (speedCounter == null) speedCounter = new SpeedCounter();
        return speedCounter;
    }

    private SpeedCounter() {
        coordinates = new LinkedList<>();
        times = new LinkedList<>();
    }

    public void addNextPoint(long time, @NonNull Location location) {
        coordinates.add(0, location);
        times.add(0, time);
        if (coordinates.size() > HISTORY + 1) {
            coordinates.remove(coordinates.size() - 1);
            times.remove(times.size() - 1);
        }
    }

    public double getSpeed() {
        if (coordinates.size() < HISTORY + 1) return 0;

        double[] deltaLatSpeed = new double[HISTORY];
        double[] deltaLonSpeed = new double[HISTORY];

        Iterator<Location> locationIterator = coordinates.iterator();
        Iterator<Long> timeIterator = times.iterator();
        Location lastLocation = locationIterator.next();
        long lastTime = timeIterator.next();

        // Calculate speed by X and Y coordinates separately.
        // Calculate speed using each point in history as previous and then get mean of them (for accuracy)
        int counter = 0;
        while (locationIterator.hasNext() && counter < HISTORY) {
            Location location = locationIterator.next();
            // (lastTime - timeIterator.next()) - time between 2 points
            double deltaTime = lastTime - timeIterator.next();

            //take middle longitude and count correct latitude
            double lengthLat = Math.cos((location.getLongitude() + lastLocation.getLongitude()) / 2) * EQUATOR_LENGTH;

            // getLatitude() - not in the metres. Need to make metres. Same for getLongitude()
            double delaLatMeters = lengthLat * (lastLocation.getLatitude() - location.getLatitude()) / 360;
            double deltaLonMeters = MERIDIAN_LENGTH * (lastLocation.getLongitude() - location.getLongitude()) / 180;

            deltaLatSpeed[counter] = delaLatMeters / (deltaTime) * 1000;
            deltaLonSpeed[counter] = deltaLonMeters / (deltaTime) * 1000;

            counter++;
        }

        // Get mean speed from all points
        double deltaLatSpeedMean = 0;
        double deltaLonSpeedMean = 0;
        for (int i = 0; i < HISTORY; i++) {
            deltaLatSpeedMean += deltaLatSpeed[i];
            deltaLonSpeedMean += deltaLonSpeed[i];
        }
        deltaLatSpeedMean /= HISTORY;
        deltaLonSpeedMean /= HISTORY;

        // Calculate full speed
        return Math.hypot(deltaLatSpeedMean, deltaLonSpeedMean);
    }

    public double updateDistance() {
        if (lastDistanceMeanLocation == null) {
            lastDistanceMeanLocation = getMeanLocation();
            return distance;
        }

        Location location = getMeanLocation();
        if (location == null) return distance;

        double lengthLat = Math.cos((location.getLongitude() + lastDistanceMeanLocation.getLongitude()) / 2) * EQUATOR_LENGTH;

        double deltaLat = lengthLat * (Math.abs(lastDistanceMeanLocation.getLatitude() - location.getLatitude())) / 360;
        double deltaLon = MERIDIAN_LENGTH * (Math.abs(lastDistanceMeanLocation.getLongitude() - location.getLongitude())) / 180;

        lastDistanceMeanLocation = location;

        // Add calculated distance to full distance
        distance += Math.hypot(deltaLat, deltaLon);
        return distance;
    }

    //Get average point of all points in history. Calculate separately by latitude and longitude and then put them together.
    private Location getMeanLocation() {
        if (coordinates.size() < HISTORY + 1) return null;
        double latitude = 0;
        double longitude = 0;
        int count = 0;
        for (Location location : coordinates) {
            latitude += location.getLatitude();
            longitude += location.getLongitude();
            count++;
        }

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude / count);
        location.setLongitude(longitude / count);
        return location;
    }
}
