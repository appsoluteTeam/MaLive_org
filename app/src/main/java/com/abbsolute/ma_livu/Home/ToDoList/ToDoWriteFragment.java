package com.abbsolute.ma_livu.Home.ToDoList;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.insertData;

public class ToDoWriteFragment extends Fragment implements refreshInterface {
    // newInstance constructor for creating fragment with arguments
    //파이버베이스 인증 변수
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
    private FragmentTransaction fragmentTransaction;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.todo_fragment_write,container,false);
        //todo : 페이지 1 카테고리 리스트 어뎁터 생성
        categoryRecyclerview=view.findViewById(R.id.todo_list_category);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        categoryRecyclerview.setLayoutManager(linearLayoutManager);
        categoryAdapter=new ToDoCategoryAdapter();
        categoryInfos.add(new ToDoCategoryInfo("청소하기"));
        categoryInfos.add(new ToDoCategoryInfo("빨래하기"));
        categoryInfos.add(new ToDoCategoryInfo("쓰레기"));
        categoryInfos.add(new ToDoCategoryInfo("기타"));
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
                SharedPreferences pfModify=getContext().getSharedPreferences("pref2",Activity.MODE_PRIVATE);
                boolean isUpdated=pfModify.getBoolean("modify",false);
                int positon=pfModify.getInt("pos",0);
                SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                if(isUpdated==true){//수정작업
                    SharedPreferences getEmail=getContext().getSharedPreferences("pref",Activity.MODE_PRIVATE);
                    final String email=getEmail.getString("email_id","");
                    final DocumentReference documentReference=firestore.collection(FirebaseID.ToDoLists).document(email+" ToDo");
                    String res=pf.getString("toDo","");
                    String resDetailTodo=write.getText().toString();
                    long systemTime = System.currentTimeMillis();
                    SimpleDateFormat formatter= null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    }
                    String date=formatter.format(systemTime);
                    String dDate=date;
                    if(year>=2020&&month>=1&&day>=1)
                    {
                        String months="";
                        String days="";
                        if(month<10){
                            months="0"+month;
                        }else{
                            months=Integer.toString(month);
                        }
                        if(day<10){
                            days="0"+day;
                        }else{
                            days=Integer.toString(day);
                        }
                        dDate=year+"년"+months+"월"+days+"일";
                    }
                    ToDoInfo toDoInfo=new ToDoInfo(res,resDetailTodo,date,dDate, Color.WHITE);
                    HashMap<String,Object> data=new HashMap<>();
                    data.put("contents"+positon,toDoInfo.content);
                    data.put("detailContents"+positon,toDoInfo.detailContent);
                    data.put("dates"+positon,toDoInfo.dates);
                    data.put("dDates"+positon,toDoInfo.dDay);
                    documentReference.update(data);
                    SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref2", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("modify",false);
                    editor.commit();
                    ((HomeActivity)getActivity()).setFragment(0);//ToDoFragment로 전환
                }else{//추가
                    ToDoFragment toDoFragment=new ToDoFragment();
                    Bundle bundle2=new Bundle(1);
                    String res=pf.getString("toDo","");
                    String resDetailTodo=write.getText().toString();
                    bundle2.putString("write_result_detail",resDetailTodo);
                    bundle2.putString("write_result",res);
                    toDoFragment.setArguments(bundle2);
                    Intent intent=new Intent();
                    getActivity().setResult(RESULT_OK,intent);
                    //sqlite쓰기
                    if(!res.equals("")&&!resDetailTodo.equals(""))
                    {
                        addData();
                    }else{
                        Toast.makeText(getContext(),"데이터를 입력하세요",Toast.LENGTH_SHORT).show();
                    }
                    SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref2",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("modify",false);
                    editor.commit();
                    refresh();
                    ((HomeActivity)getActivity()).setFragment(0);//ToDoFragment로 전환
                }
            }
        });
        return view;
    }

    public void addData(){
        SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String data=pf.getString("toDo","");
        final String detailData=write.getText().toString();
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        final String date=formatter.format(systemTime);
        String dDate=date;
        if(year>=2020&&month>=1&&day>=1)
        {
            String months="";
            if(month<10){
                months="0"+month;
            }else{
                months=Integer.toString(month);
            }
            dDate=year+"년"+months+"월"+day+"일";
        }
        final ToDoInfo toDoInfo=new ToDoInfo(data,detailData,date,dDate, R.drawable.todo_border);
        insertData("todoInfo",toDoInfo);
        //파이어베이스에 todo데이터 올리기
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref",Activity.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email_id","");
        final DocumentReference documentReference=firestore.collection(FirebaseID.ToDoLists).document(email+" ToDo");
        //final String finalDDate = dDate;
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> data = new HashMap<>();
                    //ex) Content1, Content2 이런식 으로 저장하도록 만듦
                    DocumentSnapshot snapshot=task.getResult();
                    if(snapshot.exists()){
                        String cnt= (String) snapshot.getData().get("Count");
                        int tmp=Integer.parseInt(cnt);
                        tmp++;
                        String newCnt=Integer.toString(tmp);
                        data.put("contents"+newCnt,toDoInfo.content);
                        data.put("detailContents"+newCnt,toDoInfo.detailContent);
                        data.put("dates"+newCnt,toDoInfo.dates);
                        data.put("dDates"+newCnt,toDoInfo.dDay);
                        data.put("Count",newCnt);
                        String colors=Integer.toString(R.drawable.todo_border);
                        data.put("color"+newCnt,colors);
                        documentReference.set(data, SetOptions.merge());
                    }else{
                        int tmp=0;
                        String cnt=Integer.toString(tmp);
                        data.put("contents"+cnt,toDoInfo.content);
                        data.put("detailContents"+cnt,toDoInfo.detailContent);
                        data.put("dates"+cnt,toDoInfo.dates);
                        data.put("dDates"+cnt,toDoInfo.dDay);
                        data.put("Count",cnt);
                        String colors=Integer.toString(R.drawable.todo_border);
                        data.put("color"+cnt,colors);
                        documentReference.set(data, SetOptions.merge());
                    }
                }
            }
        });
    }

    @Override
    public void refresh() {
        fragmentTransaction = getFragmentManager().beginTransaction();
        ToDoFragment toDoFragment=new ToDoFragment();
        fragmentTransaction.detach(toDoFragment).attach(toDoFragment).commit();
    }
}

