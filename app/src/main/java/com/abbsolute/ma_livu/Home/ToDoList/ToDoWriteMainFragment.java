package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class ToDoWriteMainFragment extends Fragment implements OnBackPressedListener {
    OnBackPressedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view=(ViewGroup)inflater.inflate(R.layout.todo_write_main,container,false);
        SharedPreferences pf = getContext().getSharedPreferences("pref2", Activity.MODE_PRIVATE);
        ViewPager pager = view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addItem(new ToDoWriteFragment());
        adapter.addItem(new ToDoFixWriteFragment());
        pager.setAdapter(adapter);
        return view;
    }



    @Override
    public void onBackPressed() {
        if (listener != null) {
            listener.onBackPressed();
        }
    }


    //어댑터 안에서 각각의 아이템을 데이터로서 관리한다
    class PagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ToDoWriteFragment.newInstance();
                case 1:
                    return ToDoFixWriteFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}

