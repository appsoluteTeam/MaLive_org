package com.abbsolute.ma_livu.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private String email, password,nickname;
    private ImageButton btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        //파이어스토어 도큐먼트 이름 넘겨받기
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        nickname="";


        tv_warning2 = (TextView) findViewById(R.id.tv_wanning2);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        btn_next2 = (Button) findViewById(R.id.btn_next2);

        btn_back=(ImageButton)findViewById(R.id.btn_backkey);
        btn_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //getFragmentManager().popBackStack();
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        btn_next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname=et_nickname.getText().toString();
                isSameNickname();
            }
        });
    }

    private void isSameNickname() {
        firestore.collection(FirebaseID.user).whereEqualTo("nickname", et_nickname.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean nicknameDouble = false;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                //겹치는 이메일이 있는것
                                Map<String, Object> shot = snapshot.getData();
                                tv_warning2.setText("이미 존재하는 닉네임입니다.");
                                nicknameDouble = true;
                                break;
                            }
                            if(nicknameDouble == false){//중복이 아닐 때
                                if(isValidNickname()) {//유효성 검사
                                    Intent intent = new Intent(Signup2Activity.this, Signup3Activity.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("password",password);
                                    intent.putExtra("nickname",nickname);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });
    }

    private boolean isValidNickname() {
        if (nickname.isEmpty()) {
            // 이메일 공백
            tv_warning2.setText("닉네임을 입력해주세요");
            return false;
        }else if(nickname.length()<1 ){
            tv_warning2.setText("2자~10자 사이의 닉네임을 설정해주세요");
            return false;
        }else {
            return  true;
        }
    }


}
