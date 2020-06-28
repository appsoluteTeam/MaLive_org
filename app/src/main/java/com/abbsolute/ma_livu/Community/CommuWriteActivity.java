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

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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
                    data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                    data.put(FirebaseID.title,et_title.getText().toString());
                    data.put(FirebaseID.writer,et_writer.getText().toString());
                    data.put(FirebaseID.content,et_content.getText().toString());
                    firestore.collection(FirebaseID.Community).document(et_title.getText().toString()).set(data, SetOptions.merge());
                    Toast.makeText(CommuWriteActivity.this, "글쓰기 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });


    }
}
