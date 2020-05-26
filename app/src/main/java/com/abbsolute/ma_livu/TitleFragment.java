package com.abbsolute.ma_livu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TitleFragment extends Fragment {
    public static TitleFragment newInstance() {
        Bundle args = new Bundle();
        TitleFragment titleFragment = new TitleFragment();

        return titleFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup=(ViewGroup)inflater.inflate(R.layout.fragment_title,container,false);
        return viewGroup;
    }
}
