package com.abbsolute.ma_livu.Community;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.abbsolute.ma_livu.MyPage.payItemListView;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public class CommunityFragment extends Fragment {
    // 프래그먼트
    public static Stack<Fragment> fragmentStack;
    private FragmentTransaction fragmentTransaction;

    private View view;
    private ImageButton btn_commu_write,btn_back;
    private Button btn_what_eat,btn_what_do,btn_how_do;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // 정렬
    private ImageButton btn_commu_sort;
    private LinearLayout layout_commu_sort;
    private View view_darker;



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

    private String[] img_uri;

    private String likeCount;
    private String saveCount;

    // 정렬 라디오 버튼 관련
    private RadioButton commu_sort_date, commu_sort_like, commu_sort_save;
    private RadioGroup radioGroup;

    CollectionReference what_eat, what_do, how_do;

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

        // 정렬 버튼 눌렸을 때 리스너
        recycler_community = (RecyclerView)view.findViewById(R.id.recycler_community);
        btn_commu_sort = (ImageButton)view.findViewById(R.id.btn_commu_sort);
        layout_commu_sort = view.findViewById(R.id.layout_commu_sort);
        view_darker = view.findViewById(R.id.view_darker);

        // 초기화면에서 정렬창 안보이게
        layout_commu_sort.setVisibility(view.GONE);
        view_darker.setVisibility(view.GONE);

        // 컬렉션 레퍼런스
        what_eat = firestore.collection("Community").document("what_eat").collection("sub_Community");
        what_do = firestore.collection("Community").document("what_do").collection("sub_Community");
        how_do = firestore.collection("Community").document("how_do").collection("sub_Community");


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
//                        Fragment nextFragment = fragmentStack.pop();
//                        fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
                        break;
                }
            }
        };

        btn_what_eat.setOnClickListener(onClickListener);
        btn_what_do.setOnClickListener(onClickListener);
        btn_how_do.setOnClickListener(onClickListener);
        btn_commu_write.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);
        btn_commu_sort.setOnClickListener(onClickListener);

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
                bundle.putString("Writer",item.getWriter());

                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityPostsFragment communityPostsFragment = new CommunityPostsFragment();
                communityPostsFragment.setArguments(bundle);

                // 버튼 누르면 화면 전환
                fragmentTransaction.replace(R.id.main_frame, communityPostsFragment);
                fragmentTransaction.commit();
            }
        });
    }


    public void callRecycler(int n){
        switch (n){
            case 0:
                // 처음엔 날짜순 정렬로 세팅
                callSortRecycler(0, what_eat);

                btn_commu_sort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout_commu_sort.setVisibility(view.VISIBLE);
                        view_darker.setVisibility(view.VISIBLE);
                        radioSet(what_eat);
                    }
                });

                // 파이어 스토어에서 데이터 받아오는 부분 아래에 메소드로 뺐어용~~~~
//                firestore.collection("Community").document("what_eat").collection("sub_Community")
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if(task.isSuccessful()){
//                                    if(task.getResult() != null){
//                                        arrayList.clear();
//
//                                        for(DocumentSnapshot snapshot : task.getResult()){
//                                            Map<String,Object> shot = snapshot.getData();
//                                            String documentID = String.valueOf(shot.get(FirebaseID.documentID));
//                                            title = String.valueOf(shot.get(FirebaseID.title));
//                                            content =String.valueOf(shot.get(FirebaseID.content));
//                                            category = String.valueOf(shot.get(FirebaseID.category));
//                                            date = String.valueOf(shot.get(FirebaseID.commu_date));
//
////                                            for(int i=0; i<FirebaseID.Commu_image_URI.length(); i++){
////                                                img_uri[i]= (String) shot.get(FirebaseID.Commu_image_URI);
////                                            }
////                                            Log.d("CommunityFragment", "img_uri = "+img_uri);
//                                            writer=String.valueOf(shot.get(FirebaseID.Nickname));
//
//
//                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
//                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));
//
//                                            bringData data = new bringData(documentID,title,category,content,date,writer,likeCount,saveCount);
//
//                                            arrayList.add(data);
//                                        }
//                                        adapter.notifyDataSetChanged();
//                                    }
//                                }
//                            }
//                        });
                break;
            case 1:
                callSortRecycler(0, what_do);
                btn_commu_sort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout_commu_sort.setVisibility(view.VISIBLE);
                        view_darker.setVisibility(view.VISIBLE);
                        radioSet(what_do);
                    }
                });
                break;
            case 2:
                callSortRecycler(0, how_do);
                btn_commu_sort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout_commu_sort.setVisibility(view.VISIBLE);
                        view_darker.setVisibility(view.VISIBLE);
                        radioSet(how_do);
                    }
                });
                break;
        }
    }

    // 정렬 버튼 set
    public void radioSet(final CollectionReference section){

        //정렬화면 보이게
        layout_commu_sort.setVisibility(view.VISIBLE);

        //라디오버튼
        commu_sort_date = (RadioButton)view.findViewById(R.id.commu_sort_date);
        commu_sort_like = (RadioButton)view.findViewById(R.id.commu_sort_like);
        commu_sort_save = (RadioButton)view.findViewById(R.id.commu_sort_save);

        //라디오 그룹
        radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);

        //라디오 버튼 클릭 리스너
        RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){ @Override
        public void onClick(View view) {
        }
        };

        //라디오 그룹 클릭 리스너
        RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() { @Override
        public void onCheckedChanged(RadioGroup radioGroup,int i)
        {
            layout_commu_sort.setVisibility(view.GONE);
            view_darker.setVisibility(view.GONE);

            if(i == R.id.commu_sort_date){
                callSortRecycler(0, section);
            }else if(i == R.id.commu_sort_like){
                callSortRecycler(1, section);
            }else if(i == R.id.commu_sort_save){
                callSortRecycler(2, section);
            }
        }
        };

        commu_sort_date.setOnClickListener(radioButtonClickListener);
        commu_sort_like.setOnClickListener(radioButtonClickListener);
        commu_sort_save.setOnClickListener(radioButtonClickListener);

        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
    }

    public void callSortRecycler(int n, CollectionReference section){
        switch (n){
            case 0:// 날짜순 정렬
                section
                        .orderBy("commu_date")
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
                                            writer=String.valueOf(shot.get(FirebaseID.Nickname));

                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                            bringData data = new bringData(documentID,title,category,content,date,writer,likeCount,saveCount);
                                            arrayList.add(data);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                break;
            case 1:// 추천순
                section
                        .orderBy("commu_like_count")
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
                                            writer=String.valueOf(shot.get(FirebaseID.Nickname));

                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                            bringData data = new bringData(documentID,title,category,content,date,writer,likeCount,saveCount);
                                            arrayList.add(data);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                break;
            case 2:// 스크랩순
                section
                        .orderBy("commu_save_count")
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
                                            writer=String.valueOf(shot.get(FirebaseID.Nickname));

                                            likeCount = String.valueOf(shot.get(FirebaseID.commu_like_count));
                                            saveCount = String.valueOf(shot.get(FirebaseID.commu_save_count));

                                            bringData data = new bringData(documentID,title,category,content,date,writer,likeCount,saveCount);
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

