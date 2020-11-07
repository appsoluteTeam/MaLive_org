package com.abbsolute.ma_livu.Community;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.MainActivity;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SearchFragment<adapter> extends Fragment {

    private View view;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ImageButton btn_back;

    //리사이클러뷰
    public CommunityAdapter adapter;
    private RecyclerView recycler_search_community;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList = new ArrayList<>();

    private FragmentTransaction fragmentTransaction;
    private Context Context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commu_search, container, false);
        Context = container.getContext();

        SearchView searchView = view.findViewById(R.id.commu_search);
        searchView.setQueryHint("지금, 어떤 고민을 하고 있나요?");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(Context, "[검색버튼클릭] 검색어 = "+query, Toast.LENGTH_LONG).show();
                final String[] communityCategory = {"how_do","what_do","what_eat"};
                for (int i = 0; i < communityCategory.length; i++) {
                    String allCategory = communityCategory[i];
                    searchData(allCategory,query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //값이 변경 됐을 때
                return false;
            }
        });

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }

    private void searchData(String allCategory, String query) {
        firestore.collection("Community").document(allCategory).collection("sub_Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String content = (String) document.get("content");
                                String nickname = (String) document.get("nickname");
                                String title = (String) document.get("title");

                                if (content != null && nickname != null && title != null) {
                                    if (content.contains(query) || nickname.contains(query) || title.contains(query)) {
                                        String documentID = (String)document.getId();
                                        String category = (String) document.get("category");
                                        String date = (String)document.get("commu_date");
                                        String writer = (String) document.get("nickname");
                                        Object likeCount = document.get("commu_like_count");
                                        Object saveCount = document.get("commu_save_count");
                                        String Url;

                                        Log.d("출력", "내용 " + content + " 닉네임 " + nickname + " 제목" + title);

                                        if ((String) document.get("Url0") != null) {
                                            Url = (String) document.get("Url0");
                                        } else {
                                            Url = null;
                                        }
                                        bringData data = new bringData(documentID,title, category,content,date,writer, (String)likeCount, (String) saveCount,Url);

                                        arrayList.add(data);
                                        setRecycler();

                                    } else {
                                        Log.d("출력", "검색 결과가 없습니다.");
                                    }
                                }

                            }
                        }
                    }
                });

    }




    private void setRecycler() {


        recycler_search_community = (RecyclerView) view.findViewById(R.id.recycler_search_community);
        recycler_search_community.setHasFixedSize(true);
        adapter = new CommunityAdapter(this.arrayList);
        layoutManager = new LinearLayoutManager(getActivity());

        recycler_search_community.scrollToPosition(0);
        recycler_search_community.setItemAnimator(new DefaultItemAnimator());

        recycler_search_community.setLayoutManager(layoutManager);
        recycler_search_community.setAdapter(adapter);

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
