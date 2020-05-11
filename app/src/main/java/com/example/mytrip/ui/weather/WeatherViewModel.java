package com.example.mytrip.ui.weather;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.data.remote.ApiInterface;
import com.example.mytrip.data.remote.weather.WeatherToken;

import javax.inject.Inject;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WeatherViewModel extends ViewModel {
    private static final String appid = "dbd0949535c06c6f1ec557a7099d7126";
    private static final String TAG = "sayed";
    private ApiInterface apiInterface;
    private MediatorLiveData<Resource<WeatherToken>> weatherData = new MediatorLiveData<>();

    @Inject
    WeatherViewModel(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    LiveData<Resource<WeatherToken>> observeWeatherData(double lat, double lon) {
        weatherData.setValue(Resource.loading(null));
        LiveData<Resource<WeatherToken>> source = LiveDataReactiveStreams.fromPublisher(
                apiInterface.getWeather(lat, lon, appid)
                        .onErrorReturn(throwable -> {
                            throwable.printStackTrace();
                            Log.i(TAG, "observeWeatherData: " + throwable.toString());
                            WeatherToken weatherToken = new WeatherToken();
                            weatherToken.setId(-1);
                            return weatherToken;
                        })
                        .map((Function<WeatherToken, Resource<WeatherToken>>) weatherToken -> {
                            if (weatherToken.getId() == -1) {
                                return Resource.error("", null);
                            } else {
                                return Resource.success(weatherToken);
                            }
                        }).subscribeOn(Schedulers.io()));
        weatherData.addSource(source, weatherTokenResource -> {
            weatherData.setValue(weatherTokenResource);
            weatherData.removeSource(source);
        });
        return weatherData;
    }
}
