package com.example.mytrip.ui.weather;

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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.RequestManager;
import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.data.remote.weather.WeatherToken;
import com.example.mytrip.databinding.FragmentWeatherBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class WeatherFragment extends DaggerFragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    RequestManager requestManager;
    private FragmentWeatherBinding binding;
    private WeatherViewModel mViewModel;
    private FusedLocationProviderClient client;
    private LocationManager locationManager;
    private Location currentLocation = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, providerFactory).get(WeatherViewModel.class);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            retrieveCurrentLocation();
        }
    }

    private void subscribeObserver() {
        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();
        mViewModel.observeWeatherData(lat, lon).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        showSnackBar("Something went wrong. Please try again latter.");
                        break;
                    case SUCCESS:
                        binding.progressBar.setVisibility(View.GONE);
                        if (resource.data == null) {
                            showSnackBar("Nothing found. Please try again latter.");
                        } else {
                            binding.weather.setVisibility(View.VISIBLE);
                            updateUI(resource.data);
                        }
                        break;
                }
            }
        });
    }

    private void updateUI(WeatherToken data) {
        binding.cityName.setText(data.getName());
        binding.date.setText(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
        requestManager.load("http://openweathermap.org/img/wn/" + data.getWeather().get(0).getIcon() + "@2x.png")
                .fitCenter()
                .into(binding.icon);
        binding.temp.setText(new DecimalFormat("##.##").format(data.getMain().getTemp() - 273.15) + "\u00B0");
        binding.maxTemp.setText(new DecimalFormat("##.##").format(data.getMain().getTempMax() - 273.15) + "\u00B0" + " / ");
        binding.minTemp.setText(new DecimalFormat("##.##").format(data.getMain().getTempMin() - 273.15) + "\u00B0");
        binding.condition.setText(data.getWeather().get(0).getMain());
        binding.pressure.setText(new DecimalFormat("##.##").format(data.getMain().getPressure() / 10) + " kPa");
        binding.humadity.setText(data.getMain().getHumidity() + " %");
        binding.wind.setText(data.getWind().getSpeed() + " m/s");
    }

    private void retrieveCurrentLocation() {
        client.getLastLocation().addOnSuccessListener(location -> {
            currentLocation = location;
            if (currentLocation != null) {
                subscribeObserver();
            }
        });
    }

    private void requestLocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(600);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }

        };
        client.requestLocationUpdates(locationRequest, callback, null);
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

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

}
