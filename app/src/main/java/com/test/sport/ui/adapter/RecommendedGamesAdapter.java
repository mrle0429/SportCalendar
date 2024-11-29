package com.test.sport.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.nba.R;
import com.test.sport.db.entity.Game;
import com.test.sport.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecommendedGamesAdapter extends RecyclerView.Adapter<RecommendedGamesAdapter.ViewHolder> {
    private final Context context;
    private List<Game> dataList = new ArrayList<>();
    private Set<String> favoriteTeams;
    private OnClickListener listener;

    public RecommendedGamesAdapter(Context context, List<Game> games, Set<String> favoriteTeams) {
        this.context = context;
        this.dataList = games;
        this.favoriteTeams = favoriteTeams;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommended_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = dataList.get(position);
        Log.d("RecommendDebug", "绑定推荐比赛视图 position: " + position);
        
        // 设置球队名称
        if (game.getCompetitors() != null && game.getCompetitors().size() > 0) {
            if (game.getCompetitors().get(0).getQualifier().equals("home")) {
                holder.tvHomeTeam.setText(game.getCompetitors().get(0).getAbbreviation());
                holder.tvAwayTeam.setText(game.getCompetitors().get(1).getAbbreviation());
            } else {
                holder.tvHomeTeam.setText(game.getCompetitors().get(1).getAbbreviation());
                holder.tvAwayTeam.setText(game.getCompetitors().get(0).getAbbreviation());
            }
        }
        Log.d("RecommendDebug", "设置球队名称: " + game.getCompetitors().get(0).getAbbreviation() + " vs " + game.getCompetitors().get(1).getAbbreviation());
        
        // 设置比赛时间
        try {
            String time = game.getStart_time();
            holder.tvTime.setText(Tools.StringToDate(time, "yyyy-MM-dd HH:mm", " HH:mm"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 设置联赛名称
        holder.tvLeague.setText(game.getCompetition_name());

        // 设置推荐原因
        String reason = getRecommendReason(game);
        holder.tvRecommendReason.setText(reason);
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });
    }

    private String getRecommendReason(Game game) {
        if (game.getCompetitors() != null && game.getCompetitors().size() > 0) {
            String homeTeam = game.getCompetitors().get(0).getAbbreviation();
            String awayTeam = game.getCompetitors().get(1).getAbbreviation();
            
            if (favoriteTeams.contains(homeTeam) || favoriteTeams.contains(awayTeam)) {
                return "关注球队";
            }
        }
        
        // 判断是否为重要联赛
        String competition = game.getCompetition_name();
        if (competition != null && (
            competition.contains("NBA") || 
            competition.contains("Premier League") || 
            competition.contains("Champions League"))) {
            return "热门赛事";
        }
        
        return "推荐赛事";
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void setGames(List<Game> games) {
        this.dataList = games;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick(int pos);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeTeam, tvAwayTeam, tvTime, tvLeague, tvRecommendReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeTeam = itemView.findViewById(R.id.tv_home_team);
            tvAwayTeam = itemView.findViewById(R.id.tv_away_team);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLeague = itemView.findViewById(R.id.tv_league);
            tvRecommendReason = itemView.findViewById(R.id.tv_recommend_reason);
        }
    }
}
