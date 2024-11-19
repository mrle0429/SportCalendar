package com.test.sport.ui.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.test.nba.R;
import com.test.nba.databinding.FragmentChatBinding;
import com.test.sport.base.BaseFragment;
import com.test.sport.db.entity.Message;
import com.test.sport.http.OkHttpUtil;
import com.test.sport.ui.adapter.ChatAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ChatFragment extends BaseFragment<FragmentChatBinding> implements View.OnClickListener{
    private ChatAdapter chatAdapter;
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void initData() {
        super.initData();

        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        getBinding().rvChatList.setLayoutManager(layoutManager);
        getBinding().rvChatList.setAdapter(chatAdapter);

        addMessage("Hello, I'm your assistant. How can I help you?", Message.TYPE_AI);
    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().btnSend.setOnClickListener(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_chat;
    }




    @Override
    protected FragmentChatBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentChatBinding.inflate(inflater,parent,false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send){
            String message = getBinding().etMessage.getText().toString();
            
            sendMessage(message);
            getBinding().etMessage.setText("");
        }

    }

    private void sendMessage(String message) {
        if (message.trim().isEmpty()) return;
        
        // 添加用户消息
        addMessage(message, Message.TYPE_USER);

        // 发送API请求
        OkHttpUtil.sendChatRequest(message, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    addMessage("Network error: " + e.getMessage(), Message.TYPE_AI);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String content = jsonObject.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        getActivity().runOnUiThread(() -> {
                            addMessage(content, Message.TYPE_AI);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            addMessage("Error processing response", Message.TYPE_AI);
                        });
                    }
                }
            }
        });
    }

    


    private void addMessage(String content, int type) {
        Message message = new Message(content, type);
        chatAdapter.addMessage(message);
        scrollToBottom();
    }

    private void scrollToBottom() {
        getBinding().rvChatList.post(() -> {
            getBinding().rvChatList.scrollToPosition(chatAdapter.getItemCount() - 1);
        });
    }
}
