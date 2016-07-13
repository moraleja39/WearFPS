package me.oviedo.wearfps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StartActivity extends AppCompatActivity {

    private TextView statusText;
    private ProgressBar progressBar;
    private Button retryButton;
    private FloatingActionButton fab;
    private CoordinatorLayout root;

    private boolean shouldFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            if (getWindow().hasFeature(Window.FEATURE_CONTENT_TRANSITIONS)) Log.e("StartActivity", "CONTENT_TRANSITIONS");
        }*/

        setContentView(R.layout.activity_start);
        findViews();
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.settingsPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRemoteIp();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestRemoteIp();
        UpdateChecker.check(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shouldFinish) finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_just_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        statusText = (TextView) findViewById(R.id.connectStatusText);
        progressBar = (ProgressBar) findViewById(R.id.connectProgressbar);
        retryButton = (Button) findViewById(R.id.retryConnectButton);
        fab = (FloatingActionButton) findViewById(R.id.start_fab);
        root = (CoordinatorLayout) findViewById(R.id.start_root);
    }

    private void setFindingServer() {
        statusText.setText(getText(R.string.finding_server));
        retryButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        fab.hide();
    }

    private void setError() {
        statusText.setText(getText(R.string.server_not_found));
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private void setReady() {
        statusText.setText(getText(R.string.server_ready));
        retryButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fab.show();
    }


    private AsyncTask<Void, Void, Void> async_cient;
    //private static final String Message = "IP_REQ";
    private static final byte[] IP_REQ_MSG = { 0x00 };
    private final int UDP_PORT = 55632;
    private String ipadd;
    DatagramSocket ds = null;

    private void createConnectTask() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d("RequestIP", "Sending request packet...");
                    if (ds == null) ds = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName("255.255.255.255");
                    DatagramPacket dp = new DatagramPacket(IP_REQ_MSG, IP_REQ_MSG.length, addr, UDP_PORT);
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
                    setReady();
                } else {
                    setError();
                }
            }
        };
    }

    private void connect() {
        // Start background service first
        Intent intent = new Intent(this, BackgroundService.class);
        intent.setAction(BackgroundService.START_INTENT_ACTION);
        intent.putExtra(BackgroundService.IP_EXTRA, ipadd);
        startService(intent);

        //Then start MainActivity
        intent = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra("shouldBeTranslucent", true);
            //getWindow().setExitTransition(null);
            @SuppressWarnings("unchecked") ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create((View)fab, "fab_transition"));
            //options.update(ActivityOptionsCompat.makeSceneTransitionAnimation(this));
            ActivityCompat.startActivity(this, intent, options.toBundle());
            //overridePendingTransition(0, R.anim.nice_fade_out);
            shouldFinish = true;
            //finish();
        } else {
            startActivity(intent);
            overridePendingTransition(0, R.anim.nice_fade_out);
            finish();
        }

        //Finally, finish this activity
        //finish();

        //disconnectMenuItem.setVisible(true);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, mainFragment, MAIN_FRAGMENT_TAG).commit();
    }

    private void requestRemoteIp() {
        createConnectTask();
        setFindingServer();
        async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
