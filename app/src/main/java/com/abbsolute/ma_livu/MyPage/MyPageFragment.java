package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
//import com.abbsolute.ma_livu.MyPage.AboutFriends.FriendListFragment;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoFragment_final;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Stack;

/* 마이페이지 메인 fragment */

public class MyPageFragment extends Fragment implements View.OnClickListener {
    private View view;

    //reCyclerView 관련 변수
    private static RecyclerPostAdapter mAdapter = null;

    private MyPageDataListener dataListener;
    private Button btn_back, btnMyPage_informationSet, btnMyPage_title, btnMyPage_pay, btnMyPage_active, todo;
    private TextView nickname, textView_email;
    private ProgressBar clean_progressBar, wash_progressBar, trash_progressBar, etc_progressBar;
    private TextView clean_percent, wash_percent, trash_percent, etc_percent;

    /*파이어베이스 변수*/
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference myPageRef;
    private static String email;
    private static String str_nickname;
    private static long clean_complete, trash_complete, todo_complete, wash_complete,etc_complete;


    public static RecyclerPostAdapter adapter;
    public static ArrayList<postItemListView> arrayList, arrayList2, arrayList3;
    private static int myPost_count = 0, myComment_count = 0, mySavedPosts_count = 0;


    public MyPageFragment() {
    }

    ;

    public MyPageFragment(String email){
        MyPageFragment.email = email;
        Log.d("email",email);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Calendar calendar = Calendar.getInstance();

        String format_time = format1.format(calendar.getTime());

        String year_month = format_time.substring(0, 7);

        Log.d("year_month", year_month);

//        calendar.get(Calendar.YEAR);
//        calendar.get(Calendar.MONTH);


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
                                str_nickname = shot.get(FirebaseID.Nickname).toString();
                                Log.d("MyPageFragment", "user nickname Get 완료");

                            } else {
                                str_nickname = "";
                                Log.d("MyPageFragment", "No such document");
                            }
                        } else {
                            Log.d("MyPageFragment", "get failed with ", task.getException());
                        }
                    }
                });

        /*이번 달 todo 각 횟수 가져오기*/
        //todo:로그인할때만 받아오니까 수정필요 메인에서 받아오든가 해야함..
        firestore.collection(FirebaseID.ToDoLists).document(email).collection("EveryMonth").document(year_month)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                /*
                                editor.putLong(emailCleanComplete,(long)shot.get(FirebaseID.clean_complete));
                                editor.putLong(emailWashComplete,(long)shot.get(FirebaseID.wash_complete));
                                editor.putLong(emailTrashComplete,(long)shot.get(FirebaseID.trash_complete));
                                editor.putLong(emailTodoComplete,(long)shot.get(FirebaseID.todo_complete));
                                editor.commit();
                                */

                                if (shot.get(FirebaseID.clean_complete) == null) {
                                    clean_complete = 0;
                                } else {
                                    clean_complete = (long) shot.get(FirebaseID.clean_complete);

                                }

                                if (shot.get(FirebaseID.trash_complete) == null) {
                                    trash_complete = 0;
                                } else {
                                    trash_complete = (long) shot.get(FirebaseID.trash_complete);

                                }

                                if (shot.get(FirebaseID.wash_complete) == null) {
                                    wash_complete = 0;
                                } else {
                                    wash_complete = (long) shot.get(FirebaseID.wash_complete);

                                }

                                if(shot.get("기타complete")== null){
                                    etc_complete = 0;
                                }else{
                                    etc_complete = (long) shot.get("기타complete");

                                }


                                todo_complete = wash_complete + clean_complete + trash_complete + etc_complete;
                                Log.d("MyPageFragment", "todo 가져오기 완료");
                                Log.d("washComplte", Long.valueOf(wash_complete).toString());
                            } else {
                                clean_complete = 0;
                                wash_complete = 0;
                                trash_complete = 0;
                                todo_complete = 0;
                                etc_complete = 0;

                                Log.d("myPageFragment", "No such document");
                            }
                        } else {
                            Log.d("myPageFragment", "get failed with ", task.getException());
                        }
                    }
                });

    }

    /*HomeActivity에 데이터 보내기 위해 필요한 코드*/
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyPageDataListener) {
            dataListener = (MyPageDataListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement DataListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // count값 초기화
        myPost_count = 0;
        myComment_count = 0;
        mySavedPosts_count = 0;

        // MyPage -> 활동 데이터 불러오기
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        final String[] communityCategory = {"how_do","what_do","what_eat"};

        arrayList.clear();
        arrayList2.clear();
        arrayList3.clear();

        for(int i = 0; i < communityCategory.length; i++) {
            final String CategoryAll = communityCategory[i];
//            final ArrayList<String> Title = new ArrayList<String>();

            // 내가 쓴 글 불러오기
            bringMyPost(CategoryAll);

            // 댓글 단 글 불러오기
            bringMyCommentPost(CategoryAll);

        }
        // 저장한 글 불러오기
        bringMySavedPosts();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* myPage fragment 처음 */
        view = inflater.inflate(R.layout.fragment_mypage, container, false);

        /* 정보설정*/
        btnMyPage_informationSet = view.findViewById(R.id.btnMyPage_informationSet);

        todo = view.findViewById(R.id.todo_mypage);

        /* 칭호, 결제, 활동, 친구 아이디값 찾기 */
        btnMyPage_title = view.findViewById(R.id.btnMyPage_title);
        btnMyPage_pay = view.findViewById(R.id.btnMyPage_pay);
        btnMyPage_active = view.findViewById(R.id.btnMyPage_active);


        /*대표칭호,email findViewByID*/
        nickname = view.findViewById(R.id.nickname);
        textView_email = view.findViewById(R.id.email);

        /*progressBar,% 아이디값 찾기*/
        clean_progressBar = view.findViewById(R.id.clean_progressBar);
        wash_progressBar = view.findViewById(R.id.wash_progressBar);
        trash_progressBar = view.findViewById(R.id.trash_progressBar);
        etc_progressBar = view.findViewById(R.id.etc_progressBar);

        clean_percent = view.findViewById(R.id.clean_percent);
        wash_percent = view.findViewById(R.id.wash_percent);
        trash_percent = view.findViewById(R.id.trash_percent);
        etc_percent = view.findViewById(R.id.etc_percent);

        /*닉네임 ,email 설정*/
        nickname.setText(str_nickname);
        textView_email.setText(email);

        /*progressBar,% (달성률) 설정*/
        //setProgree반응왤케느림;
        int cleanPercent = (int) clean_complete % 100;
        clean_progressBar.setProgress(cleanPercent);
        wash_progressBar.setProgress((int) wash_complete % 100);
        trash_progressBar.setProgress((int) trash_complete % 100);
        etc_progressBar.setProgress((int) etc_complete % 100);

        clean_percent.setText(String.valueOf(clean_complete % 100));
        wash_percent.setText(String.valueOf(wash_complete % 100));
        trash_percent.setText(String.valueOf(trash_complete % 100));
        etc_percent.setText(String.valueOf(etc_complete % 100));

        return view;
    }


    public void onClick(View view) {
        /* HomeActivity에 어떤 카테고리를 눌렀는지 데이터 전달 */
        /* 0:칭호 , 1:결제, 2:활동 , 3:친구, 4:정보설정 */
        switch (view.getId()) {
            case R.id.btnMyPage_title://칭호
                dataListener.myPageDataSet(0);
                break;
            case R.id.btnMyPage_pay://결제
                dataListener.myPageDataSet(1);
                break;
            case R.id.btnMyPage_informationSet://정보 설정
                dataListener.myPageDataSet(4);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // MyPage -> 활동
        btnMyPage_active.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        activeFragment activeFragment = new activeFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("nickname", str_nickname);
                        bundle.putString("MyPost_count", String.valueOf(myPost_count));
                        bundle.putString("MyComment_count", String.valueOf(myComment_count));
                        bundle.putString("MySavedPosts_count", String.valueOf(mySavedPosts_count));

                        activeFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.main_frame, activeFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
            }
        });

        //임시로 만든 투두 버튼
        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToDoFragment_final toDoFragment_final = new ToDoFragment_final();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, toDoFragment_final).commit();            }
        });

    }

    // 내가 쓴 글 불러오기
    public void bringMyPost(String CategoryAll) {
        firestore.collection(FirebaseID.Community).document(CategoryAll).collection("sub_Community")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        arrayList.clear();
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {

                                    Map<String, Object> shot = snapshot.getData();
                                    String Category = String.valueOf(shot.get(FirebaseID.category));
                                    String Title = String.valueOf(shot.get(FirebaseID.title));
                                    String Content = String.valueOf(shot.get(FirebaseID.content));
                                    String Date = String.valueOf(shot.get(FirebaseID.commu_date));

                                    postItemListView data = new postItemListView(Category, Title, Content, Date, str_nickname);
                                    arrayList.add(data);
                                    myPost_count++;
                                    Log.d("myPost count: ",String.valueOf(myPost_count));
                                }
                            } else {
                                myPost_count = 0;
                                arrayList.clear();
                            }
                        }
                    }
                });
    }

    // 댓글 단 글 불러오기
    public void bringMyCommentPost(final String CategoryAll) {
        firestore.collection(FirebaseID.Community).document(CategoryAll).collection("sub_Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 댓글 단 글 받아오기
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    final Map<String, Object> shot = snapshot.getData();
//                                  Title.add(String.valueOf(shot.get(FirebaseID.title)));
                                         String idGet = snapshot.getId();

                                    firestore.collection(FirebaseID.Community).document(CategoryAll).collection("sub_Community")
                                            .document(idGet).collection("Community_comment")
                                            .whereEqualTo("email", email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            for (DocumentSnapshot snapshot : task.getResult()) {
                                                                String Title = String.valueOf(shot.get(FirebaseID.title));
                                                                String Content = String.valueOf(shot.get(FirebaseID.content));
                                                                String Date = String.valueOf(shot.get(FirebaseID.commu_date));
                                                                String Writer = String.valueOf(shot.get(FirebaseID.Nickname));

                                                                postItemListView data = new postItemListView(CategoryAll, Title, Content, Date, Writer);
                                                                arrayList2.add(data);
                                                                myComment_count++;
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                    }
                                } else {
                                myComment_count = 0;
                                arrayList2.clear();
                            }
                        }
                    }
                });
    }

    public void bringMySavedPosts () {
        firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.savedPosts)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

//                     내가 저장한 글 불러오기
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {

                                    Map<String, Object> shot = snapshot.getData();
                                    String Category = String.valueOf(shot.get(FirebaseID.category));
                                    String Title = String.valueOf(shot.get(FirebaseID.title));
                                    String Content = String.valueOf(shot.get(FirebaseID.content));
                                    String Date = String.valueOf(shot.get(FirebaseID.commu_date));
                                    String Writer = String.valueOf(shot.get(FirebaseID.writer));

                                    postItemListView data = new postItemListView(Category, Title, Content, Date, Writer);
                                    arrayList3.add(data);
                                    mySavedPosts_count++;
                                    Log.d("mySavedPosts_count: ", String.valueOf(mySavedPosts_count));
                                }
                            } else {
                                mySavedPosts_count = 0;
                                arrayList3.clear();
                            }
                        }
                    }
                });
    }

}