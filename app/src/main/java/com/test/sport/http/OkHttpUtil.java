package com.test.sport.http;


import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpUtil {
    public static void sendHttpRequest(String address,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .get()
                .addHeader("accept", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
