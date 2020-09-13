package com.abbsolute.ma_livu.MyPage.AboutFriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    ArrayList<FriendListInfo> friendListInfoArrayList=new ArrayList<>();
    Context context;
    GoRoomClick goRoomClick;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.show_friends_list,
                parent,false);
        FriendListAdapter.ViewHolder holder=new FriendListAdapter.ViewHolder(view);
        return holder;
    }
    public void setContext(Context context,GoRoomClick goRoomClick){
        this.context=context;
        this.goRoomClick=goRoomClick;
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        FriendListInfo friendListInfo=friendListInfoArrayList.get(position);
        /*Glide.with(holder.itemView)
                .load(friendListInfo.getFriendIcon())
                .into(holder.friendProfile);*/
        holder.friendProfile.setBackgroundResource(R.mipmap.ic_launcher_round);
        holder.nickName.setText(friendListInfo.getName());
        holder.todayCount.setText(friendListInfo.getToday());
        //방 놀러가기 누르면 홈 화면으로 이동
        holder.goRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                goRoomClick.onClick(2);
            }
        });
        //더보기 클릭 시 보이기
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.extra.getVisibility()==View.INVISIBLE){
                    holder.extra.setVisibility(View.VISIBLE);
                }else{
                    holder.extra.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public void setItem(ArrayList<FriendListInfo> friendListInfos){
        friendListInfoArrayList=friendListInfos;
    }
    public void addItem(FriendListInfo friendListInfo){
        friendListInfoArrayList.add(friendListInfo);
    }
    @Override
    public int getItemCount() {
        return friendListInfoArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        protected ImageView friendProfile;
        protected TextView nickName;
        protected TextView todayCount;
        protected Button goRoom;
        protected ImageView more;
        protected LinearLayout extra;
        public ViewHolder(View view){
            super(view);
            this.friendProfile=view.findViewById(R.id.friend_list_profile);
            this.nickName=view.findViewById(R.id.friend_nickname);
            this.more=view.findViewById(R.id.friend_list_more);
            this.goRoom=view.findViewById(R.id.go_room);
            this.todayCount=view.findViewById(R.id.today_count);
            this.extra=view.findViewById(R.id.friend_comment_extra);
        }
    }
}
