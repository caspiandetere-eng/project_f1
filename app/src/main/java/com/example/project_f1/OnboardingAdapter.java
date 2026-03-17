package com.example.project_f1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    public static class OnboardingPage {
        public final String emoji;
        public final String title;
        public final String description;

        public OnboardingPage(String emoji, String title, String description) {
            this.emoji = emoji;
            this.title = title;
            this.description = description;
        }
    }

    private final OnboardingPage[] pages;

    public OnboardingAdapter(OnboardingPage[] pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingPage page = pages[position];
        holder.tvEmoji.setText(page.emoji);
        holder.tvTitle.setText(page.title);
        holder.tvDescription.setText(page.description);

        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300);
    }

    @Override
    public int getItemCount() {
        return pages.length;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvTitle, tvDescription;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
