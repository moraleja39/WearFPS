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

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class BackgroundService extends Service {

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

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //new Thread(new Udp()).start();
        running = true;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (intent.getAction().equals(FINISH_SELF_INTENT)) {
            if (boundToWearService) {
                wearBinder.finishActivity();
                unbindService(mConnection);
            }
            //isExiting = true;
            try {
                tcpSocket.close();
            } catch (Exception e) {
                Log.i("BackgroundService", "Socket already closed.");
                stopSelf();
            }
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
                        String[] values = serverMessage.split(";");
                        cl = Integer.valueOf(values[0]);
                        gl = Integer.valueOf(values[1]);
                        fps = Integer.valueOf(values[2]);
                        ct = Integer.valueOf(values[3]);
                        gt= Integer.valueOf(values[4]);
                        cf= Integer.valueOf(values[5]);
                        gf= Integer.valueOf(values[6]);

                        if (boundToWearService) {
                            buffer.clear();
                            buffer.putInt(cl);
                            buffer.putInt(gl);
                            buffer.putInt(fps);
                            buffer.putInt(ct);
                            buffer.putInt(gt);
                            buffer.putInt(cf);
                            buffer.putInt(gf);
                            wearBinder.sendData(buffer.array());
                        }

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
                stopSelf();
            }

        }

    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        running = false;
        Log.i("BackgroundService", "Destroying service...");
    }
}