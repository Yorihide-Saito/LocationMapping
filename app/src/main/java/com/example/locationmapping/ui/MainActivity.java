package com.example.locationmapping.ui;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.locationmapping.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private LocationViewModel locationViewModel;
    private GoogleMap googleMap;

    private Button buttonGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        buttonGetLocation = findViewById(R.id.button_get_location);
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     checkPermissionAndRequestIfNeeded();
                 }
             }
        );

        locationViewModel.getLocationLiveData().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (googleMap != null && location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    // カメラ移動
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    // マーカー追加
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                }
            }
        });
    }

    public void checkPermissionAndRequestIfNeeded() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            locationViewModel.startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationViewModel.stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // シンプルにチェック
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationViewModel.startLocationUpdates();
                if (googleMap != null) {
                    // 位置情報パーミッションが許可された後、マップのマイロケーション表示を有効化
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                }
            }
        }
    }
}