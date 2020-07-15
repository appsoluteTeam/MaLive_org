package com.abbsolute.ma_livu.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup3Activity extends AppCompatActivity {

    private RadioGroup rg_test1;
    private RadioGroup rg_test2;
    private Button btn_next3;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        rg_test1 =(RadioGroup) findViewById(R.id.rg_test1);
        rg_test2=(RadioGroup)findViewById(R.id.rg_test2);
        btn_next3=(Button)findViewById(R.id.btn_next3);

        //선택된 라디오버튼값 가져오기
       int id = rg_test1.getCheckedRadioButtonId();
        RadioButton selectedButton = (RadioButton) findViewById(id);
        int id2 =rg_test2.getCheckedRadioButtonId();
        RadioButton selectedButton2 =(RadioButton)findViewById(id2);

        //파이어스토어 도큐먼트 이름 넘겨받기
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        btn_next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTest();
            }
        });
    }

    public void SaveTest(){
        if (firebaseAuth.getCurrentUser() != null){
            int id = rg_test1.getCheckedRadioButtonId();
            RadioButton selectedButton = (RadioButton) findViewById(id);
            int id2 =rg_test2.getCheckedRadioButtonId();
            RadioButton selectedButton2 =(RadioButton)findViewById(id2);

            Map<String,Object> userMap = new HashMap<>();
            userMap.put(FirebaseID.Test1,selectedButton.getText().toString());
            userMap.put(FirebaseID.Test2,selectedButton2.getText().toString());

            firestore.collection(FirebaseID.user).document(email).set(userMap, SetOptions.merge());
            Intent intent = new Intent(Signup3Activity.this, LoginActivity.class);
            Toast.makeText(this.getApplicationContext(),"회원가입 성공", Toast.LENGTH_SHORT);
            startActivity(intent);
        }
    }
}
