package me.oviedo.wearfps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;
    //private boolean darkMode;

    private static final String TAG = "MainFragment";

    private String sDegs, sMhz;

    /* Views*/
    private TextView cpuTempText, gpuTempText, cpuNameText, gpuNameText, cpuFreqText, gpuFreqText, gpuOfflineText;
    private LoadView cpuLoadView, gpuLoadView;
    private LinearLayout contentView;

    private BroadcastReceiver mBroadcastReceiver;

    private boolean isAmbientMode = false;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(boolean darkMode) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putBoolean("dark_mode", darkMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            darkMode = getArguments().getBoolean("dark_mode");
        }*/

        //Log.d(TAG, "onCreate called");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Log.d(TAG, "Restoring instance state...");
            cpuNameText.setText(savedInstanceState.getString("cpu"));
            gpuNameText.setText(savedInstanceState.getString("gpu"));
            isAmbientMode = savedInstanceState.getBoolean("ambient", false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cpu", cpuNameText.getText().toString());
        outState.putString("gpu", gpuNameText.getText().toString());
        outState.putBoolean("ambient", isAmbientMode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_main, container, false);
        findViews(root);
        return root;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }*/
        loadResources();
        setBroadcastReceiver();
        startBroadcastReceiver();
    }

    private void loadResources() {
        sDegs = getString(R.string.placeholder_celsius);
        sMhz = getString(R.string.placeholder_megahertz);
    }

    private void findViews(View rootView) {
        contentView = (LinearLayout) rootView.findViewById(R.id.main_content);
        cpuTempText = (TextView) rootView.findViewById(R.id.cpuTempText);
        gpuTempText = (TextView) rootView.findViewById(R.id.gpuTempText);
        gpuLoadView = (LoadView) rootView.findViewById(R.id.gpuLoadBar);
        cpuLoadView = (LoadView) rootView.findViewById(R.id.cpuLoadBar);
        cpuNameText = (TextView) rootView.findViewById(R.id.cpuNameText);
        gpuNameText = (TextView) rootView.findViewById(R.id.gpuNameText);
        cpuFreqText = (TextView) rootView.findViewById(R.id.cpuCoreText);
        gpuFreqText = (TextView) rootView.findViewById(R.id.gpuCoreText);
        gpuOfflineText = (TextView) rootView.findViewById(R.id.gpuOfflineText);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
        stopBroadcastReceiver();
    }

    public void setAmbientMode(boolean ambientMode) {
        if (ambientMode & !isAmbientMode) {
            contentView.setBackgroundColor(getResources().getColor(android.R.color.black));
            isAmbientMode = true;
        } else if (isAmbientMode) {
            contentView.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            isAmbientMode = false;
        }
    }

    private boolean isGpuOffline = false;
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
                    if (GL < 0) {
                        if (!isGpuOffline) {
                            isGpuOffline = true;
                            gpuLoadView.setVisibility(View.GONE);
                            gpuOfflineText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (isGpuOffline) {
                            gpuLoadView.setVisibility(View.VISIBLE);
                            gpuOfflineText.setVisibility(View.GONE);
                            isGpuOffline = false;
                        }
                        gpuLoadView.setPercentage(GL);
                    }
                    //fpsText.setText(String.format("%.0f", FPS));
                    cpuTempText.setText(String.format(sDegs, CT));
                    if (GT < 0 ) gpuTempText.setText("---");
                    else gpuTempText.setText(String.format(sDegs, GT));
                    cpuFreqText.setText(String.format(sMhz, CF));
                    if (GF < 0) gpuFreqText.setText("---");
                    else gpuFreqText.setText(String.format(sMhz, GF));

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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, filter);
    }

    private void stopBroadcastReceiver() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
