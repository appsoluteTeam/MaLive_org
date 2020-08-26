package com.abbsolute.ma_livu.Community;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CustomViewHolder> {

    private ArrayList<bringData> arrayList;

    public CommunityAdapter(ArrayList<bringData> arrayList){
        this.arrayList =arrayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener listener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener ;
    }


    @NonNull
    @Override
    public CommunityAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.CustomViewHolder holder, int position) {
        holder.community_title.setText(arrayList.get(position).getTitle());
        holder.community_content.setText(arrayList.get(position).getContent());
        holder.community_like_count.setText(arrayList.get(position).getLikeCount());
        holder.community_save_count.setText(arrayList.get(position).getSaveCount());

    }

    @Override
    public int getItemCount()  {
        return (arrayList !=null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView community_title;
        TextView community_content;
        TextView community_like_count;
        TextView community_save_count;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            // 아이템 클릭 시 게시글로 이동
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(listener != null) {
                            Log.d("HomeActivity", "Element " + getAdapterPosition() + " clicked.");
                            listener.onItemClick(v, pos);
                        }
                    }
                }
            });


            this.community_title = itemView.findViewById(R.id.commu_title);
            this.community_content =itemView.findViewById(R.id.commu_content);
            this.community_like_count = itemView.findViewById(R.id.commu_like_count);
            this.community_save_count = itemView.findViewById(R.id.commu_save_count);
        }
    }

    public bringData getItem(int position) {
        return arrayList.get(position);
    }
}
