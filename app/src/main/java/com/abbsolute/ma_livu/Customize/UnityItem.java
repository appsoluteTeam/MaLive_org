package com.abbsolute.ma_livu.Customize;

import java.util.HashMap;
import java.util.Map;

public class UnityItem {
    private int id;
    private String name;
    private int type;
    private int isPossessed;

    public static final int SkinTone = 0;
    public static final int Face = 1;
    public static final int Hat = 2;
    public static final int Back = 3;
    public static final int UpperBody = 4;
    public static final int Shoes = 5;
    public static final int Etc = 6;

    public UnityItem(int id, String name, int type, int isPossessed) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isPossessed = isPossessed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getIsPossessed() {
        return isPossessed;
    }

    public void setIsPossessed(int isPossessed) {
        this.isPossessed = isPossessed;
    }


    public Map<String,Object> getItemMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("type",type);
        return map;
    }
}
