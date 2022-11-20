package com.rulers.walk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class SlashAcitivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_acitivity);

        Handler handler=new Handler(Looper.myLooper());
        handler.postDelayed(() -> {
            Intent i=new Intent(getBaseContext(),MainActivity.class);
            startActivity(i);
            finish();
        },1000);
    }
}