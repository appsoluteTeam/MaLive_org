package com.abbsolute.ma_livu.Alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class AlarmFragment extends Fragment {
    //todo 모든 기능의 데이터가 구축되어야 할 수 있는 일
    private View view;
    private AlarmFriendRequestListAdapter alarmFriendRequestListAdapter;
    private AlarmPrevNotificationListAdapter alarmPrevNotificationListAdapter;
    private RecyclerView friendRequestListView;
    private RecyclerView prevNotificationListView;
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private ArrayList<String> dDayList=new ArrayList<>();
    ///
    String nickName="";
    ArrayList<AlarmFriendRequestInfo> alarmFriendRequestInfoArrayList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm,container,false);
        alarmFriendRequestListAdapter=new AlarmFriendRequestListAdapter();
        alarmPrevNotificationListAdapter=new AlarmPrevNotificationListAdapter();
        friendRequestListView=view.findViewById(R.id.friend_request_list);
        prevNotificationListView=view.findViewById(R.id.prev_notification_list);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        friendRequestListView.setLayoutManager(layoutManager1);
        FirebaseUser user=firebaseAuth.getCurrentUser();
        //닉네임 찾기
        String email=user.getEmail();
        firestore.collection(FirebaseID.user).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null){
                                DocumentSnapshot snapshot=task.getResult();
                                if(snapshot.exists()){
                                    Map<String,Object> data=snapshot.getData();
                                    if(data.containsKey("nickname")){
                                        nickName=(String)snapshot.getData().get("nickname");
                                        String requestMessage=nickName+"님이 친구요청을 보냈습니다.";
                                        //todo 마이페이지에서 친구요청 구현하면 시간데이터 넣기
                                        AlarmFriendRequestInfo friendRequestInfo=
                                                new AlarmFriendRequestInfo(R.drawable.user1,
                                                        requestMessage,"4시간전");
                                        alarmFriendRequestInfoArrayList.add(friendRequestInfo);
                                        alarmFriendRequestListAdapter.setItem(alarmFriendRequestInfoArrayList,nickName);
                                        friendRequestListView.setHasFixedSize(true);
                                        friendRequestListView.setAdapter(alarmFriendRequestListAdapter);
                                    }else{

                                    }
                                }
                            }
                        }
                    }
                });

        ///이전알림 시작
        LinearLayoutManager layoutManager2=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        prevNotificationListView.setLayoutManager(layoutManager2);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");
        firestore.collection(FirebaseID.ToDoLists).document(id+" ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null){
                                DocumentSnapshot snapshot=task.getResult();
                                if(snapshot.exists()){
                                    String count= (String) snapshot.getData().get("Count");
                                    int siz=Integer.parseInt(count);
                                    for(int i=0;i<=siz;i++){
                                        String dDay=(String)snapshot.getData().get("dDates"+i);
                                        dDayList.add(dDay);
                                    }
                                    long systemTime = System.currentTimeMillis();
                                    SimpleDateFormat formatter= null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                    }
                                    String date=formatter.format(systemTime);
                                    String[] splitData=date.split("-");
                                    String tmp1=splitData[0];
                                    String tmp2=splitData[1];
                                    String tmp3=splitData[2];
                                    String today=tmp1+"년"+tmp2+"월"+tmp3+"일";
                                    for(int i=0;i<dDayList.size();i++){
                                        if(dDayList.get(i).compareTo(today)<0){
                                            //PrevNotificationInfo prevNotificationInfo=new PrevNotificationInfo()
                                        }
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
