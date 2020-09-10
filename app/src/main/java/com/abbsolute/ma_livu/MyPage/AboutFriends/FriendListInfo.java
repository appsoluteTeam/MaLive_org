package com.abbsolute.ma_livu.MyPage.AboutFriends;

public class FriendListInfo {
    int friendIcon;
    String name;
    String today;
    public FriendListInfo(int friendIcon,String name,String today){
        this.friendIcon=friendIcon;
        this.name=name;
        this.today=today;
    }
    public int getFriendIcon() {
        return friendIcon;
    }

    public void setFriendIcon(int friendIcon) {
        this.friendIcon = friendIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }
}
