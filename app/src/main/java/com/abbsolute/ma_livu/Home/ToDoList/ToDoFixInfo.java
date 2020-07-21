package com.abbsolute.ma_livu.Home.ToDoList;

public class ToDoFixInfo {//고정 리스트 정보 클래스
    public String fixToDo;
    public String fixPeriod;

    public ToDoFixInfo(String fixToDo,String fixPeriod){
        this.fixPeriod=fixPeriod;
        this.fixToDo=fixToDo;
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
