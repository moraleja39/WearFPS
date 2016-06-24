package me.oviedo.wearfps;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class WearCommService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WearCommService";

    private final IBinder mBinder = new WearBinder();

    public static final String START_ACTIVITY_PATH = "/me.oviedo.wearfps/start/MainActivity";
    public static final String ALL_DATA_PATH = "/me.oviedo.wearfps/data/all";
    public static final String FINISH_ACTIVITY_PATH = "/me.oviedo.wearfps/finish/MainActivity";

    private GoogleApiClient mGoogleApiClient;

    private boolean connectedToGAPI;

    private List<Node> nodes;

    private boolean activityStarted = false;

    public WearCommService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        connectedToGAPI = false;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        return mBinder;
    }

    public class WearBinder extends Binder {
        void sendData(byte[] data) {
            talkToWear(ALL_DATA_PATH, data);
        }

        void finishActivity() {
            talkToWear(FINISH_ACTIVITY_PATH, new byte[0]);
        }
    }

    private void startActivityIfNecessary() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("pref_wear_autostart", false)) {
            talkToWear(START_ACTIVITY_PATH, new byte[0]);
        }
    }

    private void talkToWear(final String path, final byte[] msg) {
        if (!connectedToGAPI) return;
        for (Node node : nodes) {
            sendMessage(node.getId(), path, msg);
        }
    }

    private void talkToWearSync(final String path, final byte[] msg) {
        if (!connectedToGAPI) return;
        for (Node node : nodes) {
            sendMessageSync(node.getId(), path, msg);
        }
    }

    ResultCallback<MessageApi.SendMessageResult> sendMessageResultResultCallback = new ResultCallback<MessageApi.SendMessageResult>() {
        @Override
        public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
            if (!sendMessageResult.getStatus().isSuccess()) {
                Log.e("GoogleApi", "Failed to send message with status code: "
                        + sendMessageResult.getStatus().getStatusCode());
            }
        }
    };

    private void sendMessage(String node, String path, byte[] msg) {
        Wearable.MessageApi.sendMessage(mGoogleApiClient, node, path, msg).setResultCallback(sendMessageResultResultCallback);
    }

    private void sendMessageSync(String node, String path, byte[] msg) {
        MessageApi.SendMessageResult res = Wearable.MessageApi.sendMessage(mGoogleApiClient, node, path, msg).await();
        sendMessageResultResultCallback.onResult(res);
    }

    private void findNodes() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                nodes = getConnectedNodesResult.getNodes();
                if (!activityStarted) {
                    startActivityIfNecessary();
                    activityStarted = true;
                }
            }
        });
    }

    NodeApi.NodeListener nodeListener = new WearableListenerService() {
        @Override
        public void onPeerConnected(Node node) {
            super.onPeerConnected(node);
            Log.v(TAG, "Wear node connected: " + node.getDisplayName());
            findNodes();
        }

        @Override
        public void onPeerDisconnected(Node node) {
            super.onPeerDisconnected(node);
            Log.v(TAG, "Wear node disconnected: " + node.getDisplayName());
            findNodes();
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        connectedToGAPI = true;
        Wearable.NodeApi.addListener(mGoogleApiClient, nodeListener);
        findNodes();
        Log.d(TAG, "onConnected: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectedToGAPI = false;
        Log.d(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }
}
