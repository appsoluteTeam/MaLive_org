package com.abbsolute.ma_livu.Alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class AlarmFragment extends Fragment {

    private View view;
    private AlarmPrevNotificationListAdapter alarmPrevNotificationListAdapter;
    private RecyclerView prevNotificationListView;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String nickName = "";
    private ArrayList<PrevNotificationInfo> prevNotificationInfos = new ArrayList<>();

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    TextView allLook;
    String email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

        prevNotificationListView = view.findViewById(R.id.prev_notification_list);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = user.getEmail();

        alarmPrevNotificationListAdapter = new AlarmPrevNotificationListAdapter(prevNotificationInfos);
        //닉네임찾기
        getNickName();

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        prevNotificationListView.setLayoutManager(layoutManager2);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");

        //내 댓글 가져오기
        getCommentInfo();

  //      ((HomeActivity)getActivity()).setOnBackPressedListener(this);

        alarmPrevNotificationListAdapter.setOnItemClickListener(new AlarmPrevNotificationListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                PrevNotificationInfo item = alarmPrevNotificationListAdapter.getItem(position);

                Log.d("category",item.getPost_title());
                // CommunityPostsFragment로 데이터 넘기기
                Bundle bundle = new Bundle();
                bundle.putString("Category", item.getPost_category());
                bundle.putString("Title", item.getPost_title());
                bundle.putString("Writer", item.getPost_writer());
                bundle.putString("Content", item.getPost_content());
                bundle.putString("Date", item.getPost_date());

                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityPostsFragment communityPostsFragment = new CommunityPostsFragment();
                communityPostsFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                fragmentTransaction.replace(R.id.main_frame, communityPostsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    //닉네임 얻기
    public void getNickName(){
        firestore.collection(FirebaseID.user).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                final DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    Map<String, Object> data = snapshot.getData();
                                    if (data.containsKey("nickname")) {
                                        nickName = (String) snapshot.getData().get("nickname");
                                    } else {
                                    }
                                }
                            }
                        }
                    }
                });
    }//getNickName()


    public void getCommentInfo() {
        final ArrayList<String> titleNameList = new ArrayList<>();
        final String[] category = {"what_eat", "what_do", "how_do"};
        for (int i = 0; i < 3; i++) {
            final int index = i;
            firestore.collection(FirebaseID.Community).document(category[index]).
                    collection("sub_Community")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {
                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                        Map<String, Object> data = snapshot.getData();
                                        final String title = String.valueOf(data.get("title"));
                                        final String Title = String.valueOf(snapshot.get(FirebaseID.title));
                                        final String Content = String.valueOf(snapshot.get(FirebaseID.content));
                                        final String Date = String.valueOf(snapshot.get(FirebaseID.commu_date));
                                        final String Writer = String.valueOf(snapshot.get(FirebaseID.Nickname));

                                        String myNickName = String.valueOf(data.get("nickname"));
                                        Log.d("nickName", myNickName);
                                        Log.d("my", nickName);
                                        if (nickName.equals(myNickName)) {
                                            Log.d("title!!!", title);
                                            firestore.collection(FirebaseID.Community).document(category[index])
                                                    .collection("sub_Community")
                                                    .document(title)
                                                    .collection(FirebaseID.Community_Comment)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                if (task.getResult() != null) {
                                                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                                                        if (snapshot.exists()) {
                                                                            HashSet<PrevNotificationInfo> set = new HashSet<PrevNotificationInfo>(prevNotificationInfos);
                                                                            ArrayList<PrevNotificationInfo> newArrays = new ArrayList<>(set);
                                                                            Map<String, Object> data = snapshot.getData();
                                                                            String commentDate = "";
                                                                            String content = "";
                                                                            if (data.containsKey(FirebaseID.commu_comment_date)) {
                                                                                commentDate = String.valueOf(data.get(FirebaseID.commu_comment_date));
                                                                            }
                                                                            if (data.containsKey(FirebaseID.commu_comment_comment)) {
                                                                                content = String.valueOf(data.get(FirebaseID.commu_comment_comment));
                                                                            }
                                                                            SimpleDateFormat formatter = null;
                                                                            Date date = null;
                                                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
                                                                                try {
                                                                                    date = formatter.parse(commentDate);
                                                                                } catch (ParseException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                                PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                                                                if (date != null) {
                                                                                    Log.d("OK!!", "Okay!!!~~");
                                                                                    String res = prevTimeSetClass.formatTimeString(date) + " | " + content;
                                                                                    String responseText = "내 글에 댓글이 달렸어요";


                                                                                    Log.d("title,content",Title +  Content);
                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                            responseText, res, category[index],Title, Content, Date, Writer );
                                                                                    //newArrays.add(prevNotificationInfo);
                                                                                    alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                                                                    //alarmPrevNotificationListAdapter.setItem(newArrays);
                                                                                    alarmPrevNotificationListAdapter.notifyDataSetChanged();
                                                                                    prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                                                                }

                                                                            }
                                                                        }


                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });//댓글
                                            //대댓글(댓글->댓글)
                                            firestore.collection(FirebaseID.Community).document(category[index])
                                                    .collection("sub_Community")
                                                    .document(title)
                                                    .collection(FirebaseID.Community_Comment)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                if (task.getResult() != null) {
                                                                    for (DocumentSnapshot newDocumentSnapShot : task.getResult()) {
                                                                        if (newDocumentSnapShot.exists()) {
                                                                            Map<String, Object> data = newDocumentSnapShot.getData();
                                                                            String commentComment = String.valueOf(data.get(FirebaseID.commu_comment_comment));
                                                                            firestore.collection(FirebaseID.Community).document(category[index])
                                                                                    .collection("sub_Community")
                                                                                    .document(title)
                                                                                    .collection(FirebaseID.Community_Comment)
                                                                                    .document(commentComment)
                                                                                    .collection(FirebaseID.Community_Comment_Comment)
                                                                                    .get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                if (task.getResult() != null) {
                                                                                                    for (DocumentSnapshot snapshot1 : task.getResult()) {
                                                                                                        if (snapshot1.exists()) {
                                                                                                            Map<String, Object> newData = snapshot1.getData();
                                                                                                            String CommentCommentDate = "";
                                                                                                            String content = "";
                                                                                                            if (newData.containsKey(FirebaseID.commu_comment_comment_date)) {
                                                                                                                CommentCommentDate = String.valueOf(newData.get(FirebaseID.commu_comment_comment_date));
                                                                                                            }
                                                                                                            if (newData.containsKey(FirebaseID.commu_comment_comment_comment)) {
                                                                                                                content = String.valueOf(newData.get(FirebaseID.commu_comment_comment_comment));
                                                                                                            }
                                                                                                            HashSet<PrevNotificationInfo> set = new HashSet<PrevNotificationInfo>(prevNotificationInfos);
                                                                                                            ArrayList<PrevNotificationInfo> newArrays = new ArrayList<>(set);
                                                                                                            SimpleDateFormat formatter = null;
                                                                                                            Date date = null;
                                                                                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                                                                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
                                                                                                                try {
                                                                                                                    date = formatter.parse(CommentCommentDate);
                                                                                                                } catch (ParseException e) {
                                                                                                                    e.printStackTrace();
                                                                                                                }
                                                                                                                PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                                                                                                if (date != null) {

                                                                                                                    String res = prevTimeSetClass.formatTimeString(date) + " | " + content;
                                                                                                                    String responseText = "내 글에 대댓글이 달렸어요";

                                                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                                                            responseText, res, category[index], Title, Content, Date, Writer);
                                                                                                                    //newArrays.add(prevNotificationInfo);
                                                                                                                    alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                                                                                                    //alarmPrevNotificationListAdapter.setItem(newArrays);
                                                                                                                    alarmPrevNotificationListAdapter.notifyDataSetChanged();
//                                                                                                                    prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                                                                                                }

                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    });//대댓글의 정보찾기
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });//댓글에 댓글==대댓글
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
        prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
    }

    public void onStart() {
        super.onStart();

        prevNotificationInfos.clear();
    }

}
