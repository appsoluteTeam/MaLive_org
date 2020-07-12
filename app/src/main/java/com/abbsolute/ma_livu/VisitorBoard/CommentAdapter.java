package com.abbsolute.ma_livu.VisitorBoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<CommentItem> arrayList;
    private OnItemClick callback;


    public CommentAdapter(ArrayList<CommentItem> arrayList, OnItemClick listener) {
            this.arrayList = arrayList;
            this.callback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 최초로 만들어내는 역할
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) { //각 아이템에 대한 매칭
        Glide.with(holder.itemView) //프로필 이미지를 url로 받아오기 용이하도록 글라이드 이용
                .load(arrayList.get(position).getIcon())
                .into(holder.CommentIcon);
//        holder.CommentIcon.setImageDrawable(arrayList.get(position).getIcon());
        holder.CommentNum.setText(arrayList.get(position).getNum());
        holder.CommentName.setText(arrayList.get(position).getName());
        holder.CommentDate.setText(arrayList.get(position).getDate());
        holder.Comment.setText(arrayList.get(position).getComment());


        // '삭제' 버튼 클릭 시 데이터 삭제하기
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deleteItem(position);
            }
        });

        // '신고' 버튼 클릭 시 데이터 신고하기
        holder.btn_report.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                callback.reportItem(position);
             }
        });

        // '좋아요' 버튼 클릭 시 작성자에게 푸쉬 알람 가기
        holder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());

                if(v.isSelected()) {
                    //TODO 좋아요 누를 시 글 작성자에게 푸쉬알람 가기
                } else {

                }
            }
        });
    }


    //arrayList의 아이템 개수만큼 반환
    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CommentNum;
        TextView CommentName;
        TextView CommentDate;
        TextView Comment;
        ImageView CommentIcon;

        Button btn_delete;
        Button btn_report;
        Button btn_like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.CommentNum = itemView.findViewById(R.id.CommentNum);
            this.CommentName = itemView.findViewById(R.id.CommentName);
            this.CommentDate = itemView.findViewById(R.id.CommentDate);
            this.Comment = itemView.findViewById(R.id.Comment);
            this.CommentIcon = itemView.findViewById(R.id.CommentIcon);

            this.btn_delete = itemView.findViewById(R.id.btn_delete);
            this.btn_report = itemView.findViewById(R.id.btn_report);
            this.btn_like = itemView.findViewById(R.id.btn_like);
        }
    }

//    public void addItem(CommentItem item){
//        arrayList.add(item);
//    }
}

