package com.abbsolute.ma_livu.Community;

public class bringData {

    // 리사이클러뷰 데이터

    private String documentID;
    private String title;
    private String content;
    private String date;
    private String category;
  

    public bringData(String documentID, String title, String category, String content,String date) {
        this.documentID = documentID;
        this.title = title;
        this.content = content;
        this.category =category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
