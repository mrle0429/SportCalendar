package com.test.sport.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.nba.R;
import com.test.sport.model.Team;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private List<Team> teams;
    private OnTeamFavoriteClickListener listener;

    public interface OnTeamFavoriteClickListener {
        void onFavoriteClick(Team team, boolean newState);
    }

    public TeamAdapter(List<Team> teams, OnTeamFavoriteClickListener listener) {
        this.teams = teams;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.tvTeamName.setText(team.getName());
        updateFavoriteState(holder.ivFavorite, team.isFavorite());

        holder.ivFavorite.setOnClickListener(v -> {
            boolean newState = !team.isFavorite();
            team.setFavorite(newState);
            updateFavoriteState(holder.ivFavorite, newState);
            if (listener != null) {
                listener.onFavoriteClick(team, newState);
            }
        });
    }

    private void updateFavoriteState(ImageView imageView, boolean isFavorite) {
        imageView.setImageResource(isFavorite ?
                android.R.drawable.star_big_on : android.R.drawable.star_big_off);
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void updateData(List<Team> newTeams) {
        this.teams = newTeams;
        notifyDataSetChanged();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTeamName;
        ImageView ivFavorite;

        TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeamName = itemView.findViewById(R.id.tv_team_name);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
        }
    }
} 