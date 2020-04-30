package com.example.voiceassistant2.Weather;

import com.example.voiceassistant2.Weather.ForecastApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastService {

    public static ForecastApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.weatherstack.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create().create())
//Конвертер, необходимый для преобразования JSON&#39;а в объекты
                .build();
        return retrofit.create(ForecastApi.class); //Создание объекта, при помощи
        //которого будут выполняться запросы
    }
}