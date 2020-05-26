package com.abbsolute.ma_livu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        write=findViewById(R.id.write_todo);
        detailWrite=findViewById(R.id.write_todo_detail);
        storing=findViewById(R.id.store);
        setDday=findViewById(R.id.set_d_day);
        SQLiteDatabase todo;
        storing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=getIntent();
                String word=intent1.getStringExtra("modify");
                if(word!=null){
                    String res=write.getText().toString();
                    String resDetailTodo=detailWrite.getText().toString();
                    long systemTime = System.currentTimeMillis();
                    SimpleDateFormat formatter= null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    }
                    String date=formatter.format(systemTime);
                    String dDate="디데이";
                    if(year>0&&month>0&&day>0)
                        dDate=year+"-"+month+"-"+day;
                    ToDoInfo toDoInfo=new ToDoInfo(res,resDetailTodo,date,dDate);
                    AppHelper.updateData(getApplicationContext(),"todoInfo",toDoInfo,word);
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    ToDoFragment toDoFragment=new ToDoFragment();
                    Bundle bundle=new Bundle(1);
                    String res=write.getText().toString();
                    String resDetailTodo=detailWrite.getText().toString();
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
        cancel=findViewById(R.id.undo);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setDday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDate();
            }
        });


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
        String data=write.getText().toString();
        String detailData=detailWrite.getText().toString();
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date=formatter.format(systemTime);
        String dDate="디데이";
        if(year>0&&month>0&&day>0)
            dDate=year+"-"+month+"-"+day;
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
