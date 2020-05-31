package com.abbsolute.ma_livu;

public class bringData {

    private String documentID;
    private String title;
    private String content;
    private String writer;

    public bringData(){

    }

    public bringData(String documentID, String title, String writer, String content) {
        this.documentID = documentID;
        this.title = title;
        this.content = content;
        this.writer = writer;
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


}
