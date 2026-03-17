package com.example.project_f1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class CoachMarkOverlay extends FrameLayout {

    public static class CoachMark {
        public final View target;
        public final String title;
        public final String description;

        public CoachMark(View target, String title, String description) {
            this.target = target;
            this.title = title;
            this.description = description;
        }
    }

    private final Paint overlayPaint = new Paint();
    private final Paint clearPaint = new Paint();
    private final RectF spotlightRect = new RectF();
    private CoachMark[] marks;
    private int currentIndex = 0;
    private final LinearLayout tooltip;
    private Runnable onDoneListener;

    public CoachMarkOverlay(Context context) {
        super(context);
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        overlayPaint.setColor(0xCC000000);
        overlayPaint.setStyle(Paint.Style.FILL);

        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setStyle(Paint.Style.FILL);

        tooltip = new LinearLayout(context);
        tooltip.setOrientation(LinearLayout.VERTICAL);
        tooltip.setBackgroundResource(R.drawable.bg_glass_card);
        tooltip.setPadding(dp(16), dp(14), dp(16), dp(14));

        FrameLayout.LayoutParams tooltipParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tooltip.setLayoutParams(tooltipParams);
        addView(tooltip);

        setOnClickListener(v -> advance());
    }

    public void show(Activity activity, CoachMark[] marks, Runnable onDone) {
        this.marks = marks;
        this.currentIndex = 0;
        this.onDoneListener = onDone;

        ViewGroup root = activity.findViewById(android.R.id.content);
        root.addView(this, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setAlpha(0f);
        animate().alpha(1f).setDuration(300);
        showMark(0);
    }

    private void showMark(int index) {
        CoachMark mark = marks[index];
        int[] loc = new int[2];
        mark.target.getLocationInWindow(loc);

        // Offset by overlay's own position in window
        int[] overlayLoc = new int[2];
        getLocationInWindow(overlayLoc);
        int left = loc[0] - overlayLoc[0];
        int top = loc[1] - overlayLoc[1];

        int pad = dp(10);
        spotlightRect.set(
            left - pad,
            top - pad,
            left + mark.target.getWidth() + pad,
            top + mark.target.getHeight() + pad
        );
        invalidate();
        updateTooltip(mark, index);
    }

    private void updateTooltip(CoachMark mark, int index) {
        tooltip.removeAllViews();

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(mark.title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(15);
        tvTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.barlow_condensed),
            android.graphics.Typeface.BOLD);
        tooltip.addView(tvTitle);

        TextView tvDesc = new TextView(getContext());
        tvDesc.setText(mark.description);
        tvDesc.setTextColor(0xFF999999);
        tvDesc.setTextSize(13);
        tvDesc.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter));
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        descParams.setMargins(0, dp(6), 0, 0);
        tvDesc.setLayoutParams(descParams);
        tooltip.addView(tvDesc);

        String btnLabel = (index == marks.length - 1) ? "Got it ✓" : "Next →  (" + (index + 1) + "/" + marks.length + ")";
        TextView tvBtn = new TextView(getContext());
        tvBtn.setText(btnLabel);
        tvBtn.setTextColor(0xFFE10600);
        tvBtn.setTextSize(13);
        tvBtn.setTypeface(ResourcesCompat.getFont(getContext(), R.font.barlow_condensed),
            android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, dp(10), 0, 0);
        tvBtn.setLayoutParams(btnParams);
        tooltip.addView(tvBtn);

        // Position tooltip dynamically after layout
        tooltip.post(() -> positionTooltip());

        tooltip.setAlpha(0f);
        tooltip.animate().alpha(1f).setDuration(200);
    }

    private void positionTooltip() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        int tooltipW = tooltip.getWidth();
        int tooltipH = tooltip.getHeight();
        int margin = dp(16);
        int gap = dp(12);

        // Clamp tooltip width to screen width minus margins
        int maxW = screenWidth - margin * 2;
        if (tooltipW > maxW) tooltipW = maxW;

        // Horizontal: center over spotlight, clamped to screen
        float spotCenterX = spotlightRect.centerX();
        int left = (int) (spotCenterX - tooltipW / 2f);
        left = Math.max(margin, Math.min(left, screenWidth - tooltipW - margin));

        // Vertical: prefer below spotlight, fall back to above if not enough room
        int topBelow = (int) (spotlightRect.bottom + gap);
        int topAbove = (int) (spotlightRect.top - tooltipH - gap);
        int top;
        if (topBelow + tooltipH <= screenHeight - margin) {
            top = topBelow;
        } else if (topAbove >= margin) {
            top = topAbove;
        } else {
            // Not enough room either side — place at bottom of screen
            top = screenHeight - tooltipH - margin;
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tooltip.getLayoutParams();
        params.leftMargin = left;
        params.topMargin = top;
        params.width = maxW;
        params.gravity = 0; // clear gravity so margins take effect
        tooltip.setLayoutParams(params);
    }

    private void advance() {
        currentIndex++;
        if (currentIndex < marks.length) {
            showMark(currentIndex);
        } else {
            animate().alpha(0f).setDuration(200).withEndAction(() -> {
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null) parent.removeView(this);
                if (onDoneListener != null) onDoneListener.run();
            });
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
        if (!spotlightRect.isEmpty()) {
            canvas.drawRoundRect(spotlightRect, dp(12), dp(12), clearPaint);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
