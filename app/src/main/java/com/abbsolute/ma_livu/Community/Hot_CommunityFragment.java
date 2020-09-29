package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.abbsolute.ma_livu.Home.ToDoList.OnBackPressedListener;
import com.abbsolute.ma_livu.MainActivity;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Hot_CommunityFragment extends Fragment implements OnBackPressedListener {

    private View view;
    private Button btn_more_text;
    private ImageButton btn_commu_write;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Button btn_today_post,btn_today_room;

    //리사이클러뷰
    public CommunityAdapter adapter;
    private RecyclerView recycler_hot_community;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    private String title,content,category,date,writer,likeCount,saveCount,img1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hot_community_fragment,container,false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
        ((HomeActivity)getActivity()).setOnBackPressedListener(this);
        //커뮤니티에서 더 많은 글 버튼을 눌렀을 때

        //버튼 아이디값 찾기
        btn_more_text=view.findViewById(R.id.btn_more_text);
        btn_commu_write=(ImageButton)view.findViewById(R.id.btn_commu_write);
        btn_today_post=(Button)view.findViewById(R.id.btn_today_post);


        //버튼리스너 생성
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_today_post: //오늘의 글 선택
                        callRecycler();
                        break;
                    case R.id.btn_more_text: // 작성하기 아이콘 클릭
                        ((HomeActivity)getActivity()).setFragment(50);
                        break;
                    case R.id.btn_commu_write: // 뒤로가기 아이콘 클릭
                        ((HomeActivity)getActivity()).setFragment(51);
                        break;
                }
            }
        };

        btn_today_post.setOnClickListener(onClickListener);
        btn_more_text.setOnClickListener(onClickListener);
        btn_commu_write.setOnClickListener(onClickListener);

        return view;
    }

    private void callRecycler() {
        firestore.collection("Community").document("what_eat").collection("sub_Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String documentID = String.valueOf(shot.get(FirebaseID.documentID));
                                    title = String.valueOf(shot.get(FirebaseID.title));
                                    content = String.valueOf(shot.get(FirebaseID.content));
                                    category = String.valueOf(shot.get(FirebaseID.category));
                                    date = String.valueOf(shot.get(FirebaseID.commu_date));
                                    writer = String.valueOf(shot.get(FirebaseID.Nickname));

                                    likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                    saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                    if (String.valueOf(shot.get((FirebaseID.Url) + 0)) != null) {
                                        img1 = ((String) shot.get((FirebaseID.Url) + 0));
                                    } else {
                                        img1 = null;
                                    }
                                    bringData data = new bringData(documentID, title, category, content, date, writer, likeCount, saveCount, img1);
                                    arrayList.add(data);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        arrayList = new ArrayList<>();
        callRecycler();

        // 리사이클러뷰에 가져온 정보 넣기
        recycler_hot_community=(RecyclerView)view.findViewById(R.id.recycler_hot_community);
        recycler_hot_community.setHasFixedSize(true);
        adapter = new CommunityAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());

        // 리사이클러뷰 역순 출력
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        recycler_hot_community.scrollToPosition(0);
        recycler_hot_community.setItemAnimator(new DefaultItemAnimator());

        recycler_hot_community.setLayoutManager(layoutManager);
        recycler_hot_community.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        ((HomeActivity)getActivity()).setFragment(0);
    }
}
