package com.abbsolute.ma_livu.MyPage;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.content.Context.MODE_PRIVATE;

/* 활동 창 fragment */

public class activeFragment extends Fragment {
    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static Stack<Fragment> fragmentStack;
    private static String email;

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private Fragment activeFragment;

    private Button btn_back;


    public activeFragment(){}

    public activeFragment(String email){
        this.email = email;
    }


    //활동으로 넘어가는 버튼
    private Button btn_myPost, btn_myComment,  btn_myScrape;

    private static String str_nickname;
    private static String email;

    private TextView myPost_count_top, myPost_count_bottom;
    private String myPostCountName;
    private int myPost_count;

    public activeFragment() { }

    public activeFragment(String email) {
        this.email = email;
        Log.d("email",email);

        /*user firestore에서 닉네임 정보 가져오기 */
        firestore.collection(FirebaseID.user).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                str_nickname  = shot.get(FirebaseID.Nickname).toString();
                                Log.d("activeFragment", "user nickname Get 완료");

                            } else {
                                Log.d("activeFragment", "No such document");
                            }
                        } else {
                            Log.d("activeFragment", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

          myPostCountName = email + "-myPostCountFile";

           SharedPreferences sharedPreferences = getActivity().getSharedPreferences(myPostCountName,MODE_PRIVATE);
           myPost_count = sharedPreferences.getInt("myPost_count",0);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mypage_active, container, false);

        btn_back = view.findViewById(R.id.btn_back);
        activeFragment = new activeFragment();

        btn_myPost = view.findViewById(R.id.btn_myPost);
        btn_myComment = view.findViewById(R.id.btn_myComment);
        btn_myScrape = view.findViewById(R.id.btn_myScrape);

        fragmentStack = HomeActivity.fragmentStack;
        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

         myPost_count_top = view.findViewById(R.id.myPost_count_top);
         myPost_count_bottom = view.findViewById(R.id.myPost_count_bottom);

//        각 값 setText
         myPost_count_top.setText(Integer.valueOf(myPost_count).toString());
         myPost_count_bottom.setText(Integer.valueOf(myPost_count).toString());

        Toast.makeText(this.getContext(), "닉네임 : "+ str_nickname, Toast.LENGTH_LONG).show();

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
//                fragmentTransaction = fm.beginTransaction();
                switch (v.getId()) {
                    case R.id.btn_back:
                        Log.d("activeFragment back","back buttonclick");
                        Fragment nextFragment = fragmentStack.pop();
                        fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
                        break;

                    case R.id. btn_myPost:
                        Log.d("activeFragment go myPostFragment","myPost buttonclick");
                        activeMyPostFragment activeMyPostFragment = new activeMyPostFragment();

                        // activeMyPostFragment로 데이터 넘기기
                        Bundle bundle = new Bundle();
                        bundle.putString("Nickname", str_nickname);
                        activeMyPostFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.main_frame, activeMyPostFragment);
                        fragmentTransaction.commit();
                        break;
                    case R.id. btn_myComment:
                        break;
                    case R.id. btn_myScrape:
                        break;

                }
            }
        };

        /* 각 버튼 setOnClickListener해주기 */
        btn_back.setOnClickListener(onClickListener);
        btn_myPost.setOnClickListener(onClickListener);
        btn_myComment.setOnClickListener(onClickListener);
        btn_myScrape.setOnClickListener(onClickListener);

        return view;
    }


}