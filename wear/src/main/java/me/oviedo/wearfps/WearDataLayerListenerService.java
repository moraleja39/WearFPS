package me.oviedo.wearfps;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


public class WearDataLayerListenerService extends WearableListenerService {
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    public static final String FINISH_ACTIVITY_PATH = "/finish/MainActivity";
    public static final String ALL_DATA_PATH = "/data/all";

    public static final String APP_INTENT = "me.oviedo.wearfps.service.UPDATE_UI";
    public static final String FINISH_INTENT = "me.oviedo.wearfps.service.FINISH_REQUESTED";
    //public static final String APP_EXTRA = "me.oviedo.wearfps.service.GET_DATA";

    private static LocalBroadcastManager lbcm;

    @Override
    public void onCreate() {
        super.onCreate();
        lbcm = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if(messageEvent.getPath().equals(START_ACTIVITY_PATH)){
            Intent intent = new Intent(this , MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (messageEvent.getPath().equals(ALL_DATA_PATH)) {
            String s = new String(messageEvent.getData());
            String[] values = s.split("-");
            Intent intent = new Intent(APP_INTENT);
            intent.putExtra("CU", Float.valueOf(values[0]));
            intent.putExtra("GU", Float.valueOf(values[1]));
            intent.putExtra("FPS", Float.valueOf(values[2]));
            intent.putExtra("CT", Float.valueOf(values[3]));
            intent.putExtra("GT", Float.valueOf(values[4]));
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