package com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Community.CommunityComment.CommuCommentOnItemClick;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentAdapter;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentFragment;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentItem;
import com.abbsolute.ma_livu.Community.CommunityFragment;
import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommunityCommentCommentFragment extends Fragment implements CommuCommentCommentOnItemClick {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //프래그먼트 전환 변수
    private FragmentTransaction transaction;

    private RecyclerView recyclerView;
    public static CommunityCommentCommentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CommunityCommentCommentItem> arrayList;

    private SimpleDateFormat dateform;
    private Calendar date;

    private String category;
    private String title;
    private String commentName;
    private String commentDate;
    private String commentComment;
    private String commentLike;

    private TextView commentname;
    private TextView commentdate;
    private TextView commentcomment;
    private TextView commentlike;
    private TextView commentcommentcount;
    private TextView recommentCount;

    private Button btn_back;
    private Button btn_insert;

    private EditText recomment;
    private static int count;
    private static int comment_like_count;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_comment_comment,container,false);

        btn_back = view.findViewById(R.id.btn_back);
        btn_insert = view.findViewById(R.id.btn_comment_comment_insert);

        commentname = view.findViewById(R.id.CommentName);
        commentdate = view.findViewById(R.id.CommentDate);
        commentcomment = view.findViewById(R.id.Comment);
        commentlike = view.findViewById(R.id.commu_comment_like);
        commentcommentcount = view.findViewById(R.id.commu_comment_comment_count);

        recomment = view.findViewById(R.id.WriteComment);
        recommentCount = view.findViewById(R.id.comment_comment_count);

        // CommunityCommentFragment에서 데이터 받아오기
        if(getArguments() != null){
            title = getArguments().getString("Title");
            category = getArguments().getString("Category");
            commentName = getArguments().getString("CommentName");
            commentDate = getArguments().getString("CommentDate");
            commentComment = getArguments().getString("CommentComment");
            commentLike = getArguments().getString("CommentLike");
        }

        // CommunityCommentFragment에서 받아온 텍스트 데이터 set
        commentname.setText(commentName);
        commentdate.setText(commentDate);
        commentcomment.setText(commentComment);
        commentlike.setText(commentLike);

        // '뒤로가기' 버튼 눌렀을 시
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityCommentFragment communityCommentFragment = new CommunityCommentFragment();

                // 받은 데이터 다시 넘겨주기
                Bundle bundle = new Bundle();
                bundle.putString("Category", category);
                bundle.putString("Title", title);
                communityCommentFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityCommentFragment);
                transaction.commit();
            }
        });

        // '입력' 버튼 눌렀을 시 DB에 데이터 추가하기
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {

                    // 게시글 작성 시간 받아오기
                    long now = System.currentTimeMillis();
                    dateform = new SimpleDateFormat("yyyy-MM-dd");
                    date = Calendar.getInstance();

                    comment_like_count = 0;

                    // DB에 데이터 추가
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
                    data.put(FirebaseID.commu_comment_comment_name, firebaseAuth.getCurrentUser().getEmail());
                    data.put(FirebaseID.commu_comment_comment_comment, recomment.getText().toString());
                    data.put(FirebaseID.commu_comment_comment_date, dateform.format(date.getTime()));
                    data.put(FirebaseID.commu_comment_comment_like, comment_like_count);

                    // DB에 저장되는 경로 Community->category->sub_Community->Community_Comment
                    firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                            .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(recomment.getText().toString())
                            .set(data, SetOptions.merge());
                }
                refresh();
            }
        });

        // DB의 데이터 불러와 어레이리스트에 넣기
        arrayList = new ArrayList<>();
        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                arrayList.clear();

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String CommentCommentName = String.valueOf(shot.get(FirebaseID.commu_comment_comment_name));
                                    String CommentComment = String.valueOf(shot.get(FirebaseID.commu_comment_comment_comment));
                                    String CommentCommentDate = String.valueOf(shot.get(FirebaseID.commu_comment_comment_date));
                                    String CommentCommentLike = String.valueOf(shot.get(FirebaseID.commu_comment_comment_like));

                                    CommunityCommentCommentItem data = new CommunityCommentCommentItem(CommentCommentName, CommentComment, CommentCommentDate, CommentCommentLike);
                                    arrayList.add(data);
                                }
                                // 전체 답글 수 받아오기
                                count =  adapter.getItemCount();
                                // 맨 위에 표시되는 전체 답글 수
                                recommentCount.setText(Integer.toString(count));
                                // 리사이클러뷰에 표시되는 전체 답글 수
                                commentcommentcount.setText(Integer.toString(count));

                                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                            }
                        }
                    }
                });

        // 어댑터와 리사이클러뷰 연결해서 화면에 띄우기
        recyclerView = (RecyclerView) view.findViewById(R.id.Community_Comment_CommentList);
        recyclerView.setHasFixedSize(true);
        adapter = new CommunityCommentCommentAdapter(arrayList, (CommuCommentCommentOnItemClick) this);
        layoutManager = new LinearLayoutManager(getActivity());

        // 리사이클러뷰 역순 출력
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        recyclerView.scrollToPosition(0);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // 리사이클러뷰 새로고침 메소드
    private void refresh() {
        transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }

    @Override
    public void commentLike(int position) {
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(arrayList.get(position).getComment());
            data.update(FirebaseID.commu_comment_comment_like, String.valueOf(Integer.parseInt(arrayList.get(position).getComment_like())+1));
        }
    }

    @Override
    public void commentDislike(int position) {
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(arrayList.get(position).getComment());
            data.update(FirebaseID.commu_comment_comment_like, String.valueOf(Integer.parseInt(arrayList.get(position).getComment_like())));
        }
    }
}
