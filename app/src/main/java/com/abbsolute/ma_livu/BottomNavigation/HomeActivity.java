package com.abbsolute.ma_livu.BottomNavigation;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.Alarm.AlarmFragment;
import com.abbsolute.ma_livu.Community.Commu_WriteFragment;
import com.abbsolute.ma_livu.Community.CommunityFragment;

import com.abbsolute.ma_livu.Community.Hot_CommunityFragment;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookFragment;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookWriteFragment;

import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView main_bottom; // 메인으로 고정되는 하단탭
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private Hot_CommunityFragment hotCommunityFragment;
    private Commu_WriteFragment commu_writeFragment;
    private CommunityFragment communityFragment;
    private MyPageFragment myPageFragment;
    private AlarmFragment alarmFragment;
    private GuestBookFragment guestBookFragment;
    private GuestBookWriteFragment guestBookWriteFragment;

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //fragment
        homeFragment = new HomeFragment();
        myPageFragment = new MyPageFragment();
        alarmFragment = new AlarmFragment();

        //커뮤니티 프래그먼트
        hotCommunityFragment = new Hot_CommunityFragment();
        communityFragment = new CommunityFragment();
        commu_writeFragment = new Commu_WriteFragment();

        main_bottom =findViewById(R.id.main_bottom);
        BottomNavigationHelper.disableShiftMode(main_bottom); //  바텀 쉬프트모드 해제

        // 하단바를 눌렀을 때 프래그먼트가 변경되게 함
        main_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_home:
                        setFragment(0);
                        break;
                    case R.id.bottom_community:
                        setFragment(1);
                        break;
                    case R.id.bottom_mypage:
                        setFragment(2);
                        break;
                    case R.id.bottom_alarm:
                        setFragment(3);
                        break;
                }
                return true;
            }
        });

        homeFragment = new HomeFragment();
        communityFragment = new CommunityFragment();
        myPageFragment = new MyPageFragment();
        alarmFragment = new AlarmFragment();
        guestBookFragment = new GuestBookFragment();
        guestBookWriteFragment = new GuestBookWriteFragment();

        setFragment(0); // 첫번째 프래그먼트 화면을 뭘로 띄어 줄 지
    }

    // 프래그먼트 교체가 일어나는 함수
    public void setFragment(int n){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n){
            case 0:
                fragmentTransaction.replace(R.id.main_frame,homeFragment).commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.main_frame, hotCommunityFragment).commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.main_frame,myPageFragment).commit();
                break;
            case 3:
                fragmentTransaction.replace(R.id.main_frame,alarmFragment).commit();
                break;
            
            // 방명록 프래그먼트에서 버튼 눌렀을 
            case 4:
                fragmentTransaction.replace(R.id.main_frame,guestBookFragment);
                fragmentTransaction.commit();
                break;
            case 5:
                fragmentTransaction.replace(R.id.main_frame,guestBookWriteFragment);
                fragmentTransaction.commit();
                break;

            // 커뮤니티 프래그먼트에서 버튼 눌렀을 때
            case 50:
                fragmentTransaction.replace(R.id.main_frame,communityFragment).commit();

                break;
            case 51:
                fragmentTransaction.replace(R.id.main_frame,commu_writeFragment).commit();
                break;
            

        }
    }
}
