package com.test.sport.model;

public class Message {
    public static final int TYPE_AI = 0;
    public static final int TYPE_USER = 1;

    private String content;
    private int type;
    private long timestamp;

    public Message(String content, int type) {
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}