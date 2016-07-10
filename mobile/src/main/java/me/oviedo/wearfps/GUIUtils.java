package me.oviedo.wearfps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

@TargetApi(21)
public class GUIUtils {

    private static final String TAG = "UIUtils";

    public static void animateRevealShow(final Context ctx, final View view, final int startRadius, @ColorRes final int color, int x, int y, final MainActivity.OnRevealAnimationListener listener) {
        Log.d(TAG, "width: " + view.getWidth() + ", height: " + view.getHeight());
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight()) / 2;
        Log.d(TAG, "finalradius: " + finalRadius);
        Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, 1440);
        // TODO poner un int decente
        //anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
        anim.setDuration(350);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ContextCompat.getColor(ctx, color));
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
                if(listener != null) {
                    listener.onRevealShow();
                }
            }
        });
        anim.start();
    }

    public static void animateRevealHide(final Context ctx, final View view, final @ColorRes int color,
                                         final int finalRadius, final MainActivity.OnRevealAnimationListener listener) {
        int cx = (view.getWidth()) / 2;
        int cy = view.getHeight() / 2;
        int startRadius = view.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, finalRadius);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ContextCompat.getColor(ctx,color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(listener != null) {
                    listener.onRevealHide();
                }
                view.setVisibility(View.INVISIBLE);
            }
        });
        //anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
        anim.setDuration(1000);
        anim.start();
    }
}
