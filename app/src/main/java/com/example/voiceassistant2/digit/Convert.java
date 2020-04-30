package com.example.voiceassistant2.digit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Convert implements Serializable {

        @SerializedName("str")
        @Expose
        public String convertedStr;
}
