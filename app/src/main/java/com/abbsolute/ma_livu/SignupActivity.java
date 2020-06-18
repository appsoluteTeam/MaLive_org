package com.abbsolute.ma_livu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private String password_pattern ="^[a-zA-Z0-9]{8,30}$" ; // 영문,숫자 혼용하여 8~30 글자
    private Pattern PASSWORD_PATTERN = Pattern.compile(password_pattern);


    //파이어베이스 인증 객체
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //
    private EditText email_sign;
    private EditText pass_sign;
    private EditText pwd_check;
    private ImageView img_check;
    private ImageView img_check2;
    private Button btn_sign;
    private TextView tv_wanning;

    //
    private String email = "";
    private String password = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        email_sign =(EditText) findViewById(R.id.email_sign);
        pass_sign =(EditText) findViewById(R.id.pass_sign);
        pwd_check =(EditText) findViewById(R.id.pwd_check);
        img_check =(ImageView)findViewById(R.id.img_check);
        img_check2 =(ImageView)findViewById(R.id.img_check2);
        btn_sign =(Button) findViewById(R.id.btn_sign);
        tv_wanning = (TextView) findViewById(R.id.tv_wanning);


        //비밀번호 형식이 어긋날 때
        pass_sign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(PASSWORD_PATTERN.matcher(pass_sign.getText().toString()).matches()){
                    img_check2.setImageResource(R.drawable.check);
                }else{
                    img_check2.setImageResource(R.drawable.x);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //패스워드 규칙 확인할 때 & 다시 입력했을 때
        pwd_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(pass_sign.getText().toString().equals(pwd_check.getText().toString())) {
                    img_check.setImageResource(R.drawable.check);
                } else {
                    img_check.setImageResource(R.drawable.x);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //버튼을 눌렀을 때
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_sign.getText().toString();
                password = pass_sign.getText().toString();

                if(isValidEmail() && isValidPasswd() && isSamePasswd()) {
                    createUser(email, password);
                }
            }
        });

        //이메일 인증 방법을 지침하는 객체
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.abbsolute.ma_livu",
                                true,
                                "12" )
                        .build();
    }


    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            tv_wanning.setText("이메일을 입력해주세요");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            tv_wanning.setText("이메일 형식으로 작성해 주세요");
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            tv_wanning.setText("비밀번호를를 입력해주세요.");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            tv_wanning.setText("비밀번호 형식이 맞지 않습니다.");
            return false;
        } else {
            return true;
        }
    }

    //비밀번호 재입력 일치하는지 검사
    private boolean isSamePasswd(){
        if(pwd_check.getText().toString().equals("")){
            tv_wanning.setText("비밀번호를 다시한번 입력해주세요.");
            return false;
        } else if(!password.equals(pwd_check.getText().toString())){
            tv_wanning.setText("비밀번호가 일치하지 않습니다.");
            return false;
        }else {
            return true;
        }
    }

    //이메일 정보과 파이어베이스와 연결
    private void createUser(final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //파이어스토어에 정보 저장
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user != null){
                                Map<String,Object> userMap = new HashMap<>();
                                userMap.put(FirebaseID.documentID,user.getUid());
                                userMap.put(FirebaseID.Email,email);
                                userMap.put(FirebaseID.Password,password);
                                firestore.collection(FirebaseID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                                finish();
                            }
                            // 회원가입 성공
                            Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // 회원가입 실패
                            Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


