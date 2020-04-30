package com.example.voiceassistant2.digit;

import com.example.voiceassistant2.digit.ConvertApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConvertService {
    public static ConvertApi getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru") // базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) // конвертер для преобразования JSON в объекты
                .build();
        return retrofit.create(ConvertApi.class);
    }
}
