package me.oviedo.wearfps;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class BackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String IP_EXTRA = "IP";

    public static final String START_INTENT_ACTION = "START";
    public static final String FINISH_SELF_INTENT = "KILL_URSELF";

    private static final int FOREGROUND_NOTIFICATION_ID = 183;

    public static final String START_ACTIVITY_PATH = "/me.oviedo.wearfps/start/MainActivity";
    public static final String ALL_DATA_PATH = "/me.oviedo.wearfps/data/all";
    public static final String FINISH_ACTIVITY_PATH = "/me.oviedo.wearfps/finish/MainActivity";

    public static final String MOBILE_DATA_INTENT = "me.oviedo.wearfps.DATA_INTENT";
    public static final String MOBILE_INFO_INTENT = "me.oviedo.wearfps.INFO_INTENT";

    public static boolean running = false;

    //private String remoteAddr = null;
    private final int TCP_PORT = 55633;
    private String serverIP = "192.168.1.10";
    private int receivedCount = 0;

    private boolean isExiting = false;

    private Socket tcpSocket;

    private GoogleApiClient mGoogleApiClient;

    private LocalBroadcastManager lbm;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //new Thread(new Udp()).start();
        running = true;

        if (intent.getAction().equals(FINISH_SELF_INTENT)) {
            talkToWear(FINISH_ACTIVITY_PATH, new byte[0]);
            isExiting = true;
            try {
                tcpSocket.close();
            } catch (Exception e) {
                Log.i("BackgroundService", "Socket already closed.");
            }
            return super.onStartCommand(intent, flags, startId);
        }

        if (intent.hasExtra(IP_EXTRA)) {
            serverIP = intent.getStringExtra(IP_EXTRA);
        }

        lbm = LocalBroadcastManager.getInstance(this);

        // Put the service on the foreground
        Intent notificationIntent = new Intent(this, BackgroundService.class);
        notificationIntent.setAction(FINISH_SELF_INTENT);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this).setContentTitle("WearFPS").setTicker("WearFPS")
                .setContentText("Conectado").setSmallIcon(R.drawable.ic_wear_syncing)
                .setContentIntent(pendingIntent).build();

        /*Intent notificationIntent = new Intent(this, ExampleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.notification_title),
                getText(R.string.notification_message), pendingIntent);*/
        startForeground(FOREGROUND_NOTIFICATION_ID, notification);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("pref_wear_autostart", false)) {
            talkToWear(START_ACTIVITY_PATH, new byte[0]);
        }

        new Thread(new TCPClient()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //does not provide binding
        return null;
    }

    class TCPClient implements Runnable {

        char[] buf = new char[256];
        String serverMessage;
        int len = 0;

        @Override
        public void run() {

            try {
                Log.d("TCPClient", "Intentando conectar con el host remoto...");
                //InetAddress remote = InetAddress.getByName(serverIP);
                tcpSocket = new Socket();
                SocketAddress addr = new InetSocketAddress(serverIP, TCP_PORT);
                tcpSocket.connect(addr, 3500);
                if (!tcpSocket.isConnected()) {
                    throw new SocketException("No se ha podido conectar con el host remoto");
                } else Log.d("TCPClient", "Conectado!");

                tcpSocket.setSoTimeout(5000);

                InputStreamReader isr = new InputStreamReader(tcpSocket.getInputStream());
                //BufferedReader in = new BufferedReader(isr);
                //InputStream is = tcpSocket.getInputStream();

                // Declaramos las variables utilizadas para los datos, para evitar que se reasignen en cada mensaje recibido
                int cl, gl, fps, ct, gt, cf, gf;
                ByteBuffer buffer = ByteBuffer.allocate(4 * 7);

                while ((len = isr.read(buf, 0, buf.length)) > 0) {
                    //serverMessage = in.readLine();
                    //buf[len] = 0x00;
                    serverMessage = new String(buf, 0, len);
                    Log.v("TCPClient", serverMessage);


                    if (serverMessage.startsWith(":")) {
                        Intent intent = new Intent(MOBILE_INFO_INTENT);
                        String[] msgs = serverMessage.trim().split("\n");
                        for (String m : msgs) {
                            m = m.substring(1);
                            String[] values = m.split("=");
                            Log.d("TCPClient", values[0] + ": " + values[1]);
                            intent.putExtra(values[0], values[1]);
                        }
                        lbm.sendBroadcast(intent);
                        //Log.i("BackgroundService", values[0] + ": " + values[1]);
                    } else if (!serverMessage.equals("")) {
                        receivedCount++;

                        //En caso de que se junten dos mensajes, nos quedamos con el útlimo
                        serverMessage = serverMessage.trim();
                        serverMessage = serverMessage.substring(serverMessage.lastIndexOf('\n') + 1);

                        //Log.v("TCPClient", "Message " + receivedCount + ": " + serverMessage);
                        buffer.clear();
                        String[] values = serverMessage.split(";");
                        cl = Integer.valueOf(values[0]); buffer.putInt(cl);
                        gl = Integer.valueOf(values[1]); buffer.putInt(gl);
                        fps = Integer.valueOf(values[2]); buffer.putInt(fps);
                        ct = Integer.valueOf(values[3]); buffer.putInt(ct);
                        gt= Integer.valueOf(values[4]); buffer.putInt(gt);
                        cf= Integer.valueOf(values[5]); buffer.putInt(cf);
                        gf= Integer.valueOf(values[6]); buffer.putInt(gf);

                        talkToWear(ALL_DATA_PATH, buffer.array());

                        Intent intent = new Intent(MOBILE_DATA_INTENT);
                        intent.putExtra("CL", cl);
                        intent.putExtra("GL", gl);
                        intent.putExtra("FPS", fps);
                        intent.putExtra("CT", ct);
                        intent.putExtra("GT", gt);
                        intent.putExtra("CF", cf);
                        intent.putExtra("GF", gf);
                        lbm.sendBroadcast(intent);

                    }
                    serverMessage = null;
                    /*if (receivedCount > 5) {
                        tcpSocket.close();
                        Log.i("TCPClient", "Disconnected from host");
                        break;
                    }*/
                }
            } catch (SocketTimeoutException e) {
                Log.w("TCPClient", "No se ha podido conectar con el host remoto tras 3500ms");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                talkToWear(FINISH_ACTIVITY_PATH, new byte[0]);
                stopSelf();
            }

        }

    }


    private void talkToWear(final String path, final byte[] msg) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    sendMessage(node.getId(), path, msg);
                }
            }
        });
    }

    private void sendMessage(String node, String path, byte[] msg) {
        Wearable.MessageApi.sendMessage(mGoogleApiClient, node, path, msg).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (isExiting) {
                    mGoogleApiClient.disconnect();
                    stopSelf();
                }
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        running = false;
        Log.i("BackgroundService", "Destroying service...");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GoogleApi", "onConnected: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GoogleApi", "onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GoogleApi", "onConnectionFailed: " + connectionResult);
    }
}