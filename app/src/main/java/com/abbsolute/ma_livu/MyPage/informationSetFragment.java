package com.abbsolute.ma_livu.MyPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abbsolute.ma_livu.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class informationSetFragment extends Fragment {
    private View view;

    public informationSetFragment(){};

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 정보설정 fragment */
        view = inflater.inflate(R.layout.fragment_information_set, container, false);
        return view;
    }
}
