package com.example.voiceassistant2.Weather;

import com.example.voiceassistant2.Weather.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=f8358192f7f32cc6357555e1c2303d70")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
