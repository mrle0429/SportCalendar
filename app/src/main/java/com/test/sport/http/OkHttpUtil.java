package com.test.sport.http;


import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpUtil {

    private static final String API_KEY = "sk-proj-1TC3ibKsouIWSoJez5ayT3BlbkFJarMV73oJVjDLCsE7K71v";
    private static final String ACCESS_KEY = "ALTAKpjFQkgZDaC6wGmZyAOTeP";
    private static final String SECRET_KEY = "56050c09b69443c4abeca47759683522";
    private static Qianfan qianfan;

    static {
        qianfan = new Qianfan(ACCESS_KEY, SECRET_KEY);
    }

    public static void sendHttpRequest(String address,okhttp3.Callback callback) {
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
                        .model("ERNIE-4.0-8K")
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
