package com.example.shashank.firebase_1.data;

public class FilesSharedUrl {

    String filename;
    String url;

    public FilesSharedUrl() {
        this.filename = "";
        this.url = "";
    }

    public String getFilename() {
        return filename;
    }

    public FilesSharedUrl(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }

    public void setFilename(String key) {
        this.filename = key;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }
}
