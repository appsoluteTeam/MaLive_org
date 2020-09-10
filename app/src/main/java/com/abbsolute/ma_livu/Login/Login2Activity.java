package com.abbsolute.ma_livu.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Community.Commu_WriteFragment;
import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.MyPage.TitleFragment;
import com.abbsolute.ma_livu.MyPage.activeFragment;
import com.abbsolute.ma_livu.MyPage.payFragment;
import com.abbsolute.ma_livu.MyPage.informationSetFragment;
import com.abbsolute.ma_livu.MyPage.activeFragment;

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

    private HomeFragment homeFragment;

    //MyPage title fragment
    private TitleFragment titleFragment;
    private MyPageFragment myPageFragment;
    private payFragment payFragment;
    private informationSetFragment informationSetFragment;
    private activeFragment activeFragment;
    private Commu_WriteFragment commu_writeFragment;
    private activeFragment activeFragment;


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
                String pwd = pass_login.getText().toString().trim();

                auth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login2Activity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                                   //email필요한 fragment에 생성자로 email값 넘겨줌
                                    titleFragment = new TitleFragment(email);
                                    myPageFragment = new MyPageFragment(email);
                                    payFragment = new payFragment(email);
                                    informationSetFragment = new informationSetFragment(email);
                                    homeFragment = new HomeFragment(email);
                                    activeFragment = new activeFragment(email);

                                    commu_writeFragment =new Commu_WriteFragment(email);

                                    Intent intent = new Intent(Login2Activity.this, HomeActivity.class);
                                    SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email_id",email);
                                    editor.commit();
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