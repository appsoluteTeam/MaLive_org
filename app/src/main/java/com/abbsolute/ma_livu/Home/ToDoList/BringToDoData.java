package com.abbsolute.ma_livu.Home.ToDoList;

public class BringToDoData {
    public String content;
    public String detailContent;
    public String dates;
    public String dDay;
    public int color;
    public BringToDoData(String content,String detailContent,String dates,String dDay,int color){
        this.content=content;
        this.detailContent=detailContent;
        this.dates=dates;
        this.dDay=dDay;
        this.color=color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getdDay() {
        return dDay;
    }

    public void setdDay(String dDay) {
        this.dDay = dDay;
    }



    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
