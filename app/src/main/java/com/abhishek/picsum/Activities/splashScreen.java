package com.abhishek.picsum.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abhishek.picsum.R;

public class splashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT = 2000;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_splash_screen);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.mainLayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bookimg_land));
        } else {
            findViewById(R.id.mainLayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bookimg));
        }
        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(splashScreen.this,
                        booksListView.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        //According to orientation background image changes
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.mainLayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bookimg_land));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.mainLayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bookimg));
        }
    }
}