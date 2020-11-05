package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SearchFragment extends Fragment {

    private View view;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String title, content, category, date, writer,email, likeCount, saveCount, img1;

    //리사이클러뷰
    public CommunityAdapter adapter;
    private RecyclerView recycler_search_community;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commu_search, container, false);


        SearchView searchView = view.findViewById(R.id.commu_search);
        searchView.setQueryHint("지금, 어떤 고민을 하고 있나요?");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //값을 입력했을 때
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //값이 변경 됐을 때
                return false;
            }
        });

        return view;
    }

    private void searchData(String query) {
        firestore.collection("Community").document("what_eat").collection("sub_Community")
                .whereEqualTo("title", query)
                .whereEqualTo("content", query)
                .whereEqualTo("nickname", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                boolean like_count = false;
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    String document = snapshot.getId();
                                    Map<String, Object> shot = snapshot.getData();
                                    if (String.valueOf(shot.get(FirebaseID.commu_like_count)).equals("0")) {
                                        break;
                                    } else {
                                        String documentID = String.valueOf(shot.get(FirebaseID.documentID));
                                        title = String.valueOf(shot.get(FirebaseID.title));
                                        content = String.valueOf(shot.get(FirebaseID.content));
                                        category = String.valueOf(shot.get(FirebaseID.category));
                                        date = String.valueOf(shot.get(FirebaseID.commu_date));
                                        writer = String.valueOf(shot.get(FirebaseID.Nickname));
                                        email = String.valueOf(shot.get("email"));

                                        likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                        saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                        if (String.valueOf(shot.get((FirebaseID.Url) + 0)) != null) {
                                            img1 = ((String) shot.get((FirebaseID.Url) + 0));
                                        } else {
                                            img1 = null;
                                        }
                                        bringData data = new bringData(documentID, title, category, content, date, writer, likeCount, saveCount, img1);
                                        arrayList.add(data);
                                        setRecycler();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("실패다","실패다 ㅅㅂ");
                    }
                });
    }

    private void setRecycler() {

        arrayList = new ArrayList<>();

        //배열 섞어주기
        Collections.shuffle(arrayList);

        // 리사이클러뷰에 가져온 정보 넣기
        recycler_search_community=(RecyclerView) view.findViewById(R.id.recycler_hot_community);
        recycler_search_community.setHasFixedSize(true);
        adapter = new CommunityAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());

        recycler_search_community.scrollToPosition(0);
        recycler_search_community.setItemAnimator(new DefaultItemAnimator());

        recycler_search_community.setLayoutManager(layoutManager);
        recycler_search_community.setAdapter(adapter);

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
                bundle.putString("Writer",item.getWriter());

                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityPostsFragment communityPostsFragment = new CommunityPostsFragment();
                communityPostsFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                fragmentTransaction.replace(R.id.main_frame, communityPostsFragment).addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }

}
