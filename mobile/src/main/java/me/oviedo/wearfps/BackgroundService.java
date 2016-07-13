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
import android.transition.Explode;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Random;

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
    private static final int PORT = 55633;
    private String serverIP = "192.168.1.10";
    private int receivedCount = 0;

    //private boolean isExiting = false;

    private final Socket tcpSocket = new Socket();
    ;
    private DatagramChannel udpChannel;
    DatagramSocket udpSocket;
    private Thread udpThread;

    private LocalBroadcastManager lbm;

    WearCommService.WearBinder wearBinder;
    boolean boundToWearService = false;

    boolean isCleanlyExiting = false;

    //private final Object udpConnectLock = new Object();

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

        udpThread = new Thread(new UDPReceiver());
        udpThread.start();
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

        @Override
        public void run() {

            try {
                Log.d("TCPClient", "Intentando conectar con el host remoto...");
                synchronized (tcpSocket) {
                    SocketAddress addr = new InetSocketAddress(serverIP, PORT);
                    tcpSocket.connect(addr, 3500);
                    if (!tcpSocket.isConnected()) {
                        throw new SocketException("No se ha podido conectar con el host remoto");
                    } else Log.d("TCPClient", "Conectado!");
                    tcpSocket.notify();
                }

                tcpSocket.setSoTimeout(0);
                tcpSocket.setTcpNoDelay(true);

                Random random = new Random();

                OutputStream outputStream = tcpSocket.getOutputStream();

                //while ((len = isr.read(buf, 0, buf.length)) > 0) {
                while (running) {
                    outputStream.write(0x00);
                    outputStream.flush();
                    Thread.sleep(5000 + random.nextInt(5000));
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

    class UDPReceiver implements Runnable {

        ByteBuffer buffer;
        ByteString data;

        byte type;
        int size;

        @Override
        public void run() {
            buffer = ByteBuffer.allocate(4096);
            ByteBuffer wearBuffer = ByteBuffer.allocate(4 * 7);
            try {
                udpChannel = DatagramChannel.open();
                udpSocket = udpChannel.socket();
                synchronized (tcpSocket) {
                    if (!tcpSocket.isConnected()) {
                        tcpSocket.wait();
                    }
                }
                udpSocket.bind(tcpSocket.getLocalSocketAddress());
                //udpSocket.bind(new InetSocketAddress(PORT));
            } catch (Exception e) {
                Log.e(TAG, "Error creating datagram socket: " + e.getMessage());
                e.printStackTrace();
            }

            while (running) {
                try {
                    udpChannel.receive(buffer);
                    buffer.flip();
                    receivedCount++;

                    //Log.d(TAG, "buffer content: " + Arrays.toString(buffer.array()));

                    // Leemos el tipo
                    type = buffer.get();
                    // Leemos el tamaño
                    size = 0;
                    size |= (buffer.get() << 8);
                    size |= buffer.get();

                    data = ByteString.copyFrom(buffer);

                    Log.d(TAG, "UDP packet #" + receivedCount + ": Type " + type + ", size " + size);

                } catch (IOException ex) {
                    Log.e(TAG, "Error al intentar leer el stream UDP:");
                    ex.printStackTrace();
                    continue;
                }

                //dataBuffer.flip();

                //dataBuffer.rewind();

                try {
                    switch (type) {
                        // Información inicial del hardware
                        case 0:
                            WearFpsProto.ComputerInfo computerInfo = WearFpsProto.ComputerInfo.parseFrom(data);
                            //Log.d(TAG, computerInfo.toString());
                            Intent intent = new Intent(MOBILE_INFO_INTENT);
                            intent.putExtra("cpu", computerInfo.getCpuName());
                            intent.putExtra("gpu", computerInfo.getGpuName());
                            lbm.sendBroadcast(intent);
                            break;
                        // Datos en forma integer
                        case 1:
                            //Log.d(TAG, "Tipo 1");
                            WearFpsProto.DataInt dataInt = WearFpsProto.DataInt.parseFrom(data);
                            //Log.d(TAG, dataInt.toString());
                            // Si estamos conectados a un wearable, creamos un buffer simple y lo enviamos
                            if (boundToWearService) {
                                wearBuffer.clear();
                                wearBuffer.putInt(dataInt.getCpuLoad()).putInt(dataInt.getGpuLoad()).putInt(dataInt.getFps());
                                wearBuffer.putInt(dataInt.getCpuTemp()).putInt(dataInt.getGpuTemp()).putInt(dataInt.getCpuFreq()).putInt(dataInt.getGpuFreq());
                                wearBinder.sendData(wearBuffer.array());
                            }
                            Intent i = new Intent(MOBILE_DATA_INTENT);
                            i.putExtra("proto", dataInt);
                            lbm.sendBroadcast(i);
                            break;
                        default:
                            Log.e(TAG, "Unsupported message format received: " + type);
                            break;
                    }
                } catch (InvalidProtocolBufferException e) {
                    Log.e(TAG, "Invalid protobuf data: " + e.getMessage());
                }
                buffer.clear();
            }


        }
    }

    private void cleanup() {
        running = false;
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
        if (!udpSocket.isClosed()) {
            try {
                udpSocket.close();
                udpChannel.close();
            } catch (Exception e) {
                if (e.getMessage().equalsIgnoreCase("socket closed")) {
                    Log.i(TAG, "UDP socket closed");
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