package com.keepassdroid.sync.utilities;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;

import java.math.BigInteger;
import java.util.Arrays;

public class JPAKERound2PayloadAdapter {
    public String participantId;
    public String a;
    public String[] knowledgeProofForX2s;


    public JPAKERound2PayloadAdapter(){

    }

    public JPAKERound2PayloadAdapter(JPAKERound2Payload payload){
        this.participantId = payload.getParticipantId();
        this.a = payload.getA().toString();
        this.knowledgeProofForX2s = Arrays.stream(payload.getKnowledgeProofForX2s())
                .map(e -> e.toString())
                .toArray(String[]::new);

    }

    public JPAKERound2Payload toJPAKERound2Payload(){
        return new JPAKERound2Payload(
                this.participantId,
                new BigInteger(this.a),
                Arrays.stream(this.knowledgeProofForX2s).map( e -> new BigInteger(e)).toArray(BigInteger[]::new)
        );
    }
}

