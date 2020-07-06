package com.abbsolute.ma_livu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.R;

public class CommunityFragment extends Fragment {
    public static CommunityFragment newInstance() {
        Bundle args = new Bundle();
        CommunityFragment communityFragment = new CommunityFragment();

        return communityFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup=(ViewGroup)inflater.inflate(R.layout.fragment_community,container,false);
        return viewGroup;
    }
}
