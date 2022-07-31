package com.votelimes.memtask.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.viewmodels.MemtaskViewModelBase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, SearchView.OnQueryTextListener, LocationListener {

    private GoogleMap mMap;
    private boolean mPermission;
    private boolean mIsGPSEnabled;
    private FusedLocationProviderClient mLocationClient;
    private Task mTask;
    private MemtaskRepositoryBase mRepository;
    private MaterialToolbar mToolbar;
    private Marker mMarker = null;
    LocationManager locationManager;
    private int zoom = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationClient = new FusedLocationProviderClient(this);

        mToolbar = (MaterialToolbar) findViewById(R.id.map_toolbar);
        Drawable drawable = getDrawable(R.drawable.ic_round_arrow_back_24);
        drawable.setTint(getColor(R.color.secondary));
        mToolbar.setNavigationIcon(drawable);

        mToolbar.setBackgroundColor(getColor(R.color.backgroundSecondary));
        mToolbar.setTitleTextColor(getColor(R.color.toolbarTitle));
        mToolbar.setSubtitleTextColor(getColor(R.color.toolbarIcons));

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.getMenu().findItem(R.id.action_search).setVisible(true);
        MenuItem searchItem = mToolbar.getMenu().findItem(R.id.action_search);
        //Get SearchView through MenuItem
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        mRepository = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());

        String id = getIntent().getStringExtra(MemtaskViewModelBase.MTP_ID);
        mTask = mRepository.getTaskSilently(id);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        checkPermission();
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Marker mrk = addMarker(latLng.latitude, latLng.longitude);
                if(mrk != null){
                    mMarker = mrk;
                };
            }
        });
        try {
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mLocationClient.getLastLocation().addOnCompleteListener(task -> {
                        try {
                            if (task.isSuccessful()) {
                                Location location = task.getResult();
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                                mMap.clear();
                                mMarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                        .draggable(true)
                                );
                                mMap.moveCamera(cu);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    });
                    return false;
                }
            });
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    mMarker = marker;
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
            setup();
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
    @SuppressLint("MissingPermission")
    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        else{
            mPermission = true;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            mIsGPSEnabled = true;
        }else{
            mIsGPSEnabled = false;
        }
    }

    @SuppressLint("MissingPermission")
    private void setCurrentLocation(){
        if(mPermission){
            mMap.clear();
            mLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                            try {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                                    mMap.clear();
                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                    mMap.moveCamera(cu);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
        }
    }

    @SuppressLint("MissingPermission")
    private void setup(){
        try {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            if(mTask.getMapX() != 0 && mTask.getMapY() != 0){
                findPlace(mTask.getMapX(), mTask.getMapY());
            }
            else {
                if(mIsGPSEnabled) {
                    setCurrentLocation();
                }
                else{
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onLocationSave(View view){
        if(mMarker != null){
            mTask.setMapX(mMarker.getPosition().latitude);
            mTask.setMapY(mMarker.getPosition().longitude);
            mRepository.addTaskSilently(mTask);
        }
        finish();
    }

    private void findPlace(String address){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(address, 1);
            if (addresses.size() > 0)
            {
                double lat = (double) (addresses.get(0).getLatitude());
                double lon = (double) (addresses.get(0).getLongitude());

                Log.d("lat-long", "" + lat + "......." + lon);
                final LatLng user = new LatLng(lat, lon);
                /*used marker for show the location */
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(user)
                        .title(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .draggable(true)
                );

                // Move the camera instantly to hamburg with a zoom of 15.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, zoom));
                // Zoom in, animating the camera.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);
                addMarker(lat, lon);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void findPlace(double x, double y){
        Log.d("lat-long", "" + x + "......." + y);
        final LatLng user = new LatLng(x, y);
        /*used marker for show the location */
        // Move the camera instantly to hamburg with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, zoom));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);
        addMarker(x, y);
    }

    private Marker addMarker(double x, double y){
        try {
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geoCoder.getFromLocation(x, y, 5);
            MarkerOptions mo = new MarkerOptions()
                    .position(new LatLng(x, y))
                    .title(addresses.get(0).getAddressLine(0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE-10.0f))
                    .draggable(true);
            mMap.clear();
            return mMap.addMarker(mo);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        findPlace(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            mPermission = true;
                            setup();
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            mPermission = false;
                            finish();
                        } else {
                            // No location access granted.
                            mPermission = false;
                            finish();
                        }
                    }
            );

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        Location location = null;
        Double latitude;
        Double longitude;
        try {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                //this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            100, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                100, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        findPlace(location.getLatitude(), location.getLongitude());
    }
}