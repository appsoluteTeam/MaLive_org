package com.abbsolute.ma_livu.Home.GuestBook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuestBookFragment extends Fragment implements OnItemClick {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;


    private RecyclerView recyclerView;
    public static CommentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CommentItem> arrayList;

    private EditText Comment;

    private TextView CommentNum;
    private TextView CommentName;
    private TextView CommentDate;
    private ImageView CommentIcon;
    private String CommentCount;
    private String currentDate;

    private Button btn_guestbook_write;
    private Button btn_insert;
    private Button btn_delete;
    private Button btn_like;
    private Button btn_back;

    boolean likeState = false;
    boolean unlikeState = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guestbook, container, false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
//        ((HomeActivity)getActivity()).setOnBackPressedListener(this);
        CommentNum = view.findViewById(R.id.CommentNum);
        CommentName = view.findViewById(R.id.CommentName);
        CommentDate = view.findViewById(R.id.CommentDate);
        Comment = view.findViewById(R.id.WriteComment);
        CommentIcon = view.findViewById(R.id.CommentIcon);

        btn_insert = view.findViewById(R.id.btn_insert);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_back = view.findViewById(R.id.btn_back);
        btn_guestbook_write = view.findViewById(R.id.btn_guestbook_write);

        // '뒤로가기' 버튼 누를 시
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                HomeFragment homeFragment = new HomeFragment();

                // '작성'버튼 누를 시 화면 전환
                transaction.replace(R.id.main_frame, homeFragment);
                transaction.commit();
            }
        });

        // 방명록 '작성' 버튼 누를 시
        btn_guestbook_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GuestBookWriteFragment로 데이터 넘기기
                Bundle bundle = new Bundle();
                bundle.putInt("CommentCount", adapter.getItemCount());
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                GuestBookWriteFragment guestBookWriteFragment = new GuestBookWriteFragment();
                guestBookWriteFragment.setArguments(bundle);

                // '작성'버튼 누를 시 화면 전환
                transaction.replace(R.id.main_frame, guestBookWriteFragment);
                transaction.commit();
//                ((HomeActivity)getActivity()).setFragment(5);
            }
        });
        // DB의 데이터 불러와 어레이리스트에 넣기
        arrayList = new ArrayList<>();
        firestore.collection(FirebaseID.GuestBook)
                .orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                arrayList.clear();

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String CommentNum = String.valueOf(shot.get(FirebaseID.num));
                                    String CommentName = String.valueOf(shot.get(FirebaseID.name));
                                    String Comment = String.valueOf(shot.get(FirebaseID.comment));
                                    String CommentIcon = String.valueOf(shot.get(FirebaseID.icon));
                                    String CommentDate = String.valueOf(shot.get(FirebaseID.date));

                                    currentDate = CommentDate.substring(0, CommentDate.length()-3);

                                    CommentItem data = new CommentItem(CommentNum, CommentName, Comment, CommentIcon, currentDate);
                                    arrayList.add(data);
                                }
                                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                            }
                        }
                    }
                });

        // 어댑터와 리사이클러뷰 연결해서 화면에 띄우기
        recyclerView = view.findViewById(R.id.CommentList);
        recyclerView.setHasFixedSize(true);
        adapter = new CommentAdapter(arrayList, this);
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


    // 글 작성 액티비티 종료 후 리사이클러뷰 새로고침
    @Override
    public void onResume() {
        super.onResume();
        // DB의 데이터 불러와 어레이리스트에 넣기
        arrayList = new ArrayList<>();
        firestore.collection(FirebaseID.GuestBook)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                arrayList.clear();

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String CommentNum = String.valueOf(shot.get(FirebaseID.num));
                                    String CommentName = String.valueOf(shot.get(FirebaseID.name));
                                    String Comment = String.valueOf(shot.get(FirebaseID.comment));
                                    String CommentIcon = String.valueOf(shot.get(FirebaseID.icon));
                                    String CommentDate = String.valueOf(shot.get(FirebaseID.date));

                                    CommentItem data = new CommentItem(CommentNum, CommentName, Comment, CommentIcon, CommentDate);
                                    arrayList.add(data);
                                }
                                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                            }
                        }
                    }
                });

        // 어댑터와 리사이클러뷰 연결해서 화면에 띄우기
//        recyclerView = (RecyclerView) view.findViewById(R.id.CommentList);
        recyclerView.setHasFixedSize(true);
        adapter = new CommentAdapter(arrayList, this);
        layoutManager = new LinearLayoutManager(getActivity());

        // 리사이클러뷰 역순 출력
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        recyclerView.scrollToPosition(0);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    // 방명록 글 삭제 메소드
    @Override
    public void deleteItem(final int position) {

        //삭제 여부를 묻는 알림창 띄우기
       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("삭제");
        builder.setMessage("삭제하시겠습니까?");
        builder.setCancelable(true);

        // "삭제" 버튼 클릭 시
        builder.setPositiveButton(
                "삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // DB 내부의 데이터 삭제
                        firestore.collection(FirebaseID.GuestBook).document(arrayList.get(position).getNum())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Log.d("GuestBookActivity", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        Log.w("GuestBookActivity", "Error deleting document", e);
                                    }
                                });

                        // 새로 고침
                        refresh();
                    }
                });

        // "취소" 버튼 클릭 시
        builder.setNegativeButton(
                "취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // 방명록 글 신고 메소드
    public void reportItem(final int position) {

        // 신고 여부를 묻는 알림창 띄우기
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("신고");
        builder.setMessage("이 글을 신고하시겠습니까?");
        builder.setCancelable(true);

        // "신고" 버튼 클릭 시
        builder.setPositiveButton(
                "신고",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // 신고할 데이터 "GuestBook" 컬렉션에서 받아오기
                        firestore.collection(FirebaseID.GuestBook).document(arrayList.get(position).getNum())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            // 컬렉션 내의 document에 접근
                                            DocumentSnapshot document = task.getResult();

                                            if (document.exists()) {
                                                Map<String, Object> shot = document.getData();
                                                String CommentNum = String.valueOf(shot.get(FirebaseID.num));
                                                String CommentName = String.valueOf(shot.get(FirebaseID.name));
                                                String Comment = String.valueOf(shot.get(FirebaseID.comment));
                                                Log.d("GuestBookActivity", "신고할 데이터 Get 완료");

                                                // 신고할 데이터 "ReportComment" 컬렉션에 추가
                                                Map<String, Object> data = new HashMap<>();
                                                data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid());
                                                data.put(FirebaseID.name, "name");
                                                data.put(FirebaseID.comment, Comment);

                                                firestore.collection(FirebaseID.ReportComment).document(CommentNum).set(data, SetOptions.merge());
                                                Log.d("GuestBookActivity", "신고할 데이터 put 완료");

                                            } else {
                                                Log.d("GuestBookActivity", "No such document");
                                            }
                                        } else {
                                            Log.d("GuestBookActivity", "get failed with ", task.getException());
                                        }
                                    }
                                });

                        // 새로 고침
                        refresh();
                    }
                });

        // "취소" 버튼 클릭 시
        builder.setNegativeButton(
                "취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 리사이클러뷰 새로고침 메소드
    private void refresh() {
        transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }


}
