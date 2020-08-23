package com.abbsolute.ma_livu.Alarm;

public class PrevNotificationInfo {
    int friendImage;
    String notifiedText;
    String prevNotificationTime;
    PrevNotificationInfo(int friendImage,String notifiedText,String prevNotificationTime){
        this.friendImage=friendImage;
        this.notifiedText=notifiedText;
        this.prevNotificationTime=prevNotificationTime;
    }

    public int getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(int friendImage) {
        this.friendImage = friendImage;
    }

    public String getNotifiedText() {
        return notifiedText;
    }

    public void setNotifiedText(String notifiedText) {
        this.notifiedText = notifiedText;
    }

    public String getPrevNotificationTime() {
        return prevNotificationTime;
    }

    public void setPrevNotificationTime(String prevNotificationTime) {
        this.prevNotificationTime = prevNotificationTime;
    }
}
