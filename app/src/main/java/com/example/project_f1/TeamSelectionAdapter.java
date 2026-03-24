package com.example.project_f1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_f1.models.Team;
import java.util.List;

public class TeamSelectionAdapter extends RecyclerView.Adapter<TeamSelectionAdapter.TeamViewHolder> {
    private List<Team> teams;
    private String selectedTeamId;
    private OnTeamSelectedListener listener;
    private Context context;

    public interface OnTeamSelectedListener {
        void onTeamSelected(Team team);
    }

    public TeamSelectionAdapter(Context context, List<Team> teams, String selectedTeamId, OnTeamSelectedListener listener) {
        this.context = context;
        this.teams = teams;
        this.selectedTeamId = selectedTeamId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_card, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.bind(team, team.id.equals(selectedTeamId));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void setSelectedTeam(String teamId) {
        this.selectedTeamId = teamId;
        notifyDataSetChanged();
    }

    class TeamViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivTeamLogo;
        ImageView vSelectionIndicator;
        TextView tvTeamName;

        TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardTeam);
            ivTeamLogo = itemView.findViewById(R.id.ivTeamLogo);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            vSelectionIndicator = itemView.findViewById(R.id.vSelectionIndicator);
        }

        void bind(Team team, boolean isSelected) {
            tvTeamName.setText(team.name);
            ivTeamLogo.setImageResource(team.logoResId != 0 ? team.logoResId : R.drawable.ic_launcher_foreground);

            try {
                int teamColor = Color.parseColor(team.color);
                int r = Color.red(teamColor);
                int g = Color.green(teamColor);
                int b = Color.blue(teamColor);

                int blendedR = (10 + Math.min(r + 20, 255)) / 2;
                int blendedG = (10 + Math.min(g + 20, 255)) / 2;
                int blendedB = (10 + Math.min(b + 20, 255)) / 2;
                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                Color.argb(255, 10, 10, 10),
                                Color.argb(255, blendedR, blendedG, blendedB)
                        });
                gradient.setCornerRadius(context.getResources().getDisplayMetrics().density * 16);
                cardView.setBackgroundDrawable(gradient);
                cardView.setCardBackgroundColor(Color.TRANSPARENT);

                // Stroke: bright team color when selected, subtle when not
                if (isSelected) {
                    cardView.setStrokeWidth((int) (context.getResources().getDisplayMetrics().density * 3));
                    cardView.setStrokeColor(Color.WHITE);
                } else {
                    cardView.setStrokeWidth((int) (context.getResources().getDisplayMetrics().density * 1));
                    cardView.setStrokeColor(Color.argb(80, r, g, b));
                }
            } catch (Exception e) {
                cardView.setCardBackgroundColor(0xFF1A1A1A);
            }

            vSelectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            cardView.setOnClickListener(v -> {
                selectedTeamId = team.id;
                notifyDataSetChanged();
                if (listener != null) listener.onTeamSelected(team);
            });
        }
    }
}
