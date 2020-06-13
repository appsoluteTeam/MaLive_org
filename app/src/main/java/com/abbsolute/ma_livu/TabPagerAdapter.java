package com.abbsolute.ma_livu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.abbsolute.ma_livu.Fragments.CommunityFragment;
import com.abbsolute.ma_livu.Fragments.HomeFragment;
import com.abbsolute.ma_livu.Fragments.ProfileFragment;
import com.abbsolute.ma_livu.Fragments.TitleFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    public TabPagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm, tabCount);
        this.tabCount=tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return CommunityFragment.newInstance();
            case 2:
                return TitleFragment.newInstance();
            case 3:
                return ProfileFragment.newInstance();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
