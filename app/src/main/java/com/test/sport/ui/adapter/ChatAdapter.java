package com.test.sport.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.nba.R;
import com.test.sport.db.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    
    private List<Message> messages = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        
        // 根据消息类型显示对应的布局
        if (message.getType() == Message.TYPE_USER) {
            holder.aiContainer.setVisibility(View.GONE);
            holder.userContainer.setVisibility(View.VISIBLE);
            holder.userMessageText.setText(message.getContent());
        } else {
            holder.userContainer.setVisibility(View.GONE);
            holder.aiContainer.setVisibility(View.VISIBLE);
            holder.aiMessageText.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout aiContainer;
        LinearLayout userContainer;
        TextView aiMessageText;
        TextView userMessageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            aiContainer = itemView.findViewById(R.id.ai_message_container);
            userContainer = itemView.findViewById(R.id.user_message_container);
            aiMessageText = itemView.findViewById(R.id.ai_message_text);
            userMessageText = itemView.findViewById(R.id.user_message_text);
        }
    }
}