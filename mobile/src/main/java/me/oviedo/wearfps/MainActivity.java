package me.oviedo.wearfps;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    //private FrameLayout fragmentHolder;

    private static final String TAG = "MainActivity";

    private MenuItem disconnectMenuItem;

    private Toolbar mToolbar;

    private LinearLayout mContentView;
    private View mCircle;

    private String sDegs, sMhz;

    /* Views*/
    private TextView cpuTempText, gpuTempText, cpuNameText, gpuNameText, cpuFreqText, gpuFreqText, gpuOfflineText;
    private LoadView cpuLoadView, gpuLoadView;
    private List<View> contentViews = new ArrayList<>();

    private BroadcastReceiver mBroadcastReceiver;

    //private static final String MAIN_FRAGMENT_TAG = "MainFragment";
    //private static final String WELCOME_FRAGMENT_TAG = "WelcomeFragment";
    //private MainFragment mainFragment = null;

    //private boolean isSavedInstance = false;

    private boolean shouldFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (getIntent().getBooleanExtra("shouldBeTranslucent", false)) {
            Log.e(TAG, "Translucent");
            getIntent().putExtra("shouldBeTranslucent", false);
            setTheme(R.style.AppTheme_Main_Translucent);
            super.setTheme(R.style.AppTheme_Main_Translucent);
        } else {
            setTheme(R.style.AppTheme_NoActionBar);
            super.setTheme(R.style.AppTheme_NoActionBar);
        }*/

        //getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.translucent)));

        super.onCreate(savedInstanceState);

        /*if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }*/

        setContentView(R.layout.activity_main);
        findMyViews();

        setSupportActionBar(mToolbar);

        Log.i(TAG, "sysuivisivility:" + coordinatorLayout.getSystemUiVisibility());

        if (Build.VERSION.SDK_INT >= 21) {
            //mContentView.setVisibility(View.INVISIBLE);
            //fab.setVisibility(View.INVISIBLE);
            getWindow().setTransitionBackgroundFadeDuration(2500);
            setupEnterAnimation();
            if (savedInstanceState == null) mCircle.setVisibility(View.VISIBLE);
        }

        /*if (savedInstanceState != null && savedInstanceState.getBoolean("fullscreen", false)) {
            //setTheme(R.style.AppTheme_NoActionBar);
            ActionBar ab = getSupportActionBar();
            if (ab != null) ab.hide();
        }*/

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        if (savedInstanceState == null) {
            mVisible = true;
            //fab.setVisibility(View.GONE);
        }/* else {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
            welcomeFragment = (WelcomeFragment) getSupportFragmentManager().findFragmentByTag(WELCOME_FRAGMENT_TAG);
            isSavedInstance = true;

            if (welcomeFragment!= null && ipadd != null) {
                welcomeFragment.setReady();
                fab.setVisibility(View.VISIBLE);
            }
        }*/

        if (savedInstanceState != null) {
            cpuNameText.setText(savedInstanceState.getString("cpu"));
            gpuNameText.setText(savedInstanceState.getString("gpu"));
            if (savedInstanceState.getBoolean("fullscreen", false)) {
                Log.d("MainActivity", "Saved instance: fullscreen");
                //getSupportActionBar().hide();
                hide();
                //getSupportActionBar().show();
            } else {
                Log.d("MainActivity", "Saved instance: normal screen");
            }
        }

        loadResources();
        setBroadcastReceiver();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BackgroundService.running) {
                    hide();

                }
                else {
                    //requestRemoteIp();
                    Log.w(TAG, "Fullscreen button presed, but BackgroundService is not running");
                }
            }
        });
    }

    private void findMyViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //mContentView = findViewById(R.id.main_activity_content);
        //fragmentHolder = (FrameLayout) findViewById(R.id.fragmentHolder);
        mContentView = (LinearLayout) findViewById(R.id.main_content);
        for (int i = 0; i < mContentView.getChildCount(); i++) {
            contentViews.add(mContentView.getChildAt(i));
        }
        mCircle = findViewById(R.id.middle_cirlce);

        cpuTempText = (TextView) findViewById(R.id.cpuTempText);
        gpuTempText = (TextView) findViewById(R.id.gpuTempText);
        gpuLoadView = (LoadView) findViewById(R.id.gpuLoadBar);
        cpuLoadView = (LoadView) findViewById(R.id.cpuLoadBar);
        cpuNameText = (TextView) findViewById(R.id.cpuNameText);
        gpuNameText = (TextView) findViewById(R.id.gpuNameText);
        cpuFreqText = (TextView) findViewById(R.id.cpuCoreText);
        gpuFreqText = (TextView) findViewById(R.id.gpuCoreText);
        gpuOfflineText = (TextView) findViewById(R.id.gpuOfflineText);
    }

    private void loadResources() {
        sDegs = getString(R.string.placeholder_celsius);
        sMhz = getString(R.string.placeholder_megahertz);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("fullscreen", !mVisible);
        outState.putString("cpu", cpuNameText.getText().toString());
        outState.putString("gpu", gpuNameText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (BackgroundService.running) moveTaskToBack(true);
        else super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startBroadcastReceiver();
        //if (!mVisible) show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBroadcastReceiver();
        if (shouldFinish) finish();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "Destroying...");
        UpdateChecker.reset();
    }*/

    /*@Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.e("MainActivity", "onAttachFragment");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        disconnectMenuItem = menu.findItem(R.id.action_stop_server);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem disc = menu.findItem(R.id.action_stop_server);
        if (BackgroundService.running) {
            disc.setVisible(true);
        } else {
            disc.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);

        } else if (id == R.id.action_stop_server) {
            // Stop BackgroundServer
            Intent intent = new Intent(this, BackgroundService.class);
            intent.setAction(BackgroundService.FINISH_SELF_INTENT);
            startService(intent);

            // This may not be necessary
            disconnectMenuItem.setVisible(false);

            // Launch the start activity
            final Intent i = new Intent(this, LaunchActivity.class);
            //if (Build.VERSION.SDK_INT < 21) {
            if (true) {
                startActivity(i);
                overridePendingTransition(0, R.anim.nice_fade_out);
                //Finally, close the activity
                finish();

            } else {
                GUIUtils.animateRevealHide(this, mContentView, R.color.lightGrey, mCircle.getWidth() / 2,
                    new OnRevealAnimationListener() {
                        @Override
                        public void onRevealHide() {
                            /*ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, Pair.create(mCircle, "fab_transition"));
                            //options.update(ActivityOptionsCompat.makeSceneTransitionAnimation(this));
                            ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());
                            shouldFinish = true;*/
                            finish();
                        }

                        @Override
                        public void onRevealShow() {

                        }
                    });
            }

        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isGpuOffline = false;
    private void setBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d("BroadcastReceiver", "Reveived intent with action " + intent.getAction());
                if (intent.getAction().equals(BackgroundService.MOBILE_DATA_INTENT)) {

                    final WearFpsProto.DataInt dataInt = (WearFpsProto.DataInt) intent.getSerializableExtra("proto");

                    cpuLoadView.setPercentage(dataInt.getCpuLoad());
                    if (dataInt.getGpuLoad() < 0) {
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
                        gpuLoadView.setPercentage(dataInt.getGpuLoad());
                    }
                    //fpsText.setText(String.format("%.0f", FPS));
                    cpuTempText.setText(String.format(sDegs, dataInt.getCpuTemp()));
                    if (dataInt.getGpuTemp() < 0 ) gpuTempText.setText("---");
                    else gpuTempText.setText(String.format(sDegs, dataInt.getGpuTemp()));
                    cpuFreqText.setText(String.format(sMhz, dataInt.getCpuFreq()));
                    if (dataInt.getGpuFreq() < 0) gpuFreqText.setText("---");
                    else gpuFreqText.setText(String.format(sMhz, dataInt.getGpuFreq()));

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
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void stopBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }


    @TargetApi(21)
    private void setupEnterAnimation() {
        Transition shared = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_arcmotion);
        shared.setDuration(300);
        getWindow().setSharedElementEnterTransition(shared);

        //getWindow().setEnterTransition(null);

        Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        //Transition fade = new Fade();
        fade.setDuration(300);
        getWindow().setEnterTransition(fade);

        shared.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

                //getWindow().translucent

                mContentView.setVisibility(View.INVISIBLE);
                for (View v : contentViews) {
                    v.setVisibility(View.INVISIBLE);
                }
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Handler h = new Handler();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        mCircle.setVisibility(View.GONE);
                    }
                });
                transition.removeListener(this);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                animation.setDuration(350);
                for (View v : contentViews) {
                    v.startAnimation(animation);
                    v.setVisibility(View.VISIBLE);
                }
                animateRevealShow(mContentView);

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    @TargetApi(21)
    private void animateRevealShow(final View viewRoot) {
        //int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cx = (viewRoot.getWidth()) / 2;
        //int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int cy = (viewRoot.getHeight()) / 2;
        GUIUtils.animateRevealShow(this, viewRoot, mCircle.getWidth() / 2, R.color.lightGrey, cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    @TargetApi(21)
    private void initViews() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
         @Override
         public void run() {
             fab.show();
             mCircle.setVisibility(View.GONE);
             //mContentView.startAnimation(animation);
             //mIvClose.startAnimation(animation);
             //mContentView.setVisibility(View.VISIBLE);
             //mIvClose.setVisibility(View.VISIBLE);
         }
     }
        );
    }

    public interface OnRevealAnimationListener {
        void onRevealHide();
        void onRevealShow();
    }


    /* Fullscreen */
    //private final Handler mHideHandler = new Handler();
    //private static final int UI_ANIMATION_DELAY = 300;
    private boolean mVisible = true;
    private static final int fullscreenDecorFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    //private static final int fullscreenDecorFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/ | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private void hide() {
        coordinatorLayout.setOnTouchListener(mClickListener);

        mVisible = false;
        //coordinatorLayout.setFitsSystemWindows(false);
        //mContentView.setFitsSystemWindows(false);
        coordinatorLayout.setSystemUiVisibility(fullscreenDecorFlags);
        getSupportActionBar().hide();
        //getWindow().getDecorView().setSystemUiVisibility(fullscreenDecorFlags);
        fab.setVisibility(View.GONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mainFragment.setAmbientMode(true);
    }

    private void show() {
        // Show the system bar

        //coordinatorLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        ActionBar ab = getSupportActionBar();
        if (ab != null && !ab.isShowing()) {
            ab.show();
        }

        coordinatorLayout.setSystemUiVisibility(0);
        //coordinatorLayout.requestLayout();


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //this.onto
        fab.setVisibility(View.VISIBLE);
        //mainFragment.setAmbientMode(false);

        coordinatorLayout.setOnTouchListener(null);
        //coordinatorLayout.setFitsSystemWindows(true);
    }

    private final View.OnTouchListener mClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            Log.d(TAG, "click");
            show();
            return false;
        }
    };

}

