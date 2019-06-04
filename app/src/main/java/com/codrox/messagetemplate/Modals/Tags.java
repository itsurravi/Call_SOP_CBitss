package com.codrox.messagetemplate.Modals;

public class Tags {

    String TAG_ID;
    String TAG_NUMBER;
    String TAG_NAME ;

    public Tags(String TAG_ID, String TAG_NUMBER, String TAG_NAME) {
        this.TAG_ID = TAG_ID;
        this.TAG_NUMBER = TAG_NUMBER;
        this.TAG_NAME = TAG_NAME;
    }

    public String getTAG_ID() {
        return TAG_ID;
    }

    public String getTAG_NUMBER() {
        return TAG_NUMBER;
    }

    public String getTAG_NAME() {
        return TAG_NAME;
    }
}
