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

import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Community.Hot_CommunityFragment;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookFragment;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookWriteFragment;

import com.abbsolute.ma_livu.Home.HomeFragment;

import com.abbsolute.ma_livu.Home.ToDoList.ToDoFixModifyingFragment;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoFragment;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoWriteFragment;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoWriteMainFragment;
import com.abbsolute.ma_livu.MyPage.DataListener;
import com.abbsolute.ma_livu.MyPage.EmailListener;
import com.abbsolute.ma_livu.MyPage.MyPageDataListener;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.MyPage.TitleFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements MyPageDataListener, DataListener, EmailListener {

    private BottomNavigationView main_bottom; // 메인으로 고정되는 하단탭
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private Hot_CommunityFragment hotCommunityFragment;
    private Commu_WriteFragment commu_writeFragment;
    private CommunityFragment communityFragment;
    private CommunityPostsFragment communityPostsFragment;
    private MyPageFragment myPageFragment;
    private AlarmFragment alarmFragment;

    private TitleFragment titleFragment;

    /* myPage관련 변수 */
    private int myPageCategoryIndex;  //  마이페이지 카테고리 인덱스
    private String email;

    private GuestBookFragment guestBookFragment;
    private GuestBookWriteFragment guestBookWriteFragment;

    //투두 리스트 화면
    private ToDoFragment toDoFragment;
    //투두 작성 메인 화면
    private ToDoWriteMainFragment toDoWriteMainFragment;
    //고정리스트 작성 화면
    private ToDoFixModifyingFragment toDoFixModifyingFragment;
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
        communityPostsFragment = new CommunityPostsFragment();


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

        titleFragment = new TitleFragment();

        guestBookFragment = new GuestBookFragment();
        guestBookWriteFragment = new GuestBookWriteFragment();

        //투두 리스트 화면
        toDoFragment=new ToDoFragment();
        //투두 작성 메인 화면
        toDoWriteMainFragment=new ToDoWriteMainFragment();
        //고정리스트 수정 화면
        toDoFixModifyingFragment=new ToDoFixModifyingFragment();

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
            case 52:
                fragmentTransaction.replace(R.id.main_frame,communityPostsFragment).commit();
                break;
            //투두 프래그먼트로 이동
            case 100:
                fragmentTransaction.replace(R.id.main_frame,toDoFragment).commit();
                break;
            //투두 작성메인 화면
            case 101:
                fragmentTransaction.replace(R.id.main_frame,toDoWriteMainFragment).commit();
                break;
            //고정리스트
            case 102:
                fragmentTransaction.replace(R.id.main_frame,toDoFixModifyingFragment).commit();
                break;

        }
    }

    /* 마이페이지 관련 */

    /* myPageFragment에서 데이터 받는 메소드*/
    public void myPageDataSet(int myPageCategory){
        myPageCategoryIndex = myPageCategory;
    }

    /*각 프래그먼트에서 데이터 받는 메소드*/
    public void dataSet(String title, int index, int category){
        //TitleFragment에 데이터 전달해줘야한다.
        titleFragment.dataSet(title,index,category);
    }

    public void  emailSet(String email){
        //TitleFragment에 SignUpActivty에서 받은 이메일값 전달
        titleFragment.emailSet(email);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnMyPage_title:
                setMyPageFragment(0);
                break;
            case R.id.btnMyPage_pay:
                setMyPageFragment(1);
                break;
            case R.id.btnMyPage_active:
                setMyPageFragment(2);
                break;
            case R.id.btnMyPage_friend:
                setMyPageFragment(3);
                break;
        }
    }
    /* myPage카테고리에 따라서 fragment 교체 */
    public void setMyPageFragment(int myPageCategoryIndex){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (myPageCategoryIndex){
            case 0:
                fragmentTransaction.replace(R.id.main_frame,titleFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
}
