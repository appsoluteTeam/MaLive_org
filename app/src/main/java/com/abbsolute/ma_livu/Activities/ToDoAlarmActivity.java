package com.abbsolute.ma_livu.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abbsolute.ma_livu.R;

public class ToDoAlarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_alarm);
        getSupportActionBar().setTitle("Alarm Activity");
    }
}
