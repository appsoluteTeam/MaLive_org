package com.abbsolute.ma_livu.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private String password_pattern ="^[a-zA-Z0-9]{8,30}$" ; // 영문,숫자 혼용하여 8~30 글자
    private Pattern PASSWORD_PATTERN = Pattern.compile(password_pattern);

    //파이어베이스 인증 객체
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //
    private EditText email_sign;
    private TextView tv_top;

    private EditText pass_sign;
    private EditText pwd_check;
    private ImageView img_check;
    private ImageView img_check2;

    private Button btn_next1;
    private TextView tv_wanning;
    private ImageButton btn_back;

    //
    public String email = "";
    public String password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email_sign = findViewById(R.id.email_sign);
        pass_sign = findViewById(R.id.pass_sign);
        pwd_check = findViewById(R.id.pwd_check);
        img_check = findViewById(R.id.img_check);
        img_check2 = findViewById(R.id.img_check2);
        btn_next1 = findViewById(R.id.btn_next1);
        tv_wanning = findViewById(R.id.tv_wanning);
        tv_top= findViewById(R.id.tv_top);


        /*TODO:: 이메일 중복검사
          1. 버튼을 눌렀을 대 calldata를 호출해서 입력한 email이랑 같은 지 확인한다.
             같은게 존재하면 emailtemp에 값을 저장
             존재하지 않으면 emailtemp 에 null값 저장
          2. overlabemail() 을 호출해서 emailtemp값에 따라 경고텍스트 띄어주고
             중복이 안됐을 때만 return을 true로 해줌
        */

        btn_back=(ImageButton)findViewById(R.id.btn_backkey);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        //다음 버튼을 눌렀을 때
        btn_next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_sign.getText().toString();
                password = pass_sign.getText().toString();
                CheckSameEmail();
            }
        });

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

    }

    private void CheckSameEmail() {
        firestore.collection("user").whereEqualTo("email", email_sign.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean EmailDouble = false;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                //겹치는 이메일이 있는것
                                Map<String, Object> shot = snapshot.getData();
                                tv_wanning.setText("이미 등록된 계정입니다.");
                                EmailDouble = true;
                                break;
                            }
                            if(EmailDouble == false){//중복이 아닐 때
                                if(isValidEmail() && isValidPasswd() && isSamePasswd()) {//유효성 검사
                                    Intent intent = new Intent(SignupActivity.this, Signup2Activity.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("password",password);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });
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


}