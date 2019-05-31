package com.codrox.messagetemplate.Modals;

public class Modal_Call {
    String CALL_ID;
    String CALL_NUMBER;
    String CALL_MSG;
    String CALL_WAP;
    String CALL_DATE;
    String CALL_STATUS;
    String CALL_NAME;
    String CALL_AUDIO;
    String CALL_AUDIO_STATUS;


    public Modal_Call(String CALL_ID, String CALL_NUMBER, String CALL_DATE, String CALL_NAME) {
        this.CALL_NUMBER = CALL_NUMBER;
        this.CALL_ID = CALL_ID;
        this.CALL_DATE = CALL_DATE;
        this.CALL_NAME = CALL_NAME;
    }

    public Modal_Call(String CALL_ID, String CALL_NUMBER, String CALL_MSG, String CALL_WAP, String CALL_DATE, String CALL_STATUS, String CALL_NAME, String CALL_AUDIO, String CALL_AUDIO_STATUS) {
        this.CALL_ID = CALL_ID;
        this.CALL_NUMBER = CALL_NUMBER;
        this.CALL_MSG = CALL_MSG;
        this.CALL_WAP = CALL_WAP;
        this.CALL_DATE = CALL_DATE;
        this.CALL_STATUS = CALL_STATUS;
        this.CALL_NAME = CALL_NAME;
        this.CALL_AUDIO = CALL_AUDIO;
        this.CALL_AUDIO_STATUS = CALL_AUDIO_STATUS;

    }

    public String getCALL_ID() {
        return CALL_ID;
    }

    public String getCALL_NUMBER() {
        return CALL_NUMBER;
    }

    public String getCALL_MSG() {
        return CALL_MSG;
    }

    public String getCALL_WAP() {
        return CALL_WAP;
    }

    public String getCALL_DATE() {
        return CALL_DATE;
    }

    public String getCALL_STATUS() {
        return CALL_STATUS;
    }

    public String getCALL_NAME() {
        return CALL_NAME;
    }

    public String getCALL_AUDIO() {
        return CALL_AUDIO;
    }

    public String getCALL_AUDIO_STATUS() {
        return CALL_AUDIO_STATUS;
    }


}
