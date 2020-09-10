package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.MyPage.AboutFriends.FriendListFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

import static android.content.Context.MODE_PRIVATE;

/* 마이페이지 메인 fragment */

public class MyPageFragment extends Fragment implements View.OnClickListener{
    private View view;

    //reCyclerView 관련 변수
    private static RecyclerPostAdapter mAdapter = null;

    //활동창 관련 변수
    private int myPost_count = 0;
    private String PostCountName;

    private MyPageDataListener dataListener;
    private Button  btnMyPage_informationSet,btnMyPage_title,btnMyPage_pay,btnMyPage_active,btnMyPage_friend;
    private TextView nickname,textView_email;
    private ProgressBar clean_progressBar,wash_progressBar,trash_progressBar,todo_progressBar;
    private TextView clean_percent, wash_percent, trash_percent, todo_percent;

    /*파이어베이스 변수*/
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference myPageRef;
    private static String email;
    private static String str_nickname;
    private static long clean_complete, trash_complete, todo_complete, wash_complete;

    public static RecyclerPostAdapter adapter;
    public static ArrayList<postItemListView> arrayList;
    private int myPost_count;
    private String  myPostCountName;


    public MyPageFragment(){};

    /*login2Activity에서 데이터 받음*/
    public MyPageFragment(String email){
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

        /*todo 각 횟수 가져오기*/
        firestore.collection(FirebaseID.ToDoLists).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                clean_complete = (long) shot.get(FirebaseID.clean_complete);
                                wash_complete = (long) shot.get(FirebaseID.wash_complete);
                                trash_complete = (long) shot.get(FirebaseID.trash_complete);
                                todo_complete = (long) shot.get(FirebaseID.todo_complete);

                                Log.d("MyPageFragment", "todo 가져오기 완료");

                            } else {
                                clean_complete = 0;
                                wash_complete = 0;
                                trash_complete = 0;
                                todo_complete = 0;
                                Log.d("MyPageFragment", "No such document");
                            }
                        } else {
                            Log.d("MyPageFragment", "get failed with ", task.getException());
                        }
                    }
                });
        /*대표칭호 정보 myPage firestore에서 가져와서 category,index 변수에 저장*/
        //TODO: 데이터 가져오는걸 onCreateView나 onCreate에서 하면 적용이 다른 함수들보다 느리게 됨,,,,,,왜그래...

    }

    /*HomeActivity에 데이터 보내기 위해 필요한 코드*/
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof MyPageDataListener){
            dataListener = (MyPageDataListener) context;
        }else{
            throw new RuntimeException(context.toString() + "must implement DataListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = new ArrayList<>();
        String[] communityCategory = {"how_do","what_do","what_eat"};
        myPostCountName = email + "-myPostCountFile";
        myPost_count = 0;
        adapter = new RecyclerPostAdapter(arrayList);

        arrayList.clear();


        for(int i = 0; i < communityCategory.length; i++) {
            firestore.collection(FirebaseID.Community).document(communityCategory[i]).collection("sub_Community")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {
//                                    arrayList.clear();
                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                        String kor_category = "";

                                        Map<String, Object> shot = snapshot.getData();

                                        String Category = String.valueOf(shot.get(FirebaseID.category));
                                        String Title = String.valueOf(shot.get(FirebaseID.title));
                                        String Content = String.valueOf(shot.get(FirebaseID.content));
                                        String Date = String.valueOf(shot.get(FirebaseID.commu_date));

//                                        Log.d("category and title", category + title);

                                        postItemListView data = new postItemListView(Category, Title, Content, Date);
                                        arrayList.add(data);
                                        myPost_count++;
                                    }
                                    adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침

                                } else {
                                    myPost_count = 0;
                                    arrayList.clear();
                                }
                                //todo: sharedPreference에 count 값 저장하기
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(myPostCountName, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putInt("myPost_count", myPost_count);
                                editor.commit();
                            }
                        }
                    });
        }


    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* myPage fragment 처음 */
        view = inflater.inflate(R.layout.fragment_mypage,container,false);

        Log.d("Mypage-Email",email);

        /* 정보설정*/
        btnMyPage_informationSet = view.findViewById(R.id.btnMyPage_informationSet);

        /* 칭호, 결제, 활동, 친구 아이디값 찾기 */
        btnMyPage_title = view.findViewById(R.id.btnMyPage_title);
        btnMyPage_pay = view.findViewById(R.id.btnMyPage_pay);
        btnMyPage_active = view.findViewById(R.id.btnMyPage_active);
        btnMyPage_friend = view.findViewById(R.id.btnMyPage_friend);
        //btnMyPage_friend 클릭시 친구목록으로 간다
        btnMyPage_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                FriendListFragment friendListFragment=new FriendListFragment();
                fragmentTransaction.replace(R.id.main_frame,friendListFragment);
                fragmentTransaction.commit();

            }
        });
        //

        /*대표칭호,email findViewByID*/
        nickname = view.findViewById(R.id.nickname);
        textView_email = view.findViewById(R.id.email);

        /*progressBar,% 아이디값 찾기*/
        clean_progressBar = view.findViewById(R.id.clean_progressBar);
        wash_progressBar = view.findViewById(R.id.wash_progressBar);
        trash_progressBar = view.findViewById(R.id.trash_progressBar);
        todo_progressBar = view.findViewById(R.id.todo_progressBar);

        clean_percent = view.findViewById(R.id.clean_percent);
        wash_percent = view.findViewById(R.id.wash_percent);
        trash_percent = view.findViewById(R.id.trash_percent);
        todo_percent = view.findViewById(R.id.todo_percent);

        /*닉네임 ,email 설정*/
        nickname.setText(str_nickname);
        textView_email.setText(email);

        /*progressBar,% (달성률) 설정*/
        /*todo : 한달 기간으로 초기화해야한다*/
        //setProgree반응왤케느림;
        int cleanPercent = (int)clean_complete%100;
        clean_progressBar.setProgress(cleanPercent);
        wash_progressBar.setProgress((int)wash_complete%100);
        trash_progressBar.setProgress((int)trash_complete%100);
        todo_progressBar.setProgress((int)todo_complete%100);

        clean_percent.setText(String.valueOf(clean_complete%100));
        wash_percent.setText(String.valueOf(wash_complete%100));
        trash_percent.setText(String.valueOf(trash_complete%100));
        todo_percent.setText(String.valueOf(todo_complete%100));

        return view;
    }

    public void onClick(View view){
        /* HomeActivity에 어떤 카테고리를 눌렀는지 데이터 전달 */
        /* 0:칭호 , 1:결제, 2:활동 , 3:친구, 4:정보설정 */
        switch (view.getId()){
            case R.id.btnMyPage_title://칭호
                dataListener.myPageDataSet(0);
                break;
            case R.id.btnMyPage_pay://결제
                dataListener.myPageDataSet(1);
                break;
            case R.id.btnMyPage_active://활동
                dataListener.myPageDataSet(2);
                break;
            case R.id.btnMyPage_friend://친구
                dataListener.myPageDataSet(3);
                break;
            case R.id.btnMyPage_informationSet://정보 설정
                dataListener.myPageDataSet(4);
                break;
        }
    }
}
