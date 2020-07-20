package com.abbsolute.ma_livu.BottomNavigation;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.Alarm.AlarmFragment;
import com.abbsolute.ma_livu.Community.CommunityFragment;
import com.abbsolute.ma_livu.Home.HomeFragment;

import com.abbsolute.ma_livu.Home.ToDoList.ToDoFragment;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView main_bottom; // 메인으로 고정되는 하단탭
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private CommunityFragment communityFragment;
    private MyPageFragment myPageFragment;
    private AlarmFragment alarmFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


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
        setFragment(0); // 첫번째 프래그먼트 화면을 뭘로 띄어 줄 지
    }

    // 프래그먼트 교체가 일어나는 함수
    public void setFragment(int n){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n){
            case 0:
                fragmentTransaction.replace(R.id.main_frame,homeFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.main_frame,communityFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.main_frame,myPageFragment);
                fragmentTransaction.commit();
                break;
            case 3:
                fragmentTransaction.replace(R.id.main_frame,alarmFragment);
                fragmentTransaction.commit();
                break;
            //투두리스트 프래그먼트에서 버튼 눌렀을 때
            case 100:
                ToDoFragment toDoFragment=new ToDoFragment();
                fragmentTransaction.replace(R.id.main_frame,toDoFragment);
                fragmentTransaction.commit();
                break;

        }
    }
}
