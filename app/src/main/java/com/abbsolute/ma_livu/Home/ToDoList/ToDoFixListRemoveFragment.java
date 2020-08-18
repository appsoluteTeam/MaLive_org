package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.selectFixTodoInfo;

//fix_remove_list
public class ToDoFixListRemoveFragment extends Fragment {
    RecyclerView toDoRemovingFixListView;//삭제할 고정리스트
    RecyclerView categoryRecyclerview;
    ToDoCategoryAdapter categoryAdapter;
    ArrayList<ToDoCategoryInfo> categoryInfos=new ArrayList<>();
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.fragment_todo_fix_remove,container,false);

        ///카테고리
        categoryRecyclerview=view.findViewById(R.id.todo_list_category3);
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
        ////삭제할 고정 리스트 정의

        toDoRemovingFixListView=view.findViewById(R.id.fix_remove_list);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        toDoRemovingFixListView.setLayoutManager(linearLayoutManager1);
        //삭제할 리스트 어뎁터
        toDoFixRemoveListAdapter=new ToDoFixRemoveListAdapter();
        toDoFixRemoveListAdapter.getFixContext(getContext());
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email_id","");
        final DocumentReference documentReference=firestore.collection(FirebaseID.ToDoLists).document(email+" FixToDo");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> data = new HashMap<>();
                    //ex) Content1, Content2 이런식 으로 저장하도록 만듦
                    DocumentSnapshot snapshot=task.getResult();
                    if(snapshot.exists()){
                        toDoFixInfos.clear();
                        String cnt= (String) snapshot.getData().get("Count");
                        int siz=Integer.parseInt(cnt);
                        for(int i=0;i<=siz;i++){
                            String period=(String)snapshot.getData().get("period"+i);
                            String todo=(String)snapshot.getData().get("todo"+i);
                            ToDoFixInfo toDoFixInfo=new ToDoFixInfo(period,todo);
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
}
