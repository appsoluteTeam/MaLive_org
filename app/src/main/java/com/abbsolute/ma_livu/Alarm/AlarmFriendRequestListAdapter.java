package com.abbsolute.ma_livu.Alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlarmFriendRequestListAdapter extends RecyclerView.Adapter<AlarmFriendRequestListAdapter.ViewHolder> {
    ArrayList<AlarmFriendRequestInfo> alarmRequestInfoArrayList=new ArrayList<>();
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
    public void setItem(ArrayList<AlarmFriendRequestInfo> arrayList){
        this.alarmRequestInfoArrayList=arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.friend_request_list_in_alarm_fragment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmFriendRequestInfo info=alarmRequestInfoArrayList.get(position);
        holder.userImg.setBackgroundResource(info.getUserImage());
        holder.requestMsg.setText(info.getRequestMessage());
        holder.requestTime.setText(info.getPrevTime());

    }

    @Override
    public int getItemCount() {
        return alarmRequestInfoArrayList.size();
    }



}
