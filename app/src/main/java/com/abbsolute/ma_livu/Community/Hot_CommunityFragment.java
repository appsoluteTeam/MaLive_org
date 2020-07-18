package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.MainActivity;
import com.abbsolute.ma_livu.R;

public class Hot_CommunityFragment extends Fragment {

    private View view;
    private Button btn_text_more;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hot_community_fragment,container,false);

        //커뮤니티에서 버튼을 눌렀을 때
        btn_text_more = (Button)view.findViewById(R.id.btn_more_text);
        btn_text_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).onClickButton(0);
            }
        });
        return view;
    }
}
