package com.abbsolute.ma_livu.ToDoList;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abbsolute.ma_livu.Fragments.OnBackPressedListener;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class ToDoWriteMainActivity extends AppCompatActivity {
    OnBackPressedListener listener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_write_main);
        ViewPager pager=findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addItem(new ToDoWriteFragment());
        adapter.addItem(new ToDoWriteFragment2());
        pager.setAdapter(adapter);
    }
    public void setOnBackPressedListener(OnBackPressedListener listener){
        this.listener = listener;
    }
    @Override public void onBackPressed() {
        if(listener!=null){
            listener.onBackPressed();
        }else{
            super.onBackPressed();
        }
    }


    //어댑터 안에서 각각의 아이템을 데이터로서 관리한다
    class PagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(Fragment item){
        items.add(item);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ToDoWriteFragment.newInstance();
            case 1:
                return ToDoWriteFragment2.newInstance();
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
