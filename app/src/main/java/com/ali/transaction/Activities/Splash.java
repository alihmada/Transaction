package com.ali.transaction.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;

import com.ali.transaction.Activities.Master.Clients;
import com.ali.transaction.R;

public class Splash extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setStatusBarColor();

        redirectToAppropriateScreenWithDelay();
    }

    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private void setStatusBarColor() {
        getWindow().setStatusBarColor(getPrimaryColor());
    }

    private void redirectToAppropriateScreenWithDelay() {
        new Handler().postDelayed(this::redirectToMainScreen, SPLASH_DELAY_MS);
    }

    private void redirectToMainScreen() {
        Intent intent = new Intent(this, Clients.class);
        startActivity(intent);
        finish();
    }
}