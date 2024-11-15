package com.test.sport.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.shape.view.ShapeCheckBox;
import com.hjq.shape.view.ShapeTextView;
import com.test.nba.R;
import com.test.sport.db.entity.Schedule;

import java.text.ParseException;
import java.util.List;

// TODO:日程适配器
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final Context context;
    private List<Schedule> scheduleList;
    private Intent intent;

    public ScheduleAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    public void setList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Schedule schedule = scheduleList.get(position);
        holder.tvTitle.setText(schedule.getTitle());
        holder.tvTime.setText(schedule.getTime()+" "+schedule.getLocation());

        holder.ivMore.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDelete(position, view);
            }
        });

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivMore;
        private final TextView tvTime;
        private final TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivMore = itemView.findViewById(R.id.iv_more);

        }
    }

    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onDelete(int pos, View view);
        void onClick(int pos);
    }

}
