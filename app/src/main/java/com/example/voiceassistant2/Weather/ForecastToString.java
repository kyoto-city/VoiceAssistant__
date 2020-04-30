package com.example.voiceassistant2.Weather;

import android.util.Log;

import com.example.voiceassistant2.AI;
import com.example.voiceassistant2.Weather.Forecast;
import com.example.voiceassistant2.Weather.ForecastApi;
import com.example.voiceassistant2.Weather.ForecastService;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(String city, final Consumer<String> callback){

        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback <Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response){
                Forecast result = response.body();
                AI ai = new AI();
                if (result.current.temperature!=null) {
                    String answer = "Сейчас где-то " + result.current.temperature.toString() + ai.getDegreeEnding(result.current.temperature) + " и " + result.current.weather_descriptions.get(0);

                    callback.accept(answer);
                }
                else {
                    callback.accept("Не могу узнать погоду");
                }
            }
            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.w("WEATHER",t.getMessage());
            }

        });
    }
}
