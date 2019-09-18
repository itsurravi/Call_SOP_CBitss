package com.codrox.messagetemplate.Modals;

public class FileInfo {

    String title;
    String date;
    String path;

    public FileInfo(String title, String date, String path) {
        this.title = title;
        this.date = date;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }
}
