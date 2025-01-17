package com.example.algoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent learnIntent = new Intent(MainActivity.this, Main2Activity.class);
                MainActivity.this.startActivity(learnIntent);
                MainActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }
}


