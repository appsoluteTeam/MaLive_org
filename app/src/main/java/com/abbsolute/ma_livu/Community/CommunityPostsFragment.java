package com.abbsolute.ma_livu.Community;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentAdapter;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentFragment;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityPostsFragment extends Fragment {
    //프래그먼트 전환 변수
    FragmentTransaction transaction;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static bringData data;
    private String get_images,get_explain_temp;
    private static String email;

    // 값 받아오는 변수들
    private String title, writer, content, date, category, commentCount, saveCount, likeCount;
    private TextView commu_title, commu_writer, commu_date, commu_content, commu_category, commu_like_count, commu_save_count, commu_comment_count;
    private Button btn_back;
    private ImageButton btn_commu_like, btn_commu_save, btn_commu_comment;
    private ImageView get_commu_img1, get_commu_img2, get_commu_img3,get_commu_img4,get_commu_img5;
    private TextView get_commu_explain1,get_commu_explain2,get_commu_explain3,get_commu_explain4,get_commu_explain5;


    public CommunityPostsFragment() { }

    public CommunityPostsFragment(String email) {
        this.email = email;
        Log.d("email",email);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_community_posts, container, false);

        commu_title = view.findViewById(R.id.commu_title);
        commu_date = view.findViewById(R.id.commu_date);
        commu_writer = view.findViewById(R.id.commu_writer);
        commu_content = view.findViewById(R.id.commu_content);
        commu_category = view.findViewById(R.id.commu_category);
        commu_like_count = view.findViewById(R.id.commu_like_count);
        commu_save_count = view.findViewById(R.id.commu_save_count);
        commu_comment_count = view.findViewById(R.id.commu_comment_count);

        btn_back = view.findViewById(R.id.btn_back);
        btn_commu_like = view.findViewById(R.id.btn_commu_like);
        btn_commu_save = view.findViewById(R.id.btn_commu_save);
        btn_commu_comment = view.findViewById(R.id.btn_commu_comment);

        get_commu_img1 = view.findViewById(R.id.get_commu_img1);get_commu_img2 = view.findViewById(R.id.get_commu_img2);get_commu_img3 = view.findViewById(R.id.get_commu_img3);get_commu_img4 = view.findViewById(R.id.get_commu_img4);get_commu_img5 = view.findViewById(R.id.get_commu_img5);
        get_commu_explain1=view.findViewById(R.id.get_commu_explain1);get_commu_explain1.setVisibility(view.INVISIBLE);
        get_commu_explain2=view.findViewById(R.id.get_commu_explain2);get_commu_explain2.setVisibility(view.INVISIBLE);
        get_commu_explain3=view.findViewById(R.id.get_commu_explain3);get_commu_explain3.setVisibility(view.INVISIBLE);
        get_commu_explain4=view.findViewById(R.id.get_commu_explain4);get_commu_explain4.setVisibility(view.INVISIBLE);
        get_commu_explain5=view.findViewById(R.id.get_commu_explain5);get_commu_explain5.setVisibility(view.INVISIBLE);
        final TextView[] get_expain = {get_commu_explain1,get_commu_explain2,get_commu_explain3,get_commu_explain4,get_commu_explain5};
        final ImageView[] get_img ={get_commu_img1,get_commu_img2,get_commu_img3,get_commu_img4,get_commu_img5};

        if (getArguments() != null) {
            // CommunityFragment에서 데이터 받아오기
            title = getArguments().getString("Title");
            writer = getArguments().getString("Writer");
            content = getArguments().getString("Content");
            date = getArguments().getString("Date");
            category = getArguments().getString("Category");
        }
        commu_title.setText(title);
        commu_title.setSelected(true);
        commu_writer.setText(writer);
        commu_content.setText(content);
        commu_date.setText(date);
        if (category.equals("what_eat")) {
            commu_category.setText("뭐 먹지?");
        }else if (category.equals("what_do")) {
            commu_category.setText("뭐 하지?");
        }else if(category.equals("how_do")){
            commu_category.setText("어떻게 하지?");
        }


        // firestore에서 댓글 개수, 사진 정보 가져오기
        firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                commentCount = shot.get(FirebaseID.commu_comment_count).toString();
                                commu_comment_count.setText(commentCount);
                                likeCount = shot.get(FirebaseID.commu_like_count).toString();
                                commu_like_count.setText(likeCount);
                                saveCount = shot.get(FirebaseID.commu_save_count).toString();
                                commu_save_count.setText(saveCount);

                                //사진 업로드
                                Map<String, Object> shot2 = document.getData();
                                for (int i=0; i<5; i++){
                                    if(shot.get((FirebaseID.Url)+i) != null){
                                        get_images=((String)shot2.get((FirebaseID.Url)+i));
                                        Glide.with(getActivity()).load(get_images).into(get_img[i]);
                                        if(shot2.get((FirebaseID.commu_img_explain)+i) !=null){
                                            get_explain_temp= (String) shot2.get((FirebaseID.commu_img_explain)+i);
                                            get_expain[i].setVisibility(view.VISIBLE);
                                            get_expain[i].setText(get_explain_temp);
                                        }
                                    }
                                }
                            } else {
                                Log.d("CommunityPostsFragment", "댓글 개수 가져오기 실패");
                            }
                        } else {
                            Log.d("CommunityPostsFragment", "get failed with ", task.getException());
                        }
                    }
                });

        // '뒤로가기' 버튼 눌렀을 시
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityFragment communityFragment = new CommunityFragment();

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityFragment);
                transaction.commit();
            }
        });

        // '좋아요' 버튼 클릭 시 count 증가
        btn_commu_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 현재의 좋아요 갯수 받아오기
                int like_count;
                like_count = Integer.parseInt(commu_like_count.getText().toString());

                // 버튼이 눌리지 않은 상태를 기본으로 설정
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    commu_like_count.setText(Integer.toString(like_count + 1));
                    if (firebaseAuth.getCurrentUser() != null) {
                        DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title);
                        data.update(FirebaseID.commu_like_count, String.valueOf(like_count + 1));
                    }
                } else {
                    commu_like_count.setText(Integer.toString(like_count - 1));
                    if (firebaseAuth.getCurrentUser() != null) {
                        DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title);
                        data.update(FirebaseID.commu_like_count, String.valueOf(like_count - 1));
                    }
                }
            }
        });

        // '저장' 버튼 클릭했을 때
        btn_commu_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재의 좋아요 갯수 받아오기
                int save_count;
                save_count = Integer.parseInt(commu_save_count.getText().toString());

                // 버튼이 눌리지 않은 상태를 기본으로 설정
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    commu_save_count.setText(Integer.toString(save_count + 1));
                    if (firebaseAuth.getCurrentUser() != null) {
                        DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title);
                        data.update(FirebaseID.commu_save_count, String.valueOf(save_count + 1));
                        //TODO 저장버튼 눌렀을 시 데이터가 어디에 저장될지 구현하기
                    }
                    postsSave();

                } else {
                    commu_save_count.setText(Integer.toString(save_count - 1));
                    if (firebaseAuth.getCurrentUser() != null) {
                        DocumentReference data = firestore.collection(FirebaseID.Community).document(category).collection("sub_Community").document(title);
                        data.update(FirebaseID.commu_save_count, String.valueOf(save_count - 1));
                    }
                }
            }
        });
        // '댓글' 버튼 눌렀을 시
        btn_commu_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CommunityCommentFragment로 데이터 넘기기
                Bundle bundle = new Bundle();
                bundle.putString("Category", category);
                bundle.putString("Title", title);
                bundle.putString("Content", content);
                bundle.putString("Date", date);
                bundle.putString("Writer", writer);

                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityCommentFragment communityCommentFragment = new CommunityCommentFragment();
                communityCommentFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityCommentFragment);
                transaction.commit();
            }
        });
        return view;
    }

    public void postsSave () {
        if (firebaseAuth.getCurrentUser() != null) {
            // DB에 데이터 추가
            Map<String, Object> data = new HashMap<>();
            data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
            data.put(FirebaseID.Email, firebaseAuth.getCurrentUser().getEmail());
            data.put(FirebaseID.category, category);
            data.put(FirebaseID.title, title);
            data.put(FirebaseID.content, content);
            data.put(FirebaseID.commu_date, date);
            data.put(FirebaseID.writer, writer);

            // DB에 저장되는 경로 MyPage->email->savedPosts
            firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.savedPosts).document(title)
                    .set(data, SetOptions.merge());
        }
    }
}





