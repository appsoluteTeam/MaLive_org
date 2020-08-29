package com.abbsolute.ma_livu.Alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlarmPrevNotificationListAdapter extends RecyclerView.Adapter<AlarmPrevNotificationListAdapter.ViewHolder> {
    ArrayList<PrevNotificationInfo> prevInfoArrayList=new ArrayList<>();
    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected CircleImageView user;
        protected TextView prevMsg;
        protected TextView prevTime;
        ViewHolder(View view){
            super(view);
            this.user=view.findViewById(R.id.user_profile);
            this.prevMsg=view.findViewById(R.id.prev_notification_text_apart);
            this.prevTime=view.findViewById(R.id.prev_time_apart);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.prev_notification_list, parent, false);
        return new ViewHolder(itemView);
    }
    public void setItem(ArrayList<PrevNotificationInfo> arrayList){
        this.prevInfoArrayList=arrayList;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrevNotificationInfo info=prevInfoArrayList.get(position);
        holder.user.setBackgroundResource(info.getFriendImage());
        holder.prevMsg.setText(info.getNotifiedText());
        holder.prevTime.setText(info.getPrevNotificationTime());
    }

    @Override
    public int getItemCount() {
        return prevInfoArrayList.size();
    }


}
