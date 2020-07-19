package com.abbsolute.ma_livu.Home.GuestBook;

public class CommentItem  {
    private String num;
    private String name;
    private String comment;
    private String icon;
//    private Drawable icon;
    private String date;

    public CommentItem(String num, String name, String comment, String icon, String date) {
        this.num = num;
        this.name = name;
        this.comment = comment;
        this.icon = icon;
        this.date = date;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}