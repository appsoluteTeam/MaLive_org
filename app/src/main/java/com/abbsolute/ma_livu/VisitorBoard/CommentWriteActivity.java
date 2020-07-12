package com.abbsolute.ma_livu.VisitorBoard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abbsolute.ma_livu.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommentWriteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public CommentAdapter adapter;
    private ArrayList<CommentItem> arrayList;

    private EditText Comment;
    private Button btn_insert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_write);

        Comment = findViewById(R.id.WriteComment);
        btn_insert = findViewById(R.id.btn_insert);

        // 글 작성 시 DB에 데이터 추가하기
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {

                    Intent intent = getIntent();
//                    String CommentCount = intent.getStringExtra("CommentCount");
                    int CommentCount = intent.getExtras().getInt("CommentCount");

                    // 게시글 작성 시간 받아오기
                    long now = System.currentTimeMillis();
                    SimpleDateFormat dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Calendar date = Calendar.getInstance();

                    // 게시글 Number 증가
                    String newCount = String.valueOf(CommentCount + 1);

                    // DB에 데이터 추가
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
                    data.put(FirebaseID.num, newCount);
                    data.put(FirebaseID.name, "name");
                    //TODO 후에 icon부분 사용자가 직접 설정한 아이콘으로 변경
                    data.put(FirebaseID.icon, "https://firebasestorage.googleapis.com/v0/b/malivelogin.appspot.com/o/profile.png?alt=media&token=d2130f80-1023-440f-a9cc-6a183f88a975");
                    data.put(FirebaseID.comment, Comment.getText().toString());
                    data.put(FirebaseID.date, dateform.format(date.getTime()));

                    firestore.collection(FirebaseID.GuestBook).document(newCount).set(data, SetOptions.merge());
//                    firestore.collection(FirebaseID.GuestBook).document(Comment.getText().toString()).set(data, SetOptions.merge());
                    Toast.makeText(CommentWriteActivity.this, "글쓰기 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
