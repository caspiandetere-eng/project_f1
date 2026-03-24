package com.example.project_f1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class BasicsCardAdapter extends RecyclerView.Adapter<BasicsCardAdapter.ViewHolder> {

    private final List<BasicsCardItem> items;
    private final int themeAccent;

    public BasicsCardAdapter(Context context, List<BasicsCardItem> items) {
        this.items = items;
        this.themeAccent = ThemeManager.getAccentColor(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_basics_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BasicsCardItem item = items.get(position);
        boolean expanded = position == StateManager.get().getExpandedCardPosition();

        // Accent colour
        int accent = Color.parseColor(item.accentHex);
        h.accentBar.setBackgroundColor(accent);
        h.arrow.setColorFilter(accent);
        h.divider.setBackgroundColor(Color.argb(32, Color.red(accent), Color.green(accent), Color.blue(accent)));

        // Glass card background tinted with accent
        android.graphics.drawable.GradientDrawable cardBg = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                new int[]{
                        ThemeManager.blendColors(0xFF0D0D0D, accent, 0.07f),
                        ThemeManager.blendColors(0xFF111111, accent, 0.04f)
                });
        cardBg.setCornerRadius(h.itemView.getResources().getDisplayMetrics().density * 24);
        h.card.setCardBackgroundColor(ThemeManager.blendColors(0xFF0D0D0D, accent, 0.07f));
        h.card.setStrokeColor(ThemeManager.blendColors(0xFF222222, accent, 0.25f));
        h.card.setStrokeWidth(1);

        boolean isDetailed = StateManager.get().isDetailed();

        // Knowledge badge
        if (isDetailed) {
            h.levelBadge.setText("DETAILED");
            h.levelBadge.setTextColor(Color.parseColor("#FFD700"));
        } else {
            h.levelBadge.setText("BEGINNER FRIENDLY");
            h.levelBadge.setTextColor(themeAccent);
        }

        // Title & preview always show the general text (short)
        h.title.setText(item.title);
        h.preview.setText(item.generalText);

        // Full description: detailed for advanced users, general for rookies
        h.fullDesc.setText(isDetailed ? item.detailedText : item.generalText);

        // Image: only visible when expanded
        if (item.imageRes != 0) {
            h.image.setImageResource(item.imageRes);
        }

        // Snap state (no animation on bind — avoids flicker on scroll)
        h.image.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.image.setAlpha(expanded ? 1f : 0f);
        h.expandableContent.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.divider.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.divider.setAlpha(expanded ? 1f : 0f);
        h.arrow.setRotation(expanded ? 180f : 0f);
        h.preview.setMaxLines(expanded ? Integer.MAX_VALUE : 2);

        float density = h.itemView.getResources().getDisplayMetrics().density;
        h.card.setCardElevation(expanded ? 12 * density : 4 * density);

        // Staggered entrance: fade + slide up
        h.itemView.setAlpha(0f);
        h.itemView.setTranslationY(h.itemView.getResources().getDisplayMetrics().density * 20);
        h.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(position * 55L)
                .setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                .start();

        // Video button
        h.videoBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl));
                intent.setPackage("com.google.android.youtube");
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                // YouTube not installed — open in browser
                v.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl)));
            }
        });

        // Card tap
        h.card.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            int prev = StateManager.get().getExpandedCardPosition();

            if (pos == prev) {
                StateManager.get().resetExpandedCard();
                animateCollapse(h);
            } else {
                StateManager.get().setExpandedCard(pos);
                animateExpand(h, item);
            }

            if (prev != StateManager.NO_POSITION && prev != pos) {
                notifyItemChanged(prev);
            }
        });
    }

    private void animateExpand(ViewHolder h, BasicsCardItem item) {
        h.preview.setMaxLines(Integer.MAX_VALUE);

        // Reveal image first
        if (item.imageRes != 0) {
            h.image.setVisibility(View.VISIBLE);
            h.image.setAlpha(0f);
            h.image.animate().alpha(1f).setDuration(350)
                    .setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                    .start();
        }

        CardAnimationHelper.expand(h.expandableContent);
        CardAnimationHelper.rotateArrow(h.arrow, true);
        CardAnimationHelper.animateElevation(h.card, true);

        h.divider.setVisibility(View.VISIBLE);
        h.divider.setAlpha(0f);
        h.divider.animate().alpha(1f).setDuration(300).start();
    }

    private void animateCollapse(ViewHolder h) {
        h.preview.setMaxLines(2);

        // Hide image
        h.image.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> h.image.setVisibility(View.GONE))
                .start();

        CardAnimationHelper.collapse(h.expandableContent);
        CardAnimationHelper.rotateArrow(h.arrow, false);
        CardAnimationHelper.animateElevation(h.card, false);

        h.divider.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> h.divider.setVisibility(View.GONE))
                .start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final ImageView image;
        final View accentBar;
        final TextView levelBadge;
        final TextView title;
        final TextView preview;
        final ImageView arrow;
        final View divider;
        final LinearLayout expandableContent;
        final TextView fullDesc;
        final LinearLayout videoBtn;

        ViewHolder(@NonNull View v) {
            super(v);
            card = (MaterialCardView) v;
            image = v.findViewById(R.id.basicsCardImage);
            accentBar = v.findViewById(R.id.basicsAccentBar);
            levelBadge = v.findViewById(R.id.basicsLevelBadge);
            title = v.findViewById(R.id.basicsCardTitle);
            preview = v.findViewById(R.id.basicsCardPreview);
            arrow = v.findViewById(R.id.basicsCardArrow);
            divider = v.findViewById(R.id.basicsCardDivider);
            expandableContent = v.findViewById(R.id.basicsExpandableContent);
            fullDesc = v.findViewById(R.id.basicsCardFullDesc);
            videoBtn = v.findViewById(R.id.basicsVideoBtn);
        }
    }
}
