package com.abbsolute.ma_livu.Home.ToDoList;

public class ToDoList_Info {
    private String todo_date;
    private String detail_date;
    private String todo_category;
    private String todo_datail;
    private Boolean check;

    public ToDoList_Info(String todo_date,String detail_date,String todo_category,String todo_datail,Boolean check){
        this.todo_date = todo_date;
        this.detail_date = detail_date;
        this.todo_category = todo_category;
        this.todo_datail = todo_datail;
        this.check = check;
    }

//    public ToDoList_Info(String todo_category, String todo_datail, String todo_date, String detail_date,Boolean check){
//        this.todo_category = todo_category;
//        this.todo_datail = todo_datail;
//        this.todo_date = todo_date;
//        this.detail_date = detail_date;
//        this.check = check;
//    }

    public String getTodo_date() {
        return todo_date;
    }

    public void setTodo_date(String todo_date) {
        this.todo_date = todo_date;
    }

    public String getDetail_date() {
        return detail_date;
    }

    public void setDetail_date(String detail_date) {
        this.detail_date = detail_date;
    }

    public String getTodo_category() {
        return todo_category;
    }

    public void setTodo_category(String todo_category) {
        this.todo_category = todo_category;
    }

    public String getTodo_datail() {
        return todo_datail;
    }

    public void setTodo_datail(String todo_datail) {
        this.todo_datail = todo_datail;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }
}
