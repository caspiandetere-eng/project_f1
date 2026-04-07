package com.example.project_f1;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public final class CardAnimationHelper {

    private static final int DURATION = 300;
    // Spring config for elevation bounce on card tap
    private static final SpringConfig ELEVATION_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(60, 7);

    private CardAnimationHelper() {}

    /** Expand a view from 0 → measured height with alpha fade-in. */
    public static void expand(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(DURATION);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addUpdateListener(a -> {
            view.getLayoutParams().height = (int) a.getAnimatedValue();
            view.requestLayout();
            view.setAlpha((float) a.getAnimatedFraction());
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                view.requestLayout();
            }
        });
        animator.start();
    }

    /** Collapse a view from current height → 0 with alpha fade-out. */
    public static void collapse(View view) {
        int initialHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(DURATION);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addUpdateListener(a -> {
            view.getLayoutParams().height = (int) a.getAnimatedValue();
            view.requestLayout();
            view.setAlpha(1f - a.getAnimatedFraction());
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
                view.setAlpha(1f);
                view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        animator.start();
    }

    /** Rotate an arrow icon: 0→180 on expand, 180→0 on collapse. */
    public static void rotateArrow(View arrow, boolean expanding) {
        float from = expanding ? 0f : 180f;
        float to   = expanding ? 180f : 0f;
        arrow.animate()
                .rotation(to)
                .setDuration(DURATION)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    /** Elevate card on expand, restore on collapse — driven by a Rebound spring for a natural bounce. */
    public static void animateElevation(View card, boolean expanding) {
        float density = card.getResources().getDisplayMetrics().density;
        float targetDp = expanding ? 12f : 4f;

        SpringSystem system = SpringSystem.create();
        Spring spring = system.createSpring().setSpringConfig(ELEVATION_CONFIG);
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring s) {
                card.setElevation((float) s.getCurrentValue() * density);
            }
        });
        spring.setCurrentValue(expanding ? 4.0 : 12.0);
        spring.setEndValue(targetDp);
    }
}
