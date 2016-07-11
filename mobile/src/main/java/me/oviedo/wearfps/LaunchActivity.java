package me.oviedo.wearfps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BackgroundService.running) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, StartActivity.class);
            startActivity(i);
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
