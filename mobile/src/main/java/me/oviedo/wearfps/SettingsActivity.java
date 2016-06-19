package me.oviedo.wearfps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SettingsFragment()).commit();
        getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SettingsFragment()).commit();


    }


}
