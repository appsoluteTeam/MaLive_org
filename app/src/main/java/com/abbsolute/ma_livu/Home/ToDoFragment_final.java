package com.abbsolute.ma_livu.Home;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoAdapter;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoInfo;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoWriteMainFragment;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoFragment_final extends Fragment {

    private static String email;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<ToDoList_Info> todoList;
    private ToDoAdapter_final adapter;

    private Button btn_addTodo;

    //pay관련 변수들
    private String recentBalance;
    private long recentPayDocumentNum;

    //파이어베이스 관련 변수
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    public ToDoFragment_final() {
    }

    public ToDoFragment_final(String email) {
        this.email = email;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_fragment, container, false);

        todoList = new ArrayList<ToDoList_Info>();
        btn_addTodo = view.findViewById(R.id.btn_addTodo);
        recyclerView = view.findViewById(R.id.todo_recyclerview);

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();


        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);

//        Decoration spaceDecoration = new Decoration(30);
//        recyclerView.addItemDecoration(spaceDecoration);

        recyclerView.setHasFixedSize(true);




        //To-do 목록 불러오기
        String id = firebaseAuth.getCurrentUser().getEmail();
        firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {

                                todoList.clear();

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();;
                                    String todo_date = String.valueOf(shot.get("todo_date"));
                                    String detail_date = String.valueOf(shot.get("detail_date"));
                                    String todo_category = String.valueOf(shot.get("todo_category"));
                                    String todo_datail = String.valueOf(shot.get("todo_datail"));
                                    Boolean check = (Boolean)shot.get("check");
                                    ToDoList_Info toDoList_info = new ToDoList_Info(todo_date,detail_date,todo_category,todo_datail,check);

                                    todoList.add(toDoList_info);
                                }

                                //todo_date 기준으로 정렬
                                Comparator<ToDoList_Info> comparator = new Comparator<ToDoList_Info>() {
                                    public int compare(ToDoList_Info o1, ToDoList_Info o2) {
                                        return o1.getTodo_date().compareTo(o2.getTodo_date());
                                    }
                                };
                                Collections.sort(todoList, comparator);

                                //adapter과 todoList 이어주기
                                adapter = new ToDoAdapter_final(todoList);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    }
                });


        //swipe 기능 설정
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d("onMove","move");
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //todo: 체크되어있는지 확인, every_month, total에 저장, toll

                String id = firebaseAuth.getCurrentUser().getEmail();

                // 삭제되는 아이템의 포지션을 가져온다
                final int position = viewHolder.getAdapterPosition();

                String documentName = todoList.get(position).getDetail_date();
                firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo").document(documentName)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("ToDoFragment_final", "todo 삭제 완료!");

                                // 데이터의 해당 포지션을 삭제한다.
                                todoList.remove(position);

                                // 아답타에게 알린다
                                adapter.notifyItemRemoved(position);
                                adapter.notifyDataSetChanged(); //이거써줘야 리스트 초기화 후 갱신됨! 날짜 때문에 필요한 코드
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("ToDoFragment_final", "todo 삭제 실패!");
                            }
                        });

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //to-do 추가 버튼 눌렀을 때
        btn_addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoWrite toDoWrite = new ToDoWrite();

                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, toDoWrite);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void onStart() {
        super.onStart();


    }
}
