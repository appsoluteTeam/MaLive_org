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

    public static Stack<Fragment> fragmentStack;
    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private Fragment activeFragment;

    private Button btn_back;

    //활동으로 넘어가는 버튼
    private Button btn_myPost, btn_myComment,  btn_myScrape;

    private static String str_nickname;
    private static String email;

    private TextView myPost_count_top, myPost_count_bottom, myComment_count_top, myComment_count_bottom;
    private static int myPost_count = 0  , myComment_count = 0;

    public static RecyclerPostAdapter adapter;
    public static ArrayList<postItemListView> arrayList;
    public static ArrayList<postItemListView> arrayList2;

    //활동창 관련 SharedPreferences 변수

//    private SharedPreferences sharedPreferences;

//    private int myPost_count = 0;
//    private String  myPostCountName;
//
//    private int myComment_count = 0;
//    private String myCommentCountName;

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

            arrayList = new ArrayList<>();
            arrayList2 = new ArrayList<>();
            final String[] communityCategory = {"how_do","what_do","what_eat"};

            adapter = new RecyclerPostAdapter(arrayList);
            adapter = new RecyclerPostAdapter(arrayList2);
            arrayList.clear();
            arrayList2.clear();

            for(int i = 0; i < communityCategory.length; i++) {
                final String Category2 = communityCategory[i];
                final ArrayList<String> Title = new ArrayList<String>();

                // 내가 쓴 글 불러오기
                bringMyPost(Category2);

                // 댓글 단 글 불러오기
                bringMyCommentPost(Category2, Title);
            }
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

        Log.d("Check 2 Post", Integer.valueOf(myPost_count).toString());


//        각 값 setText
         myPost_count_top.setText(Integer.valueOf(myPost_count).toString());
         myPost_count_bottom.setText(Integer.valueOf(myPost_count).toString());
         myComment_count_top.setText(Integer.valueOf(myComment_count).toString());
         myComment_count_bottom.setText(Integer.valueOf(myComment_count).toString());

        Toast.makeText(this.getContext(), "닉네임 : "+ str_nickname, Toast.LENGTH_LONG).show();

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            Bundle bundle = new Bundle();

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
                        bundle.putString("Nickname", str_nickname);
                        activeMyPostFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.main_frame, activeMyPostFragment);
                        fragmentTransaction.commit();
                        break;
                    case R.id. btn_myComment:
                        Log.d("activeFragment go myPostFragment","myPost buttonclick");
                        activeMyCommentFragment activeMyCommentFragment = new activeMyCommentFragment();

                        // activeMyPostFragment로 데이터 넘기기
                        bundle.putString("Nickname", str_nickname);
                        activeMyCommentFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.main_frame, activeMyCommentFragment);
                        fragmentTransaction.commit();
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

    // 내가 쓴 글 불러오기
    public void bringMyPost(String Category2) {
        firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

//                            내가 쓴 글 불러오기
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {

                                    Map<String, Object> shot = snapshot.getData();

                                    String Category = String.valueOf(shot.get(FirebaseID.category));
                                    String Title = String.valueOf(shot.get(FirebaseID.title));
                                    String Content = String.valueOf(shot.get(FirebaseID.content));
                                    String Date = String.valueOf(shot.get(FirebaseID.commu_date));

                                    postItemListView data = new postItemListView(Category, Title, Content, Date);
                                    arrayList.add(data);
                                    myPost_count++;
                                    Log.d("Post", Integer.valueOf(myPost_count).toString());
                                }
                                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침

                            } else {
                                myPost_count = 0;
                                arrayList.clear();
                            }
//                            //todo: sharedPreference에 count 값 저장하기
//                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(myPostCountName, MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putInt("myPost_count", myPost_count);
//                            editor.commit();
                        }
                    }
                });
    }

    // 댓글 단 글 불러오기
    public void bringMyCommentPost(final String Category2, final ArrayList<String> Title) {
        firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 댓글 단 글 받아오기
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    Title.add(String.valueOf(shot.get(FirebaseID.title)));
                                }
                                for (int j = 0; j < Title.size(); j++) {
                                    final String Title2 = Title.get(j);
                                    firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community").document(Title2).collection("Community_comment")
                                            .whereEqualTo("email", email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            for (DocumentSnapshot snapshot : task.getResult()) {
                                                                Map<String, Object> shot2 = snapshot.getData();
                                                                final String Content = String.valueOf(shot2.get(FirebaseID.content));
                                                                final String Date = String.valueOf(shot2.get(FirebaseID.commu_date));
                                                                postItemListView data = new postItemListView(Category2, Title2, Content, Date);
                                                                arrayList2.add(data);
                                                                myComment_count++;
                                                                Log.d("Comment", Integer.valueOf(myComment_count).toString());
                                                            }
                                                        } else {
                                                            myComment_count = 0;
                                                            arrayList2.clear();
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }


}