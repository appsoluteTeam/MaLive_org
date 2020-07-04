package com.abbsolute.ma_livu.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.AppHelper;
import com.abbsolute.ma_livu.CategoryAdapter;
import com.abbsolute.ma_livu.CategoryInfo;
import com.abbsolute.ma_livu.Fragments.ToDoFragment;
import com.abbsolute.ma_livu.R;
import com.abbsolute.ma_livu.ToDoInfo;

import java.util.ArrayList;
import java.util.Locale;

import static com.abbsolute.ma_livu.AppHelper.insertData;


public class WriteActivity extends AppCompatActivity {
    EditText write;
    EditText detailWrite;
    TextView storing;
    TextView cancel;
    TextView setDday;
    /////
    private int year=0;
    private int month=0;
    private int day=0;
    int val=0;
    private int UPDATE_OK=5;
    ///NumberPicker 정의
    NumberPicker yearPicker;
    NumberPicker monthPicker;
    NumberPicker dayPicker;
    ///
    CategoryAdapter categoryAdapter;
    RecyclerView categoryRecyclerview;
    ArrayList<CategoryInfo> categoryInfos=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        categoryRecyclerview=findViewById(R.id.todo_list_category);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        categoryRecyclerview.setLayoutManager(linearLayoutManager);
        categoryAdapter=new CategoryAdapter();
        categoryInfos.add(new CategoryInfo(R.drawable.house_cleaning,"청소하기"));
        categoryInfos.add(new CategoryInfo(R.drawable.laundry,"빨래하기"));
        categoryInfos.add(new CategoryInfo(R.drawable.trash,"쓰레기"));
        categoryInfos.add(new CategoryInfo(R.drawable.user1,"기타"));
        categoryAdapter.setItem(categoryInfos);
        categoryAdapter.getCategoryContext(getApplicationContext());
        categoryRecyclerview.setAdapter(categoryAdapter);
        ///intent 얻기
        write=findViewById(R.id.write_todo);

        storing=findViewById(R.id.store);
        yearPicker=findViewById(R.id.set_year);
        monthPicker=findViewById(R.id.set_month);
        dayPicker=findViewById(R.id.set_day);
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
        SQLiteDatabase todo;
        //저장
        storing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=getIntent();
                String word=intent1.getStringExtra("modify");
                SharedPreferences pf=getApplicationContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
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
                    ToDoInfo toDoInfo=new ToDoInfo(res,resDetailTodo,date,dDate);
                    AppHelper.updateData(getApplicationContext(),"todoInfo",toDoInfo,word);
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else{//추가
                    ToDoFragment toDoFragment=new ToDoFragment();
                    Bundle bundle=new Bundle(1);
                    String res=pf.getString("toDo","");
                    String resDetailTodo=write.getText().toString();
                    bundle.putString("write_result_detail",resDetailTodo);
                    bundle.putString("write_result",res);
                    toDoFragment.setArguments(bundle);
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    //sqlite쓰기
                    if(!res.equals("")&&!resDetailTodo.equals(""))
                    {
                        addData();
                    }else{
                        Toast.makeText(getApplicationContext(),"데이터를 입력하세요",Toast.LENGTH_SHORT).show();
                    }
                    finish();
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
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void checkDate(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                year=y;
                month=m+1;
                day=d;
            }
        },2020,1,1);
        datePickerDialog.show();
    }
    public void addData(){
        SharedPreferences pf=getApplicationContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String data=pf.getString("toDo","");
        String detailData=write.getText().toString();
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
        ToDoInfo toDoInfo=new ToDoInfo(data,detailData,date,dDate);
        insertData("todoInfo",toDoInfo);
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
