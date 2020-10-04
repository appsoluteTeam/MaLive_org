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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private Fragment activeFragment;
    public static Stack<Fragment> fragmentStack;

    private Button btn_back;

    //활동으로 넘어가는 버튼
    private Button btn_myPost, btn_myComment,  btn_myScrape;

    private static String str_nickname;
    private static String email;

    private TextView myPost_count_top, myPost_count_bottom, myComment_count_top, myComment_count_bottom, myScrape_count_top, myScrape_count_bottom;
    private static String myPost_count , myComment_count, mySavedPosts_count;

    public static RecyclerPostAdapter adapter;
    public static ArrayList<postItemListView> arrayList;

    public activeFragment() { }

    public activeFragment(String email) {
        this.email = email;
        Log.d("email",email);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferences로 값 저장시키는 부분
//            myPostCountName = email + "-myPostCountFile";
//            myPost_count = 0;

//            myCommentCountName = email + "-myCommentCountFile";
//            myComment_count = 0;

        // SharedPreferences로 값 받아오는 부분 -> 값을 받아오는 프래그먼트에 정의하기
//          myPostCountName = email + "-myPostCountFile";
//          myCommentCountName = email + "-myCommentCountFile";
//
//           sharedPreferences = getActivity().getSharedPreferences(myPostCountName,MODE_PRIVATE);
//           myPost_count = sharedPreferences.getInt("myPost_count",0);
//
//           sharedPreferences = getActivity().getSharedPreferences(myCommentCountName,MODE_PRIVATE);
//           myComment_count = sharedPreferences.getInt("myComment_count",0);
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
         myComment_count_top = view.findViewById(R.id.myComment_count_top);
         myComment_count_bottom = view.findViewById(R.id.myComment_count_bottom);
         myScrape_count_top = view.findViewById(R.id.myScrape_count_top);
        myScrape_count_bottom = view.findViewById(R.id.myScrape_count_bottom);

        // MyPageFragment에서 값 받아오기
        if(getArguments() != null){
            str_nickname = getArguments().getString("nickname");
            myPost_count  = getArguments().getString("MyPost_count");
            myComment_count = getArguments().getString("MyComment_count");
            mySavedPosts_count = getArguments().getString("MySavedPosts_count");
        }

//        각 값 setText
        myPost_count_top.setText(myPost_count);
        myPost_count_bottom.setText(myPost_count);
        myComment_count_top.setText(myComment_count);
        myComment_count_bottom.setText(myComment_count);
        myScrape_count_top.setText(mySavedPosts_count);
        myScrape_count_bottom.setText(mySavedPosts_count);

        Toast.makeText(this.getContext(), "닉네임 : "+ str_nickname, Toast.LENGTH_LONG).show();

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            Bundle bundle = new Bundle();

            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                        Log.d("activeFragment back","back buttonclick");
//                        Fragment nextFragment = fragmentStack.pop();
//                        fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();

                        MyPageFragment myPageFragment = new MyPageFragment();
                        fragmentTransaction.replace(R.id.main_frame, myPageFragment).commit();
                        break;

                    case R.id. btn_myPost:
                        Log.d("activeFragment go myPostFragment","myPost buttonclick");
                        activeMyPostFragment activeMyPostFragment = new activeMyPostFragment();
//                        fragmentStack.push(activeMyPostFragment);

                        // activeMyPostFragment로 데이터 넘기기
                        bundle.putString("nickname", str_nickname);
                        activeMyPostFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.main_frame, activeMyPostFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;

                    case R.id. btn_myComment:
                        Log.d("activeFragment go myCommentFragment","myComment buttonclick");
                        activeMyCommentFragment activeMyCommentFragment = new activeMyCommentFragment();
//                        fragmentStack.push(activeMyCommentFragment);

                        fragmentTransaction.replace(R.id.main_frame, activeMyCommentFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case R.id. btn_myScrape:
                        Log.d("activeFragment go myMySavedPostsFragment","myMySavedPosts buttonclick");
                        activeMySavedPostsFragment activeMySavedPostsFragment = new activeMySavedPostsFragment();
//                        fragmentStack.push(activeMyCommentFragment);

                        fragmentTransaction.replace(R.id.main_frame, activeMySavedPostsFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
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