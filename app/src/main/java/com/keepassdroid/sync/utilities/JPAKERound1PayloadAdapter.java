package com.keepassdroid.sync.utilities;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;

import java.math.BigInteger;
import java.util.Arrays;

public class JPAKERound1PayloadAdapter {
    public String participantId;
    public String gx1;
    public String gx2;
    public String[] knowledgeProofForX1;
    public String[] knowledgeProofForX2;

    public JPAKERound1PayloadAdapter(){

    }

    public JPAKERound1PayloadAdapter(JPAKERound1Payload payload){
        this.participantId = payload.getParticipantId();
        this.gx1 = payload.getGx1().toString();
        this.gx2 = payload.getGx2().toString();
        this.knowledgeProofForX1 = Arrays.stream(payload.getKnowledgeProofForX1())
                .map(e -> e.toString())
                .toArray(String[]::new);
        this.knowledgeProofForX2 = Arrays.stream(payload.getKnowledgeProofForX2())
                .map(e -> e.toString())
                .toArray(String[]::new);
    }

    public JPAKERound1Payload toJPAKERound1Payload(){
        return new JPAKERound1Payload(
                this.participantId,
                new BigInteger(this.gx1),
                new BigInteger(this.gx2),
                Arrays.stream(this.knowledgeProofForX1).map(e -> new BigInteger(e)).toArray(BigInteger[]::new),
                Arrays.stream(this.knowledgeProofForX2).map( e -> new BigInteger(e)).toArray(BigInteger[]::new)
        );
    }
}
