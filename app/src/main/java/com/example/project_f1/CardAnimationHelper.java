package com.example.project_f1;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public final class CardAnimationHelper {

    private static final int DURATION = 300;

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

    /** Elevate card on expand, restore on collapse. */
    public static void animateElevation(View card, boolean expanding) {
        float from = expanding ? 4f : 12f;
        float to   = expanding ? 12f : 4f;
        ValueAnimator anim = ValueAnimator.ofFloat(from, to);
        anim.setDuration(DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> card.setElevation((float) a.getAnimatedValue()
                * card.getResources().getDisplayMetrics().density));
        anim.start();
    }
}
