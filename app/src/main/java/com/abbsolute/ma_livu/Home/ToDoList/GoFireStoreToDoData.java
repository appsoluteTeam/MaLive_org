package com.abbsolute.ma_livu.Home.ToDoList;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class GoFireStoreToDoData {
    public List<BringToDoData> bringToDoData;
    public List<BringToDoFixData> bringToDoFixData;
    public GoFireStoreToDoData(List<BringToDoData> bringToDoData,List<BringToDoFixData> bringToDoFixData){
        this.bringToDoData=bringToDoData;
        this.bringToDoFixData=bringToDoFixData;
    }
}
