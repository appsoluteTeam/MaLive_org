package com.abbsolute.ma_livu.MyPage;

public class postItemListView {
    private String post_category;
    private String post_title;
    private String post_content;
    private String post_date;
    private String post_writer;

    public postItemListView(String post_category,String post_title, String post_content, String post_date, String post_writer){
        this.post_category = post_category;
        this.post_title = post_title;
        this.post_content = post_content;
        this.post_date = post_date;
        this.post_writer = post_writer;
    }


    public String getPost_category() {
        return post_category;
    }

    public void setPost_category(String post_category) {
        this.post_category = post_category;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_writer() {
        return post_writer;
    }

    public void setPost_writer(String post_writer) {
        this.post_writer = post_writer;
    }
}
