package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentFragment;
import com.abbsolute.ma_livu.R;

public class CommunityPostsFragment extends Fragment {
    //프래그먼트 전환 변수
    FragmentTransaction transaction;

    // 값 받아오는 변수들
    private String title;
    private String writer;
    private String content;
    private String date;
    private String category;

    private TextView commu_title;
    private TextView commu_writer;
    private TextView commu_date;
    private TextView commu_content;
    private TextView commu_category;


    private Button btn_back;
    private Button btn_commu_comment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_posts,container,false);

        commu_title = view.findViewById(R.id.commu_title);
        commu_date = view.findViewById(R.id.commu_date);
        commu_writer=view.findViewById(R.id.commu_writer);
        commu_content = view.findViewById(R.id.commu_content);
        commu_category=view.findViewById(R.id.commu_category);

        btn_back = view.findViewById(R.id.btn_back);
        btn_commu_comment = view.findViewById(R.id.btn_commu_comment);


        if(getArguments() != null){
            // CommunitFragment에서 데이터 받아오기
            title = getArguments().getString("Title");
            writer = getArguments().getString("Writer");
            content = getArguments().getString("Content");
            date = getArguments().getString("Date");
            category = getArguments().getString("Category");
        }

        commu_title.setText(title);
        //commu_writer.setText(writer);
        commu_content.setText(content);
        commu_date.setText(date);
        if (category.equals("what_eat")) {
            commu_category.setText("뭐 먹지?");
        }
        if(category.equals("what_do")){
            commu_category.setText("뭐 하지?");
        }
        if(category.equals("how_do")){
            commu_category.setText("어떻게 하지?");
        }

        // '뒤로가기' 버튼 눌렀을 시
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityFragment communityFragment = new CommunityFragment();

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityFragment);
                transaction.commit();
            }
        });

        // '댓글' 버튼 눌렀을 시
        btn_commu_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CommunityCommentFragment로 데이터 넘기기
                Bundle bundle = new Bundle();
                bundle.putString("Category", category);
                bundle.putString("Title", title);
                bundle.putString("Content", content);
                bundle.putString("Date", date);

                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityCommentFragment communityCommentFragment = new CommunityCommentFragment();
                communityCommentFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityCommentFragment);
                transaction.commit();
            }
        });

        return view;
    }
}
