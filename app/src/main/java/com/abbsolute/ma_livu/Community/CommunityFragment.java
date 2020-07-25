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
    private RecyclerView recycler_community;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_fragment,container,false);

        arrayList = new ArrayList<>();
        commu_navigation = view.findViewById(R.id.commu_navigation);
        commu_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //뭐 먹지? 버튼이 눌렸을 경우 어레이리스트에 저장되는 값
                    case R.id.what_eat:
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
                                                    String title = String.valueOf(shot.get(FirebaseID.title));
                                                    String content =String.valueOf(shot.get(FirebaseID.content));
                                                    String category = String.valueOf(shot.get(FirebaseID.category));
                                                    bringData data = new bringData(documentID,title,category,content);
                                                    arrayList.add(data);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                        break;
                    //뭐 하지? 버튼이 눌렸을 경우 어레이리스트에 저장되는 값
                    case R.id.what_do:
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
                                                    String title = String.valueOf(shot.get(FirebaseID.title));
                                                    String content =String.valueOf(shot.get(FirebaseID.content));
                                                    String category = String.valueOf(shot.get(FirebaseID.category));
                                                    bringData data = new bringData(documentID,title,category,content);
                                                    arrayList.add(data);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                        break;
                    //어떻게 하지? 버튼이 눌렸을 경우 어레이리스트에 저장되는 값
                    case R.id.how_do:
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
                                                    String title = String.valueOf(shot.get(FirebaseID.title));
                                                    String content =String.valueOf(shot.get(FirebaseID.content));
                                                    String category = String.valueOf(shot.get(FirebaseID.category));
                                                    bringData data = new bringData(documentID,title,category,content);
                                                    arrayList.add(data);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                        break;
                }
                return true;
            }
        });

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
    }


}
