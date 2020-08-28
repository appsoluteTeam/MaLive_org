package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.insertFixData;
import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.selectFixTodoInfo;
import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.updateData;
import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.updateFixData;

public class ToDoFixModifyingFragment extends Fragment implements OnBackPressedListener,refreshInterface{
    int count=0;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
    TextView removing;
    private FragmentTransaction fragmentTransaction;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.todo_fix_modify_fragment,container,false);
        //count받아오기
        //todo : 페이지 2 카테고리 리스트 어뎁터 생성
        categoryRecyclerview=view.findViewById(R.id.todo_fix_modified_list_category);
        fixTodoRecyclerview=view.findViewById(R.id.fix_modified_list);
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

        final Button save=view.findViewById(R.id.write);
        fixWrite=view.findViewById(R.id.todo_write2);
        // 수정 완료 버튼 클릭
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
                    fixUpdateData();
                    fixPeriodUpdateData();
                }else{
                    Toast.makeText(getContext(),"데이터를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                ((HomeActivity)getActivity()).setFragment(100);//ToDoWriteMainFragment로 전환
            }
        });
        // todo: 고정리스트 어뎁터 생성 및 적용
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        fixTodoRecyclerview.setLayoutManager(layoutManager);
        toDoFixListAdapter=new ToDoFixListAdapter();
        toDoFixListAdapter.getFixContext(getContext());
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref",Activity.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email_id","");
        firestore.collection(FirebaseID.ToDoLists).document(email).collection("FixToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null){
                                toDoFixInfos.clear();
                                for(DocumentSnapshot snapshot:task.getResult()){
                                    Map<String,Object> data=snapshot.getData();
                                    String todo=String.valueOf(data.get("todo"));
                                    String period=String.valueOf(data.get("period"));
                                    ToDoFixInfo toDoFixInfo=new ToDoFixInfo(todo,period);
                                    toDoFixInfos.add(toDoFixInfo);
                                }
                                toDoFixListAdapter.setFixItem(toDoFixInfos);
                                toDoFixListAdapter.notifyDataSetChanged();
                                fixTodoRecyclerview.setAdapter(toDoFixListAdapter);
                            }
                        }
                    }
                });
        toDoFixListAdapter.setFixItem(toDoFixInfos);
        fixTodoRecyclerview.setAdapter(toDoFixListAdapter);
        ///fixToDoList 삭제 변수 정의
        removing=(TextView)view.findViewById(R.id.fix_todo_remove);
        //todo: fixToDoList 수정 기능 정의
        return view;
    }
    // todo: 고정 리스트 데이터 추가 함수
    private void fixPeriodUpdateData(){
        String fixDate="";
        String detailData=fixWrite.getText().toString();
        if(periodPos==0||periodPos==1){// 매주, 격주 요일 ex) 매주 월요일
            fixDate=values[periodPos]+" "+day[dayPos];
        }else if(periodPos==2){// 매달 ex) 매달 3일
            fixDate=values[periodPos]+" "+dates[dayPos]+"일";
        }

        final ToDoFixInfo toDoFixInfo=new ToDoFixInfo(detailData,fixDate);
        // toDoFixInfos.add(toDoFixInfo);
        SharedPreferences pref = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        final String upDateContent=pref.getString("upDateToDo","");
        //updateFixData("fixToDoInfo",toDoFixInfo,upDateContent);//고정리스트 데이터 db 삽입
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref",Activity.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email_id","");
        final String finalFixDate = fixDate;
        firestore.collection(FirebaseID.ToDoLists).document(email)
                .collection("FixToDo")
                .whereEqualTo("todo",upDateContent)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d("~~~","성공");
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                Map<String,Object> data=snapshot.getData();
                                String detailData=fixWrite.getText().toString();
                                data.put("todo",detailData);
                                data.put("period", finalFixDate);
                                firestore.collection(FirebaseID.ToDoLists).document(email)
                                        .collection("FixToDo")
                                        .document(upDateContent)
                                        .delete();
                                firestore.collection(FirebaseID.ToDoLists).document(email)
                                        .collection("FixToDo")
                                        .document(detailData)
                                        .set(data, SetOptions.merge());
                            }
                        }

                    }
                });

    }
    //todo: 고정 할 일 데이터 추가 함수
    private void fixUpdateData() {
        SharedPreferences pref = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        final String upDateContent=pref.getString("upDateToDo","");
        //
        SharedPreferences pf=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String data=pf.getString("toDo","");
        String detailData=fixWrite.getText().toString();
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date=formatter.format(systemTime);
        String dates[]=date.split("-");
        String dYear=dates[0];
        String dMonth=dates[1];
        String dDay=dates[2];
        date=dYear+"년"+dMonth+"월"+dDay+"일";
        String dDate=date;
        final ToDoInfo toDoInfo=new ToDoInfo(data,detailData,date,dDate, R.drawable.todo_border2);
        //파이어베이스에 FixTodo 수정 데이터 올리기
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref",Activity.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email_id","");
        firestore.collection(FirebaseID.ToDoLists).document(email)
                .collection("ToDo")
                .whereEqualTo("detailContent",upDateContent)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d("!!!","성공");
                            String cnt="";
                            for(DocumentSnapshot snapshot:task.getResult()){
                                Map<String,Object> d=snapshot.getData();
                                cnt=String.valueOf(d.get("fixNum"));
                            }
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                Map<String,Object> data=snapshot.getData();
                                data.put("content",toDoInfo.content);
                                data.put("detailContent",toDoInfo.detailContent);
                                data.put("date",toDoInfo.dates);
                                data.put("dDay",toDoInfo.dDay);
                                data.put("num",cnt);
                                firestore.collection(FirebaseID.ToDoLists).document(email)
                                        .collection("FixToDo")
                                        .document(upDateContent)
                                        .delete();
                                firestore.collection(FirebaseID.ToDoLists).document(email)
                                        .collection("FixToDo")
                                        .document(toDoInfo.detailContent)
                                        .set(data, SetOptions.merge());

                            }
                        }

                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).setOnBackPressedListener(this);
    }

    @Override
    public void onBackPressed() {
        ((HomeActivity)getActivity()).setCurrentScene(this);//writeMainFragment로
    }
    public void setCount(int counts){
        count=counts;
    }

    @Override
    public void refresh() {
        // 리사이클러뷰 새로고침 메소드
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }
}
