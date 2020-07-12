package com.abbsolute.ma_livu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abbsolute.ma_livu.ToDoList.ToDoMainActivity;
import com.abbsolute.ma_livu.Community.HotCommuActivity;
import com.abbsolute.ma_livu.Login.LoginActivity;
import com.abbsolute.ma_livu.VisitorBoard.GuestBookActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView tv_result; // 닉네임 text
    private ImageView iv_profile; //이미지 뷰
    private Button btn_logout; //로그아웃 버튼
    private Button btn_community; //커뮤니티 버튼
    private Button btn_todo; // 투두버튼
    private Button btn_guestBook; // 방명록 버튼
    private Button btn_myPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String nickName = intent.getStringExtra("nickname");
        String photoURL = intent.getStringExtra("photo");

        tv_result = findViewById(R.id.tv_result);
        tv_result.setText(nickName);

        //구글로그인 했을 때 이미지 가져오기
        iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this).load(photoURL).into(iv_profile); //글라이드를 이용해서 이미지url 받아오기

        //커뮤니티 버튼을 눌렀을 때
        btn_community =(Button) findViewById(R.id.btn_community);
        btn_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HotCommuActivity.class);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "커뮤니티 입성!", Toast.LENGTH_SHORT).show();
            }
        });

        //투두리스트 버튼을 눌렀을 때
        btn_todo = (Button) findViewById(R.id.goto_todo_button);
        btn_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ToDoMainActivity.class);
                startActivity(intent);
            }
        });

        //방명록 버튼을 눌렀을 때
        btn_guestBook = (Button) findViewById(R.id.btn_guestBook);
        btn_guestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GuestBookActivity.class);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "방명록으로 이동!", Toast.LENGTH_SHORT).show();
            }
        });

        //마이페이지 버튼을 눌렀을 때
        btn_myPage = (Button)findViewById(R.id.myPage);
        btn_myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, myPage.class);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "마이페이지로 이동!", Toast.LENGTH_SHORT).show();
            }
        });

        //로그아웃 버튼을 눌렀을 때
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
