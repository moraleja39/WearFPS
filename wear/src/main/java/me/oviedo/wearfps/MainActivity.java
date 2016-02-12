package me.oviedo.wearfps;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private WatchViewStub mContainerView;

    private UsageView rUsageView, lUsageView;
    private TextView fpsText, cpuTempText, gpuTempText, lPercentText, rPercentText, batteryText;
    private float currentRotation = 0;
    //private TextView mTextView;
    //private TextView mClockView;

    private BroadcastReceiver mBroadcastReceiver;
    private PowerManager.WakeLock wakeLock;
    public static final String WAKELOCK_TAG = "me.oviedo.wearfps.wakelock";

    private static Timer timeoutTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKELOCK_TAG);
        wakeLock.acquire();

        mContainerView = (WatchViewStub) findViewById(R.id.container);
        mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
        //mTextView = (TextView) findViewById(R.id.text);
        //mClockView = (TextView) findViewById(R.id.clock);


        mContainerView.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Log.w("LayoutInflated", "Layout inflated called");
                setBroadcastReceiver();
                // Now you can access your views
                lUsageView = (UsageView) findViewById(R.id.lUsage);
                rUsageView = (UsageView) findViewById(R.id.rUsage);
                fpsText = (TextView) findViewById(R.id.fpsText);
                cpuTempText = (TextView) findViewById(R.id.cpuTempText);
                gpuTempText = (TextView) findViewById(R.id.gpuTempText);
                lPercentText = (TextView) findViewById(R.id.lPercentText);
                rPercentText = (TextView) findViewById(R.id.rPercentText);
                batteryText = (TextView) findViewById(R.id.batteryText);

                //fpsText.setText("0");

                setOrientationListener();

                timeoutTimer = new Timer();
                timeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setBrightness(0.0f);
                    }
                }, 5000);

                mContainerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d("TouchEvent", "Touch event caught");
                        timeoutTimer.cancel();
                        timeoutTimer.purge();
                        timeoutTimer = new Timer();
                        setBrightness(-1f);

                        timeoutTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setBrightness(0.0f);
                            }
                        }, 5000);
                        return false;
                    }
                });
            }
        });

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d("BroadcastReceiver", "Reveived intent with action " + intent.getAction());
                if (intent.getAction().equals(WearDataLayerListenerService.APP_INTENT)) {
                    final float CU = intent.getFloatExtra("CU", 0);
                    final float GU = intent.getFloatExtra("GU", 0);
                    final float FPS = intent.getFloatExtra("FPS", 0);
                    final float CT = intent.getFloatExtra("CT", 0);
                    final float GT = intent.getFloatExtra("GT", 0);

                    lPercentText.setText(String.format("%.0f%%", CU));
                    rPercentText.setText(String.format("%.0f%%", GU));
                    fpsText.setText(String.format("%.0f", FPS));
                    cpuTempText.setText(String.format("%.0fºC", CT));
                    gpuTempText.setText(String.format("%.0fºC", GT));

                    /* Nivel de la batería */
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = context.registerReceiver(null, ifilter);
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    //int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    //Log.d("BatteryStatus", "level: " + level + ", scale: " + scale);
                    //int batteryPct = 100*(level / scale);

                    batteryText.setText(level + "%");

                    /*ValueAnimator rAnimator = ValueAnimator.ofFloat(rUsageView.getPercent(), GU);
                    rAnimator.setDuration(300);
                    rAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            rUsageView.setPercent((float) animation.getAnimatedValue());
                        }
                    });
                    rAnimator.start();
                    ValueAnimator lAnimator = ValueAnimator.ofFloat(lUsageView.getPercent(), CU);
                    lAnimator.setDuration(300);
                    lAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            lUsageView.setPercent((float) animation.getAnimatedValue());
                        }
                    });
                    lAnimator.start();*/
                    rUsageView.setPercent(GU);
                    lUsageView.setPercent(CU);
                }
                if (intent.getAction().equals(WearDataLayerListenerService.FINISH_INTENT)) {
                    Log.w("Main", "Received finish intent");
                    finish();
                }
                //lUsageView.setPercent(L);
                //rUsageView.setPercent(R);
            }
        };

    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TouchEvent", "Touch event caught");
        timeoutTimer.cancel();
        setBrightness(-1f);
        timeoutTimer.schedule(dimScreenTask, 5000);
        return super.onTouchEvent(event);
    }*/

    private void setBrightness(final float level) {
        mContainerView.post(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = level;// i needed to dim the display
                getWindow().setAttributes(lp);
            }
        });
    }

    private void setAnimatedOrientationListener() {
        OrientationEventListener orientationEventListener = new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                //fpsText.setText("" + orientation);
                if (orientation > 45 && orientation <= 135) {
                    if (currentRotation != 90) {
                        mContainerView.clearAnimation();
                        if (currentRotation == 0) {
                            mContainerView.animate().rotation(-90).setDuration(400).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mContainerView.setRotation(270);
                                    animation.removeAllListeners();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else {
                            mContainerView.animate().rotation(270).setDuration(400).setInterpolator(new DecelerateInterpolator());
                        }
                        currentRotation = 90;
                    }
                } else if (orientation > 135 && orientation <= 225) {
                    if (currentRotation != 180) {
                        mContainerView.clearAnimation();
                        mContainerView.animate().rotation(180).setDuration(400).setInterpolator(new DecelerateInterpolator());
                        currentRotation = 180;
                    }
                } else if (orientation > 225 && orientation <= 315) {
                    if (currentRotation != 270) {
                        mContainerView.clearAnimation();
                        mContainerView.animate().rotation(90).setDuration(400).setInterpolator(new DecelerateInterpolator());
                        currentRotation = 270;
                    }
                } else if (orientation != -1) {
                    if (currentRotation != 0) {
                        mContainerView.clearAnimation();
                        if (currentRotation == 90) {
                            mContainerView.animate().rotation(360).setDuration(400).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mContainerView.setRotation(0);
                                    animation.removeAllListeners();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else {
                            mContainerView.animate().rotation(0).setDuration(400).setInterpolator(new DecelerateInterpolator());
                        }
                        currentRotation = 0;
                    }
                }
            }
        };
        orientationEventListener.enable();
    }

    private void setOrientationListener() {
        OrientationEventListener orientationEventListener = new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                //fpsText.setText("" + orientation);
                if (orientation > 45 && orientation <= 135) {
                    mContainerView.setRotation(270);
                } else if (orientation > 135 && orientation <= 225) {
                    mContainerView.setRotation(180);
                } else if (orientation > 225 && orientation <= 315) {
                    mContainerView.setRotation(90);
                } else if (orientation != -1) {
                    mContainerView.setRotation(0);
                }
            }
        };
        orientationEventListener.enable();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void setBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WearDataLayerListenerService.APP_INTENT);
        filter.addAction(WearDataLayerListenerService.FINISH_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        wakeLock.release();
        this.finish();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            //mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            //mTextView.setTextColor(getResources().getColor(android.R.color.white));
            //mClockView.setVisibility(View.VISIBLE);

            //mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            //mContainerView.setBackground(null);
            //mTextView.setTextColor(getResources().getColor(android.R.color.black));
            //mClockView.setVisibility(View.GONE);
        }
    }
}
