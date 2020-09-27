package com.abbsolute.ma_livu.Home;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookFragment;
import com.abbsolute.ma_livu.Home.ToDoList.OnBackPressedListener;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements OnBackPressedListener {

    private View view;
    private Button go_Todo;
    private Button go_GuestBook;

    private static String email;
    private long atCount;

    //출석체크 관련 변수
    private String beforeCalendar;
    private LinearLayout layout_checkin;
    private TextView at_close_button;

    //파이어스토에 저장하기 위한 변수
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public HomeFragment(){};
    public HomeFragment(String email){
        this.email = email;
    }

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
        ((HomeActivity)getActivity()).setOnBackPressedListener(this);
        layout_checkin = view.findViewById(R.id.layout_checkin);
        at_close_button = view.findViewById(R.id.at_close_button);

        attendance_check();

        go_GuestBook = (Button)view.findViewById(R.id.go_GuestBook);
        go_GuestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).setFragment(4);
            }
        });
        go_Todo=view.findViewById(R.id.go_Todo);
        go_Todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getContext().getSharedPreferences("pref2", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("upload",0);
                editor.commit();
                ((HomeActivity)getActivity()).setFragment(100);
            }
        });

        at_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_checkin.setVisibility(View.GONE);
            }
        });



        return view;
    }

    //출석체크 todo:로그인할때 받아오는데 자동로그인일 때는 어떻게 하징? 홈액티비티에서 말고 메인에서 보여줘야하나
    public void attendance_check(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("time",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //현재 접속 년원일 구하기
        Calendar cal = Calendar.getInstance();
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String date = Integer.toString(cal.get(Calendar.DATE));

        //현재 접속 년월일 String형으로
        String currentCalendar = year + "-" + month + "-" + date;

        String emailBeforeCalendar = "beforeCalendar-" + email; //sharedPreference에 email값과 함께 저장하기 위해 만든 String형 key값

        beforeCalendar = sharedPreferences.getString(emailBeforeCalendar,"null");

        Log.d("currenCalendar",currentCalendar);
        Log.d("beforeCalendar",beforeCalendar);

        if(beforeCalendar.equals(currentCalendar)) {//둘의 날짜가 같으면 중복인것임...
            layout_checkin.setVisibility(View.GONE);
            Toast.makeText(getContext(),"마지막 접속 하고 하루 안지났음!",Toast.LENGTH_LONG).show();
        }else{//두 값이 다르면 중복이 아님 -> 즉 출석체크 해줘야한다.
            //todo: 출첵 팝업창(dialog)띄어주고(완료), 파이어스토어 저장(완료)
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                public void run() { // 시간 지난 후 실행할 코딩
                    layout_checkin.setVisibility(View.VISIBLE);
                }
            }, 3000); // 0.3초후
            firestore.collection(FirebaseID.Attendance).document(email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // 컬렉션 내의 document에 접근
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> shot = document.getData();
                                    atCount = (long)shot.get(FirebaseID.attendanceCount);
                                    atCount += 1;

                                    //출석체크 +1 추가한 값 저장
                                   firestore.collection(FirebaseID.Attendance).document(email).update(FirebaseID.attendanceCount,atCount);
                                    Log.d("HomeFragment", "at count get 완료");
                                } else {
                                    Log.d("HomeFragment", "No such document");
                                }
                            } else {
                                Log.d("HomeFragment", "get failed with ", task.getException());
                            }
                        }
                    });

            Toast.makeText(getContext(), "출석체크 완료!", Toast.LENGTH_SHORT).show();
        }


        //sharedPreference에 마지막 접속시간 저장
        //commit해야지 sharedPreference에 저장 완료
        editor.putString(emailBeforeCalendar,currentCalendar);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}
