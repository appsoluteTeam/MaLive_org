package com.appsolute.org.todolist;

import android.view.View;

public  interface OnItemClickListner {
    public void onItemClick(ToDoAdapter.ViewHolder holder, View view, int position);
}
