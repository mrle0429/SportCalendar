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
    private static final String ACCESS_KEY = "ALTAKPscAps3SmNZHj7cSUJ5Qd";
    private static final String SECRET_KEY = "d6bf44c6a7b54fb6a3ea4c2b982c9940";
    private static Qianfan qianfan;

    private static final String SYSTEM_PROMPT = 
        "You are a professional sports assistant, helping users understand game information, " +
        "rules explanations, and match analysis. Please respond in English with a concise and " +
        "friendly tone. Key response guidelines:\n" +
        "1. Provide accurate and clear explanations of game rules\n" +
        "2. Maintain objectivity in match analysis\n" +
        "3. Support opinions with historical data when relevant\n" +
        "4. Focus on game analysis and avoid sensitive topics like gambling\n" +
        "5. Clearly indicate when information is uncertain\n" +
        "6. Keep responses focused on sports-related topics\n" +
        "7. Use appropriate sports terminology\n" +
        "8. Provide context when discussing specific matches or players";

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
                        .model("ERNIE-3.5-8K")
                        .addMessage("user", SYSTEM_PROMPT)
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
