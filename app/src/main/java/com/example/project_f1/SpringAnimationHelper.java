package com.example.project_f1;

import android.view.MotionEvent;
import android.view.View;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public final class SpringAnimationHelper {

    // Snappy press feel: high tension, good friction
    private static final SpringConfig PRESS_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 6);
    // Bouncy card pop: lower tension for a visible overshoot
    private static final SpringConfig BOUNCE_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(200, 10);

    private SpringAnimationHelper() {}

    /**
     * Attaches a spring press-scale effect to a view.
     * On finger-down the view shrinks to [scaleDown]; on release it bounces back to 1.0.
     */
    public static void attachPressSpring(View view, double scaleDown) {
        SpringSystem system = SpringSystem.create();
        Spring spring = system.createSpring().setSpringConfig(PRESS_CONFIG);

        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring s) {
                float value = (float) s.getCurrentValue();
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });

        spring.setCurrentValue(1.0);

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    spring.setEndValue(scaleDown);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    spring.setEndValue(1.0);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    break;
            }
            return true;
        });
    }

    /** Convenience overload with default scale-down of 0.93. */
    public static void attachPressSpring(View view) {
        attachPressSpring(view, 0.93);
    }

    /**
     * Triggers a one-shot bounce pop on a view (scale 0 → 1 with overshoot).
     * Useful for entrance animations on cards/items.
     */
    public static void popIn(View view) {
        SpringSystem system = SpringSystem.create();
        Spring spring = system.createSpring().setSpringConfig(BOUNCE_CONFIG);

        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring s) {
                float value = (float) s.getCurrentValue();
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });

        spring.setCurrentValue(0.0);
        spring.setEndValue(1.0);
    }
}
