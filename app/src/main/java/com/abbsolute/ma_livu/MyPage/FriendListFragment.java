package com.abbsolute.ma_livu.MyPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.R;

public class FriendListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.friend_list_fragment,container,false);
        Button backButton=view.findViewById(R.id.btn_back);
        //뒤로가기 구현
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                MyPageFragment myPageFragment=new MyPageFragment();
                fragmentTransaction.replace(R.id.main_frame,myPageFragment);
                fragmentTransaction.commit();
            }
        });
        //
        return view;
    }
}
