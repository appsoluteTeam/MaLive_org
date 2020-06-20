package com.abbsolute.ma_livu.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindpwdActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText et_sendemail;
    private Button btn_sendeamil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);

        firebaseAuth = FirebaseAuth.getInstance();
        et_sendemail =(EditText)findViewById(R.id.et_sendemail);
        btn_sendeamil=(Button)findViewById(R.id.btn_sendemail);
        btn_sendeamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 재설정 이메일 보내기
                String emailAddress = et_sendemail.getText().toString().trim();
                firebaseAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(FindpwdActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                } else {
                                    Toast.makeText(FindpwdActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });




    }
}
