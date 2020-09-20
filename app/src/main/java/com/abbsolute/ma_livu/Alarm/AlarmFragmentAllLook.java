package com.abbsolute.ma_livu.Alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    String email = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_prevnotification_layout, container, false);
        //goBack Button누르면 뒤로
        Button goBack = view.findViewById(R.id.btn_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).setFragment(201);
            }
        });
        alarmPrevNotificationListAdapter = new AlarmPrevNotificationListAdapter();
        prevNotificationListView = view.findViewById(R.id.prev_notification_full_list);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = user.getEmail();
        //닉네임찾기
        getNickName();
        ///이전알림 시작
        //칭호얻기
        getTitle();
        //투두리스트 정보 가져오기
        getToDoListInfo();
        //내 댓글 가져오기(뭐먹지)
        getWhatEatInfo();
        //내 댓글 가져오기(뭐하지)
        getWhatDoInfo();
        //내 댓글 가져오기(어떻게 하지?)
        getHowDoInfo();

        ///이전알림 끝
        return view;
    }

    //닉네임 얻기
    public void getNickName() {
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
    }//getNickName()
    //칭호얻기
    //칭호얻기
    public void getTitle(){
        String userEmail=firebaseAuth.getCurrentUser().getEmail();
        firestore.collection(FirebaseID.ToDoLists).document(userEmail)
                .collection("total")
                .document("sub")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null){
                                DocumentSnapshot snapshot=task.getResult();
                                String content="";
                                Map<String,Object> data=snapshot.getData();
                                if(snapshot.exists()){
                                    content="빨래complete";
                                    if(data.containsKey(content)){
                                        long cnt=(long)data.get(content);
                                        if(cnt==5){//5회
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp)+" | "+" +3000톨";
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==10){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp)+" | "+" +3000톨";
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==30){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==50){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!"+" | "+" +3000톨";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }
                                    }//빨래complete 업그레이드
                                    content="쓰레기complete";
                                    if(data.containsKey(content)){
                                        long cnt=(long)data.get(content);
                                        if(cnt==5){//5회
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!"+" | "+" +3000톨";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==10){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==30){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp)+" | "+" +3000톨";
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==50){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp)+" | "+" +3000톨";
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }
                                    }//쓰레기complete 업그레이드
                                    content="청소complete";
                                    if(data.containsKey(content)){
                                        long cnt=(long)data.get(content);
                                        if(cnt==5){//5회
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==10){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==30){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }else if(cnt==50){
                                            PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                            long systemTime = System.currentTimeMillis();
                                            SimpleDateFormat formatter = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                            }
                                            String date = formatter.format(systemTime);
                                            Date tmp=null;
                                            try {
                                                tmp=formatter.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            date=prevTimeSetClass.formatTimeString(tmp);
                                            String notifiText="칭호 업그레이드 보상이 도착했어요!";
                                            PrevNotificationInfo prevNotificationInfo =
                                                    new PrevNotificationInfo(R.drawable.title_upgrade,
                                                            notifiText, date);
                                            alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                            prevNotificationListView.setHasFixedSize(true);
                                            prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                        }
                                    }//청소complete 업그레이드
                                }



                            }
                        }
                    }
                });

    }
    ///투두리스트 정보가져와서 이전알림
    public void getToDoListInfo() {
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
                                    String dDay = "";
                                    String content = "";
                                    if (data.containsKey("dDay")) {
                                        dDay = String.valueOf(data.get("dDay"));
                                    }
                                    if (data.containsKey("content")) {
                                        content = String.valueOf(data.get("content"));
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
                                    if (dDay.compareTo(today) < 0) {
                                        PrevTimeSetClass prevTimeSetClass = new PrevTimeSetClass();
                                        SimpleDateFormat simpleDateFormat = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            simpleDateFormat = new SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA);
                                        }
                                        Date parseDate = null;
                                        String res = "";
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            try {
                                                parseDate = simpleDateFormat.parse(dDay);
                                                res = prevTimeSetClass.formatTimeString(parseDate);//~전 으로 변경
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
                                        String notifiText = content + " 지정일이 지났어요";
                                        PrevNotificationInfo prevNotificationInfo =
                                                new PrevNotificationInfo(R.drawable.prev_todo,
                                                        notifiText, res);
                                        //prevNotificationInfos.add(prevNotificationInfo);
                                        //alarmPrevNotificationListAdapter.setItem(prevNotificationInfos);
                                        alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                        prevNotificationListView.setHasFixedSize(true);
                                        prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
                                    }
                                }//for문
                            }
                        }
                    }
                });
    }///getToDoListInfo()

    //뭐먹지 정보
    public void getWhatEatInfo() {
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
                                    Log.d("nickName", myNickName);
                                    Log.d("my", nickName);
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
                                                                                String res = prevTimeSetClass.formatTimeString(date);
                                                                                String responseText = "내 글에 댓글이 달렸어요" + "\n" + content;
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
                                                                                                                Log.d("OK!!", "Okay!!!~~");
                                                                                                                String res = prevTimeSetClass.formatTimeString(date);
                                                                                                                String responseText = "내 글에 대댓글이 달렸어요" + "\n" + content;
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
    }//

    //뭐하지 정보가져오기
    public void getWhatDoInfo() {
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
                                                                        if (data.containsKey(FirebaseID.commu_comment_date)) {
                                                                            String commentDate = String.valueOf(data.get(FirebaseID.commu_comment_date));
                                                                            String content = "";
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
                                                                                String res = "";
                                                                                if (date != null) {
                                                                                    Log.d("OK!!", "Okay!!!~~");
                                                                                    res = prevTimeSetClass.formatTimeString(date);
                                                                                    String responseText = "내 글에 댓글이 달렸어요" + "\n" + content;
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
                                                                                                                Log.d("OK!!", "Okay!!!~~");
                                                                                                                String res = prevTimeSetClass.formatTimeString(date);
                                                                                                                String responseText = "내 글에 대댓글이 달렸어요" + "\n" + content;
                                                                                                                PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                                                        responseText, res);
                                                                                                                // newArrays.add(prevNotificationInfo);
                                                                                                                alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                                                                                                // alarmPrevNotificationListAdapter.setItem(newArrays);
                                                                                                                alarmPrevNotificationListAdapter.notifyDataSetChanged();
                                                                                                                prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
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
    }//

    //뭐하지
    public void getHowDoInfo() {
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
                                                                                String res = prevTimeSetClass.formatTimeString(date);
                                                                                String responseText = "내 글에 댓글이 달렸어요" + "\n" + content;
                                                                                PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                        responseText, res);
                                                                                //  newArrays.add(prevNotificationInfo);
                                                                                //  alarmPrevNotificationListAdapter.setItem(newArrays);
                                                                                alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
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
                                                                                                                Log.d("OK!!", "Okay!!!~~");
                                                                                                                String res = prevTimeSetClass.formatTimeString(date);
                                                                                                                String responseText = "내 글에 대댓글이 달렸어요" + "\n" + content;
                                                                                                                PrevNotificationInfo prevNotificationInfo = new PrevNotificationInfo(R.drawable.comments,
                                                                                                                        responseText, res);
                                                                                                                //newArrays.add(prevNotificationInfo);
                                                                                                                //  alarmPrevNotificationListAdapter.setItem(newArrays);
                                                                                                                alarmPrevNotificationListAdapter.addItem(prevNotificationInfo);
                                                                                                                alarmPrevNotificationListAdapter.notifyDataSetChanged();
                                                                                                                prevNotificationListView.setAdapter(alarmPrevNotificationListAdapter);
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
}

