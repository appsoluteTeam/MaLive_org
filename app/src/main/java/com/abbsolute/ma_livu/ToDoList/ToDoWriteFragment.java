package com.abbsolute.ma_livu.ToDoList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Fragments.OnBackPressedListener;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.abbsolute.ma_livu.ToDoList.ToDoAppHelper.insertData;


public class ToDoWriteFragment extends Fragment implements OnBackPressedListener {
    // newInstance constructor for creating fragment with arguments
    public static ToDoWriteFragment newInstance() {
        ToDoWriteFragment fragment = new ToDoWriteFragment();

        return fragment;
    }
    EditText write;
    EditText detailWrite;
    TextView storing;
    TextView cancel;
    TextView setDday;
    /////
    private int year=0;// 작성년도, 디데이 년도
    private int month=0;// 작성월, 디데이 월
    private int day=0;// 작성일, 디데이 일
    int val=0;
    private int UPDATE_OK=5;
    ///NumberPicker 정의
    NumberPicker yearPicker;
    NumberPicker monthPicker;
    NumberPicker dayPicker;
    ///
    ToDoCategoryAdapter categoryAdapter;
    RecyclerView categoryRecyclerview;
    ArrayList<ToDoCategoryInfo> categoryInfos=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.todo_activity_write,container,false);
        //todo : 페이지 1 카테고리 리스트 어뎁터 생성
        categoryRecyclerview=view.findViewById(R.id.todo_list_category);
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
        ///intent 얻기
        write=view.findViewById(R.id.write_todo);

        storing=view.findViewById(R.id.store);
        yearPicker=view.findViewById(R.id.set_year);
        monthPicker=view.findViewById(R.id.set_month);
        dayPicker=view.findViewById(R.id.set_day);
        ////////////
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2030);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        ///////////
        ///
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date=formatter.format(systemTime);
        String[] splitData=date.split("-");
        String tmp1=splitData[0];
        String tmp2=splitData[1];
        String tmp3=splitData[2];
        int splitYear=Integer.parseInt(tmp1);
        int splitMonth=Integer.parseInt(tmp2);
        int splitDay=Integer.parseInt(tmp3);
        ///
        yearPicker.setValue(splitYear);
        monthPicker.setValue(splitMonth);
        dayPicker.setValue(splitDay);
        year=yearPicker.getValue();
        month=monthPicker.getValue();
        day=dayPicker.getValue();
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year=newVal;
                Toast.makeText(getContext(), ""+newVal, Toast.LENGTH_SHORT).show();
            }
        });
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                month=newVal;
            }
        });
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                day=newVal;
            }
        });
        SQLiteDatabase todo;
        //저장
        storing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=getActivity().getIntent();
                String word=intent1.getStringExtra("modify");
                SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                if(word!=null){//수정작업
                    String res=pf.getString("toDo","");
                    String resDetailTodo=write.getText().toString();
                    long systemTime = System.currentTimeMillis();
                    SimpleDateFormat formatter= null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    }
                    String date=formatter.format(systemTime);
                    String dDate=date;
                    if(year!=2020&&month!=1&&day!=1)
                    {
                        String months="0"+month;
                        dDate=year+"-"+months+"-"+day;
                    }
                    ToDoInfo toDoInfo=new ToDoInfo(res,resDetailTodo,date,dDate,Color.WHITE);
                    ToDoAppHelper.updateData(getContext(),"todoInfo",toDoInfo,word);
                    Intent intent=new Intent();
                    getActivity().setResult(RESULT_OK,intent);
                    getActivity().finish();
                }else{//추가
                    ToDoFragment toDoFragment=new ToDoFragment();
                    Bundle bundle=new Bundle(1);
                    String res=pf.getString("toDo","");
                    String resDetailTodo=write.getText().toString();
                    bundle.putString("write_result_detail",resDetailTodo);
                    bundle.putString("write_result",res);
                    toDoFragment.setArguments(bundle);
                    Intent intent=new Intent();
                    getActivity().setResult(RESULT_OK,intent);
                    //sqlite쓰기
                    if(!res.equals("")&&!resDetailTodo.equals(""))
                    {
                        addData();
                    }else{
                        Toast.makeText(getContext(),"데이터를 입력하세요",Toast.LENGTH_SHORT).show();
                    }
                    getActivity().finish();
                }

            }
        });
        /*cancel=findViewById(R.id.undo);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        /*setDday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDate();
            }
        });*/
        return view;
    }


    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
    public void addData(){
        SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String data=pf.getString("toDo","");
        String detailData=write.getText().toString();
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date=formatter.format(systemTime);
        String dDate=date;
        if(year>=2020&&month>=1&&day>=1)
        {
            String months="0"+month;
            dDate=year+"년"+months+"월"+day+"일";
        }
        ToDoInfo toDoInfo=new ToDoInfo(data,detailData,date,dDate, R.drawable.todo_border);
        insertData("todoInfo",toDoInfo);
        SharedPreferences pref = getContext().getSharedPreferences("set_theme", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("theme",1);
        editor.commit();
        //파이어베이스에 카테고리 클릭 할 때 마다 특정 점수 올라가는 코드 작성
    }
    final static int req1=1;
    public String a = "0"; // initialize this globally at the top of your class.

    /*public void setAlarm(Calendar target){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), req1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);
        a ="1";
    }*/
}
