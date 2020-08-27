package com.abbsolute.ma_livu.Home.ToDoList;

public class ToDoFixInfo {//고정 리스트 정보 클래스
    public String fixToDo;
    public String fixPeriod;
    public String num;
    public ToDoFixInfo(String fixToDo,String fixPeriod,String num){
        this.fixPeriod=fixPeriod;
        this.fixToDo=fixToDo;
        this.num=num;
    }
    public String getFixToDo() {
        return fixToDo;
    }

    public void setFixToDo(String fixToDo) {
        this.fixToDo = fixToDo;
    }

    public String getFixPeriod() {
        return fixPeriod;
    }

    public void setFixPeriod(String fixPeriod) {
        this.fixPeriod = fixPeriod;
    }
}
