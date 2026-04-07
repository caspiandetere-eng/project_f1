package com.example.project_f1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
        public final View   target;
        public final String title;
        public final String description;

        /** Spotlight step — highlights the target view. */
        public CoachMark(View target, String title, String description) {
            this.target      = target;
            this.title       = title;
            this.description = description;
        }

        /** Legacy full-screen constructor kept for call-site compatibility — treated as no-op spotlight. */
        public CoachMark(String emoji, String title, String description) {
            this.target      = null;
            this.title       = title;
            this.description = description;
        }
    }

    // ── Paint ─────────────────────────────────────────────────────────────────
    private final Paint overlayPaint = new Paint();
    private final Paint clearPaint   = new Paint();
    private final Paint arrowPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF spotlightRect = new RectF();
    private boolean arrowPointsDown   = true; // tooltip is above spotlight

    // ── State ─────────────────────────────────────────────────────────────────
    private CoachMark[] marks;
    private int         currentIndex = 0;
    private Runnable    onDoneListener;

    // ── Tooltip ───────────────────────────────────────────────────────────────
    private final LinearLayout tooltip;

    public CoachMarkOverlay(Context context) {
        super(context);
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        overlayPaint.setColor(0xCC000000); // slightly lighter so target is visible
        overlayPaint.setStyle(Paint.Style.FILL);

        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setStyle(Paint.Style.FILL);

        arrowPaint.setColor(0xFF1E1E1E);
        arrowPaint.setStyle(Paint.Style.FILL);

        tooltip = new LinearLayout(context);
        tooltip.setOrientation(LinearLayout.VERTICAL);
        tooltip.setPadding(dp(16), dp(14), dp(16), dp(14));

        // Dark card background with rounded corners
        android.graphics.drawable.GradientDrawable tooltipBg = new android.graphics.drawable.GradientDrawable();
        tooltipBg.setColor(0xFF1E1E1E);
        tooltipBg.setCornerRadius(dp(14));
        tooltipBg.setStroke(dp(1), 0xFF333333);
        tooltip.setBackground(tooltipBg);

        tooltip.setElevation(dp(8));
        addView(tooltip);

        setOnClickListener(v -> advance());
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void show(Activity activity, CoachMark[] marks, Runnable onDone) {
        // Filter out full-screen (null-target) steps
        java.util.List<CoachMark> filtered = new java.util.ArrayList<>();
        for (CoachMark m : marks) {
            if (m.target != null) filtered.add(m);
        }
        this.marks           = filtered.toArray(new CoachMark[0]);
        this.currentIndex    = 0;
        this.onDoneListener  = onDone;

        if (this.marks.length == 0) {
            if (onDone != null) onDone.run();
            return;
        }

        ViewGroup root = activity.findViewById(android.R.id.content);
        root.addView(this, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        setAlpha(0f);
        animate().alpha(1f).setDuration(250);
        showMark(0);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void showMark(int index) {
        CoachMark mark = marks[index];

        int[] loc        = new int[2];
        int[] overlayLoc = new int[2];
        mark.target.getLocationInWindow(loc);
        getLocationInWindow(overlayLoc);

        int left = loc[0] - overlayLoc[0];
        int top  = loc[1] - overlayLoc[1];
        int pad  = dp(10);

        spotlightRect.set(
                left  - pad,
                top   - pad,
                left  + mark.target.getWidth()  + pad,
                top   + mark.target.getHeight() + pad);

        invalidate();
        buildTooltip(mark, index);
    }

    private void buildTooltip(CoachMark mark, int index) {
        tooltip.removeAllViews();

        // Title
        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(mark.title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.titillium_web),
                android.graphics.Typeface.BOLD);
        tooltip.addView(tvTitle);

        // Description
        TextView tvDesc = new TextView(getContext());
        tvDesc.setText(mark.description);
        tvDesc.setTextColor(0xFF999999);
        tvDesc.setTextSize(12);
        tvDesc.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter));
        tvDesc.setLineSpacing(0, 1.4f);
        LinearLayout.LayoutParams descP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descP.setMargins(0, dp(5), 0, 0);
        tvDesc.setLayoutParams(descP);
        tooltip.addView(tvDesc);

        // Nav row: dots + Next/Done
        LinearLayout navRow = new LinearLayout(getContext());
        navRow.setOrientation(LinearLayout.HORIZONTAL);
        navRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams nrp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nrp.setMargins(0, dp(12), 0, 0);
        navRow.setLayoutParams(nrp);

        for (int i = 0; i < marks.length; i++) {
            View dot = new View(getContext());
            int size = (i == index) ? dp(7) : dp(5);
            LinearLayout.LayoutParams dotp = new LinearLayout.LayoutParams(size, size);
            dotp.setMargins(0, 0, dp(4), 0);
            dot.setLayoutParams(dotp);
            android.graphics.drawable.GradientDrawable dotBg = new android.graphics.drawable.GradientDrawable();
            dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            dotBg.setColor(i == index ? 0xFFE10600 : 0xFF444444);
            dot.setBackground(dotBg);
            navRow.addView(dot);
        }

        View spacer = new View(getContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        navRow.addView(spacer);

        boolean isLast = (index == marks.length - 1);
        TextView tvBtn = new TextView(getContext());
        tvBtn.setText(isLast ? "Done ✓" : "Next →");
        tvBtn.setTextColor(0xFFE10600);
        tvBtn.setTextSize(12);
        tvBtn.setTypeface(ResourcesCompat.getFont(getContext(), R.font.barlow_condensed),
                android.graphics.Typeface.BOLD);
        navRow.addView(tvBtn);
        tooltip.addView(navRow);

        tooltip.post(this::positionTooltip);
        tooltip.setAlpha(0f);
        tooltip.animate().alpha(1f).setDuration(180);
    }

    private void positionTooltip() {
        int screenW  = getWidth();
        int screenH  = getHeight();
        int tooltipH = tooltip.getMeasuredHeight();
        int margin   = dp(16);
        int gap      = dp(14);
        int maxW     = Math.min(dp(280), screenW - margin * 2);

        // Re-measure at capped width
        tooltip.measure(
                MeasureSpec.makeMeasureSpec(maxW, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0,    MeasureSpec.UNSPECIFIED));
        tooltipH = tooltip.getMeasuredHeight();

        // Horizontal: centre on spotlight, clamp to screen
        float spotCX = spotlightRect.centerX();
        int left = (int) (spotCX - maxW / 2f);
        left = Math.max(margin, Math.min(left, screenW - maxW - margin));

        // Vertical: prefer below, fall back to above
        int topBelow = (int) (spotlightRect.bottom + gap);
        int topAbove = (int) (spotlightRect.top - tooltipH - gap);

        int top;
        if (topBelow + tooltipH <= screenH - margin) {
            top            = topBelow;
            arrowPointsDown = false; // arrow on top of tooltip pointing up toward spotlight
        } else if (topAbove >= margin) {
            top            = topAbove;
            arrowPointsDown = true;  // arrow on bottom of tooltip pointing down toward spotlight
        } else {
            top            = screenH - tooltipH - margin;
            arrowPointsDown = false;
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tooltip.getLayoutParams();
        params.width      = maxW;
        params.leftMargin = left;
        params.topMargin  = top;
        params.gravity    = 0;
        tooltip.setLayoutParams(params);

        invalidate(); // redraw arrow
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    @Override
    protected void onDraw(Canvas canvas) {
        // 1. Dark overlay
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        // 2. Punch out spotlight
        if (!spotlightRect.isEmpty()) {
            canvas.drawRoundRect(spotlightRect, dp(12), dp(12), clearPaint);
        }

        // 3. Arrow triangle connecting tooltip to spotlight
        drawArrow(canvas);
    }

    private void drawArrow(Canvas canvas) {
        if (spotlightRect.isEmpty() || tooltip.getWidth() == 0) return;

        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) tooltip.getLayoutParams();
        float tooltipLeft = p.leftMargin;
        float tooltipTop  = p.topMargin;
        float tooltipW    = tooltip.getWidth();
        float tooltipH    = tooltip.getHeight();

        float arrowW = dp(12);
        float arrowH = dp(8);
        float cx     = Math.max(tooltipLeft + dp(20),
                       Math.min(spotlightRect.centerX(), tooltipLeft + tooltipW - dp(20)));

        Path path = new Path();
        if (arrowPointsDown) {
            // Arrow at bottom of tooltip pointing down toward spotlight
            float baseY = tooltipTop + tooltipH;
            path.moveTo(cx - arrowW / 2f, baseY);
            path.lineTo(cx + arrowW / 2f, baseY);
            path.lineTo(cx, baseY + arrowH);
        } else {
            // Arrow at top of tooltip pointing up toward spotlight
            float baseY = tooltipTop;
            path.moveTo(cx - arrowW / 2f, baseY);
            path.lineTo(cx + arrowW / 2f, baseY);
            path.lineTo(cx, baseY - arrowH);
        }
        path.close();
        canvas.drawPath(path, arrowPaint);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void advance() {
        currentIndex++;
        if (currentIndex < marks.length) {
            tooltip.animate().alpha(0f).setDuration(120)
                    .withEndAction(() -> showMark(currentIndex));
        } else {
            animate().alpha(0f).setDuration(200).withEndAction(() -> {
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null) parent.removeView(this);
                if (onDoneListener != null) onDoneListener.run();
            });
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
