package com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment;

public class CommunityCommentCommentItem {
    private String name;
    private String comment;
    //    private String icon;
    private String date;
    private String comment_like;

    public CommunityCommentCommentItem(String name, String comment, String date, String comment_like) {
        this.name = name;
        this.comment = comment;
        this.date = date;
        this.comment_like = comment_like;
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
}
