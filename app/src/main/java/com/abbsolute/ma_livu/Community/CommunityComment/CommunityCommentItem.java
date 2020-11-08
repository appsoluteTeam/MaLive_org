package com.abbsolute.ma_livu.Community.CommunityComment;

import android.text.BoringLayout;

public class CommunityCommentItem {
    private String name;
    private String email;
    private String comment;
//    private String icon;
    private String date;
    private String num;
    private String comment_like;
    private String comment_count;
//    private Boolean comment_like_check;

    public CommunityCommentItem(String name, String email, String comment, String date, String comment_like, String comment_count) {
        this.name = name;
        this.email = email;
        this.comment = comment;
        this.date = date;
        this.comment_like = comment_like;
        this.comment_count = comment_count;
//        this.comment_like_check = comment_like_check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public String getIcon() {
//        return icon;
//    }
//
//    public void setIcon(String icon) {
//        this.icon = icon;
//    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment_like() {
        return comment_like;
    }

    public void setComment_like(String comment_like) {
        this.comment_like = comment_like;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

//    public Boolean getComment_like_check() {
//        return comment_like_check;
//    }
//
//    public void setComment_like_check(Boolean comment_like_check) {
//        this.comment_like_check = comment_like_check;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
