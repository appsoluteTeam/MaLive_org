package com.abbsolute.ma_livu.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FindpwdActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private EditText et_sendemail;
    private Button btn_sendeamil;
    private ImageButton btn_back;
    private String emailAddress;
    private TextView tv_wanning3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);

        tv_wanning3=findViewById(R.id.tv_wanning3);

        btn_back=(ImageButton)findViewById(R.id.btn_backkey);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                //startActivity(new Intent(getApplicationContext(), Login2Activity.class));
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        et_sendemail = findViewById(R.id.et_sendemail);
        btn_sendeamil= findViewById(R.id.btn_sendemail);
        btn_sendeamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 재설정 이메일 보내기
                emailAddress = et_sendemail.getText().toString().trim();
                checkemail();
            }
        });

    }

    private void checkemail() {
        firestore.collection("user").whereEqualTo("email",emailAddress)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean findEmailDouble = false;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                Map<String, Object> shot = snapshot.getData();
                                findEmailDouble = true ;
                                break;
                            }
                            if(emailAddress.isEmpty()){
                                tv_wanning3.setText("이메일을 입력해주세요.");
                            } else if(findEmailDouble == false){//중복이 아닐 때
                                tv_wanning3.setText("가입되지 않은 이메일 입니다.");
                            }else{
                                firebaseAuth.sendPasswordResetEmail(emailAddress)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(FindpwdActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                } else {
                                                    tv_wanning3.setText("메일 보내기를 실패했습니다. \n 다시 시도해주세요.");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
