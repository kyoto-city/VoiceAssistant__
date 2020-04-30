package com.example.voiceassistant2;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistant2.Message.Message;
import com.example.voiceassistant2.Message.MessageEntity;
import com.example.voiceassistant2.Message.MessageListAdapter;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    DBHelper dBHelper;
    SQLiteDatabase database;
    private static final String APP_PREFERENCES = "mysettings";
    protected Button sendButton;
    SharedPreferences sPref;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    private boolean isLight = true;
    private String THEME = "THEME";
    private static final String TEXTVIEW_STATE_KEY = "TEXTVIEW_STATE_KEY";
    protected MessageListAdapter messageListAdapter;

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mL", (Serializable) messageListAdapter.messageList);
        Log.i("LOG", "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messageListAdapter.messageList = (ArrayList<Message>) savedInstanceState.getSerializable("mL");
        Log.i("LOG", "onRestoreInstanceState");
    }
    protected void onDestroy() {
        super.onDestroy();
        Log.i("LOG", "onDestroy");
    }

    protected void onPause() {
        super.onPause();
        Log.i("LOG", "onPause");
    }

    protected void onRestart() {
        super.onRestart();
        Log.i("LOG", "onRestart");
    }

    protected void onResume() {
        super.onResume();
        Log.i("LOG", "onResume ");
    }

    protected void onStart() {
        super.onStart();
        Log.i("LOG", "onStart");
    }

    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        editor.apply();
        database.delete(dBHelper.TABLE_MESSAGES, null, null);
        for (int i = 0; i < messageListAdapter.messageList.size(); i++) {
            MessageEntity entity = new MessageEntity(messageListAdapter.messageList.get(i));
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);
            database.insert(dBHelper.TABLE_MESSAGES,null,contentValues);
        }
        Log.i("LOG", "onStop");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                isLight = true;
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//установка дневной темы
                break;
            case R.id.night_settings:
                isLight = false;
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//установка ночной темы
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        isLight = sPref.getBoolean(THEME, true);
        if(isLight == true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);
        Cursor cursor = database.query(dBHelper.TABLE_MESSAGES, null, null, null,
                null, null, null);
        if (cursor.moveToFirst()){
            int messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE);
            int dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE);
            int sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND);
            do{
                MessageEntity entity = new
                        MessageEntity(cursor.getString(messageIndex),
                        cursor.getString(dateIndex), cursor.getInt(sendIndex));
                Message message = null;
                try {
                    message = new Message(entity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                messageListAdapter.messageList.add(message);
            }while (cursor.moveToNext());
        }
        cursor.close();


        textToSpeech = new TextToSpeech(getApplicationContext(),new
                TextToSpeech.OnInitListener()   {
                    @Override
                    public void onInit (int i) {
                    if (i!=TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(new Locale("ru"));
                    }
                    }
                });

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            protected void onSend() {
                String text = questionText.getText().toString();
                text=text.trim();
                text=text.replaceAll("[\\s]{2,}", " ");
                final String finalText = text;
                questionText.setText("");
                messageListAdapter.messageList.add(new Message(finalText, true));
                AI ai = new AI();
                ai.getAnswer(text, new Consumer<String>() {
                    @Override
                    public void accept(String answer) {
                        messageListAdapter.messageList.add(new Message(answer, false));
                        messageListAdapter.notifyDataSetChanged();
                        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
                        chatMessageList.smoothScrollToPosition(chatMessageList.getAdapter().getItemCount() - 1);
                    }
                });
                questionText.setText("");
            }
            @Override
            public void onClick(View view) {
                onSend();
                chatMessageList.smoothScrollToPosition(chatMessageList.getAdapter().getItemCount() - 1);

            }
    });

        String text = "";
//        if (savedInstanceState != null && savedInstanceState.containsKey(TEXTVIEW_STATE_KEY))
//            text = savedInstanceState.getString(TEXTVIEW_STATE_KEY);
        //messageListAdapter.messageList.add(new Message(text, false));
        //messageListAdapter.notifyDataSetChanged();
        //chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);


    }
//    @Override
//    public void onSaveInstanceState(Bundle saveInstanceState) {
//        // получаем ссылку на текстовую метку
//        TextView myTextView = (TextView)findViewById(R.id.chatMessageList);
//        // Сохраняем его состояние
//        saveInstanceState.putString(TEXTVIEW_STATE_KEY, myTextView.getText().toString());
//        super.onSaveInstanceState(saveInstanceState);
//    }


}
