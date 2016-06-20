package me.oviedo.wearfps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;

    private BroadcastReceiver mBroadcastReceiver;

    /* Views*/
    private TextView cpuTempText, gpuTempText, cpuNameText, gpuNameText, cpuFreqText, gpuFreqText;
    private LoadView cpuLoadView, gpuLoadView;

    private final String sDegCen = "ºC";

    private MenuItem disconnectMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findMyViews();

        if (savedInstanceState != null) {
            cpuNameText.setText(savedInstanceState.getString("cpu"));
            gpuNameText.setText(savedInstanceState.getString("gpu"));
        }

        setBroadcastReceiver();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BackgroundService.running) {
                    //invalidateOptionsMenu();
                    Intent i = new Intent(getApplicationContext(), FullscreenActivity.class);
                    startActivity(i);

                }
                else {
                    requestRemoteIp();
                }
                //new Thread(new Udp()).start();

                /*Snackbar.make(view, "L:" + L + ", R: " + R, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                String tmp = L + "-" + R;
                talkToWear(ALL_DATA_PATH, tmp.getBytes());*/
            }
        });
    }

    private void findMyViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cpuTempText = (TextView) findViewById(R.id.cpuTempText);
        gpuTempText = (TextView) findViewById(R.id.gpuTempText);
        gpuLoadView = (LoadView) findViewById(R.id.gpuLoadBar);
        cpuLoadView = (LoadView) findViewById(R.id.cpuLoadBar);
        cpuNameText = (TextView) findViewById(R.id.cpuNameText);
        gpuNameText = (TextView) findViewById(R.id.gpuNameText);
        cpuFreqText = (TextView) findViewById(R.id.cpuCoreText);
        gpuFreqText = (TextView) findViewById(R.id.gpuCoreText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cpu", cpuNameText.getText().toString());
        outState.putString("gpu", gpuNameText.getText().toString());
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
        startBroadcastReceiver();
    }

    private void setFabImage(boolean running) {
        if (running) {
            fab.setImageResource(R.drawable.ic_fullscreen_24dp);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    /*private void goAmbient(boolean fullscreen)
    {
        View decorView = getWindow().getDecorView();
        int uiOptions;
        if (fullscreen) {
            // Hide the status bar.
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            getSupportActionBar().hide();
            coordinatorLayout.setFitsSystemWindows(false);
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            getSupportActionBar().show();
            coordinatorLayout.setFitsSystemWindows(true);
        }
        decorView.setSystemUiVisibility(uiOptions);
    }*/

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
                    gpuLoadView.setPercentage(GL);
                    //fpsText.setText(String.format("%.0f", FPS));
                    cpuTempText.setText(CT + sDegCen);
                    gpuTempText.setText(GT + sDegCen);
                    cpuFreqText.setText(CF + "MHz");
                    gpuFreqText.setText(GF + "MHz");

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

    @Override
    protected void onStop() {
        super.onStop();
        stopBroadcastReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        disconnectMenuItem = menu.findItem(R.id.action_stop_server);
        return true;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem disc = menu.findItem(R.id.action_stop_server);
        if (BackgroundService.running) {
            disc.setVisible(true);
        } else {
            disc.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }*/

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
    private String Message = "IP_REQ";
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
                } else {
                }
            }
        };

        async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

