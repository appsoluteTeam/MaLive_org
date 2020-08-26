package com.abbsolute.ma_livu.Community;

public class bringData {

    // 리사이클러뷰 데이터

    private String documentID;
    private String title;
    private String content;
    private String date;
    private String category;

    private String img_uri;
    private String writer;
    private String likeCount;
    private String saveCount;
  

    public bringData(String documentID, String title, String category, String content, String date,String writer,String likeCount, String saveCount) {

        this.documentID = documentID;
        this.title = title;
        this.content = content;
        this.category =category;
        this.date = date;

        this.img_uri= img_uri;
        this.writer=writer;

        this.likeCount = likeCount;
        this.saveCount = saveCount;

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


    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(String saveCount) {
        this.saveCount = saveCount;

    }
}
