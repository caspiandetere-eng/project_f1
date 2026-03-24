package com.example.project_f1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.project_f1.models.Driver;
import com.example.project_f1.models.Team;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DriverSelectionAdapter extends RecyclerView.Adapter<DriverSelectionAdapter.ViewHolder> {

    public interface OnDriverSelectedListener {
        void onDriverSelected(Driver driver);
    }

    private final Context context;
    private final List<Driver> drivers;
    private String selectedDriverId;
    private final OnDriverSelectedListener listener;

    public DriverSelectionAdapter(Context context, List<Driver> drivers,
                                  String selectedDriverId, OnDriverSelectedListener listener) {
        this.context = context;
        this.drivers = drivers;
        this.selectedDriverId = selectedDriverId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_driver_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Driver driver = drivers.get(h.getAdapterPosition());
        boolean selected = driver.id.equals(selectedDriverId);

        Team team = Team.getTeamById(driver.teamId);
        int teamColor;
        try {
            teamColor = team != null ? Color.parseColor(team.color) : 0xFFE10600;
        } catch (Exception e) {
            teamColor = 0xFFE10600;
        }

        h.tvName.setText(driver.getFullName());
        if (h.tvTeam != null) {
            h.tvTeam.setText(team != null ? team.name.toUpperCase() : driver.teamId.toUpperCase());
            h.tvTeam.setTextColor(teamColor);
        }
        if (h.tvNationality != null) {
            h.tvNationality.setText(driver.nationality);
        }

        // Card styling
        float dp = context.getResources().getDisplayMetrics().density;
        h.card.setCardBackgroundColor(ThemeManager.blendColors(0xFF0D0D0D, teamColor, 0.08f));
        h.card.setStrokeColor(selected ? teamColor : Color.TRANSPARENT);
        h.card.setStrokeWidth(selected ? (int)(3 * dp) : 0);

        if (h.vIndicator != null) {
            h.vIndicator.setVisibility(selected ? View.VISIBLE : View.GONE);
            h.vIndicator.setColorFilter(teamColor);
        }

        // Image Loading with Top-Crop logic and custom horizontal alignment
        if (h.ivPhoto != null) {
            if (h.progressBar != null) h.progressBar.setVisibility(View.VISIBLE);

            Object imageSource = driver.photoResId != 0 ? driver.photoResId : driver.getPhotoUrl();

            h.ivPhoto.setScaleType(ImageView.ScaleType.MATRIX);

            Glide.with(context)
                    .load(imageSource)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (h.progressBar != null) h.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (h.progressBar != null) h.progressBar.setVisibility(View.GONE);
                            
                            h.ivPhoto.post(() -> {
                                Matrix matrix = new Matrix();
                                float viewWidth = h.ivPhoto.getWidth();
                                float viewHeight = h.ivPhoto.getHeight();
                                float drawableWidth = resource.getIntrinsicWidth();
                                float drawableHeight = resource.getIntrinsicHeight();

                                float scale = viewWidth / drawableWidth;
                                if (drawableHeight * scale < viewHeight) {
                                    scale = viewHeight / drawableHeight;
                                }
                                
                                matrix.setScale(scale, scale);
                                
                                // Horizontal adjustment for specific drivers
                                float dx = 0;
                                if (driver.id.equals("russell") || driver.id.equals("albon") || driver.id.equals("antonelli")) {
                                    // Move content a bit to the right (shifting the image right)
                                    dx = 15 * dp; 
                                }
                                
                                matrix.postTranslate(dx, 0);
                                h.ivPhoto.setImageMatrix(matrix);
                            });
                            return false;
                        }
                    })
                    .into(h.ivPhoto);
        }

        h.card.setOnClickListener(v -> {
            selectedDriverId = driver.id;
            notifyDataSetChanged();
            if (listener != null) listener.onDriverSelected(driver);
        });

        // Entrance animation
        h.card.setAlpha(0f);
        h.card.setTranslationY(12 * dp);
        h.card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(260)
                .setStartDelay(position * 30L)
                .setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                .start();
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final ImageView ivPhoto;
        final TextView tvName, tvTeam, tvNationality;
        final ProgressBar progressBar;
        final ImageView vIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            this.card = (MaterialCardView) itemView.findViewById(R.id.cardDriver);
            this.ivPhoto = itemView.findViewById(R.id.ivDriverPhoto);
            this.tvName = itemView.findViewById(R.id.tvDriverName);
            this.tvTeam = itemView.findViewById(R.id.tvTeamName);
            this.tvNationality = itemView.findViewById(R.id.tvNationality);
            this.progressBar = itemView.findViewById(R.id.progressBar);
            this.vIndicator = itemView.findViewById(R.id.vSelectionIndicator);
        }
    }
}
