package com.keepassdroid.sync.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepassdroid.sync.models.WebSocketMessage;

public class MessageController {
    WebSocketClient client;
    JPAKESession jpakeSession;
    SecureSession secureSession;

    public MessageController(){

    }
    void handleMessage(String data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketMessage webSocketMessage = objectMapper.readValue(data, WebSocketMessage.class);

        switch (webSocketMessage.type){
            case "READY":
                break;

            case "JPAKE_MESSAGE":
                break;

            case "JPAKE_COMPLETED":
                break;

            case "ERROR":
                break;

            default:
                throw new Error("Message type not recognized");
        }
    }
}
