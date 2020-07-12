package com.abbsolute.ma_livu.ToDoList;

public class ToDoFixInfo {
    String fixToDo;
    String fixPeriod;
    public ToDoFixInfo(String fixPeriod,String fixToDo){
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
