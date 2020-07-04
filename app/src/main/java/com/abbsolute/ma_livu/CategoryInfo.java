package com.abbsolute.ma_livu;

public class CategoryInfo {
    int toDoImage;
    String toDoText;
    public CategoryInfo(int toDoImage, String toDoText){
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
