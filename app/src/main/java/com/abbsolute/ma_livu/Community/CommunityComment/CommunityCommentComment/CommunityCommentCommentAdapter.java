package com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment;

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
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class CommunityCommentCommentAdapter extends RecyclerView.Adapter<CommunityCommentCommentAdapter.ViewHolder> {
    private ArrayList<CommunityCommentCommentItem> arrayList;
    private CommuCommentCommentOnItemClick callback;

    public CommunityCommentCommentAdapter(ArrayList<CommunityCommentCommentItem> arrayList, CommuCommentCommentOnItemClick listener) {
        this.arrayList = arrayList;
        this.callback = listener;
    }

    @NonNull
    @Override
    public CommunityCommentCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_comment_comment_item, parent, false);
        CommunityCommentCommentAdapter.ViewHolder holder = new CommunityCommentCommentAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommunityCommentCommentAdapter.ViewHolder holder, final int position) {
        holder.CommentCommentName.setText(arrayList.get(position).getName());
        holder.CommentCommentDate.setText(arrayList.get(position).getDate());
        holder.CommentComment.setText(arrayList.get(position).getComment());
        holder.commu_comment_comment_like.setText(arrayList.get(position).getComment_like());

        // '좋아요' 버튼 클릭 시 count 증가
        holder.btn_comment_comment_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재의 좋아요 갯수 받아오기
                int like_count;
                like_count = Integer.parseInt(holder.commu_comment_comment_like.getText().toString());

                // 버튼이 눌리지 않은 상태를 기본으로 설정
                v.setSelected(!v.isSelected());
                if(v.isSelected()) {
                    holder.commu_comment_comment_like.setText(Integer.toString(like_count+1));
                    callback.commentLike(position);
                } else {
                    holder.commu_comment_comment_like.setText(Integer.toString(like_count-1));
                    callback.commentDislike(position);
                }
            }
        });

        // '더보기' 버튼 클릭 시 보이기
        holder.btn_comment_comment_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback.checkUser(position) == true) {
                        if (holder.community_comment_comment_extra_true.getVisibility() == View.INVISIBLE) {
                        holder.community_comment_comment_extra_true.setVisibility(View.VISIBLE);
                        } else {
                            holder.community_comment_comment_extra_true.setVisibility(View.INVISIBLE);
                        }
                } else {
                    holder.community_comment_comment_extra_true.setVisibility(View.INVISIBLE);

                    if (holder.community_comment_comment_extra_false.getVisibility() == View.INVISIBLE) {
                        holder.community_comment_comment_extra_false.setVisibility(View.VISIBLE);
                    } else {
                        holder.community_comment_comment_extra_false.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });

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
        // '신고' 버튼 클릭 시 데이터 신고하기
        holder.btn_commu_report_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.reportItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CommentCommentName;
        TextView CommentCommentDate;
        TextView CommentComment;
        ImageView CommentCommentIcon;

        Button btn_comment_comment_like;
        TextView commu_comment_comment_like;

        ImageButton btn_comment_comment_extra;
        LinearLayout community_comment_comment_extra_true;
        LinearLayout community_comment_comment_extra_false;

        Button btn_commu_delete;
        Button btn_commu_report;
        Button btn_commu_report_false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.CommentCommentName = itemView.findViewById(R.id.CommentCommentName);
            this.CommentCommentDate = itemView.findViewById(R.id.CommentCommentDate);
            this.CommentComment = itemView.findViewById(R.id.CommentComment);
            this.CommentCommentIcon = itemView.findViewById(R.id.CommentCommentIcon);

            this.btn_comment_comment_like = itemView.findViewById(R.id.btn_comment_comment_like);
            this.commu_comment_comment_like = itemView.findViewById(R.id.commu_comment_comment_like);

            this.btn_comment_comment_extra = itemView.findViewById(R.id.btn_comment_comment_extra);
            this.community_comment_comment_extra_true = itemView.findViewById(R.id.community_comment_comment_extra_true);
            this.community_comment_comment_extra_false = itemView.findViewById(R.id.community_comment_comment_extra_false);

            this.btn_commu_delete = itemView.findViewById(R.id.btn_commu_delete);
            this.btn_commu_report = itemView.findViewById(R.id.btn_commu_report);
            this.btn_commu_report_false = itemView.findViewById(R.id.btn_commu_report_false);

        }
    }
}
