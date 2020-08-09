package com.abbsolute.ma_livu.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.MyPage.TitleFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login2Activity extends AppCompatActivity {

    private Button btn_login;
    private EditText email_login;
    private EditText pass_login;
    private Button btn_findpwd;
    private FirebaseAuth auth; //파이어베이스 인증 객체

    //MyPage title fragment
    private TitleFragment titleFragment;
    private MyPageFragment myPageFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        auth = FirebaseAuth.getInstance(); //파이어베이스 인증 객체 초기화

        titleFragment = (TitleFragment)getSupportFragmentManager().findFragmentById(R.id.main_frame);//titleFragment


        email_login =(EditText) findViewById(R.id.btn_email_login); //이메일 입력
        pass_login =(EditText) findViewById(R.id.pass_login); // 패스워드 입력

        //로그인 버튼을 눌렀을 때
        btn_login =(Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_login.getText().toString();
                //listener안에서 사용하려고 final로 바꿔줬습니다!
                String pwd = pass_login.getText().toString().trim();

                auth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login2Activity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                    titleFragment = new TitleFragment(email);
                                    myPageFragment = new MyPageFragment(email);
                                    Intent intent = new Intent(Login2Activity.this, HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Login2Activity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //비밀번호 찾기 버튼을 눌렀을 떄
        btn_findpwd =(Button)findViewById(R.id.btn_findpwd);
        btn_findpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login2Activity.this, FindpwdActivity.class);
                startActivity(intent);
            }
        });
    }
}