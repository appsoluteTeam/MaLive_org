package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

//fix_remove_list
public class ToDoFixListRemoveFragment extends Fragment implements OnBackPressedListener{
    RecyclerView toDoRemovingFixListView;//삭제할 고정리스트
    RecyclerView categoryRecyclerview;
    ArrayList<ToDoFixInfo> toDoFixInfos=new ArrayList<>();
    ToDoFixRemoveListAdapter toDoFixRemoveListAdapter;
    //완료 버튼
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    TextView completeBtn;
    ////매주, 요일 설정
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
    private int count=0;
    //
    protected TextView cleanText;
    protected TextView laundryText;
    protected TextView trashText;
    protected TextView etcText;
    ///
    boolean cleanFlag=false;
    boolean laundryFlag=false;
    boolean trashFlag=false;
    boolean todoFlag=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.fragment_todo_fix_remove,container,false);
        if(getArguments()!=null){
            count=getArguments().getInt("TotalRemoveCount");
            Log.d("받아오기5!!!~~",count+"");
        }
        ///카테고리
        cleanText=view.findViewById(R.id.clean_todo_image_text);
        laundryText=view.findViewById(R.id.laundry_todo_image_text);
        trashText=view.findViewById(R.id.trash_todo_image_text);
        etcText=view.findViewById(R.id.etc_todo_image_text);
        cleanText.setText("청소하기");
        laundryText.setText("빨래하기");
        trashText.setText("쓰레기");
        etcText.setText("기타");
        cleanText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanFlag=false;
                //다른애들 다 흰색으로
                laundryText.setBackgroundResource(R.drawable.todo_text_background);
                laundryText.setTextColor(Color.BLACK);
                trashText.setBackgroundResource(R.drawable.todo_text_background);
                trashText.setTextColor(Color.BLACK);
                etcText.setBackgroundResource(R.drawable.todo_text_background);
                etcText.setTextColor(Color.BLACK);
                SharedPreferences pref = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(getContext(), "청소하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                editor.putString("toDo", "청소");
                editor.commit();
                if (cleanFlag == false) {
                    cleanText.setBackgroundResource(R.drawable.todo_text_background2);
                    cleanText.setTextColor(Color.WHITE);
                    cleanFlag = true;
                } else {
                    cleanFlag = false;
                    cleanText.setBackgroundResource(R.drawable.todo_text_background);
                    cleanText.setTextColor(Color.BLACK);
                }
            }
        });
        laundryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                laundryFlag=false;
                cleanText.setBackgroundResource(R.drawable.todo_text_background);
                cleanText.setTextColor(Color.BLACK);
                trashText.setBackgroundResource(R.drawable.todo_text_background);
                trashText.setTextColor(Color.BLACK);
                etcText.setBackgroundResource(R.drawable.todo_text_background);
                etcText.setTextColor(Color.BLACK);
                SharedPreferences pref = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(getContext(), "빨래하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                editor.putString("toDo", "빨래");
                editor.commit();
                if (laundryFlag == false) {
                    laundryText.setBackgroundResource(R.drawable.todo_text_background2);
                    laundryText.setTextColor(Color.WHITE);
                    laundryFlag = true;
                } else {
                    laundryFlag = false;
                    laundryText.setBackgroundResource(R.drawable.todo_text_background);
                    laundryText.setTextColor(Color.BLACK);
                }
            }
        });
        trashText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trashFlag=false;
                cleanText.setBackgroundResource(R.drawable.todo_text_background);
                cleanText.setTextColor(Color.BLACK);
                laundryText.setBackgroundResource(R.drawable.todo_text_background);
                laundryText.setTextColor(Color.BLACK);
                etcText.setBackgroundResource(R.drawable.todo_text_background);
                etcText.setTextColor(Color.BLACK);
                SharedPreferences pref = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(getContext(), "쓰레기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                editor.putString("toDo", "쓰레기");
                editor.commit();
                if (trashFlag == false) {
                    trashText.setBackgroundResource(R.drawable.todo_text_background2);
                    trashText.setTextColor(Color.WHITE);
                    trashFlag = true;
                } else {
                    trashFlag = false;
                    trashText.setBackgroundResource(R.drawable.todo_text_background);
                    trashText.setTextColor(Color.BLACK);
                }
            }
        });
        etcText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoFlag=false;
                cleanText.setBackgroundResource(R.drawable.todo_text_background);
                cleanText.setTextColor(Color.BLACK);
                laundryText.setBackgroundResource(R.drawable.todo_text_background);
                laundryText.setTextColor(Color.BLACK);
                trashText.setBackgroundResource(R.drawable.todo_text_background);
                trashText.setTextColor(Color.BLACK);
                SharedPreferences pref = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(getContext(), "기타 이미지 클릭", Toast.LENGTH_SHORT).show();
                editor.putString("toDo", "기타");
                editor.commit();
                if (todoFlag == false) {
                    etcText.setBackgroundResource(R.drawable.todo_text_background2);
                    etcText.setTextColor(Color.WHITE);
                    todoFlag = true;
                } else {
                    todoFlag = false;
                    etcText.setBackgroundResource(R.drawable.todo_text_background);
                    etcText.setTextColor(Color.BLACK);
                }
            }
        });
        ////삭제할 고정 리스트 정의

        toDoRemovingFixListView=view.findViewById(R.id.fix_remove_list);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        toDoRemovingFixListView.setLayoutManager(linearLayoutManager1);
        //삭제할 리스트 어뎁터
        toDoFixRemoveListAdapter=new ToDoFixRemoveListAdapter();
        toDoFixRemoveListAdapter.getFixContext(getContext());
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email_id","");
        firestore.collection(FirebaseID.ToDoLists).document(email).collection("FixToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null){
                                toDoFixInfos.clear();
                                for(DocumentSnapshot snapshot: task.getResult()){
                                    String period=String.valueOf(snapshot.get("period"));
                                    String todo=String.valueOf(snapshot.get("todo"));
                                    ToDoFixInfo toDoFixInfo=new ToDoFixInfo(todo,period);
                                    toDoFixInfos.add(toDoFixInfo);
                                }
                                toDoFixRemoveListAdapter.setFixItem(toDoFixInfos);
                                toDoFixRemoveListAdapter.notifyDataSetChanged();
                                toDoRemovingFixListView.setAdapter(toDoFixRemoveListAdapter);
                            }
                        }
                    }
                });

        toDoFixRemoveListAdapter.setFixItem(toDoFixInfos);
        toDoRemovingFixListView.setAdapter(toDoFixRemoveListAdapter);
        completeBtn=view.findViewById(R.id.complete);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //완료버튼 누르면 작성메인 프레그먼트로 넘어감
                ((HomeActivity)getActivity()).setFragment(101);
            }
        });
        ///요일 설정하기 디자인 만 구성하기
        ///기본 카데고리
        setPeriod=view.findViewById(R.id.set_period_remove);
        setPeriodDay=view.findViewById(R.id.set_period_day_remove);
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
        return view;
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
}
