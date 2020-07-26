package com.abbsolute.ma_livu.Community;

public class bringData {

    // 리사이클러뷰 데이터

    private String documentID;
    private String title;
    private String content;
    private String writer;
    private String date;

    public bringData(String documentID, String title, String writer, String content, String date) {
        this.documentID = documentID;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
