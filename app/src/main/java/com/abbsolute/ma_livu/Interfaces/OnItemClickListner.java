package com.abbsolute.ma_livu.Interfaces;

import android.view.View;

import com.abbsolute.ma_livu.ToDoAdapter;

public  interface OnItemClickListner {
    public void onItemClick(ToDoAdapter.ViewHolder holder, View view, int position);
}
