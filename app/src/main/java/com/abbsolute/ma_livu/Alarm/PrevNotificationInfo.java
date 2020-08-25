package com.abbsolute.ma_livu.Alarm;

public class PrevNotificationInfo {
    int image;
    String notifiedText;
    String prevNotificationTime;
    PrevNotificationInfo(int image,String notifiedText,String prevNotificationTime){
        this.image=image;
        this.notifiedText=notifiedText;
        this.prevNotificationTime=prevNotificationTime;
    }

    public int getFriendImage() {
        return image;
    }

    public void setFriendImage(int image) {
        this.image = image;
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
