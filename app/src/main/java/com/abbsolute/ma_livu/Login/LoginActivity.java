package com.abbsolute.ma_livu.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Community.Commu_WriteFragment;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.MyPage.TitleFragment;
import com.abbsolute.ma_livu.MyPage.activeFragment;
import com.abbsolute.ma_livu.MyPage.informationSetFragment;
import com.abbsolute.ma_livu.MyPage.payFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button btn_email_login;
    private TextView btn_signup;

    //구글 로그인에 필요한 변수들
    private SignInButton btn_google; //구글 로그인 버튼
    private FirebaseAuth auth; //파이어베이즈 인증 객체
    private GoogleApiClient googleApiClient; //구글API클라이언트
    private static final int REQ_SIGN_GOOGLE = 100; //구글 로그인 결과 코드

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();


    private HomeFragment homeFragment;

    //MyPage title fragment
    private TitleFragment titleFragment;
    private MyPageFragment myPageFragment;
    private payFragment payFragment;
    private informationSetFragment informationSetFragment;
    private activeFragment activeFragment;
    private Commu_WriteFragment commu_writeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance(); //파이어베이스 인증 객체 초기화

        //이메일로 로그인 버튼을 눌렀을 때
        btn_email_login = findViewById(R.id.btn_email_login);
        btn_email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Login2Activity.class);
                startActivity(intent);
            }
        });

        //회원가입 버튼을 눌렀을 때
        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


        //구글 로그인 설정
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)//프래그먼트로 구현하면 this 말고 getContext로 작성
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        //구글 로그인 버튼을 눌렀을 때
        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,REQ_SIGN_GOOGLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //구글 로그인 과정
        if(requestCode == REQ_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){ //인증결과가 성공적 인지
                GoogleSignInAccount account = result.getSignInAccount(); //account는 구글 로그인 정보를 담고 있음
                resultLogin(account); //로그인 결과값 출력하라는 메소드

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //구글 로그인 과정
    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { //로그인 성공했을 때
                        if(task.isSuccessful()){
                            if (firebaseAuth.getCurrentUser() != null){
                                Map<String,Object> userMap = new HashMap<>();
                                userMap.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                userMap.put(FirebaseID.Email,account.getEmail());
                                userMap.put(FirebaseID.Nickname,account.getDisplayName());
                                String email=account.getEmail();
                                firestore.collection(FirebaseID.user).document(email).set(userMap, SetOptions.merge());

                                //email필요한 fragment에 생성자로 email값 넘겨줌
                                titleFragment = new TitleFragment(email);
                                myPageFragment = new MyPageFragment(email);
                                payFragment = new payFragment(email);
                                informationSetFragment = new informationSetFragment(email);
                                homeFragment = new HomeFragment(email);
                                activeFragment = new activeFragment(email);



                                finish();
                            }
                            Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        }else{ //로그인 실패 했을 때
                            Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}