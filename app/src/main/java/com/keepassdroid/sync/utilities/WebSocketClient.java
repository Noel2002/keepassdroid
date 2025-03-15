package com.keepassdroid.sync.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keepassdroid.sync.models.CredentialChange;
import com.keepassdroid.sync.models.ProcessProgressManager;
import com.keepassdroid.sync.models.WebSocketMessage;
import com.keepassdroid.sync.views.TabViewModel;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


@ClientEndpoint
public class WebSocketClient {
    private Session session;
    private static JPAKESession jpakeSession;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
        WebSocketClient.jpakeSession = new JPAKESession();
        this.session = session;
        WebSocketMessage webSocketMessage = new WebSocketMessage("START", "START");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.sendMessage(objectMapper.writeValueAsString(webSocketMessage));
            TabViewModel tabViewModel = TabViewModel.getInstance();
            tabViewModel.moveToProgressTab();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e){
            System.err.println(e.getLocalizedMessage());
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received: " + message);
        try {
            this.handleMessage(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }

    public void sendMessage(String message) {
        System.out.println("sending message: " + message);
        this.session.getAsyncRemote().sendText(message);
    }

    void handleMessage(String data) throws JsonProcessingException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            WebSocketMessage webSocketMessage = objectMapper.readValue(data, WebSocketMessage.class);
            String payload;
            WebSocketMessage messageToBeSent;
            SecureSession secureSession;
            CipherTextMessage cipherMessage;
            String plaintext;

            switch (webSocketMessage.type){
                case "READY":
                    WebSocketClient.jpakeSession.startSession();
                    payload = this.jpakeSession.getRoundPayload();
                    messageToBeSent = new WebSocketMessage("JPAKE_MESSAGE", payload);
                    this.sendMessage(objectMapper.writeValueAsString(messageToBeSent));
                    break;

                case "JPAKE_MESSAGE":
                    WebSocketClient.jpakeSession.handleMessage(webSocketMessage.content);
                    payload = WebSocketClient.jpakeSession.getRoundPayload();
                    if(payload.equals("JPAKE_COMPLETED")){
                        messageToBeSent = new WebSocketMessage("JPAKE_COMPLETED", payload);
                    }
                    else{
                        messageToBeSent = new WebSocketMessage("JPAKE_MESSAGE", payload);
                    }
                    this.sendMessage(objectMapper.writeValueAsString(messageToBeSent));
                    break;

                case "JPAKE_COMPLETED":
                    System.out.println("======> Session completed");
                    WebSocketClient.jpakeSession.handleMessage(webSocketMessage.content);
                    break;

                case "ERROR":
                    System.out.println("ERROR OCCURRED");
                    break;

                case "MODIFIED_ENTRIES":
                    try{
                        secureSession = SecureSession.getInstance();

                        Merger merger = Merger.getInstance();

                        objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        cipherMessage = objectMapper.readValue(webSocketMessage.content, CipherTextMessage.class);
                        plaintext = secureSession.decrypt(cipherMessage);

                        CredentialEntry[] receivedModifiedEntries = objectMapper.readValue(plaintext, CredentialEntry[].class);

                        CipherTextMessage cipherTextMessage = secureSession.encrypt(objectMapper.writeValueAsString(merger.getModifiedEntries()));
                        messageToBeSent = new WebSocketMessage("MODIFIED_ENTRIES", objectMapper.writeValueAsString(cipherTextMessage));
                        this.sendMessage(objectMapper.writeValueAsString(messageToBeSent));
                        System.out.println("Number of received entries: " + receivedModifiedEntries.length);


                        ProcessProgressManager manager = ProcessProgressManager.getInstance();
                        manager.addProcessProgress("Received modified entries", "Successful");

                        List<CredentialChange> changes = merger.merge(Arrays.asList(receivedModifiedEntries));
                        for(CredentialChange change: changes){
                            manager.addCredentialChange(change);
                        }

                        manager.setStateText(changes.size() + " entries merged");
                    } catch(Exception e){
                        System.out.println(e.getLocalizedMessage());
                    }
                    break;

                case "ENCRYPTED_MESSAGE":
                    secureSession = SecureSession.getInstance();
                    objectMapper = new ObjectMapper();
                    cipherMessage = objectMapper.readValue(webSocketMessage.content, CipherTextMessage.class);
                    plaintext = secureSession.decrypt(cipherMessage);
                    System.out.println("----> Message: " + plaintext);
                    break;

                default:
                    throw new Error("Message type not recognized");
            }
        }catch (Exception e){
            System.out.println("Error occurred:"+ e.getStackTrace());
            System.out.println("Error Message:"+ e);
            ObjectMapper objectMapper = new ObjectMapper();
            this.sendMessage(objectMapper.writeValueAsString(new WebSocketMessage("ERROR", "ERROR")));
        }

    }

}
