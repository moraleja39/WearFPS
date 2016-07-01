package me.oviedo.wearfps;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    //private FrameLayout fragmentHolder;

    private static final String TAG = "MainActivity";

    private MenuItem disconnectMenuItem;

    private View mContentView;

    private String sDegs, sMhz;

    /* Views*/
    private TextView cpuTempText, gpuTempText, cpuNameText, gpuNameText, cpuFreqText, gpuFreqText, gpuOfflineText;
    private LoadView cpuLoadView, gpuLoadView;

    private BroadcastReceiver mBroadcastReceiver;

    //private static final String MAIN_FRAGMENT_TAG = "MainFragment";
    //private static final String WELCOME_FRAGMENT_TAG = "WelcomeFragment";
    //private MainFragment mainFragment = null;

    //private boolean isSavedInstance = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.getBoolean("fullscreen", false)) {
            //setTheme(R.style.AppTheme_NoActionBar);
            ActionBar ab = getSupportActionBar();
            if (ab != null) ab.hide();
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        findMyViews();

        if (savedInstanceState == null) {
            mVisible = true;
            //fab.setVisibility(View.GONE);
        }/* else {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
            welcomeFragment = (WelcomeFragment) getSupportFragmentManager().findFragmentByTag(WELCOME_FRAGMENT_TAG);
            isSavedInstance = true;

            if (welcomeFragment!= null && ipadd != null) {
                welcomeFragment.setReady();
                fab.setVisibility(View.VISIBLE);
            }
        }*/

        if (savedInstanceState != null) {
            cpuNameText.setText(savedInstanceState.getString("cpu"));
            gpuNameText.setText(savedInstanceState.getString("gpu"));
            if (savedInstanceState.getBoolean("fullscreen", false)) {
                Log.d("MainActivity", "Saved instance: fullscreen");
                //getSupportActionBar().hide();
                hide();
                //getSupportActionBar().show();
            } else {
                Log.d("MainActivity", "Saved instance: normal screen");
            }
        }

        loadResources();
        setBroadcastReceiver();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BackgroundService.running) {
                    hide();

                }
                else {
                    //requestRemoteIp();
                    Log.w(TAG, "Fullscreen button presed, but BackgroundService is not running");
                }
            }
        });
    }

    private void findMyViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //mContentView = findViewById(R.id.main_activity_content);
        //fragmentHolder = (FrameLayout) findViewById(R.id.fragmentHolder);
        mContentView = coordinatorLayout;

        cpuTempText = (TextView) findViewById(R.id.cpuTempText);
        gpuTempText = (TextView) findViewById(R.id.gpuTempText);
        gpuLoadView = (LoadView) findViewById(R.id.gpuLoadBar);
        cpuLoadView = (LoadView) findViewById(R.id.cpuLoadBar);
        cpuNameText = (TextView) findViewById(R.id.cpuNameText);
        gpuNameText = (TextView) findViewById(R.id.gpuNameText);
        cpuFreqText = (TextView) findViewById(R.id.cpuCoreText);
        gpuFreqText = (TextView) findViewById(R.id.gpuCoreText);
        gpuOfflineText = (TextView) findViewById(R.id.gpuOfflineText);
    }

    private void loadResources() {
        sDegs = getString(R.string.placeholder_celsius);
        sMhz = getString(R.string.placeholder_megahertz);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("fullscreen", !mVisible);
        outState.putString("cpu", cpuNameText.getText().toString());
        outState.putString("gpu", gpuNameText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (BackgroundService.running) moveTaskToBack(false);
        else super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startBroadcastReceiver();
        //if (!mVisible) show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBroadcastReceiver();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "Destroying...");
        UpdateChecker.reset();
    }*/

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.e("MainActivity", "onAttachFragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        disconnectMenuItem = menu.findItem(R.id.action_stop_server);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem disc = menu.findItem(R.id.action_stop_server);
        if (BackgroundService.running) {
            disc.setVisible(true);
        } else {
            disc.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);

        } else if (id == R.id.action_stop_server) {
            // Stop BackgroundServer
            Intent intent = new Intent(this, BackgroundService.class);
            intent.setAction(BackgroundService.FINISH_SELF_INTENT);
            startService(intent);

            // This may not be necessary
            disconnectMenuItem.setVisible(false);

            // Launch the start activity
            intent = new Intent(this, LaunchActivity.class);
            startActivity(intent);
            overridePendingTransition(0, R.anim.nice_fade_out);

            //Finally, close the activity
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isGpuOffline = false;
    private void setBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d("BroadcastReceiver", "Reveived intent with action " + intent.getAction());
                if (intent.getAction().equals(BackgroundService.MOBILE_DATA_INTENT)) {
                    final int CL = intent.getIntExtra("CL", 0);
                    final int GL = intent.getIntExtra("GL", 0);
                    final int FPS = intent.getIntExtra("FPS", 0);
                    final int CT = intent.getIntExtra("CT", 0);
                    final int GT = intent.getIntExtra("GT", 0);
                    final int CF = intent.getIntExtra("CF", 0);
                    final int GF = intent.getIntExtra("GF", 0);

                    cpuLoadView.setPercentage(CL);
                    if (GL < 0) {
                        if (!isGpuOffline) {
                            isGpuOffline = true;
                            gpuLoadView.setVisibility(View.GONE);
                            gpuOfflineText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (isGpuOffline) {
                            gpuLoadView.setVisibility(View.VISIBLE);
                            gpuOfflineText.setVisibility(View.GONE);
                            isGpuOffline = false;
                        }
                        gpuLoadView.setPercentage(GL);
                    }
                    //fpsText.setText(String.format("%.0f", FPS));
                    cpuTempText.setText(String.format(sDegs, CT));
                    if (GT < 0 ) gpuTempText.setText("---");
                    else gpuTempText.setText(String.format(sDegs, GT));
                    cpuFreqText.setText(String.format(sMhz, CF));
                    if (GF < 0) gpuFreqText.setText("---");
                    else gpuFreqText.setText(String.format(sMhz, GF));

                } else if (intent.getAction().equals(BackgroundService.MOBILE_INFO_INTENT)) {
                    if (intent.hasExtra("cpu")) {
                        cpuNameText.setText(intent.getStringExtra("cpu"));
                    }
                    if (intent.hasExtra("gpu")) {
                        gpuNameText.setText(intent.getStringExtra("gpu"));
                    }
                }

            }
        };
    }

    private void startBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.MOBILE_DATA_INTENT);
        filter.addAction(BackgroundService.MOBILE_INFO_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void stopBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }


    /* Fullscreen */
    //private final Handler mHideHandler = new Handler();
    //private static final int UI_ANIMATION_DELAY = 300;
    private boolean mVisible = true;
    private static final int fullscreenDecorFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


    private void hide() {
        mContentView.setOnClickListener(mClickListener);

        mVisible = false;
        coordinatorLayout.setFitsSystemWindows(false);
        //mContentView.setFitsSystemWindows(false);
        mContentView.setSystemUiVisibility(fullscreenDecorFlags);
        fab.setVisibility(View.GONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mainFragment.setAmbientMode(true);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        ActionBar ab = getSupportActionBar();
        if (ab != null && !ab.isShowing()) {
            ab.show();
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //this.onto
        fab.setVisibility(View.VISIBLE);
        //mainFragment.setAmbientMode(false);

        mContentView.setOnClickListener(null);
        coordinatorLayout.setFitsSystemWindows(true);
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            show();
        }
    };

}

