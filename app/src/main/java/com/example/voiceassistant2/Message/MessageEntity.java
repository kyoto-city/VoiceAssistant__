package com.example.voiceassistant2.Message;

import com.example.voiceassistant2.Message.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageEntity {
    public String text;
    public String date;
    public int isSend;

    public MessageEntity(String text, String date, int isSend){
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }


    public MessageEntity(Message message) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        this.text = message.text;
        this.date = dateFormat.format(message.date);
        this.isSend = message.isSend ? 1 : 0;
    }
}
