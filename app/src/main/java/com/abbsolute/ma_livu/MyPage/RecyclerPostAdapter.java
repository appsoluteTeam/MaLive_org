package com.abbsolute.ma_livu.MyPage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Community.CommunityAdapter;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentItem;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class RecyclerPostAdapter extends RecyclerView.Adapter<RecyclerPostAdapter.ViewHolder> {
    private ArrayList<postItemListView> arrayList;

    public RecyclerPostAdapter(ArrayList<postItemListView> arrayList) {
        this.arrayList = arrayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private RecyclerPostAdapter.OnItemClickListener listener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(RecyclerPostAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 최초로 만들어내는 역할
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypost_recycler_item2, parent, false);
        RecyclerPostAdapter.ViewHolder holder = new RecyclerPostAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerPostAdapter.ViewHolder holder, final int position) { //각 아이템에 대한 매칭
        holder.post_category.setText(arrayList.get(position).getPost_category());
        holder.post_title.setText(arrayList.get(position).getPost_title());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView post_category;
        private TextView post_title;

        public ViewHolder(View view){
            super(view);

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

            this.post_category = view.findViewById(R.id.post_category);
            this.post_title = view.findViewById(R.id.post_title);
        }
    }

    public postItemListView getItem(int position) {
        return arrayList.get(position);
    }

}
