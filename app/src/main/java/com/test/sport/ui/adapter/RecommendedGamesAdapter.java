package com.test.sport.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Collections;
import java.util.HashSet;
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
        List<String> reasons = new ArrayList<>();

        if (game.getCompetitors() != null && game.getCompetitors().size() > 0) {
            String homeTeam = game.getCompetitors().get(0).getCompetitors_name();
            String awayTeam = game.getCompetitors().get(1).getCompetitors_name();

            Log.d("RecommendDebug1", "检查球队是否在关注列表中:");
            Log.d("RecommendDebug1", "喜欢的球队： " + favoriteTeams);
            Log.d("RecommendDebug1", "主队: " + homeTeam + " 是否关注: " + favoriteTeams.contains(homeTeam));
            Log.d("RecommendDebug1", "客队: " + awayTeam + " 是否关注: " + favoriteTeams.contains(awayTeam));
            
            if (favoriteTeams.contains(homeTeam) || favoriteTeams.contains(awayTeam)) {
                reasons.add("Followed Team");
            }
        }
        
        // 判断是否为重要联赛
        String competition = game.getCompetition_name();
        if (competition != null) {

        
        // 热门赛事
        if (Tools.isPopularFootballEvent(competition) || Tools.isPopularBasketballEvent(competition)||Tools.isPopularHockeyEvent(competition)||Tools.isPopularTennisEvent(competition)) {
            reasons.add("Popular Event");
        }
        


    }
        
        // 检查是否在偏好时间段
    try {
        int hour = Integer.parseInt(game.getStart_time().substring(11, 13));
        SharedPreferences prefs = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        Set<String> preferredTimes = prefs.getStringSet("preferred_times", new HashSet<>());
        
        for (String timeRange : preferredTimes) {
            if (timeRange.contains("Morning") && hour >= 6 && hour < 12 ||
                timeRange.contains("Afternoon") && hour >= 12 && hour < 18 ||
                timeRange.contains("Evening") && hour >= 18 && hour < 24 ||
                timeRange.contains("Dawn") && hour >= 0 && hour < 6) {
                reasons.add("Preferred Time");
                break;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // 如果没有任何推荐原因,返回默认文本
    if (reasons.isEmpty()) {
        return "Recommended Event";
    }
    
    // 将所有原因用 "·" 连接
        return (String.join(" · ", reasons));
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

    public void updateFavoriteTeams(Set<String> newFavoriteTeams) {
        Log.d("RecommendDebug", "更新收藏球队列表");
        Log.d("RecommendDebug", "原收藏球队: " + this.favoriteTeams);
        Log.d("RecommendDebug", "新收藏球队: " + newFavoriteTeams);
        
        this.favoriteTeams = newFavoriteTeams;
        notifyDataSetChanged();
    }
}
