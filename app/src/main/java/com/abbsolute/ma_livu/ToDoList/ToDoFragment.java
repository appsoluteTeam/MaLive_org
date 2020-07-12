package com.abbsolute.ma_livu.ToDoList;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    //CheckBox checkBox;
    ///


    ///
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        recyclerView = view.findViewById(R.id.todo_recyclerview);

//  fragmentTransaction.add(R.id.contentMain,FirstFragment.newInstance());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        toDoAdapter = new ToDoAdapter();

        Button fab = view.findViewById(R.id.fab);//추가
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ToDoWriteMainActivity.class);
                startActivityForResult(intent, WRITE_RESULT);
            }
        });
        toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
        toDoAdapter.setItem(toDoInfos);
        toDoAdapter.GetContext(getContext());
        toDoAdapter.notifyDataSetChanged();
        recyclerView.setItemAnimator(null);

        //밀어서 할일 삭제
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.LEFT){
                    final int position = viewHolder.getAdapterPosition();
                    ToDoAppHelper.deleteData(getContext(), "todoInfo", position, toDoInfos.get(position));
                    toDoInfos.remove(position);
                    toDoAdapter.notifyItemRemoved(position);
                }else if(direction==ItemTouchHelper.RIGHT){
                    toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
                    // toDoAdapter.clearData();
                    toDoAdapter.setItem(toDoInfos);
                    recyclerView.setAdapter(toDoAdapter);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);

        return view;
    }
    //다시 그리기
    @Override
    public void onResume() {
        super.onResume();
        toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
        // toDoAdapter.clearData();
        toDoAdapter.setItem(toDoInfos);
        recyclerView.setAdapter(toDoAdapter);
    }
    //작성하기 내용 보여주기
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_RESULT) {
            if (resultCode == RESULT_OK) {
                toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
                Comparator<ToDoInfo> cmpAsc = new Comparator<ToDoInfo>() {

                    @Override
                    public int compare(ToDoInfo o1, ToDoInfo o2) {
                        return o2.getDates().compareTo(o1.getDates()) ;
                    }
                } ;
                // toDoAdapter.clearData();
                Collections.sort(toDoInfos,cmpAsc);

                toDoAdapter.setItem(toDoInfos);
                recyclerView.setAdapter(toDoAdapter);
            }
        }
    }///onActivityResult



}
