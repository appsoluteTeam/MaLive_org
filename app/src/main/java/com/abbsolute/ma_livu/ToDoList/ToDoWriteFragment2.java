package com.abbsolute.ma_livu.ToDoList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.abbsolute.ma_livu.ToDoList.ToDoAppHelper.insertData;
import static com.abbsolute.ma_livu.ToDoList.ToDoAppHelper.insertFixData;
import static com.abbsolute.ma_livu.ToDoList.ToDoAppHelper.selectFixTodoInfo;

public class ToDoWriteFragment2 extends Fragment {
    ToDoCategoryAdapter categoryAdapter;
    ToDoFixListAdapter toDoFixListAdapter;
    ArrayList<ToDoFixInfo> toDoFixInfos=new ArrayList<>();
    RecyclerView fixTodoRecyclerview;
    RecyclerView categoryRecyclerview;
    ArrayList<ToDoCategoryInfo> categoryInfos=new ArrayList<>();
    NumberPicker setPeriodDay;
    NumberPicker setPeriod;
    final String[] values={"매주","격주","매달"};
    int periodPos=0;
    int dayPos=0;
    final String[] day={"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};
    EditText fixWrite;
    final String[] dates={"1","2","3","4","5","6",
            "7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23",
            "24","25","26","27","28","29","30"};
    // newInstance constructor for creating fragment with arguments
    public static ToDoWriteFragment2 newInstance() {
        ToDoWriteFragment2 fragment = new ToDoWriteFragment2();

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.todo_activity_write2,container,false);
        //todo : 페이지 2 카테고리 리스트 어뎁터 생성
        categoryRecyclerview=view.findViewById(R.id.todo_list_category2);
        fixTodoRecyclerview=view.findViewById(R.id.fix_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        categoryRecyclerview.setLayoutManager(linearLayoutManager);
        categoryAdapter=new ToDoCategoryAdapter();
        categoryInfos.add(new ToDoCategoryInfo(R.drawable.house_cleaning,"청소하기"));
        categoryInfos.add(new ToDoCategoryInfo(R.drawable.laundry,"빨래하기"));
        categoryInfos.add(new ToDoCategoryInfo(R.drawable.trash,"쓰레기"));
        categoryInfos.add(new ToDoCategoryInfo(R.drawable.user1,"기타"));
        categoryAdapter.setItem(categoryInfos);
        categoryAdapter.getCategoryContext(getContext());
        categoryRecyclerview.setAdapter(categoryAdapter);
        ///기본 카데고리
        setPeriod=view.findViewById(R.id.set_period);
        setPeriodDay=view.findViewById(R.id.set_period_day);
        //기간설정
        setPeriod.setMinValue(0);
        setPeriod.setMaxValue(values.length-1);
        setPeriod.setWrapSelectorWheel(true);
        setPeriod.setDisplayedValues(values);
        //요일 설정
        setPeriodDay.setMinValue(0);
        setPeriodDay.setMaxValue(day.length-1);
        setPeriodDay.setWrapSelectorWheel(true);
        setPeriodDay.setDisplayedValues(day);
        //
        setPeriod.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                periodPos=newVal;
                if(newVal==2){//매달
                    setPeriodDay.setDisplayedValues(null);
                    setPeriodDay.setMinValue(1);
                    setPeriodDay.setMaxValue(dates.length);
                    setPeriodDay.setWrapSelectorWheel(true);
                    setPeriodDay.setDisplayedValues(dates);
                }
                if(newVal==0||newVal==1){// 매주 혹은 격주
                    setPeriodDay.setDisplayedValues(null);
                    setPeriodDay.setMinValue(0);
                    setPeriodDay.setMaxValue(day.length-1);
                    setPeriodDay.setWrapSelectorWheel(true);
                    setPeriodDay.setDisplayedValues(day);
                }
            }
        });
        //
        setPeriodDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dayPos=newVal;//현재 요일의 위치
            }
        });
        //

        Button save=view.findViewById(R.id.write);
        fixWrite=view.findViewById(R.id.todo_write2);
        // todo: 등록하기 버튼
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                String res=pf.getString("toDo","");
                String resDetailTodo=fixWrite.getText().toString();
                Intent intent=new Intent();
                getActivity().setResult(RESULT_OK,intent);
                //sqlite쓰기
                if(!res.equals("")&&!resDetailTodo.equals(""))
                {
                    fixAddData();
                    fixPeriodAddData();
                }else{
                    Toast.makeText(getContext(),"데이터를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
            }
        });
        // todo: 고정리스트 어뎁터 생성 및 적용
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        fixTodoRecyclerview.setLayoutManager(layoutManager);
        toDoFixListAdapter=new ToDoFixListAdapter();
        toDoFixListAdapter.getFixContext(getContext());
        ArrayList<ToDoFixInfo> toDoFixInfos=selectFixTodoInfo("fixToDoInfo");
        toDoFixListAdapter.setFixItem(toDoFixInfos);
        fixTodoRecyclerview.setAdapter(toDoFixListAdapter);
        return view;
    }
    // todo: 고정 리스트 데이터 추가 함수
    private void fixPeriodAddData(){
        String fixDate="";
        String detailData=fixWrite.getText().toString();
        if(periodPos==0||periodPos==1){// 매주, 격주 요일 ex) 매주 월요일
            fixDate=values[periodPos]+" "+day[dayPos];
        }else if(periodPos==2){// 매달 ex) 매달 3일
            fixDate=values[periodPos]+" "+dates[dayPos]+"일";
        }

        ToDoFixInfo toDoFixInfo=new ToDoFixInfo(detailData,fixDate);
        // toDoFixInfos.add(toDoFixInfo);
        insertFixData("fixToDoInfo",toDoFixInfo);//고정리스트 데이터 db 삽입
    }
    //todo: 고정 할 일 데이터 추가 함수
    private void fixAddData() {
        SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String data=pf.getString("toDo","");
        String detailData=fixWrite.getText().toString();
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date=formatter.format(systemTime);
        String dDate=date;
        ToDoInfo toDoInfo=new ToDoInfo(data,detailData,date,dDate, R.drawable.todo_border2);
        insertData("todoInfo",toDoInfo);


        //파이어베이스에 카테고리 클릭 할 때 마다 특정 점수 올라가는 코드 작성
    }


}
