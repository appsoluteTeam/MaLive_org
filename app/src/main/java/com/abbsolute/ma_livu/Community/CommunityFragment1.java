package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CommunityFragment1 extends Fragment {

    private View view;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //리사이클러뷰
    private RecyclerView recycler_what_eat;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.community_fragment1,container,false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayList = new ArrayList<>();
        firestore.collection("Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult() != null){
                                // 파이어스토어에서 데이터 가져오기
                                for(DocumentSnapshot snapshot : task.getResult()){
                                    Map<String,Object> shot = snapshot.getData();
                                    String documentID = String.valueOf(shot.get(FirebaseID.documentID));
                                    String title = String.valueOf(shot.get(FirebaseID.title));
                                    String writer = String.valueOf(shot.get(FirebaseID.writer));
                                    String content =String.valueOf(shot.get(FirebaseID.content));
                                    bringData data = new bringData(documentID,title,writer,content);
                                    arrayList.add(data);
                                }
                                // 리사이클러뷰에 가져온 정보 넣기
                                recycler_what_eat = (RecyclerView)view.findViewById(R.id.recycler_what_eat);
                                recycler_what_eat.setHasFixedSize(true);

                                adapter = new CustomAdapter(arrayList);
                                layoutManager = new LinearLayoutManager(getActivity());
                                recycler_what_eat.setLayoutManager(layoutManager);
                                recycler_what_eat.scrollToPosition(0);
                                recycler_what_eat.setItemAnimator(new DefaultItemAnimator());
                                recycler_what_eat.setAdapter(adapter);
                            }
                        }
                    }
                });
    }
}
