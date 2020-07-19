package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
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
    private Button btn_commu_write;
    private BottomNavigationView commu_navigation; // 쓸꺼임
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //리사이클러뷰
    private RecyclerView recycler_what_eat;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_fragment,container,false);

        // 쓸꺼임 무조건 쓸꺼임... 진ㅉ ㅏ쓸꺼임
//        commu_navigation = view.findViewById(R.id.commu_navigation);
//        commu_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.what_eat:
//                        callRecycler();
//                        break;
//                    case R.id.what_do:
//                        //callRecycler();
//                        break;
//                    case R.id.how_do:
//                        //callRecycler();
//                        break;
//                }
//                return true;
//            }
//        });

        //커뮤니티에서 글 작성하기 버튼을 눌렀을 때
        btn_commu_write =(Button)view.findViewById(R.id.btn_commu_write);
        btn_commu_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).setFragment(51);
            }
        });
        return view;
    }

    public void onStart() {
        super.onStart();
//       쓸꺼임 진짜 쓸꺼임 쓸꺼임~~~
//        switch (n) {
//            case 0:
//                // 뭐먹지 프래그먼트가 선택 됐을 때
//                // 파이어스토어에서 데이터 가져오기
//                break;
//            case 1:
//                // 뭐하지 프래그먼트가 선택 됐을 때
//
//                break;
//            case 2:
//                // 어떻게 하지 프래그먼트가 선택 됐을 때
//                break;
//        }
        arrayList = new ArrayList<>();
        firestore.collection("Community")
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
                                    String title = String.valueOf(shot.get(FirebaseID.title));
                                    String writer = String.valueOf(shot.get(FirebaseID.writer));
                                    String content =String.valueOf(shot.get(FirebaseID.content));
                                    bringData data = new bringData(documentID,title,writer,content);
                                    arrayList.add(data);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        // 리사이클러뷰에 가져온 정보 넣기
        recycler_what_eat = (RecyclerView)view.findViewById(R.id.recycler_community);
        recycler_what_eat.setHasFixedSize(true);

        adapter = new CommunityAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_what_eat.setLayoutManager(layoutManager);
        recycler_what_eat.scrollToPosition(0);
        recycler_what_eat.setItemAnimator(new DefaultItemAnimator());
        recycler_what_eat.setAdapter(adapter);
    }


}
