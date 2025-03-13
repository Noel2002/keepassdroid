package com.keepassdroid.sync.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;

import java.math.BigInteger;

enum JPAKESessionStage {
    INIT,
    ROUND1,
    ROUND2,
    ROUND3,
    COMPLETED,
    FINISHED
}

public class JPAKESession {
//    private final String password;

    public static String sharedPassword;
    private JPAKESessionStage stage;
    private BigInteger derivedSecret = null;
    private JPAKEParticipant self;
    private ObjectMapper objectMapper;

//    public JPAKESession(String password) {
//        this.stage = JPAKESessionStage.INIT;
//        this.password = password;
//        this.self = new JPAKEParticipant("bob", password.toCharArray());
//        this.objectMapper = new ObjectMapper();
//    }

    public JPAKESession(){
        this.stage = JPAKESessionStage.INIT;
        this.self = new JPAKEParticipant("bob", sharedPassword.toCharArray());
        this.objectMapper = new ObjectMapper();
    }

    public void startSession() {
        stage = JPAKESessionStage.ROUND1;
    }

    public JPAKESessionStage getStage(){
        return stage;
    }

    public void handleMessage(String message) throws Exception {
        if (message == null) {
            return;
        }

        switch (this.stage) {
            case ROUND1:
                JPAKERound1PayloadAdapter receivedRound1Payload = objectMapper.readValue(message, JPAKERound1PayloadAdapter.class);
                self.validateRound1PayloadReceived(receivedRound1Payload.toJPAKERound1Payload());
                stage = JPAKESessionStage.ROUND2;
                break;
            case ROUND2:
                JPAKERound2PayloadAdapter receivedRound2Payload = objectMapper.readValue(message, JPAKERound2PayloadAdapter.class);
                self.validateRound2PayloadReceived(receivedRound2Payload.toJPAKERound2Payload());
                BigInteger keyMaterial = self.calculateKeyingMaterial();
                derivedSecret = keyMaterial;
                stage = JPAKESessionStage.ROUND3;
                break;
            case ROUND3:
                if (derivedSecret == null) throw new IllegalArgumentException("Missing derived key at third state");
                JPAKERound3PayloadAdapter receivedRound3Payload = objectMapper.readValue(message, JPAKERound3PayloadAdapter.class);
                self.validateRound3PayloadReceived(receivedRound3Payload.toJPAKERound3Payload(), derivedSecret);
                stage = JPAKESessionStage.COMPLETED;
                break;

            case COMPLETED:
                // Initialize the secure session using the derived secret
                // TODO: Check what will happen if the other client fails the key confirmation
                SecureSession.initializeSession(this.derivedSecret);
                this.stage = JPAKESessionStage.FINISHED;
                break;

            default:
                break;
        }
    }

    public String getRoundPayload() throws Exception {
        switch (stage) {
            case ROUND1:
                JPAKERound1Payload round1Payload = self.createRound1PayloadToSend();
                return objectMapper.writeValueAsString(new JPAKERound1PayloadAdapter(round1Payload));

            case ROUND2:
                JPAKERound2Payload round2Payload = self.createRound2PayloadToSend();
                return objectMapper.writeValueAsString(new JPAKERound2PayloadAdapter(round2Payload));

            case ROUND3:
                if (derivedSecret == null){
                    System.err.println("Missing derived secret");
                    return "ERROR";
                }
                JPAKERound3Payload round3Payload = self.createRound3PayloadToSend(derivedSecret);
                return objectMapper.writeValueAsString(new JPAKERound3PayloadAdapter(round3Payload));

            case COMPLETED:
                System.out.println("SHARED SECRET= " + this.derivedSecret);
                return "JPAKE_COMPLETED";

            case INIT:
                return "INIT";

            default:
                return "FINISHED";
        }
    }
}

