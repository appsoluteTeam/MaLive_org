package com.abbsolute.ma_livu.Community;

public class bringData {
    private String img1;


    // 리사이클러뷰 데이터

    private String documentID;
    private String title;
    private String content;
    private String date;
    private String category;

    private String writer;
    private String likeCount;
    private String saveCount;
  

    public bringData(String documentID, String title, String category, String content, String date,String writer,String likeCount, String saveCount,String img1) {

        this.documentID = documentID;
        this.title = title;
        this.content = content;
        this.category =category;
        this.date = date;
        this.writer=writer;
        this.likeCount = likeCount;
        this.saveCount = saveCount;
        this.img1 = img1;

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


    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

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

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }
}
