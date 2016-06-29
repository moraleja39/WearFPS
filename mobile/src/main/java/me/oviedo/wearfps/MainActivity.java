package me.oviedo.wearfps;

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
import android.widget.TextView;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private FrameLayout fragmentHolder;

    private MenuItem disconnectMenuItem;

    private View mContentView;

    private static final String MAIN_FRAGMENT_TAG = "MainFragment";
    private MainFragment mainFragment = null;

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
            mainFragment = MainFragment.newInstance(false);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, mainFragment, MAIN_FRAGMENT_TAG).commit();
            mVisible = true;
        } else {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("fullscreen", false)) {
                Log.d("MainActivity", "Saved instance: fullscreen");
                //getSupportActionBar().hide();
                hide();
                //getSupportActionBar().show();
            } else {
                Log.d("MainActivity", "Saved instance: normal screen");
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BackgroundService.running) {
                    hide();

                }
                else {
                    requestRemoteIp();
                }
            }
        });
    }

    private void findMyViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //mContentView = findViewById(R.id.main_activity_content);
        fragmentHolder = (FrameLayout) findViewById(R.id.fragmentHolder);
        mContentView = coordinatorLayout;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("fullscreen", !mVisible);
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
        setFabImage(BackgroundService.running);
        //if (!mVisible) show();
    }

    private void setFabImage(boolean running) {
        if (running) {
            fab.setImageResource(R.drawable.ic_fullscreen_24dp);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
            intent.setAction(BackgroundService.FINISH_SELF_INTENT);
            startService(intent);
            setFabImage(false);
            disconnectMenuItem.setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }


    private AsyncTask<Void, Void, Void> async_cient;
    private static final String Message = "IP_REQ";
    private final int UDP_PORT = 55632;
    private String ipadd;
    DatagramSocket ds = null;


    public void requestRemoteIp() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d("RequestIP", "Sending request packet...");
                    if (ds == null) ds = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName("255.255.255.255");
                    DatagramPacket dp = new DatagramPacket(Message.getBytes(), Message.length(), addr, UDP_PORT);
                    ds.setBroadcast(true);
                    ds.send(dp);

                    Log.d("RequestIP", "Listening for response...");
                    byte[] lMsg = new byte[4096];
                    dp = new DatagramPacket(lMsg, lMsg.length);
                    ds.setSoTimeout(4000);
                    ds.receive(dp);
                    ipadd = new String(lMsg, 0, dp.getLength());
                    Log.i("RequestIP", "Server address: " + ipadd);

                    Log.d("RequestIP", "Finished!");

                } catch (InterruptedIOException e) {
                    Log.w("RequestIP", "The remote server did not answer. Maybe it is not started?");
                    ipadd = null;

                } catch (Exception e) {
                    Log.e("RequestIP", "Exception");
                    e.printStackTrace();
                    ipadd = null;
                } finally {
                    if (ds != null) {
                        ds.close();
                        ds = null;
                    }
                }
                return null;
            }



            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (ipadd != null) {

                    Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                    intent.setAction(BackgroundService.START_INTENT_ACTION);
                    intent.putExtra(BackgroundService.IP_EXTRA, ipadd);
                    startService(intent);
                    setFabImage(true);
                    disconnectMenuItem.setVisible(true);
                }
            }
        };

        async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

