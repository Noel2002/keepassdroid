package com.keepassdroid.sync.utilities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class CipherTextMessage {
    private String cipherText;
    private String iv;
    private String mac;

    public CipherTextMessage(){} // default constructor
    public CipherTextMessage(String cipherText, String iv, String mac) {
        this.cipherText = cipherText;
        this.iv = iv;
        this.mac = mac;
    }

    @JsonGetter("ciphertext")
    public String getCipherText() {
        return cipherText;
    }

    @JsonGetter("iv")
    public String getIV() {
        return iv;
    }

    @JsonSetter("mac")
    public String getMac() {
        return mac;
    }

    @JsonSetter("ciphertext")
    public void setCipherText(String cipherText){
        this.cipherText = cipherText;
    }

    @JsonSetter("iv")
    public void setIv(String iv){
        this.iv = iv;
    }

    @JsonSetter("mac")
    public void setMac(String mac){
        this.mac = mac;
    }
}
