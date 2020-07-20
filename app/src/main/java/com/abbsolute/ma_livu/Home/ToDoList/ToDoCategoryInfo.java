package com.abbsolute.ma_livu.Home.ToDoList;

public class ToDoCategoryInfo {
    public int toDoImage;
    public String toDoText;
    public ToDoCategoryInfo(int toDoImage, String toDoText){
        this.toDoImage=toDoImage;
        this.toDoText=toDoText;
    }

    public int getToDoImage() {
        return toDoImage;
    }

    public void setToDoImage(int toDoImage) {
        this.toDoImage = toDoImage;
    }

    public String getToDoText() {
        return toDoText;
    }

    public void setToDoText(String toDoText) {
        this.toDoText = toDoText;
    }
}
