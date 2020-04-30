package com.example.voiceassistant2.digit;

import android.util.Log;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertToString {
    public static void getConvert(String number, final Consumer<String> callback){
        ConvertApi api = ConvertService.getApi();
        Call<Convert> call = api.getConvert(number);
        call.enqueue(new Callback<Convert>() {
            @Override
            public void onResponse(Call<Convert> call, Response<Convert> response){
                Convert result = response.body();
                if (result.convertedStr!=null) {
                    String answer = result.convertedStr;
                    callback.accept(answer);
                }
                else {
                    callback.accept("Не могу перевести число в строку");
                }
            }
            @Override
            public void onFailure(Call<Convert> call, Throwable t) {
                Log.w("CONVERTNUMBER",t.getMessage());

            }
        });

    }
}
