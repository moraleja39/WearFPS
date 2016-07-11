package me.oviedo.wearfps;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    final static String ACTION_PREFS_WEAR = "me.oviedo.wearfps.prefs.WEAR";
    boolean isWearPrefs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle fragBundle = new Bundle();

        String action = getIntent().getAction();
        if (action != null && action.equals(ACTION_PREFS_WEAR)) {
            //addPreferencesFromResource(R.xml.preferences);
            isWearPrefs = true;
            fragBundle.putBoolean("isWearScreen", true);
            setTitle(R.string.title_activity_wear_settings);

        } else {
            fragBundle.putBoolean("isWearScreen", false);
        }

        SettingsFragment sf = new SettingsFragment();
        sf.setArguments(fragBundle);
        transaction.replace(R.id.fragmentHolder, sf).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isWearPrefs) {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    finish();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //dialog


}
