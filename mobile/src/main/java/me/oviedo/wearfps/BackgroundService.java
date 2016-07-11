package me.oviedo.wearfps;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";

    public static final String IP_EXTRA = "IP";

    public static final String START_INTENT_ACTION = "START";
    public static final String FINISH_SELF_INTENT = "KILL_URSELF";

    private static final int FOREGROUND_NOTIFICATION_ID = 183;

    public static final String MOBILE_DATA_INTENT = "me.oviedo.wearfps.DATA_INTENT";
    public static final String MOBILE_INFO_INTENT = "me.oviedo.wearfps.INFO_INTENT";

    public static boolean running = false;

    //private String remoteAddr = null;
    private final int TCP_PORT = 55633;
    private String serverIP = "192.168.1.10";
    private int receivedCount = 0;

    //private boolean isExiting = false;

    private Socket tcpSocket;

    private LocalBroadcastManager lbm;

    WearCommService.WearBinder wearBinder;
    boolean boundToWearService = false;

    boolean isCleanlyExiting = false;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //new Thread(new Udp()).start();
        running = true;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (intent.getAction().equals(FINISH_SELF_INTENT)) {
            isCleanlyExiting = true;
            cleanup();
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        if (sharedPref.getBoolean("pref_wear_enabled", false)) {
            Intent i = new Intent(this, WearCommService.class);
            bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        }

        if (intent.hasExtra(IP_EXTRA)) {
            serverIP = intent.getStringExtra(IP_EXTRA);
        }

        lbm = LocalBroadcastManager.getInstance(this);

        // Put the service on the foreground
        //Intent notificationIntent = new Intent(this, BackgroundService.class);
        //notificationIntent.setAction(FINISH_SELF_INTENT);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this).setContentTitle("WearFPS").setTicker("WearFPS")
                .setContentText("Conectado").setSmallIcon(R.drawable.ic_wear_syncing)
                .setContentIntent(pendingIntent).build();

        /*Intent notificationIntent = new Intent(this, ExampleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.notification_title),
                getText(R.string.notification_message), pendingIntent);*/
        startForeground(FOREGROUND_NOTIFICATION_ID, notification);

        new Thread(new TCPClient()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            wearBinder = (WearCommService.WearBinder) iBinder;
            boundToWearService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundToWearService = false;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //does not provide binding
        return null;
    }

    class TCPClient implements Runnable {

        //char[] buf = new char[256];
        byte[] buf = new byte[512];
        int type;
        //String serverMessage;
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

                //InputStreamReader isr = new InputStreamReader(tcpSocket.getInputStream());
                InputStream inputStream = tcpSocket.getInputStream();
                //BufferedReader in = new BufferedReader(isr);
                //InputStream is = tcpSocket.getInputStream();

                // Declaramos las variables utilizadas para los datos, para evitar que se reasignen en cada mensaje recibido
                //int cl, gl, fps, ct, gt, cf, gf;
                ByteBuffer buffer = ByteBuffer.allocate(4 * 7);

                //while ((len = isr.read(buf, 0, buf.length)) > 0) {
                while ((type = inputStream.read()) >= 0) {
                    receivedCount++;
                    switch (type) {
                        // Información inicial del hardware
                        case 0:
                            //Log.d(TAG, "Tipo 0");
                            /*int size = 0;
                            size |= (inputStream.read() >> 8);
                            size |= inputStream.read();
                            Log.d(TAG, "Received data size: " + size);
                            len = 0;
                            while (len < size) {
                                len += inputStream.read(buf, len, size - len);
                            }
                            Log.d(TAG, "Received bytes:\n" + Arrays.toString(buf));
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buf, 0, size);*/
                            WearFpsProto.ComputerInfo computerInfo = WearFpsProto.ComputerInfo.parseDelimitedFrom(inputStream);
                            //Log.d(TAG, computerInfo.toString());
                            Intent intent = new Intent(MOBILE_INFO_INTENT);
                            intent.putExtra("cpu", computerInfo.getCpuName());
                            intent.putExtra("gpu", computerInfo.getGpuName());
                            lbm.sendBroadcast(intent);
                            break;
                        // Datos en forma integer
                        case 1:
                            //Log.d(TAG, "Tipo 1");
                            WearFpsProto.DataInt dataInt = WearFpsProto.DataInt.parseDelimitedFrom(inputStream);
                            //Log.d(TAG, dataInt.toString());
                            // Si estamos conectados a un wearable, creamos un buffer simple y lo enviamos
                            if (boundToWearService) {
                                buffer.clear();
                                buffer.putInt(dataInt.getCpuLoad()).putInt(dataInt.getGpuLoad()).putInt(dataInt.getFps());
                                buffer.putInt(dataInt.getCpuTemp()).putInt(dataInt.getGpuTemp()).putInt(dataInt.getCpuFreq()).putInt(dataInt.getGpuFreq());
                                wearBinder.sendData(buffer.array());
                            }
                            Intent i = new Intent(MOBILE_DATA_INTENT);
                            i.putExtra("proto", dataInt);
                            lbm.sendBroadcast(i);
                            break;
                        default:
                            Log.e(TAG, "Unsupported message format received: " + type);
                            break;
                    }
                }

                Log.w(TAG, "Final del stream detectado");
            } catch (SocketTimeoutException e) {
                Log.w("TCPClient", "No se ha podido conectar con el host remoto tras 3500ms");
            } catch (Exception e) {
                if (e.getMessage().equalsIgnoreCase("socket closed")) {
                    Log.i("TCPClient", "TCP socket closed");
                } else e.printStackTrace();
            }/* finally {
                stopSelf();
            }*/

        }

    }

    private void cleanup() {
        if (boundToWearService) {
            wearBinder.finishActivity();
            unbindService(mConnection);
        }
        if (!tcpSocket.isClosed()) {
            try {
                tcpSocket.close();
            } catch (Exception e) {
                if (e.getMessage().equalsIgnoreCase("socket closed")) {
                    Log.i("TCPClient", "TCP socket closed");
                } else e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        running = false;
        if (!isCleanlyExiting) cleanup();
        Log.i("BackgroundService", "Destroying service...");
    }
}