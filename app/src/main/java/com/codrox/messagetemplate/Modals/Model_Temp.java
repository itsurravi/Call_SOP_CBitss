package com.codrox.messagetemplate.Modals;

public class Model_Temp {

    public String TEMP_ID;
    public String TEMP_MSG;
    public String TEMP_TITLE;
    public String TEMP_CAT;

    Boolean isSelected=false;

    public Model_Temp(String TEMP_ID, String TEMP_MSG, String TEMP_TITLE, String TEMP_CAT) {
        this.TEMP_ID = TEMP_ID;
        this.TEMP_MSG = TEMP_MSG;
        this.TEMP_TITLE = TEMP_TITLE;
        this.TEMP_CAT = TEMP_CAT;
    }

    public String getTEMP_ID() {
        return TEMP_ID;
    }

    public String getTEMP_MSG() {
        return TEMP_MSG;
    }

    public String getTEMP_TITLE() {
        return TEMP_TITLE;
    }

    public String getTEMP_CAT() {
        return TEMP_CAT;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean selected) {
        isSelected = selected;
    }
}
