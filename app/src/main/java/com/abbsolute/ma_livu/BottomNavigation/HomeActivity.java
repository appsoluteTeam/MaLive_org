package com.abbsolute.ma_livu.BottomNavigation;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.Alarm.AlarmFragment;
import com.abbsolute.ma_livu.Community.Commu_WriteFragment;
import com.abbsolute.ma_livu.Community.CommunityFragment;

import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Community.Hot_CommunityFragment;


import com.abbsolute.ma_livu.Home.HomeFragment;

import com.abbsolute.ma_livu.MyPage.DataListener;
import com.abbsolute.ma_livu.MyPage.MyPageDataListener;
import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.MyPage.TitleFragment;
import com.abbsolute.ma_livu.MyPage.payFragment;
import com.abbsolute.ma_livu.MyPage.activeFragment;
import com.abbsolute.ma_livu.MyPage.informationSetFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Stack;

public class HomeActivity extends AppCompatActivity implements MyPageDataListener, DataListener{

    //fragment저장할 stack

    private FrameLayout main_frame;
    private FrameLayout unity_frame;
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
    private payFragment payFragment;
    private activeFragment activeFragment;
    private informationSetFragment informationSetFragment;

    //출석체크 관련 변수
    public static final long milli24hour = 86400000; //24시간 초기준 상수
    private long currentMills;
    private long beforeMills;


    /* myPage관련 변수 */
    private int myPageCategoryIndex;  //  마이페이지 카테고리 인덱스
    private String email;

    private int count;
    //todoList관련 뒤로가기 이벤트
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //main_frame
        main_frame = findViewById(R.id.main_frame);

        //기본 fragment
        homeFragment = new HomeFragment();
        myPageFragment = new MyPageFragment();
        alarmFragment = new AlarmFragment();

        //커뮤니티 프래그먼트
        hotCommunityFragment = new Hot_CommunityFragment();
        communityFragment = new CommunityFragment();
        commu_writeFragment = new Commu_WriteFragment();
        communityPostsFragment = new CommunityPostsFragment();

        //타이틀 프래그먼트
        titleFragment = new TitleFragment();

        /// alarm 프래그먼트들//
//        alarmFragmentAllLook=new AlarmFragmentAllLook();
//
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

        /*마이페이지 관련 fragment*/
        titleFragment = new TitleFragment();
        payFragment = new payFragment();
        informationSetFragment = new informationSetFragment();
        activeFragment = new activeFragment();

       // setFragment(0); // 첫번째 프래그먼트 화면을 뭘로 띄어 줄 지

        getSupportFragmentManager().beginTransaction().add(R.id.unity_frame,homeFragment).commit();
    }

    // 프래그먼트 교체가 일어나는 함수
    public void setFragment(int n) {
        fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n) {
            case 0:
                main_frame.setVisibility(View.INVISIBLE);
                if(homeFragment.isHidden()) {
                    fragmentTransaction.show(homeFragment).commit();
                }
       //         fragmentTransaction.replace(R.id.main_frame, homeFragment).commit();

                break;
            case 1:
                main_frame.setVisibility(View.VISIBLE);
                if(!homeFragment.isHidden()){
                    fragmentTransaction.hide(homeFragment).commit();
                }

                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.main_frame, hotCommunityFragment).commit();
                break;
            case 2:
                main_frame.setVisibility(View.VISIBLE);
                if(!homeFragment.isHidden()){
                    fragmentTransaction.hide(homeFragment).commit();
                }

                //가장최신의 stack을 pop 해준다..mypage를 또 팝해주는꼴
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.main_frame, myPageFragment).commit();
                break;
            case 3:
                main_frame.setVisibility(View.VISIBLE);
                if(!homeFragment.isHidden()){
                    fragmentTransaction.hide(homeFragment).commit();
                }
                fragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.main_frame,alarmFragment).commit();
                break;

            // 커뮤니티 프래그먼트에서 버튼 눌렀을 때
            case 50:
                fragmentTransaction.replace(R.id.main_frame, communityFragment).addToBackStack(null).commit();
                break;
            case 51:
                fragmentTransaction.replace(R.id.main_frame, commu_writeFragment).addToBackStack(null).commit();
                break;
            case 52:
                fragmentTransaction.replace(R.id.main_frame, communityPostsFragment).addToBackStack(null).commit();
                break;
            case 201:
                fragmentTransaction.replace(R.id.main_frame,alarmFragment);
                fragmentTransaction.commit();
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
            case R.id.btnMyPage_informationSet:
                setMyPageFragment(4);
                break;
        }
    }

    /* myPage카테고리에 따라서 fragment 교체 */
    /* 0:칭호 , 1:결제, 2:활동 , 3:친구, 4:정보설정 창*/
    public void setMyPageFragment(int myPageCategoryIndex){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (myPageCategoryIndex){
            case 0:
                fragmentTransaction.replace(R.id.main_frame,titleFragment);
                fragmentTransaction.addToBackStack(null).commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.main_frame,payFragment);
                fragmentTransaction.addToBackStack(null).commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.main_frame,activeFragment);
                fragmentTransaction.addToBackStack(null).commit();
                break;
            case 3:
                break;
            case 4:
                fragmentTransaction.replace(R.id.main_frame,informationSetFragment);
                fragmentTransaction.addToBackStack(null).commit();
                break;
        }
    }
    //ToDoList 뒤로가기를 위한 함수
/*
    public void setOnBackPressedListener(OnBackPressedListener listener){ this.listener = listener; }
    @Override
    public void onBackPressed() {

        //커스터마이즈 뒤로가기
        if(getSupportFragmentManager().findFragmentById(R.id.customize_frame).isVisible()){
            Fragment ft = getSupportFragmentManager().findFragmentById(R.id.customize_frame);
            getSupportFragmentManager().beginTransaction().remove(ft).commit();
            return;
        }
        if(listener!=null){
            listener.onBackPressed();
            listener=null;
        }
        else
            super.onBackPressed();

    }
*/


}
