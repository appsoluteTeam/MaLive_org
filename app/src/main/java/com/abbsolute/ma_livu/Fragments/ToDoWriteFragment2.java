package com.abbsolute.ma_livu.Fragments;

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
import com.abbsolute.ma_livu.ToDoCategoryAdapter;
import com.abbsolute.ma_livu.ToDoCategoryInfo;
import com.abbsolute.ma_livu.ToDoInfo;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.abbsolute.ma_livu.ToDoAppHelper.insertData;

public class ToDoWriteFragment2 extends Fragment {
    ToDoCategoryAdapter categoryAdapter;
    RecyclerView categoryRecyclerview;
    ArrayList<ToDoCategoryInfo> categoryInfos=new ArrayList<>();
    NumberPicker setPeriodDay;
    NumberPicker setPeriod;
    final String[] values={"매주","격주","매달"};
    final String[] day={"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};
    EditText fixWrite;
    // newInstance constructor for creating fragment with arguments
    public static ToDoWriteFragment2 newInstance() {
        ToDoWriteFragment2 fragment = new ToDoWriteFragment2();

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.todo_activity_write2,container,false);
        //기본 카테고리
        categoryRecyclerview=view.findViewById(R.id.todo_list_category2);
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
        NumberPicker.OnScrollListener onScrollListener=new NumberPicker.OnScrollListener(){
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                NumberPicker picker=view;

                if(scrollState==SCROLL_STATE_IDLE){
                    if(view.getId()==R.id.set_period){
                        int val=picker.getValue();
                        if(val==values.length-1){
                            setPeriodDay.setDisplayedValues(null);
                            setPeriodDay.setMinValue(1);
                            setPeriodDay.setMaxValue(30);
                            setPeriodDay.setWrapSelectorWheel(true);
                        }
                    }
                    Toast.makeText(getContext(), ""+picker.getValue(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        Button save=view.findViewById(R.id.write);
        fixWrite=view.findViewById(R.id.todo_write2);
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
                }else{
                    Toast.makeText(getContext(),"데이터를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
            }
        });
        return view;
    }

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
        SharedPreferences pref = getContext().getSharedPreferences("set_theme", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("theme",2);
        editor.commit();
        //파이어베이스에 카테고리 클릭 할 때 마다 특정 점수 올라가는 코드 작성
    }
}
