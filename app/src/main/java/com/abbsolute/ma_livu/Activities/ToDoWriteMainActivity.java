package com.abbsolute.ma_livu.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abbsolute.ma_livu.Fragments.OnBackPressedListener;
import com.abbsolute.ma_livu.Fragments.ToDoWriteFragment;
import com.abbsolute.ma_livu.Fragments.ToDoWriteFragment2;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class ToDoWriteMainActivity extends AppCompatActivity {
    OnBackPressedListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.write_main);
        ViewPager pager=findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ToDoWriteFragment writeFragment1=new ToDoWriteFragment();
        adapter.addItem(writeFragment1);
        ToDoWriteFragment2 writeFragment2=new ToDoWriteFragment2();
        adapter.addItem(writeFragment2);
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
    class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

    public PagerAdapter(@NonNull FragmentManager fm,int behavior) {
        super(fm,behavior);
    }

    public void addItem(Fragment item){
        items.add(item);
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
}
