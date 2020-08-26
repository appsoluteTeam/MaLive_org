package com.abbsolute.ma_livu.Community;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookWriteFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CommunityFragment extends Fragment {

    private View view;
    private ImageButton btn_commu_write,btn_back;
    private Button btn_what_eat,btn_what_do,btn_how_do;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //리사이클러뷰
    public CommunityAdapter adapter;
    private RecyclerView recycler_community;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    private String title;
    private String content;
    private String category;
    private String date;
    private String writer;
    private String likeCount;
    private String saveCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_fragment,container,false);

        // 카테고리 버튼 눌렸을 때 버튼리스너
        btn_what_eat=(Button)view.findViewById(R.id.what_eat);
        btn_what_do=(Button)view.findViewById(R.id.what_do);
        btn_how_do=(Button)view.findViewById(R.id.how_do);
        btn_commu_write =(ImageButton)view.findViewById(R.id.btn_commu_write);
        btn_back = (ImageButton)view.findViewById(R.id.btn_back);

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.what_eat: //뭐 먹지 카테고리 선택
                        callRecycler(0);
                        break;
                    case R.id.what_do: //뭐 하지 카테고리 선택
                        callRecycler(1);
                        break;
                    case R.id.how_do: //어떻게 하지 카테고리 선택
                        callRecycler(2);
                        break;
                    case R.id.btn_commu_write: // 작성하기 아이콘 클릭
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Commu_WriteFragment Commu_WriteFragment = new Commu_WriteFragment();
                        transaction.replace(R.id.main_frame, Commu_WriteFragment);
                        transaction.commit();
                        break;
                    case R.id.btn_back: // 뒤로가기 아이콘 클릭
                        ((HomeActivity)getActivity()).setFragment(1);
                        break;
                }
            }
        };

        btn_what_eat.setOnClickListener(onClickListener);
        btn_what_do.setOnClickListener(onClickListener);
        btn_how_do.setOnClickListener(onClickListener);
        btn_commu_write.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);

        return view;
    }

    public void onStart() {
        super.onStart();

        arrayList = new ArrayList<>();
        callRecycler(0);
      
        // 리사이클러뷰에 가져온 정보 넣기
        recycler_community = (RecyclerView)view.findViewById(R.id.recycler_community);
        recycler_community.setHasFixedSize(true);
        adapter = new CommunityAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());


        // 리사이클러뷰 역순 출력
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        recycler_community.scrollToPosition(0);
        recycler_community.setItemAnimator(new DefaultItemAnimator());

        recycler_community.setLayoutManager(layoutManager);
        recycler_community.setAdapter(adapter);

        // 리사이클러뷰 클릭 이벤트
        adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                bringData item = adapter.getItem(position);

                // CommunityPostsFragment로 데이터 넘기기
                Bundle bundle = new Bundle();
                bundle.putString("Title", item.getTitle());
                bundle.putString("Content", item.getContent());
                bundle.putString("Date", item.getDate());
                bundle.putString("Category", item.getCategory());

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityPostsFragment communityPostsFragment = new CommunityPostsFragment();
                communityPostsFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                transaction.replace(R.id.main_frame, communityPostsFragment);
                transaction.commit();
            }
        });
    }


    public void callRecycler(int n){
        switch (n){
            case 0:
                firestore.collection("Community").document("what_eat").collection("sub_Community")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult() != null){
                                        arrayList.clear();
                                        for(DocumentSnapshot snapshot : task.getResult()){
                                            Map<String,Object> shot = snapshot.getData();
                                            String documentID = String.valueOf(shot.get(FirebaseID.documentID));
                                            title = String.valueOf(shot.get(FirebaseID.title));
                                            content =String.valueOf(shot.get(FirebaseID.content));
                                            category = String.valueOf(shot.get(FirebaseID.category));
                                            date = String.valueOf(shot.get(FirebaseID.commu_date));
                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                            bringData data = new bringData(documentID,title,category,content,date,likeCount,saveCount);
                                            arrayList.add(data);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                break;
            case 1:
                firestore.collection("Community").document("what_do").collection("sub_Community")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult() != null){
                                        arrayList.clear();
                                        for(DocumentSnapshot snapshot : task.getResult()){
                                            Map<String,Object> shot = snapshot.getData();
                                            String documentID = String.valueOf(shot.get(FirebaseID.documentID));
                                            title = String.valueOf(shot.get(FirebaseID.title));
                                            content =String.valueOf(shot.get(FirebaseID.content));
                                            category = String.valueOf(shot.get(FirebaseID.category));
                                            date = String.valueOf(shot.get(FirebaseID.commu_date));
                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                            bringData data = new bringData(documentID,title,category,content,date,likeCount,saveCount);
                                            arrayList.add(data);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                break;
            case 2:
                firestore.collection("Community").document("how_do").collection("sub_Community")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult() != null){
                                        arrayList.clear();
                                        for(DocumentSnapshot snapshot : task.getResult()){
                                            Map<String,Object> shot = snapshot.getData();
                                            String documentID = String.valueOf(shot.get(FirebaseID.documentID));
                                            title = String.valueOf(shot.get(FirebaseID.title));
                                            content =String.valueOf(shot.get(FirebaseID.content));
                                            category = String.valueOf(shot.get(FirebaseID.category));
                                            date = String.valueOf(shot.get(FirebaseID.commu_date));
                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                            bringData data = new bringData(documentID,title,category,content,date,likeCount,saveCount);
                                            arrayList.add(data);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                break;
        }
    }

}

