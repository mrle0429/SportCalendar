package com.test.sport.http;


import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.test.sport.utils.Constants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpUtil {



    private static final Qianfan qianfan;

    static {
        qianfan = new Qianfan(Constants.ACCESS_KEY, Constants.SECRET_KEY);
    }

    public static void sendHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .get()
                .addHeader("accept", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendChatRequest(String message, okhttp3.Callback callback) {
        // 创建新线程执行网络请求
        new Thread(() -> {
            try {
                ChatResponse response = qianfan.chatCompletion()
                        .model("ERNIE-3.5-8K")
                        .addMessage("user", Constants.SYSTEM_PROMPT)
                        .addMessage("assistant", "I understand. I'll help you with sports-related questions.")  // 第二条消息：AI确认
                        .addMessage("user", message)
                        .temperature(0.7)
                        .execute();

                // 检查响应
                if (response == null || response.getResult() == null) {
                    callback.onFailure(null, new IOException("Empty response from server"));
                    return;
                }

                // 构造OkHttp响应
                String jsonResponse = "{\"choices\":[{\"message\":{\"content\":\"" +
                        response.getResult() + "\"}}]}";

                Response okResponse = new Response.Builder()
                        .code(200)
                        .message("OK")
                        .body(ResponseBody.create(
                                MediaType.parse("application/json"),
                                jsonResponse))
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("http://localhost").build())
                        .build();

                callback.onResponse(null, okResponse);

            } catch (Exception e) {
                e.printStackTrace(); // 打印详细错误信息
                callback.onFailure(null, new IOException("Request failed: " + e.getMessage()));
            }
        }).start();
    }

}
