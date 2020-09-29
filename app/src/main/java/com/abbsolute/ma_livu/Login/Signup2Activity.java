package com.abbsolute.ma_livu.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup2Activity extends AppCompatActivity {

    //닉네임입력창 변수
    private EditText et_nickname;
    private Button btn_next2;
    private TextView tv_warning2;

    //파이어스토에 저장하기 위한 변수
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String email, password;
    private String nicknametemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        //파이어스토어 도큐먼트 이름 넘겨받기
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        tv_warning2 = (TextView) findViewById(R.id.tv_wanning2);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        btn_next2 = (Button) findViewById(R.id.btn_next2);
        btn_next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSameNickname();
                if(nicknametemp == null){
                    Intent intent = new Intent(Signup2Activity.this, Signup3Activity.class);
                    intent.putExtra("nickname", et_nickname.getText().toString());
                    intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    startActivity(intent);
                }
            }
        });

    }

    //파이어스토어에 닉네임 저장
    public void Savenickname() {
        if (firebaseAuth.getCurrentUser() != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put(FirebaseID.Nickname, et_nickname.getText().toString());
            firestore.collection(FirebaseID.user).document(email).set(userMap, SetOptions.merge());
            Intent intent = new Intent(Signup2Activity.this, Signup3Activity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }
    }

    private void isSameNickname() {
        firestore.collection(FirebaseID.user).whereEqualTo("nickname", et_nickname.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    tv_warning2.setText("이미 등록된 닉네임입니다..");
                                    nicknametemp = String.valueOf(shot.get("email"));
                                }
                            }
                        } else {
                            nicknametemp = null;
                        }
                    }
                });
    }
}
