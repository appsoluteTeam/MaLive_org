package com.abbsolute.ma_livu.MyPage;

import java.util.ArrayList;
import java.util.List;

//잠금여부 커스텀클래스, 파이어베이스 저장할때 한번에 저장하기 위해서 사용

public class isLocked {
    private int id;
    private ArrayList<Boolean> TODO;
    private ArrayList<Boolean> attendance;
    private ArrayList<Boolean> room;
    private ArrayList<Boolean> today;
    private int category;
    private int index;

    public isLocked(){}

    public isLocked(int id, ArrayList<Boolean> TODO, ArrayList<Boolean> attendance, ArrayList<Boolean> room, ArrayList<Boolean> today, int category, int index){
        this.id = id;
        this.TODO = TODO;
        this.attendance = attendance;
        this.room = room;
        this.today = today;
        this.category = category;
        this.index = index;
    }
    public int getId() {
        return id;
    }

    public ArrayList<Boolean> getTODO() {
        return TODO;
    }

    public ArrayList<Boolean> getAttendance() {
        return attendance;
    }

    public ArrayList<Boolean> getRoom() {
        return room;
    }

    public ArrayList<Boolean> getToday() {
        return today;
    }

    public int getCategory() {
        return category;
    }

    public int getIndex() {
        return index;
    }
}
