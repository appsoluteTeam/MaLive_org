package com.abbsolute.ma_livu.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ToDoWrite extends Fragment {

    private static String email;
    private int category = 0;
    private String str_category;
    private int todo_year = 0;
    private int todo_month = 0;
    private int todo_day = 0;

    //파이어베이스 관련 변수
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    //xml 내 변수
    private TextView cleanText;
    private TextView laundryText;
    private TextView trashText;
    private TextView etcText;

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;

    private Button btn_todo_store;
    private TextView write_todo;

    public ToDoWrite() {
    }

    public ToDoWrite(String email) {
        this.email = email;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_write, container, false);

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

        Calendar calendar = Calendar.getInstance();

        /* 카테고리 설정 */
        cleanText = view.findViewById(R.id.clean_todo_image_text);
        laundryText = view.findViewById(R.id.laundry_todo_image_text);
        trashText = view.findViewById(R.id.trash_todo_image_text);
        etcText = view.findViewById(R.id.etc_todo_image_text);

        cleanText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //clean text 빼고 흰색으로
                laundryText.setBackgroundResource(R.drawable.todo_text_background);
                trashText.setBackgroundResource(R.drawable.todo_text_background);
                etcText.setBackgroundResource(R.drawable.todo_text_background);

                cleanText.setBackgroundResource(R.drawable.todo_text_background2);

                category = 1;
            }
        });

        laundryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //laundry text 빼고 흰색으로
                cleanText.setBackgroundResource(R.drawable.todo_text_background);
                trashText.setBackgroundResource(R.drawable.todo_text_background);
                etcText.setBackgroundResource(R.drawable.todo_text_background);

                laundryText.setBackgroundResource(R.drawable.todo_text_background2);

                category = 2;
            }
        });

        trashText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //trash text 빼고 흰색으로
                cleanText.setBackgroundResource(R.drawable.todo_text_background);
                laundryText.setBackgroundResource(R.drawable.todo_text_background);
                etcText.setBackgroundResource(R.drawable.todo_text_background);

                trashText.setBackgroundResource(R.drawable.todo_text_background2);

                category = 3;
            }
        });

        etcText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //etc text 빼고 흰색으로
                cleanText.setBackgroundResource(R.drawable.todo_text_background);
                laundryText.setBackgroundResource(R.drawable.todo_text_background);
                trashText.setBackgroundResource(R.drawable.todo_text_background);

                etcText.setBackgroundResource(R.drawable.todo_text_background2);

                category = 4;
            }
        });


        /* 날짜 설정 */
        yearPicker = view.findViewById(R.id.set_year);
        monthPicker = view.findViewById(R.id.set_month);
        dayPicker = view.findViewById(R.id.set_day);

        //numberPicker 최대 최소값 설정
        //max값 끝까지 올리면 더이상 못 올라가게 하기
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2030);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(calendar.get(Calendar.YEAR));

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setWrapSelectorWheel(false);
        monthPicker.setValue(calendar.get(Calendar.MONTH)+1);


        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setValue(calendar.get(Calendar.DATE));

        //현재날짜로 초기화
        todo_year = calendar.get(Calendar.YEAR);
        todo_month = calendar.get(Calendar.MONTH) + 1;
        todo_day = calendar.get(Calendar.DATE);

        //날짜 값이 변경 될 때마다 listener 행동 설정
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                todo_year = newVal;
                Log.d("변경된 todo_year",Integer.toString(todo_year));
            }
        });
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                todo_month = newVal;
                Log.d("변경된 todo_month",Integer.toString(todo_month));
            }
        });
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                todo_day = newVal;
                Log.d("변경된 todo_day",Integer.toString(todo_day));
            }
        });

        /* 등록버튼 눌렀을 때 */
        // 그리고.. 카테고리가 선택되지 않았을 때, 날짜가 선택되지 않았을 때, 세부사항이 null이 아닐 때만 넘어가게 해줘야한다.

        //현재 시간 초단위까지 나오는 format type
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        String format_time = format.format(calendar.getTime());


//        String todo_date = Integer.toString(todo_year) + "." + Integer.toString(todo_month) + "." + Integer.toString(todo_day);

        write_todo = view.findViewById(R.id.write_todo);
        btn_todo_store = view.findViewById(R.id.btn_todo_store);
        btn_todo_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category == 0 || todo_year == 0 || todo_month == 0 || todo_day == 0 || write_todo.getText().toString() == null){
                    Log.d("na",Integer.toString(category) + "-category, " + Integer.toString(todo_year) + "-year , "+ write_todo.getText().toString());
                    Toast.makeText(getContext(), "등록 형식을 지켜주세요", Toast.LENGTH_SHORT).show();
                }else{
                    //todo: 파이어베이스에 저장, category별로 setFragment해서 fragment변경
                    String detail_content = write_todo.getText().toString();
                    switch (category){
                        case 1:
                            str_category = "청소";
                            break;
                        case 2:
                            str_category = "빨래";
                            break;
                        case 3:
                            str_category = "쓰레기";
                            break;
                        case 4:
                            str_category = "기타";
                            break;
                    }

                    String todo_date = Integer.toString(todo_year) + "." + Integer.toString(todo_month) + "." + Integer.toString(todo_day);

                    //firebase에 저장할 listview
                    ToDoList_Info toDoList_info = new ToDoList_Info(todo_date,format_time,str_category,detail_content,false);

                    Log.d("todo_date", str_category + detail_content + todo_date + format_time);
                    //파이어스토어 저장
                    String id = firebaseAuth.getCurrentUser().getEmail();

                    firestore.collection(FirebaseID.ToDoLists)
                            .document(id)
                            .collection("ToDo")
                            .document(format_time)
                            .set(toDoList_info, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ToDoWrite", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ToDoWrite", "Error writing document", e);
                                }
                            });

                    //fragment 변경
                    ToDoFragment_final toDoFragment_final = new ToDoFragment_final();

                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame, toDoFragment_final);
                    fragmentTransaction.commit();
                }

            }
        });

        return view;
    }

    //todo: firebase에 저장, todo프래그먼트로 전환
    public void setFragment(int n){
        switch (n){
            case 1://빨래
                break;
            case 2://청소
                break;
            case 3://쓰레기
                break;
            case 4://기타
                break;
        }
    }
}
