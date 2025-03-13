package com.keepassdroid.sync.utilities;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;

import java.math.BigInteger;

public class JPAKERound3PayloadAdapter {
    public String participantId;
    public String macTag;


    public JPAKERound3PayloadAdapter(){

    }

    public JPAKERound3PayloadAdapter(JPAKERound3Payload payload){
        this.participantId = payload.getParticipantId();
        this.macTag = payload.getMacTag().toString();
    }

    public JPAKERound3Payload toJPAKERound3Payload(){
        return new JPAKERound3Payload(
                this.participantId,
                new BigInteger(this.macTag)
        );
    }
}

