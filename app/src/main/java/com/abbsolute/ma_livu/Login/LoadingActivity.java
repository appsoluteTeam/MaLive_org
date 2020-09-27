package com.abbsolute.ma_livu.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Community.Commu_WriteFragment;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment.CommunityCommentCommentFragment;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentFragment;
import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookWriteFragment;
import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.MainActivity;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.MyPage.TitleFragment;
import com.abbsolute.ma_livu.MyPage.activeFragment;
import com.abbsolute.ma_livu.MyPage.informationSetFragment;
import com.abbsolute.ma_livu.MyPage.payFragment;
import com.abbsolute.ma_livu.R;

public class LoadingActivity extends AppCompatActivity {

    private HomeFragment homeFragment;

    //MyPage title fragment
    private TitleFragment titleFragment;
    private MyPageFragment myPageFragment;
    private payFragment payFragment;
    private informationSetFragment informationSetFragment;
    private activeFragment activeFragment;

    //Community Fragment
    private Commu_WriteFragment commu_writeFragment;
    private CommunityCommentFragment communityCommentFragment;
    private CommunityCommentCommentFragment communityCommentCommentFragment;
    private CommunityPostsFragment communityPostsFragment;

    //GuestBook Fragment
    private GuestBookWriteFragment guestBookWriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        startLoading();
    }

    private void startLoading() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                String email = getAuthLoginEmil();
                if(email.length() == 0) {//sharedPreference "email_id"값에 저장 안되어있음, 최초로그인
                    intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                }else{//저장되어있음 , 자동로그인하기

                    //email필요한 fragment에 생성자로 email값 넘겨줌
                    titleFragment = new TitleFragment(email);
                    myPageFragment = new MyPageFragment(email);
                    payFragment = new payFragment(email);
                    informationSetFragment = new informationSetFragment(email);
                    homeFragment = new HomeFragment(email);
                    activeFragment = new activeFragment(email);
                    commu_writeFragment =new Commu_WriteFragment(email);
                    communityCommentFragment = new CommunityCommentFragment(email);
                    communityCommentCommentFragment = new CommunityCommentCommentFragment(email);
                    communityPostsFragment = new CommunityPostsFragment(email);
                    guestBookWriteFragment = new GuestBookWriteFragment(email);

                    intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 2000);
    }

    private String getAuthLoginEmil(){
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String email = sharedPreferences.getString("email_id","");
        return email;
    }
}