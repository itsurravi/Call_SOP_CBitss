package com.codrox.messagetemplate.Modals;

public class Remarks {
    public String id;
    public String remarks;
    String  number;
    String remarks_date;


    public Remarks(String id, String remarks, String number, String remarks_date) {
        this.id = id;
        this.remarks = remarks;
        this.number = number;
        this.remarks_date = remarks_date;

    }

    public String getId() {
        return id;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getNumber() {
        return number;
    }

    public String getRemarks_date() {
        return remarks_date;
    }


}
