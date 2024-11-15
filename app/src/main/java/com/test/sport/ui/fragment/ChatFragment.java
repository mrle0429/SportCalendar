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
import com.test.sport.http.OkHttpUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import okhttp3.Call;
import okhttp3.Response;

public class ChatFragment extends BaseFragment<FragmentChatBinding> implements View.OnClickListener{

    private StringBuilder chatHistory = new StringBuilder();
    @Override
    protected void initData() {
        super.initData();

        chatHistory.append("AI Assistant: Hello, how can I assist you today?\n");
        updateChatView();

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

    private void sendMessage(String message){
        chatHistory.append("User: " + message + "\n");
        updateChatView();

        //发送API请求
        // 发送到API获取回复
        OkHttpUtil.sendChatRequest(message, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatFragment", "Request failed", e);
                // 获取完整的错误堆栈
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String errorDetails = sw.toString();
                
                getActivity().runOnUiThread(() -> {
                    String errorMessage = "Error: " + e.getMessage() + "\n" + errorDetails;
                    chatHistory.append("System: ").append(errorMessage).append("\n\n");
                    updateChatView();
                    showToast("Network error: " + e.getMessage());
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
                            chatHistory.append("AI: ").append(content).append("\n\n");
                            updateChatView();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> {
                            showToast("Response error");
                        });
                    }
                }
            }
        });
        
    }
    private void updateChatView() {
        // 更新聊天内容显示
        getBinding().rvChatList.setText(chatHistory.toString());
    }
}
