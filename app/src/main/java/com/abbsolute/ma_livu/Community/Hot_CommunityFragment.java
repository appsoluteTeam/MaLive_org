package com.abbsolute.ma_livu.Community;

import android.os.Bundle;
import android.util.Log;
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
import com.abbsolute.ma_livu.MyPage.payItemListView;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Hot_CommunityFragment extends Fragment implements OnBackPressedListener {

    private View view;
    private FragmentTransaction fragmentTransaction;
    private Button btn_more_text;
    private ImageButton btn_commu_write;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Button btn_today_post,btn_today_room;
    private String[] categoryarray={"what_eat","what_do","how_do"};

    //리사이클러뷰
    public CommunityAdapter adapter;
    private RecyclerView recycler_hot_community;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    private String title,content,category,date,writer,email,likeCount,saveCount,img1;
    private Boolean hot;

    //pay관련 변수들
    private String recentBalance;
    private long recentPayDocumentNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hot_community_fragment,container,false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
        ((HomeActivity)getActivity()).setOnBackPressedListener(this);


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
                    case R.id.btn_commu_write: // 작성하기 아이콘 클릭
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
        for(int i=0; i<3; i++){
            final int j = i;
            firestore.collection("Community").document(categoryarray[i]).collection("sub_Community")
                    .orderBy("commu_like_count", Query.Direction.DESCENDING).limit(3)
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
                                        if( String.valueOf(shot.get(FirebaseID.commu_like_count)).equals("0")) {
                                            break;
                                        } else{
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

                                            //핫게시글로 선정된게 처음인지 확인하기
                                            hot = (Boolean)shot.get("hot");
                                            if(hot == false){   //핫게시글로 설정된 적 없음
                                                //toll 얻어주기
                                                getRecentPayDocument(document,j);

                                                //hot을 true로 업데이트
                                                //여기서하면 getRecentPayDocument가 다 끝나기 전에 true로 바뀌줌 그래서 톨얻는 메소드 getToll에서 true로 바꿔줌,,,,
//                                                firestore.collection("Community").document(categoryarray[j])
//                                                        .collection("sub_Community").document(document)
//                                                        .update("hot",true);
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        arrayList = new ArrayList<>();
        callRecycler();

        //배열 섞어주기
        Collections.shuffle(arrayList);

        // 리사이클러뷰에 가져온 정보 넣기
        recycler_hot_community=(RecyclerView)view.findViewById(R.id.recycler_hot_community);
        recycler_hot_community.setHasFixedSize(true);
        adapter = new CommunityAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());


        recycler_hot_community.scrollToPosition(0);
        recycler_hot_community.setItemAnimator(new DefaultItemAnimator());

        recycler_hot_community.setLayoutManager(layoutManager);
        recycler_hot_community.setAdapter(adapter);

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

    /* getRecentPayDoument(),  getRecentBalance(), getToll() : toll얻는 메소드 */

    //가장 최근 문서 알아내기
    public void getRecentPayDocument(final String documentName,final int index){
        firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.tmpData).document("recentPayNum")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                recentPayDocumentNum = Long.parseLong(shot.get(FirebaseID.recentDocument).toString());
                                Log.d("getRecentPayDocument 안",Long.valueOf(recentPayDocumentNum).toString());
                            } else {
                                recentPayDocumentNum = 0;
                            }
                            getRecentBalance(recentPayDocumentNum,documentName,index);
                        } else {
                        }
                    }
                });
    }

    //가장 최근 문서의 잔액 알아내기
    public void getRecentBalance(final long recentPayDocumentNum,final String documentName,final int index){
        Log.d("getRecentBalance","접근완료");
        if(recentPayDocumentNum == 0){
            recentBalance = "0";
            Log.d("recentBalance final", recentBalance);
            getToll(recentBalance,recentPayDocumentNum,documentName,index);
        }else{
            Log.d("else문","else");
            firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.pay)
                    .whereEqualTo(FirebaseID.order,Long.valueOf(recentPayDocumentNum).toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    recentBalance = shot.get(FirebaseID.balance).toString();
                                    Log.d("recentBalance final", recentBalance);
                                    getToll(recentBalance,recentPayDocumentNum,documentName,index);
                                }
                            }
                        }
                    });

        }


    }

    //직전의 balance에 근거해서 톨 파이어스토어에 저장
    public void getToll(String recentBalance,long recentPayDocumentNum,String documentName,int index){

        Calendar calendar = Calendar.getInstance();

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        String format_time = format.format(calendar.getTime());


        String month = Integer.valueOf(calendar.get(Calendar.MONTH)).toString();
        String date = Integer.valueOf(calendar.get(Calendar.DATE)).toString();

        String today = month + "." + date;

        String hour = Integer.valueOf(calendar.get(Calendar.HOUR)).toString();
        String minute = Integer.valueOf(calendar.get(Calendar.MINUTE)).toString();

        String time = hour + ":" + minute;

        int amount = 500;//임시로
        int balance = Integer.parseInt(recentBalance) + amount;

        String order = String.valueOf(recentPayDocumentNum + 1);

        payItemListView payItemListView =
                new payItemListView(today,"핫게시글 선정 보상","입금","+500",Integer.valueOf(balance).toString(),time,order);

        //파이어스토어 저장
        firestore.collection(FirebaseID.myPage)
                .document(email)
                .collection(FirebaseID.pay)
                .document(format_time)
                .set(payItemListView, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        //최근문서 바꾸기
        Map<String,Object> payMap = new HashMap<>();
        payMap.put(FirebaseID.recentDocument,order);

        firestore.collection(FirebaseID.myPage)
                .document(email)
                .collection(FirebaseID.tmpData)
                .document("recentPayNum")
                .set(payMap);


        //hot을 true로 업데이트
        firestore.collection("Community").document(categoryarray[index])
                .collection("sub_Community").document(documentName)
                .update("hot",true);

    }

    @Override
    public void onBackPressed() {
        ((HomeActivity)getActivity()).setFragment(0);
    }
}
