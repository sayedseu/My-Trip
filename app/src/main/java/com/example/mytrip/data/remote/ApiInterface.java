package com.example.mytrip.data.remote;

import com.example.mytrip.data.remote.weather.WeatherToken;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("data/2.5/weather")
    Flowable<WeatherToken> getWeather(@Query("lat") double lat,
                                      @Query("lon") double lon,
                                      @Query("appid") String appid);
}
