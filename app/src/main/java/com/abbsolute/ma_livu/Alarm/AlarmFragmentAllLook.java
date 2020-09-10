package com.abbsolute.ma_livu.Alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AlarmFragmentAllLook extends Fragment {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String nickName = "";
    private RecyclerView prevNotificationListView;
    private AlarmPrevNotificationListAdapter alarmPrevNotificationListAdapter;
    private ArrayList<PrevNotificationInfo> prevNotificationInfos = new ArrayList<>();
    private ArrayList<String> dDayList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.full_prevnotification_layout, container, false);
        //goBack Button누르면 뒤로
        Button goBack=view.findViewById(R.id.btn_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).setFragment(201);
            }
        });
        alarmPrevNotificationListAdapter = new AlarmPrevNotificationListAdapter();
        prevNotificationListView = view.findViewById(R.id.prev_notification_full_list);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String email = user.getEmail();
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
                                        firestore.collection(FirebaseID.alarm_fragment).document(email)
                                                .collection("friend")
                                                .document(nickName)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult() != null) {
                                                                DocumentSnapshot snapshot1 = task.getResult();
                                                                if (!snapshot1.exists()) {
                                                                    String requestMessage = nickName + "님이 친구요청을 보냈습니다.";
                                                                    //todo 마이페이지에서 친구요청 구현하면 시간데이터 넣기
                                                                    AlarmFriendRequestInfo friendRequestInfo =
                                                                            new AlarmFriendRequestInfo(R.drawable.user1,
                                                                                    requestMessage, "4시간전");
                                                                    //alarmFriendRequestInfoArrayList.add(friendRequestInfo);
                                                                    //  alarmFriendRequestListAdapter.setItem(alarmFriendRequestInfoArrayList, nickName);
                                                                    //  alarmFriendRequestListAdapter.notifyDataSetChanged();
                                                                    // friendRequestListView.setHasFixedSize(true);
                                                                    // friendRequestListView.setAdapter(alarmFriendRequestListAdapter);
                                                                }
                                                            }
                                                        }
                                                    }
                                                });

                                    } else {

                                    }
                                }
                            }
                        }
                    }
                });
        ///이전알림 시작
        //투두리스트 정보 가져오기
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        prevNotificationListView.setLayoutManager(layoutManager2);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");
        firestore.collection(FirebaseID.ToDoLists).document(id)
                .collection("ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                prevNotificationInfos.clear();
                                //prevNotificationInfos=new ArrayList<>(set);
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> data = snapshot.getData();
                                    String dDay = String.valueOf(data.get("dDay"));
                                    String content = String.valueOf(data.get("content"));
                                    dDayList.add(dDay);
                                    contentList.add(content);
                                }
                                long systemTime = System.currentTimeMillis();
                                SimpleDateFormat formatter = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                }
                                String date = formatter.format(systemTime);
                                String[] splitData = date.split("-");
                                String tmp1 = splitData[0];
                                String tmp2 = splitData[1];
                                String tmp3 = splitData[2];
                                String today = tmp1 + "년" + tmp2 + "월" + tmp3 + "일";
                                for (int i = 0; i < dDayList.size(); i++) {
                                    if (i == 4)
                                        break;
                                    String cmp = dDayList.get(i);
                                    String inputContent = contentList.get(i);
                                    if (cmp.compareTo(today) < 0) {
                                        PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                        SimpleDateFormat simpleDateFormat = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            simpleDateFormat = new SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA);
                                        }
                                        Date parseDate = null;
                                        String res = "";
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            try {
                                                parseDate = simpleDateFormat.parse(cmp);
                                                res = prevTimeSetClass.formatTimeString(parseDate);//~전 으로 변경
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
                                        String notifiText = inputContent + " 지정일이 지났어요";
                                        PrevNotificationInfo prevNotificationInfo =
                                                new PrevNotificationInfo(R.drawable.prev_todo,
                                                        notifiText, res);
                                        //prevNotificationInfos.add(prevNotificationInfo);
                                        //alarmPrevNotificationListAdapter.setItem(prevNotificationInfos);
                                        alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                        prevNotificationListView.setHasFixedSize(true);
                                        prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                    }
                                }
                            }
                        }
                    }
                });
        //내 댓글 가져오기(뭐먹지)
        final ArrayList<String> titleNameList = new ArrayList<>();
        firestore.collection(FirebaseID.Community).document("what_eat").
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
                                    String myNickName = String.valueOf(data.get("nickname"));
                                    Log.d("nickName",myNickName);
                                    Log.d("my",nickName);
                                    if (nickName.equals(myNickName)) {
                                        Log.d("title!!!", title);
                                        firestore.collection(FirebaseID.Community).document("what_eat")
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
                                                                        if(data.containsKey(FirebaseID.commu_comment_date)){
                                                                            String commentDate = String.valueOf(data.get(FirebaseID.commu_comment_date));
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
                                                                                if(date!=null){
                                                                                    String res = prevTimeSetClass.formatTimeString(date);
                                                                                    String responseText = "내 글에 댓글이 달렸어요";
                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                            responseText, res);
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
                                                    }
                                                });//댓글
                                        //대댓글(댓글->댓글)
                                        firestore.collection(FirebaseID.Community).document("what_eat")
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
                                                                        firestore.collection(FirebaseID.Community).document("what_eat")
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
                                                                                                        if(newData.containsKey(FirebaseID.commu_comment_comment_date)){
                                                                                                            String CommentCommentDate = String.valueOf(newData.get(FirebaseID.commu_comment_comment_date));
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
                                                                                                                if(date!=null){
                                                                                                                    String res = prevTimeSetClass.formatTimeString(date);
                                                                                                                    String responseText = "내 글에 댓글이 달렸어요";
                                                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                                                            responseText, res);
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
        //내 댓글 가져오기(뭐하지)
        firestore.collection(FirebaseID.Community).document("what_do").
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
                                    String myNickName = String.valueOf(data.get("nickname"));
                                    if (nickName.equals(myNickName)) {
                                        Log.d("title!!!", title);
                                        firestore.collection(FirebaseID.Community).document("what_do")
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
                                                                        if(data.containsKey(FirebaseID.commu_comment_date)){
                                                                            String commentDate = String.valueOf(data.get(FirebaseID.commu_comment_date));
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
                                                                                if(date!=null){
                                                                                    String res = prevTimeSetClass.formatTimeString(date);
                                                                                    String responseText = "내 글에 댓글이 달렸어요";
                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                            responseText, res);
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
                                                    }
                                                });//댓글
                                        //대댓글(댓글->댓글)
                                        firestore.collection(FirebaseID.Community).document("what_do")
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
                                                                        firestore.collection(FirebaseID.Community).document("what_do")
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
                                                                                                        if(newData.containsKey(FirebaseID.commu_comment_comment_date)){
                                                                                                            String CommentCommentDate = String.valueOf(newData.get(FirebaseID.commu_comment_comment_date));
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
                                                                                                                if(date!=null){
                                                                                                                    String res = prevTimeSetClass.formatTimeString(date);
                                                                                                                    String responseText = "내 글에 댓글이 달렸어요";
                                                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                                                            responseText, res);
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
        //내 댓글 가져오기(어떻게 하지?)
        firestore.collection(FirebaseID.Community).document("how_do").
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
                                    String myNickName = String.valueOf(data.get("nickname"));
                                    if (nickName.equals(myNickName)) {
                                        Log.d("title!!!", title);
                                        firestore.collection(FirebaseID.Community).document("how_do")
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
                                                                        if(data.containsKey(FirebaseID.commu_comment_date)){
                                                                            String commentDate = String.valueOf(data.get(FirebaseID.commu_comment_date));
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
                                                                                if(date!=null){
                                                                                    String res = prevTimeSetClass.formatTimeString(date);
                                                                                    String responseText = "내 글에 댓글이 달렸어요";
                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                            responseText, res);
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
                                                    }
                                                });//댓글
                                        //대댓글(댓글->댓글)
                                        firestore.collection(FirebaseID.Community).document("how_do")
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
                                                                        firestore.collection(FirebaseID.Community).document("how_do")
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
                                                                                                        if(newData.containsKey(FirebaseID.commu_comment_comment_date)){
                                                                                                            String CommentCommentDate = String.valueOf(newData.get(FirebaseID.commu_comment_comment_date));
                                                                                                            HashSet<PrevNotificationInfo> set = new HashSet<PrevNotificationInfo>(prevNotificationInfos);
                                                                                                            ArrayList<PrevNotificationInfo> newArrays = new ArrayList<>(set);
                                                                                                            SimpleDateFormat formatter = null;
                                                                                                            Date date = null;
                                                                                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                                                                                                try {
                                                                                                                    date = formatter.parse(CommentCommentDate);
                                                                                                                } catch (ParseException e) {
                                                                                                                    e.printStackTrace();
                                                                                                                }
                                                                                                                PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                                                                                                if(date!=null){
                                                                                                                    String res = prevTimeSetClass.formatTimeString(date);
                                                                                                                    String responseText = "내 글에 댓글이 달렸어요";
                                                                                                                    PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                                                            responseText, res);
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
        ///이전알림 끝
        return view;
    }
}