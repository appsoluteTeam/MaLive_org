package com.abbsolute.ma_livu.Alarm;

public class AlarmRequestInfo {
    int userImage;
    String requestMessage;
    String prevTime;
    AlarmRequestInfo(int userImage,String requestMessage,String prevTime){
        this.userImage=userImage;
        this.requestMessage=requestMessage;
        this.prevTime=prevTime;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getPrevTime() {
        return prevTime;
    }

    public void setPrevTime(String prevTime) {
        this.prevTime = prevTime;
    }
}
