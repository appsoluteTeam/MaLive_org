package com.abbsolute.ma_livu.Alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlarmFriendRequestListAdapter extends RecyclerView.Adapter<AlarmFriendRequestListAdapter.ViewHolder> {
    ArrayList<AlarmFriendRequestInfo> alarmRequestInfoArrayList=new ArrayList<>();
    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    String nickName;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected CircleImageView userImg;
        protected TextView requestMsg;
        protected TextView requestTime;
        protected Button accepting;
        protected Button reclining;
        public ViewHolder(View v){
            super(v);
            this.userImg=v.findViewById(R.id.friend_profile);
            this.requestMsg=v.findViewById(R.id.request_msg);
            this.requestTime=v.findViewById(R.id.times);
            this.accepting=v.findViewById(R.id.accept);
            this.reclining=v.findViewById(R.id.recline);
        }
    }
    public void setItem(ArrayList<AlarmFriendRequestInfo> arrayList,String nickName){
        this.alarmRequestInfoArrayList=arrayList;
        this.nickName=nickName;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.friend_request_list_in_alarm_fragment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final AlarmFriendRequestInfo info=alarmRequestInfoArrayList.get(position);
        holder.userImg.setBackgroundResource(info.getUserImage());
        holder.requestMsg.setText(info.getRequestMessage());
        holder.requestTime.setText(info.getPrevTime());
        holder.accepting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String friendName=nickName;
                final String email=firebaseAuth.getCurrentUser().getEmail();
                firestore.collection(FirebaseID.alarm_fragment).document(email)
                        .collection("friend")
                        .document(friendName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult()!=null){
                                        DocumentSnapshot snapshot=task.getResult();
                                        if(snapshot.exists()){
                                            Map<String,Object> data=new HashMap<>();
                                            String input="friend"+nickName;
                                            data.put(input,friendName);
                                            firestore.collection(FirebaseID.alarm_fragment).document(email)
                                                    .collection("friend")
                                                    .document(friendName).set(data, SetOptions.merge());
                                            if(alarmRequestInfoArrayList.size()>0)
                                                alarmRequestInfoArrayList.remove(position);
                                        }else{
                                            Map<String,Object> data=new HashMap<>();
                                            String input="friend"+nickName;
                                            data.put(input,friendName);
                                            firestore.collection(FirebaseID.alarm_fragment).document(email)
                                                    .collection("friend")
                                                    .document(friendName).set(data, SetOptions.merge());
                                            if(alarmRequestInfoArrayList.size()>0)
                                                alarmRequestInfoArrayList.remove(position);
                                        }
                                    }
                                }
                            }
                        });

            }
        });
        holder.reclining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmRequestInfoArrayList.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmRequestInfoArrayList.size();
    }



}
