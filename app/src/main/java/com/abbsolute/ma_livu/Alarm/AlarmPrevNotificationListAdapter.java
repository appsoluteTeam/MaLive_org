package com.abbsolute.ma_livu.Alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.MyPage.RecyclerPostAdapter;
import com.abbsolute.ma_livu.MyPage.postItemListView;
import com.abbsolute.ma_livu.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlarmPrevNotificationListAdapter extends RecyclerView.Adapter<AlarmPrevNotificationListAdapter.ViewHolder> {
    ArrayList<PrevNotificationInfo> prevInfoArrayList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        protected CircleImageView user;
        protected TextView prevMsg;
        protected TextView prevTime;
        ViewHolder(View view){
            super(view);
            this.user=view.findViewById(R.id.user_profile);
            this.prevMsg=view.findViewById(R.id.prev_notification_text_apart);
            this.prevTime=view.findViewById(R.id.prev_time_apart);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }

    public AlarmPrevNotificationListAdapter(ArrayList<PrevNotificationInfo> prevInfoArrayList) {
        this.prevInfoArrayList = prevInfoArrayList;
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

    public void addItem(PrevNotificationInfo prevNotificationInfo){
        prevInfoArrayList.add(prevNotificationInfo);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrevNotificationInfo info=prevInfoArrayList.get(position);
        holder.user.setBackgroundResource(info.getFriendImage());
        //todo: prevMsg 클릭시 대댓글 창으로 이동하기 구현
        holder.prevMsg.setText(info.getNotifiedText());
        holder.prevTime.setText(info.getPrevNotificationTime());
    }



    @Override
    public int getItemCount() {
        return prevInfoArrayList.size();
    }

    public PrevNotificationInfo getItem(int position) {
        return prevInfoArrayList.get(position);
    }

}
