package com.abbsolute.ma_livu.Alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class AlarmFragment extends Fragment {

    private View view;
    private AlarmFriendRequestListAdapter alarmFriendRequestListAdapter;
    private AlarmPrevNotificationListAdapter alarmPrevNotificationListAdapter;
    private RecyclerView friendRequestListView;
    private RecyclerView prevNotificationListView;
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private ArrayList<String> dDayList=new ArrayList<>();
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
                                        //todo: db 할지 파이어스토어 할 지 정하기
                                        //
                                    }
                                }
                            }
                        }
                    }
                });
        return view;
    }
}
