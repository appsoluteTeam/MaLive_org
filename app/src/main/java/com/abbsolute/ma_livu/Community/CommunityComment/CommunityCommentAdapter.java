package com.abbsolute.ma_livu.Community.CommunityComment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CommunityCommentAdapter extends RecyclerView.Adapter<CommunityCommentAdapter.ViewHolder> {
    private ArrayList<CommunityCommentItem> arrayList;
    private CommuCommentOnItemClick callback;

    public CommunityCommentAdapter(ArrayList<CommunityCommentItem> arrayList, CommuCommentOnItemClick listener) {
        this.arrayList = arrayList;
        this.callback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 최초로 만들어내는 역할
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_comment_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        Glide.with(holder.itemView) //프로필 이미지를 url로 받아오기 용이하도록 글라이드 이용
////                .load(arrayList.get(position).getIcon())
////                .into(holder.CommentIcon);
        holder.CommentName.setText(arrayList.get(position).getName());
        holder.CommentDate.setText(arrayList.get(position).getDate());
        holder.Comment.setText(arrayList.get(position).getComment());
        holder.commu_comment_like.setText(arrayList.get(position).getComment_like());
        holder.commu_comment_comment_count.setText(arrayList.get(position).getComment_count());

        // 이 유저가 댓글에 '좋아요' 버튼을 눌렀었는지 판단
        callback.checkLikePressed(position);
        if( callback.returnBoolean(position) == true) {
//            holder.btn_comment_like.setSelected(! holder.btn_comment_like.isSelected());
        }

        // '삭제' 버튼 클릭 시 데이터 삭제하기
        holder.btn_commu_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deleteItem(position);
            }
        });

         // '신고' 버튼 클릭 시 데이터 신고하기
        holder.btn_commu_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.reportItem(position);
            }
        });
        holder.btn_commu_report_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.reportItem(position);
            }
        });

        // '좋아요' 버튼 클릭 시 count 증가
        holder.btn_comment_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재의 좋아요 갯수 받아오기
                int like_count;
                like_count = Integer.parseInt(holder.commu_comment_like.getText().toString());

                // 버튼이 눌리지 않은 상태를 기본으로 설정
                v.setSelected(!v.isSelected());
                if(v.isSelected()) {
                    holder.commu_comment_like.setText(Integer.toString(like_count+1));
                    callback.commentLike(position);
                } else {
                    holder.commu_comment_like.setText(Integer.toString(like_count-1));
                    callback.commentDislike(position);
                }
            }
        });

        // '답글'버튼 클릭 시 CommunityCommentCommentFragment로 넘어가기
        holder.btn_commu_comment_comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                callback.goCommunityCommentComment(position);
            }
        });

        // '더보기' 버튼 클릭 시 보이기
        holder.btn_comment_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 // '현재 접속한 유저' == '글쓴이'면 삭제, 신고기능 활성화
                if (callback.checkUser(position) == true) {
                    if (holder.community_comment_extra_true.getVisibility() == View.INVISIBLE) {
                        holder.community_comment_extra_true.setVisibility(View.VISIBLE);
                    } else {
                        holder.community_comment_extra_true.setVisibility(View.INVISIBLE);
                    }
                }
                // 다르면 신고기능만 활성화
                else {
                    holder.community_comment_extra_true.setVisibility(View.INVISIBLE);

                    if (holder.community_comment_extra_false.getVisibility() == View.INVISIBLE) {
                        holder.community_comment_extra_false.setVisibility(View.VISIBLE);
                    } else {
                        holder.community_comment_extra_false.setVisibility(View.INVISIBLE);
                    }
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
        TextView CommentName;
        TextView CommentDate;
        TextView Comment;
        ImageView CommentIcon;

        TextView commu_comment_like;
        TextView commu_comment_comment_count;

        Button btn_comment_like;
        Button btn_commu_comment_comment;

        Button btn_commu_delete;
        Button btn_commu_report;
        Button btn_commu_report_false;

        ImageButton btn_comment_extra;
        LinearLayout community_comment_extra_true;
        LinearLayout community_comment_extra_false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.CommentName = itemView.findViewById(R.id.CommentName);
            this.CommentDate = itemView.findViewById(R.id.CommentDate);
            this.Comment = itemView.findViewById(R.id.Comment);
            this.CommentIcon = itemView.findViewById(R.id.CommentIcon);

            this.btn_comment_like = itemView.findViewById(R.id.btn_comment_like);
            this.commu_comment_like = itemView.findViewById(R.id.commu_comment_like);

            this.btn_commu_comment_comment = itemView.findViewById(R.id.btn_commu_comment_comment);
            this.commu_comment_comment_count = itemView.findViewById(R.id.commu_comment_comment_count);

            this.btn_comment_extra = itemView.findViewById(R.id.btn_comment_extra);
            this.community_comment_extra_true = itemView.findViewById(R.id.community_comment_extra_true);
            this.community_comment_extra_false = itemView.findViewById(R.id.community_comment_extra_false);

            this.btn_commu_delete = itemView.findViewById(R.id.btn_commu_delete);
            this.btn_commu_report = itemView.findViewById(R.id.btn_commu_report);
            this.btn_commu_report_false = itemView.findViewById(R.id.btn_commu_report_false);

        }

    }
    public CommunityCommentItem getItem(int position) {
        return arrayList.get(position);
    }
}
