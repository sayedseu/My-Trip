package com.example.mytrip.ui.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.mytrip.R;
import com.example.mytrip.databinding.FragmentLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dagger.android.support.DaggerFragment;

public class LocationFragment extends DaggerFragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FragmentLocationBinding binding;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        }

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.clear();
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdate();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdate();
            }
        }
    }

    private void requestLocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateUI(location);
                }
            }

        };
        client.requestLocationUpdates(locationRequest, callback, null);
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder
                .setTitle("Enable GPS")
                .setMessage("Please open you Location.")
                .setCancelable(false).setPositiveButton("Yes", (dialog, which) -> {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateUI(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(position)
                .zoom(10)
                .bearing(0)
                .tilt(45)
                .build();
        map.addMarker(new MarkerOptions().position(position).title("Current Location"));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);
    }
}
