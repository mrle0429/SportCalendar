package com.test.sport.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.nba.R;
import com.test.sport.db.entity.Game;
import com.test.sport.utils.Tools;

import java.util.ArrayList;
import java.util.List;

// TODO: 比赛适配器
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private final Context context;
    private List<Game> dataList = new ArrayList<>();

    public GameAdapter(Context context, List<Game> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void setList(List<Game> dataList) {
        this.dataList = dataList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Game game = dataList.get(position);
        if (game.getCompetitors()!=null&&game.getCompetitors().size()>0){
            holder.tvTeam1.setText(game.getCompetitors().get(0).getAbbreviation());
            holder.tvTeam2.setText(game.getCompetitors().get(1).getAbbreviation());
        }

      String status=game.getStatus();
        if (status.equals("live")||status.equals("closed")|| status.equals("ended")||
                status.equals("not_started")){
            int home = game.getHome_score();
            int away = game.getAway_score();
            if (game.getCompetitors()!=null&&game.getCompetitors().size()>0) {
                if (game.getCompetitors().get(0).getQualifier().equals("home")) {
                    holder.tvScore1.setText(home + "".trim());
                    holder.tvScore2.setText(away + "".trim());
                    if (home > away) {
                        holder.tvScore1.setTextColor(ContextCompat.getColor(context, R.color.black));
                        holder.tvScore2.setTextColor(ContextCompat.getColor(context, R.color.text_grey));
                    } else {
                        holder.tvScore1.setTextColor(ContextCompat.getColor(context, R.color.text_grey));
                        holder.tvScore2.setTextColor(ContextCompat.getColor(context, R.color.black));
                    }
                } else {
                    holder.tvScore1.setText(away + "".trim());
                    holder.tvScore2.setText(home + "".trim());
                    if (home > away) {
                        holder.tvScore1.setTextColor(ContextCompat.getColor(context, R.color.text_grey));
                        holder.tvScore2.setTextColor(ContextCompat.getColor(context, R.color.black));
                    } else {
                        holder.tvScore1.setTextColor(ContextCompat.getColor(context, R.color.black));
                        holder.tvScore2.setTextColor(ContextCompat.getColor(context, R.color.text_grey));
                    }
                }
            }
        }
        holder.tvType.setText(game.getCompetition_name());
        try {
            String time=game.getStart_time();
            holder.tvTime.setText(Tools.StringToDate(time, "yyyy-MM-dd HH:mm", " HH:mm"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvStatus.setText(game.getStatus());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivTeam1;
        private final ImageView ivTeam2;
        private final TextView tvTeam1;
        private final TextView tvTeam2;
        private final TextView tvScore1;
        private final TextView tvScore2;
        private final TextView tvTime;
        private final TextView tvType;
        private final TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivTeam1 = itemView.findViewById(R.id.iv_team1);
            ivTeam2 = itemView.findViewById(R.id.iv_team2);
            tvTeam1 = itemView.findViewById(R.id.tv_team1);
            tvTeam2 = itemView.findViewById(R.id.tv_team2);
            tvScore1 = itemView.findViewById(R.id.tv_score1);
            tvScore2 = itemView.findViewById(R.id.tv_score2);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvType=itemView.findViewById(R.id.tv_type);
        }
    }

    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick(int pos);
    }

}
