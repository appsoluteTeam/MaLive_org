package com.abbsolute.ma_livu.MyPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Community.CommunityComment.CommuCommentOnItemClick;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentAdapter;
import com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentItem;
import com.abbsolute.ma_livu.Community.CommunityPostsFragment;
import com.abbsolute.ma_livu.Community.bringData;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class activeMyPostFragment extends Fragment {
    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    public static Stack<Fragment> fragmentStack;

    private RecyclerView recyclerView;
    public static RecyclerPostAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<postItemListView> arrayList;

    private Button btn_back;
    private static String str_nickname, myPost_count , myComment_count, mySavedPosts_count ;
    private String[] category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypost_fragment,container,false);

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

        // activeFragment에서 데이터 받아오기
        if(getArguments() != null){
            str_nickname = getArguments().getString("nickname");
            myPost_count  = getArguments().getString("MyPost_count");
            myComment_count = getArguments().getString("MyComment_count");
            mySavedPosts_count = getArguments().getString("MySavedPosts_count");
        }

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment nextFragment = fragmentStack.pop();
//                fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
                activeFragment activeFragment = new activeFragment();

                Bundle bundle = new Bundle();
                bundle.putString("nickname", str_nickname);
                bundle.putString("MyPost_count", String.valueOf(myPost_count));
                bundle.putString("MyComment_count", String.valueOf(myComment_count));
                bundle.putString("MySavedPosts_count", String.valueOf(mySavedPosts_count));
                activeFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.main_frame, activeFragment).commit();
            }
        });

        //MyPageFragment에서 받아온 내가 쓴 글 데이터 ArrayList<postItemListView>에 집어넣기
        arrayList = MyPageFragment.arrayList;

        // 어댑터와 리사이클러뷰 연결해서 화면에 띄우기
        recyclerView = (RecyclerView) view.findViewById(R.id.active_recyclerVIew);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerPostAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());

        // 리사이클러뷰 역순 출력
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        recyclerView.scrollToPosition(0);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.setOnItemClickListener(new RecyclerPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                postItemListView item = adapter.getItem(position);

                // CommunityPostsFragment로 데이터 넘기기
                Bundle bundle = new Bundle();
                bundle.putString("Category", item.getPost_category());
                bundle.putString("Title", item.getPost_title());
                bundle.putString("Writer", str_nickname);
                bundle.putString("Content", item.getPost_content());
                bundle.putString("Date", item.getPost_date());

                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityPostsFragment communityPostsFragment = new CommunityPostsFragment();
                communityPostsFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                fragmentTransaction.replace(R.id.main_frame, communityPostsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
