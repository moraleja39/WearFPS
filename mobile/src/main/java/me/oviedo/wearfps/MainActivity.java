package me.oviedo.wearfps;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {


    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BackgroundService.running) {
                    Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                    intent.setAction(BackgroundService.FINISH_SELF_INTENT);
                    startService(intent);
                    setFabImage(false);
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

    @Override
    protected void onStart() {
        super.onStart();
        setFabImage(BackgroundService.running);
    }

    private void setFabImage(boolean running) {
        if (running) {
            fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
                } else {
                }
            }
        };

        async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

