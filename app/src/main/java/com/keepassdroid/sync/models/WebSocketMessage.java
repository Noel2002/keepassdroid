package com.keepassdroid.sync.models;

public class WebSocketMessage {
    public String type;
    public String content;

    public  WebSocketMessage(){}
    public WebSocketMessage(String type, String content) {
        this.type = type;
        this.content = content;
    }
}
