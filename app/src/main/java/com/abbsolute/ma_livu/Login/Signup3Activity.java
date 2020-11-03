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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup3Activity extends AppCompatActivity {

    private RadioGroup rg_test1;
    private RadioGroup rg_test2;
    private Button btn_next3;
    private ImageButton btn_back;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String email,password,nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        rg_test1 = findViewById(R.id.rg_test1);
        rg_test2= findViewById(R.id.rg_test2);
        btn_next3= findViewById(R.id.btn_next3);

        //선택된 라디오버튼값 가져오기
       int id = rg_test1.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(id);
        int id2 =rg_test2.getCheckedRadioButtonId();
        RadioButton selectedButton2 = findViewById(id2);

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

    public void SaveTest(){
        if (firebaseAuth.getCurrentUser() != null){
            int id = rg_test1.getCheckedRadioButtonId();
            RadioButton selectedButton = findViewById(id);
            int id2 =rg_test2.getCheckedRadioButtonId();
            RadioButton selectedButton2 = findViewById(id2);

            Map<String,Object> userMap = new HashMap<>();
            userMap.put(FirebaseID.Test1,selectedButton.getText().toString());
            userMap.put(FirebaseID.Test2,selectedButton2.getText().toString());
            firestore.collection(FirebaseID.user).document(email).set(userMap, SetOptions.merge());


        }
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

                                int id = rg_test1.getCheckedRadioButtonId();
                                RadioButton selectedButton = (RadioButton) findViewById(id);
                                int id2 =rg_test2.getCheckedRadioButtonId();
                                RadioButton selectedButton2 =(RadioButton)findViewById(id2);

                                userMap.put(FirebaseID.Test1,selectedButton.getText().toString());
                                userMap.put(FirebaseID.Test2,selectedButton2.getText().toString());
                                firestore.collection(FirebaseID.user).document(email).set(userMap,SetOptions.merge());

                                DocumentReference db = firestore.collection("Customize")
                                        .document(email);
                                db.set(new HashMap<String, Object>());
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
