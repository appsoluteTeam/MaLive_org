package com.abbsolute.ma_livu.Community;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbsolute.ma_livu.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CommuWriteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // 작성자UID를 가져오기 위해서 선언
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // 파이어스토어를 사용하기 위해서 선언
    private EditText et_title;
    private EditText et_writer;
    private EditText et_content;
    private Button btn_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_write);

        et_title=findViewById(R.id.et_title);
        et_writer=findViewById(R.id.et_writer);
        et_content=findViewById(R.id.et_content);
        btn_upload = findViewById(R.id.btn_upload);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Map<String,Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid()); // FirebaseID 라는 클래스에서 선언한 필드이름에 , 사용자 UID를 저장
                    data.put(FirebaseID.title,et_title.getText().toString()); // title 이란 필드이름으로 작성한 제목 저장
                    data.put(FirebaseID.writer,et_writer.getText().toString());
                    data.put(FirebaseID.content,et_content.getText().toString());
                    firestore.collection(FirebaseID.Community)
                            .document(et_title.getText().toString()).set(data, SetOptions.merge()); // Community 라는 컬렉션에 title를 문서로 설정해서 저장
                    Toast.makeText(CommuWriteActivity.this, "글쓰기 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });


    }
}
