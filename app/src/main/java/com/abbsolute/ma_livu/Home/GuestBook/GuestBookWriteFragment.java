package com.abbsolute.ma_livu.Home.GuestBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GuestBookWriteFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public CommentAdapter adapter;
    private ArrayList<CommentItem> arrayList;

    private EditText Comment;
    private Button btn_back;
    private Button btn_insert;

    // 값 받아오는 변수들
    private int CommentCount;
    private static String str_nickname, email;
    private SimpleDateFormat dateform;
    private Calendar date;

    public GuestBookWriteFragment(){};
    public GuestBookWriteFragment(String email) {
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
        View view = inflater.inflate(R.layout.fragment_guestbook_write, container, false);

        Comment = view.findViewById(R.id.WriteComment);
        btn_insert = view.findViewById(R.id.btn_insert);
        btn_back = view.findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '뒤로가기' 버튼 누를 시 화면 전환
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                GuestBookFragment guestBookFragment = new GuestBookFragment();

                transaction.replace(R.id.main_frame, guestBookFragment);
                transaction.commit();
            }
        });

        // 게시글 Count 받아오기
        if(getArguments() != null){
            CommentCount = getArguments().getInt("CommentCount");
        }

        // 글 작성 시 DB에 데이터 추가하기
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (firebaseAuth.getCurrentUser() != null) {

                        // 게시글 작성 시간 받아오기
                        long now = System.currentTimeMillis();
                        dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = Calendar.getInstance();
                        // 게시글 Number 증가
                        CommentCount++;

                        // DB에 데이터 추가
                        Map<String, Object> data = new HashMap<>();
                        data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
                        data.put(FirebaseID.num, CommentCount);
                        data.put(FirebaseID.name, str_nickname);
                        //TODO 후에 icon부분 사용자가 직접 설정한 아이콘으로 변경
                        data.put(FirebaseID.icon, "https://firebasestorage.googleapis.com/v0/b/malivelogin.appspot.com/o/profile.png?alt=media&token=d2130f80-1023-440f-a9cc-6a183f88a975");
                        data.put(FirebaseID.comment, Comment.getText().toString());
                        data.put(FirebaseID.date, dateform.format(date.getTime()));

                        firestore.collection(FirebaseID.GuestBook).document(Comment.getText().toString()).set(data, SetOptions.merge());
                    }
                ((HomeActivity)getActivity()).setFragment(4);
            }
        });
        return view;
    }
}
