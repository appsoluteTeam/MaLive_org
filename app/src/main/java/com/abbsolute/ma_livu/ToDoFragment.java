package com.abbsolute.ma_livu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class ToDoFragment extends Fragment {//ToDoList 추가, 삭제, 수정 클래스
    RecyclerView recyclerView;
    ToDoAdapter toDoAdapter;
    private static int WRITE_RESULT = 100;
    String res;
    Bundle bundle;
    ArrayList<ToDoInfo> toDoInfos;
    LinearLayout linearLayout;
    TextView Contents;
    private int UPDATE_OK = 5;
    //멤버변수
    int Year, Month, Day;
    int Hour, Min;
    AlarmManager alarmManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        recyclerView = view.findViewById(R.id.todo_recyclerview);
//  fragmentTransaction.add(R.id.contentMain,FirstFragment.newInstance());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        toDoAdapter = new ToDoAdapter();



        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WriteActivity.class);
                startActivityForResult(intent, WRITE_RESULT);
            }
        });
        toDoInfos = AppHelper.selectTodoInfo("todoInfo");
        toDoAdapter.setItem(toDoInfos);
        toDoAdapter.GetContext(getContext());
        toDoAdapter.notifyDataSetChanged();
        recyclerView.setItemAnimator(null);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                AppHelper.deleteData(getContext(), "todoInfo", position, toDoInfos.get(position));
                toDoInfos.remove(position);
                toDoAdapter.notifyItemRemoved(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        toDoInfos = AppHelper.selectTodoInfo("todoInfo");
        // toDoAdapter.clearData();
        toDoAdapter.setItem(toDoInfos);
        recyclerView.setAdapter(toDoAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_RESULT) {
            if (resultCode == RESULT_OK) {
                toDoInfos = AppHelper.selectTodoInfo("todoInfo");
                // toDoAdapter.clearData();
                toDoAdapter.setItem(toDoInfos);
                recyclerView.setAdapter(toDoAdapter);
            }
        }
    }///onActivityResult



}
