package com.abbsolute.ma_livu.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup3Activity extends AppCompatActivity {

    private Button btn_next3;
    private ImageButton btn_back;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String email,password,nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        btn_next3= findViewById(R.id.btn_next3);


        //파이어스토어 도큐먼트 이름 넘겨받기
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        nickname=intent.getStringExtra("nickname");

        btn_back=(ImageButton)findViewById(R.id.btn_backkey);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Signup2Activity.class));
            }
        });

        btn_next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(email, password);
            }
        });
    }

    private void createUser(final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //파이어스토어에 정보 저장
                            if (firebaseAuth.getCurrentUser() != null){
                                Map<String,Object> userMap = new HashMap<>();
                                userMap.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                userMap.put(FirebaseID.Email,email);
                                userMap.put(FirebaseID.Nickname,nickname);

                                firestore.collection(FirebaseID.user).document(email).set(userMap,SetOptions.merge());
                                finish();
                            }
                            // 회원가입 성공
                            Intent intent = new Intent(Signup3Activity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {

                        }
                    }
                });
    }
}
