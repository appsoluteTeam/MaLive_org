package com.abbsolute.ma_livu.MyPage.AboutFriends;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.MyPage.MyPageFragment;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class FriendListFragment extends Fragment {
    RecyclerView friendRecyclerview;
    FriendListAdapter friendListAdapter;
    //ArrayList<FriendListInfo> arrayList=new ArrayList<>();
    LinearLayout layoutFriendSort;
    FragmentTransaction fragmentTransaction;
    private FragmentManager friendFm;
    private View view;
    //RadioButton
    private RadioButton friendLatestSort,friendNameSort,friendCommitDateSort;
    private RadioGroup radioGroup;
    //최신순,이름순,등록날짜순
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.friend_list_fragment,container,false);
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
        friendRecyclerview=view.findViewById(R.id.friends_list);
        friendListAdapter=new FriendListAdapter();
        FriendListInfo friendListInfo=new FriendListInfo(R.mipmap.ic_launcher_round,"nickName","100");
        friendListAdapter.addItem(friendListInfo);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        friendRecyclerview.setLayoutManager(layoutManager);
        friendRecyclerview.setAdapter(friendListAdapter);
        ///정렬 imageview 정의 및 클릭 이벤트
        layoutFriendSort=view.findViewById(R.id.layout_friend_sort);
        layoutFriendSort.setVisibility(View.GONE);

        friendFm=getActivity().getSupportFragmentManager();
        ImageView friendOrder=view.findViewById(R.id.friend_order);
        friendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction=friendFm.beginTransaction();
                switch (v.getId()){
                    case R.id.layout_friend_sort:
                        layoutFriendSort.setVisibility(v.VISIBLE);
                        friendRecyclerview.setBackgroundColor(Color.parseColor("#F5F5F5"));
                        radioSet();
                        break;
                }
            }
        });
        return view;
    }
    public void radioSet(){
        //정렬화면 보이게
        layoutFriendSort.setVisibility(view.VISIBLE);

        //라디오버튼
        friendLatestSort = (RadioButton)view.findViewById(R.id.friend_sort_latest);
        friendNameSort = (RadioButton)view.findViewById(R.id.friend_sort_name);
        friendCommitDateSort = (RadioButton)view.findViewById(R.id.friend_sort_commit);
        radioGroup=view.findViewById(R.id.friend_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                layoutFriendSort.setVisibility(view.GONE);
                friendRecyclerview.setBackgroundColor(Color.WHITE);
                if(checkedId==R.id.friend_sort_latest){
                    friendCallRecycler(1);
                }else if(checkedId==R.id.friend_sort_name){
                    friendCallRecycler(2);
                }else if(checkedId==R.id.friend_sort_commit){
                    friendCallRecycler(3);
                }
            }
        });
    }
    public void friendCallRecycler(int n){
        switch (n){
            case 1:

            case 2:

            case 3:
        }
    }
}
