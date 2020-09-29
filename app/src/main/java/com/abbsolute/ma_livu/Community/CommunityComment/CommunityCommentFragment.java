package com.abbsolute.ma_livu.Community.CommunityComment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Community.Commu_WriteFragment;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment.CommunityCommentCommentAdapter;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment.CommunityCommentCommentFragment;
import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Community.bringData;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommunityCommentFragment extends Fragment implements CommuCommentOnItemClick {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //프래그먼트 전환 변수
    private FragmentTransaction transaction;
    private Commu_WriteFragment commu_writeFragment;

    private RecyclerView recyclerView;
    public static CommunityCommentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CommunityCommentItem> arrayList;

    private SimpleDateFormat dateform;
    private Calendar date;

    private static int count, like_count, comment_count, num=0;
    private static boolean commentLikeCheck = false;
    private boolean iscommentLikeCheck;
    private TextView CommentCount, CommentName, CommentDate;
    private ImageView CommentIcon;
    private EditText Comment;
    private Button btn_back, btn_insert, btn_comment_like;
    private static String str_nickname, email;
    private String category, title, writer, content, posts_date,commentLikeCount;
    private ArrayList<String> LikeUser;

    public CommunityCommentFragment(){};
    public CommunityCommentFragment(String email) {
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
        View view = inflater.inflate(R.layout.fragment_community_comment,container,false);

        CommentCount = view.findViewById(R.id.comment_count);
        Comment = view.findViewById(R.id.WriteComment);

        btn_back = view.findViewById(R.id.btn_back);
        btn_insert = view.findViewById(R.id.btn_comment_insert);
        btn_comment_like = view.findViewById(R.id.btn_comment_like);

        // CommunityFragment에서 데이터 받아오기
        if(getArguments() != null){
            title = getArguments().getString("Title");
            category = getArguments().getString("Category");
            writer = getArguments().getString("Writer");
            content = getArguments().getString("Content");
            posts_date = getArguments().getString("Date");
            iscommentLikeCheck = getArguments().getBoolean("CommentLikeCheck");
        }

        Log.d("commentLikeCheck", String.valueOf(iscommentLikeCheck));
//        if(iscommentLikeCheck == true) {
//            btn_comment_like.setSelected(!btn_comment_like.isSelected());
//        } else {
//            btn_comment_like.setSelected(btn_comment_like.isSelected());
//        }

        // '뒤로가기' 버튼 눌렀을 시
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityPostsFragment communityPostsFragment = new CommunityPostsFragment();

                // 받은 데이터 다시 넘겨주기
                Bundle bundle = new Bundle();
                bundle.putString("Category", category);
                bundle.putString("Title", title);
                bundle.putString("Content", content);
                bundle.putString("Date", posts_date);
                bundle.putString("Writer", writer);
                communityPostsFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityPostsFragment);
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

//                    count =  adapter.getItemCount();
//                    count = count + 1;

                    like_count = 0;
                    comment_count = 0;

                    // DB에 데이터 추가
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
                    data.put(FirebaseID.Email, firebaseAuth.getCurrentUser().getEmail());
                    data.put(FirebaseID.commu_comment_name, str_nickname);
                    data.put(FirebaseID.commu_comment_comment, Comment.getText().toString());
                    data.put(FirebaseID.commu_comment_date, dateform.format(date.getTime()));
                    data.put(FirebaseID.commu_comment_num, count);
                    data.put(FirebaseID.commu_comment_like, like_count);
                    data.put(FirebaseID.commu_comment_comment_count, comment_count);

                    // DB에 저장되는 경로 Community->category->sub_Community->Community_Comment
                    firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                            .collection(FirebaseID.Community_Comment)
                            .document(Comment.getText().toString())
                            .set(data, SetOptions.merge());
//                            .add(data);

                }
                Comment.setText(null);
                refresh();
            }
        });


        // DB의 데이터 불러와 어레이리스트에 넣기
        arrayList = new ArrayList<>();
        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                .collection(FirebaseID.Community_Comment)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                arrayList.clear();

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String CommentName = String.valueOf(shot.get(FirebaseID.commu_comment_name));
                                    String Comment = String.valueOf(shot.get(FirebaseID.commu_comment_comment));
                                    String CommentDate = String.valueOf(shot.get(FirebaseID.commu_comment_date));
//                                    String CommentNum = String.valueOf(shot.get(FirebaseID.commu_comment_num));
                                    String CommentLike = String.valueOf(shot.get(FirebaseID.commu_comment_like));
                                    String CommentCount = String.valueOf(shot.get(FirebaseID.commu_comment_comment_count)); //답글 수

                                    CommunityCommentItem data = new CommunityCommentItem(CommentName, Comment, CommentDate, CommentLike, CommentCount);
                                    arrayList.add(data);
                                }
                                // 전체 댓글 수 받아오기
                                count =  adapter.getItemCount();
                                CommentCount.setText(Integer.toString(count));
                                commentCount(count);

                                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                            }
                        }
                    }
                });

        // 어댑터와 리사이클러뷰 연결해서 화면에 띄우기
        recyclerView = (RecyclerView) view.findViewById(R.id.CommunityCommentList);
        recyclerView.setHasFixedSize(true);
        adapter = new CommunityCommentAdapter(arrayList, (CommuCommentOnItemClick) this);
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

    // 댓글 좋아요 판단 메소드
    @Override
    public void checkLikePressed(int position) {
//                firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
//                .collection(FirebaseID.Community_Comment).document(arrayList.get(position).getComment())
//                .collection("comment_Like")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        LikeUser = new ArrayList<>();
//                        for (DocumentSnapshot snapshot : task.getResult()) {
//                            Map<String, Object> shot = snapshot.getData();
//                            String CommentLikeUser = String.valueOf(shot.get());
//
//                            LikeUser.add(CommentLikeUser);
//                        }
//                    }
//                });
    }
    // 댓글 좋아요 메소드
    @Override
    public void commentLike(int position) {
        commentLikeCheck = true;
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(arrayList.get(position).getComment());
            data.update(FirebaseID.commu_comment_like, String.valueOf(Integer.parseInt(arrayList.get(position).getComment_like())+1));
        }
        if (firebaseAuth.getCurrentUser() != null) {
            firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(arrayList.get(position).getComment())
                    .collection("comment_Like").document(email).set(SetOptions.merge());
        }
    }

    // 댓글 좋아요 취소 메소드
    @Override
    public void commentDislike(int position) {
        commentLikeCheck = false;
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(arrayList.get(position).getComment());
            data.update(FirebaseID.commu_comment_like, String.valueOf(Integer.parseInt(arrayList.get(position).getComment_like())));
        }
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data2 = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                    .collection(FirebaseID.Community_Comment).document(arrayList.get(position).getComment())
                    .collection("comment_Like").document(email);
            data2.delete();
        }
    }

    // 대댓글 메소드
    @Override
    public void goCommunityCommentComment(int position) {
        CommunityCommentItem item = adapter.getItem(position);

        // CommunityCommentCommentFragment로 데이터 넘기기
        Bundle bundle = new Bundle();
        bundle.putString("Category", category);
        bundle.putString("Title", title);
        bundle.putString("CommentName", item.getName());
        bundle.putString("CommentDate", item.getDate());
        bundle.putString("CommentComment", item.getComment());
        bundle.putString("CommentLike", item.getComment_like() );
        bundle.putBoolean("CommentLikeCheck", commentLikeCheck);

        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        CommunityCommentCommentFragment communityCommentCommentFragment = new CommunityCommentCommentFragment();
        communityCommentCommentFragment.setArguments(bundle);

        // 버튼 누르면 화면 전환
        transaction.replace(R.id.main_frame, communityCommentCommentFragment);
        transaction.commit();
     }



    // 댓글 삭제 메소드
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
                        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document()
                                .collection(FirebaseID.Community_Comment)
                                .document(arrayList.get(position).getComment())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("CommunityCommentFragment", "커뮤니티 댓글 삭제 완료!");
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

     // 댓글 신고 메소드
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

                        // 신고할 데이터 받아오기
                        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                                .collection(FirebaseID.Community_Comment).document(arrayList.get(position).getComment())
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

    // 전체 댓글 수 받아오는 메소드
    private void commentCount(int count) {
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference data = firestore.collection(FirebaseID.Community).document(category)
                    .collection("sub_Community").document(title);
            data.update(FirebaseID.commu_comment_count, count);
        }
    }

    // 리사이클러뷰 새로고침 메소드
    private void refresh() {
        transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }
}