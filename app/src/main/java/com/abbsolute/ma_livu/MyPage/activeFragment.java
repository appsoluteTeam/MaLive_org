package com.abbsolute.ma_livu.MyPage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.R;

import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/* 활동 창 fragment */

public class activeFragment extends Fragment {
    private View view;

    public static Stack<Fragment> fragmentStack;

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private Button btn_back;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mypage_active, container, false);

        btn_back = view.findViewById(R.id.btn_back);

        fragmentStack = HomeActivity.fragmentStack;
        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fm.beginTransaction();
                switch (v.getId()) {
                    case R.id.btn_back:
                        Log.d("activeFragment back","back buttonclick");
                        Fragment nextFragment = fragmentStack.pop();
                        fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
                        break;
                }
            }
        };

        /* 각 버튼 setOnClickListener해주기 */
        btn_back.setOnClickListener(onClickListener);
        return view;
    }
}