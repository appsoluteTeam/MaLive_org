package com.abbsolute.ma_livu.ToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ToDoInfo {

    String content;
    String detailContent;
    String dates;
    String dDay;
    Context context;
    int color;
    public ToDoInfo(){

    }
    public ToDoInfo(String content, String detailContent, String dates, String dDay,int color){
        this.content=content;
        this.detailContent=detailContent;
        this.dates=dates;
        this.dDay=dDay;
        this.color=color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getdDay() {
        return dDay;
    }

    public void setdDay(String dDay) {
        this.dDay = dDay;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailContent(){return detailContent;}
    public void setDetailContent(String detailContent){
        this.detailContent=detailContent;
    }

}
