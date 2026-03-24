package com.example.project_f1;

import android.graphics.Color;
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

public class ExpandableCardAdapter extends RecyclerView.Adapter<ExpandableCardAdapter.CardViewHolder> {

    private final List<ExpandableCardItem> items;

    public ExpandableCardAdapter(List<ExpandableCardItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expandable_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder h, int position) {
        ExpandableCardItem item = items.get(position);
        boolean expanded = (position == StateManager.get().getExpandedCardPosition());

        h.title.setText(item.title);
        h.preview.setText(item.description);
        h.fullDesc.setText(item.description);
        h.image.setImageResource(item.imageRes);
        int accent = Color.parseColor(item.accentHex);
        h.accentBar.setBackgroundColor(accent);
        h.label.setTextColor(accent);
        h.arrow.setColorFilter(accent);

        // Glass card background tinted with accent
        h.card.setCardBackgroundColor(ThemeManager.blendColors(0xFF0D0D0D, accent, 0.07f));
        h.card.setStrokeColor(ThemeManager.blendColors(0xFF222222, accent, 0.25f));
        h.card.setStrokeWidth(1);

        // Snap state without animation on bind (RecyclerView recycle)
        h.expandableContent.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.divider.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.divider.setAlpha(expanded ? 1f : 0f);
        h.arrow.setRotation(expanded ? 180f : 0f);
        h.preview.setMaxLines(expanded ? Integer.MAX_VALUE : 2);

        float density = h.itemView.getResources().getDisplayMetrics().density;
        h.card.setCardElevation(expanded ? 12 * density : 4 * density);

        // Entrance animation: stagger fade + slide up
        h.itemView.setAlpha(0f);
        h.itemView.setTranslationY(h.itemView.getResources().getDisplayMetrics().density * 20);
        h.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(position * 60L)
                .setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                .start();

        h.card.setOnClickListener(v -> {
            int clickedPos = h.getAdapterPosition();
            if (clickedPos == RecyclerView.NO_POSITION) return;

            int previousExpanded = StateManager.get().getExpandedCardPosition();

            if (clickedPos == previousExpanded) {
                StateManager.get().resetExpandedCard();
                animateCollapse(h);
            } else {
                StateManager.get().setExpandedCard(clickedPos);
                animateExpand(h);
            }

            if (previousExpanded != StateManager.NO_POSITION && previousExpanded != clickedPos) {
                notifyItemChanged(previousExpanded);
            }
        });
    }

    private void animateExpand(CardViewHolder h) {
        h.preview.setMaxLines(Integer.MAX_VALUE);
        CardAnimationHelper.expand(h.expandableContent);
        CardAnimationHelper.rotateArrow(h.arrow, true);
        CardAnimationHelper.animateElevation(h.card, true);

        h.divider.setVisibility(View.VISIBLE);
        h.divider.setAlpha(0f);
        h.divider.animate().alpha(1f).setDuration(300).start();
    }

    private void animateCollapse(CardViewHolder h) {
        h.preview.setMaxLines(2);
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

    static class CardViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final ImageView image;
        final View accentBar;
        final TextView title;
        final TextView label;
        final TextView preview;
        final ImageView arrow;
        final View divider;
        final LinearLayout expandableContent;
        final TextView fullDesc;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            image = itemView.findViewById(R.id.cardImage);
            accentBar = itemView.findViewById(R.id.cardAccentBar);
            title = itemView.findViewById(R.id.cardTitle);
            label = itemView.findViewById(R.id.cardLabel);
            preview = itemView.findViewById(R.id.cardPreview);
            arrow = itemView.findViewById(R.id.cardArrow);
            divider = itemView.findViewById(R.id.cardDivider);
            expandableContent = itemView.findViewById(R.id.cardExpandableContent);
            fullDesc = itemView.findViewById(R.id.cardFullDesc);
        }
    }
}
