package com.example.locationmapping.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LocationViewModel extends AndroidViewModel {
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private LocationManager locationManager;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            locationLiveData.postValue(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };

    public LocationViewModel(@NonNull Application application) {
        super(application);
        // Object → LocationsManager キャスト
        locationManager = (LocationManager) application.getSystemService(Application.LOCATION_SERVICE);
    }

    public MutableLiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10,
                locationListener
        );
    }

    public void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }
}
