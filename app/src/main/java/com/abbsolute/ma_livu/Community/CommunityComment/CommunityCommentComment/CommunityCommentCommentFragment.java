package com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static String str_nickname, email;
    private String category, title, commentName, commentDate, commentComment, commentLike, currentDate;
    private Boolean commentLikeCheck;
    private TextView commentname, commentdate, commentcomment, commentlike, commentcommentcount, recommentCount;
    private EditText recomment;

    private Button btn_back, btn_insert, btn_comment_like;

    private static int count;
    private static int comment_like_count;

    public CommunityCommentCommentFragment(){};
    public CommunityCommentCommentFragment(String email) {
        this.email = email;
        Log.d("email",email);

        /*user firestore에서 닉네임 정보 가져오기 */
        firestore.collection(FirebaseID.user).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                str_nickname  = shot.get(FirebaseID.Nickname).toString();
                                Log.d("activeFragment", "user nickname Get 완료");

                            } else {
                                Log.d("activeFragment", "No such document");
                            }
                        } else {
                            Log.d("activeFragment", "get failed with ", task.getException());
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_comment_comment,container,false);

        btn_back = view.findViewById(R.id.btn_back);
        btn_insert = view.findViewById(R.id.btn_comment_comment_insert);
        btn_comment_like = view.findViewById(R.id.btn_comment_like);

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
            commentLikeCheck = getArguments().getBoolean("CommentLikeCheck");
        }

        Toast.makeText(this.getContext(), "CommentLike " + commentLikeCheck, Toast.LENGTH_SHORT).show();

        // CommunityCommentFragment에서 받아온 텍스트 데이터 set
        commentname.setText(commentName);
        commentdate.setText(commentDate);
        commentcomment.setText(commentComment);
        commentlike.setText(commentLike);

        if(commentLikeCheck == true) {
            btn_comment_like.setSelected(!btn_comment_like.isSelected());
        } else {
            btn_comment_like.setSelected(btn_comment_like.isSelected());
        }

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
                bundle.putBoolean("CommentLikeCheck", commentLikeCheck);
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
                    dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    date = Calendar.getInstance();

                    comment_like_count = 0;

                    // DB에 데이터 추가
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
                    data.put(FirebaseID.Email, firebaseAuth.getCurrentUser().getEmail());
                    data.put(FirebaseID.commu_comment_comment_name, str_nickname);
                    data.put(FirebaseID.commu_comment_comment_comment, recomment.getText().toString());
                    data.put(FirebaseID.commu_comment_comment_date, dateform.format(date.getTime()));
                    data.put(FirebaseID.commu_comment_comment_like, comment_like_count);

                    // DB에 저장되는 경로 Community->category->sub_Community->Community_Comment->Community_Comment_Comment
                    firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                            .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(recomment.getText().toString())
                            .set(data, SetOptions.merge());
                }
                // editText에 남아있는 값 지우기
                recomment.setText(null);

                // 새로고침
                refresh();
            }
        });

        btn_comment_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재의 좋아요 갯수 받아오기
                int like_count;
                like_count = Integer.parseInt(commentLike);

                // 버튼이 눌리지 않은 상태를 기본으로 설정
                if(v.isSelected() == true) {
//                    v.setSelected(v.isSelected());
                }
                else {
                }
                //TODO 구현한거 아님 걍 복붙한 코드
//                v.setSelected(!v.isSelected());
//                if(v.isSelected()) {
//                    commu_comment_like.setText(Integer.toString(like_count+1));
//                    callback.commentLike(position);
//                } else {
//                    holder.commu_comment_like.setText(Integer.toString(like_count-1));
//                    callback.commentDislike(position);
//                }
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

                                    // 받아온 date값에 HH:mm 자르기
                                    currentDate = CommentCommentDate.substring(0, CommentCommentDate.length()-6);

                                    CommunityCommentCommentItem data = new CommunityCommentCommentItem(CommentCommentName, CommentComment, currentDate, CommentCommentLike);
                                    arrayList.add(data);
                                }
                                // 전체 답글 수 받아오기
                                count =  adapter.getItemCount();
                                // 맨 위에 표시되는 전체 답글 수
                                recommentCount.setText(Integer.toString(count));
                                // 리사이클러뷰에 표시되는 전체 답글 수
                                commentcommentcount.setText(Integer.toString(count));
                                // CommunityCommentFragment에 전체 답글 수 넘겨주기 위해 Firestore에 데이터 추가
                                commentCount();

                                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                            }
                        }
                    }
                });

        // 어댑터와 리사이클러뷰 연결해서 화면에 띄우기
        recyclerView = view.findViewById(R.id.Community_Comment_CommentList);
        recyclerView.setHasFixedSize(true);
        adapter = new CommunityCommentCommentAdapter(arrayList, this);
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
        if(firebaseAuth.getCurrentUser() != null) {
            firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(recomment.getText().toString())
                    .collection("commentcomment_Like").document(email)
                    .set(SetOptions.merge());
        }
    }

    @Override
    public void commentDislike(int position) {
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(arrayList.get(position).getComment());
            data.update(FirebaseID.commu_comment_comment_like, String.valueOf(Integer.parseInt(arrayList.get(position).getComment_like())));
        }
        if(firebaseAuth.getCurrentUser() != null) {
            firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(recomment.getText().toString())
                    .collection("commentcomment_Like").document(email)
                    .delete();
        }
    }

    @Override
    public void deleteItem(final int position) {
        //삭제 여부를 묻는 알림창 띄우기
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("삭제");
        builder.setMessage("삭제하시겠습니까?");
        builder.setCancelable(true);

        // "삭제" 버튼 클릭 시
        builder.setPositiveButton(
                "삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // DB 내부의 데이터 삭제
                        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                                .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(arrayList.get(position).getComment())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("HomeActivity", "커뮤니티 댓글 삭제 완료!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("HomeActivity", "커뮤니티 댓글 삭제 실패!");
                                    }
                                });

                        // 새로 고침
                        refresh();
                    }
                });

        // "취소" 버튼 클릭 시
        builder.setNegativeButton(
                "취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void reportItem(final int position) {
        // 신고 여부를 묻는 알림창 띄우기
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("신고");
        builder.setMessage("이 글을 신고하시겠습니까?");
        builder.setCancelable(true);

        // "신고" 버튼 클릭 시
        builder.setPositiveButton(
                "신고",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // 신고할 데이터 "GuestBook" 컬렉션에서 받아오기
                        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                                .collection(FirebaseID.Community_Comment).document(commentComment).collection(FirebaseID.Community_Comment_Comment).document(arrayList.get(position).getComment())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            // 컬렉션 내의 document에 접근
                                            DocumentSnapshot document = task.getResult();

                                            if (document.exists()) {
                                                Map<String, Object> shot = document.getData();
                                                String CommentName = String.valueOf(shot.get(FirebaseID.commu_comment_name));
                                                String Comment = String.valueOf(shot.get(FirebaseID.commu_comment_comment));
                                                String CommentDate = String.valueOf(shot.get(FirebaseID.commu_comment_date));

                                                // 신고할 데이터 "ReportComment" 컬렉션에 추가
                                                Map<String, Object> data = new HashMap<>();
                                                data.put(FirebaseID.commu_comment_name, CommentName);
                                                data.put(FirebaseID.commu_comment_comment, Comment);
                                                data.put(FirebaseID.commu_comment_date, CommentDate);

                                                firestore.collection(FirebaseID.Community).document(category).collection(FirebaseID.Community_Comment_Report).document(title)
                                                        .collection(FirebaseID.Community_Comment).document(Comment).set(data, SetOptions.merge());

                                            } else {
//                                                Log.d("HomeActivity", "No such document");
                                            }
                                        } else {
//                                            Log.d("HomeActivity", "get failed with ", task.getException());
                                        }
                                    }
                                });

                        // 새로 고침
                        refresh();
                    }
                });

        // "취소" 버튼 클릭 시
        builder.setNegativeButton(
                "취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void commentCount() {
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(commentComment);
            data.update(FirebaseID.commu_comment_comment_count, String.valueOf(adapter.getItemCount()));
        }
    }
}
