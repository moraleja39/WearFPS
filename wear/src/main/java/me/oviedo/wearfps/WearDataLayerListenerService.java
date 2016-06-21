package me.oviedo.wearfps;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;


public class WearDataLayerListenerService extends WearableListenerService {
    public static final String START_ACTIVITY_PATH = "/me.oviedo.wearfps/start/MainActivity";
    public static final String FINISH_ACTIVITY_PATH = "/me.oviedo.wearfps/finish/MainActivity";
    public static final String ALL_DATA_PATH = "/me.oviedo.wearfps/data/all";

    public static final String APP_INTENT = "me.oviedo.wearfps.service.UPDATE_UI";
    public static final String FINISH_INTENT = "me.oviedo.wearfps.service.FINISH_REQUESTED";
    //public static final String APP_EXTRA = "me.oviedo.wearfps.service.GET_DATA";

    private static LocalBroadcastManager lbcm;

    private ByteBuffer buffer;
    // En orden
    private int cl, gl, fps, ct, gt, cf, gf;

    @Override
    public void onCreate() {
        super.onCreate();
        lbcm = LocalBroadcastManager.getInstance(this);
        //buffer = ByteBuffer.allocate(4 * 7);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if(messageEvent.getPath().equals(START_ACTIVITY_PATH)){
            Intent intent = new Intent(this , MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (messageEvent.getPath().equals(ALL_DATA_PATH)) {
            //buffer.clear();
            buffer = ByteBuffer.wrap(messageEvent.getData());
            Intent intent = new Intent(APP_INTENT);
            intent.putExtra("CU", buffer.getInt());
            intent.putExtra("GU", buffer.getInt());
            intent.putExtra("FPS", buffer.getInt());
            intent.putExtra("CT", buffer.getInt());
            intent.putExtra("GT", buffer.getInt());
            lbcm.sendBroadcast(intent);

        } else if (messageEvent.getPath().equals(FINISH_ACTIVITY_PATH)) {
            Log.d("WearDataLayerListener", "Received finish activity request");
            Intent intent = new Intent(FINISH_INTENT);
            //intent.putExtra("damn", 0);
            lbcm.sendBroadcast(intent);
            Log.d("WearDataLayerListener", "FINISH_INTENT sent");
        }
    }
}