package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.MyPage.payItemListView;
import com.google.firebase.firestore.DocumentReference;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ToDoFragment_final extends Fragment {

    private static String email;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<ToDoList_Info> todoList;
    private ToDoAdapter_final adapter;
    private long count = 0;
    private boolean delete_todo;

    private Button btn_addTodo;
    private Button btn_back;
    public ToDoListCustomDialog customDialog;

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
        btn_back = view.findViewById(R.id.btn_back);
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

                String id = firebaseAuth.getCurrentUser().getEmail();
                final int position = viewHolder.getAdapterPosition();
                String documentName = todoList.get(position).getDetail_date();

                //custom Dialog 리스너 등록
                View.OnClickListener positiveListener = new View.OnClickListener() {
                    public void onClick(View v) {
                        //to-do 삭제
                        delete_todo(id,position,documentName);

                        //톨저장
                        getRecentPayDocument(); //toll얻기

                        customDialog.dismiss();
                    }
                };

                View.OnClickListener negativeListener = new View.OnClickListener() {
                    public void onClick(View v) {
                        customDialog.dismiss();

                        //리스트뷰 새로 띄우기!
                        adapter.notifyDataSetChanged();
                    }
                };

                //todoList 체크되어있는지 확인
                boolean todo_check = todoList.get(position).isCheck();

                Log.d("todo_check",Boolean.toString(todo_check));
                if(todo_check == true){
                    //to-do 삭제
                    delete_todo(id,position,documentName);

                    //톨저장
                    getRecentPayDocument(); //toll얻기

                }else{//체크가 안되어있을 때 다이얼로그 띄어주기
                    customDialog = new ToDoListCustomDialog(getContext(), "완료하지 않은 리스트입니다.",
                            "그래도 삭제하시겠습니까?", positiveListener, negativeListener);
                    customDialog.show();
                }

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

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).setFragment(0);
            }
        });

        return view;
    }

    //todo 삭제
    public void delete_todo(String id,int position, String documentName){
        firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo").document(documentName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ToDoFragment_final", "todo 삭제 완료!");

                        //현재 년도-월 찾기
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();
                        String format_time = format1.format(calendar.getTime());
                        String year_month = format_time.substring(0, 7);

                        String todo_category = todoList.get(position).getTodo_category();

                        //everyMonth저장
                        DocumentReference ref = firestore.collection("ToDoList").document(id).collection("EveryMonth").document(year_month);
                        ref.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // 컬렉션 내의 document에 접근
                                            DocumentSnapshot document = task.getResult();

                                            if (document.exists()) {
                                                Map<String, Object> shot = document.getData();
                                                count = (Long)shot.get(todo_category + "complete");
                                            } else {    //문서가 존재하지 않으면 모든 카테고리에 0넣어줘야한다.
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("빨래complete",0);
                                                data.put("청소complete",0);
                                                data.put("쓰레기complete",0);
                                                data.put("기타complete",0);
                                                ref.set(data, SetOptions.merge());

                                                count = 0;
                                            }

                                            Log.d("count",Long.toString(count));

                                            Map<String, Object> data = new HashMap<>();
                                            data.put(todo_category + "complete",count+1);
                                            ref.set(data, SetOptions.merge());
                                        } else {
                                        }
                                    }
                                });


                        //total저장
                        DocumentReference total_ref = firestore.collection("ToDoList").document(id).collection("total").document("sub");

                        total_ref.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // 컬렉션 내의 document에 접근
                                            DocumentSnapshot document = task.getResult();

                                            if (document.exists()) {
                                                Map<String, Object> shot = document.getData();
                                                count = (Long)shot.get(todo_category + "complete");
                                            } else {    //문서가 존재하지 않으면 모든 카테고리에 0넣어줘야한다.
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("빨래complete",0);
                                                data.put("청소complete",0);
                                                data.put("쓰레기complete",0);
                                                data.put("기타complete",0);
                                                total_ref.set(data, SetOptions.merge());

                                                count = 0;
                                            }

                                            Log.d("count",Long.toString(count));

                                            Map<String, Object> data = new HashMap<>();
                                            data.put(todo_category + "complete",count+1);
                                            total_ref.set(data, SetOptions.merge());
                                        } else {
                                        }
                                    }
                                });

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

    /* getRecentPayDoument(),  getRecentBalance(), getToll() : toll얻는 메소드 */

    //가장 최근 문서 알아내기
    public void getRecentPayDocument(){
        String id = firebaseAuth.getCurrentUser().getEmail();

        firestore.collection(FirebaseID.myPage).document(id).collection(FirebaseID.tmpData).document("recentPayNum")
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
                            getRecentBalance(recentPayDocumentNum);
                        } else {
                        }
                    }
                });
    }

    //가장 최근 문서의 잔액 알아내기
    public void getRecentBalance(final long recentPayDocumentNum){
        Log.d("getRecentBalance","접근완료");
        String id = firebaseAuth.getCurrentUser().getEmail();

        if(recentPayDocumentNum == 0){
            recentBalance = "0";
            Log.d("recentBalance final", recentBalance);
            getToll(recentBalance,recentPayDocumentNum);
        }else{
            Log.d("else문","else");
            firestore.collection(FirebaseID.myPage).document(id).collection(FirebaseID.pay)
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
                                    getToll(recentBalance,recentPayDocumentNum);
                                }
                            }
                        }
                    });

        }


    }

    //직전의 balance에 근거해서 톨 파이어스토어에 저장
    public void getToll(String recentBalance,long recentPayDocumentNum){
        String id = firebaseAuth.getCurrentUser().getEmail();

        Calendar calendar = Calendar.getInstance();

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        String format_time = format.format(calendar.getTime());


        String month = Integer.valueOf(calendar.get(Calendar.MONTH)).toString();
        String date = Integer.valueOf(calendar.get(Calendar.DATE)).toString();

        String today = month + "." + date;

        String hour = Integer.valueOf(calendar.get(Calendar.HOUR)).toString();
        String minute = Integer.valueOf(calendar.get(Calendar.MINUTE)).toString();

        String time = hour + ":" + minute;

        int amount = 10;//임시로
        int balance = Integer.parseInt(recentBalance) + amount;

        String order = String.valueOf(recentPayDocumentNum + 1);

        payItemListView payItemListView =
                new payItemListView(today,"TODO 달성 보상","입금","+10",Integer.valueOf(balance).toString(),time,order);

        //파이어스토어 저장
        firestore.collection(FirebaseID.myPage)
                .document(id)
                .collection(FirebaseID.pay)
                .document(format_time)
                .set(payItemListView,SetOptions.merge())
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

        //todo:팝업 톨 얻었다고 띄우기
        View dialogView = getLayoutInflater().inflate(R.layout.custom_popup, null);
        TextView popup_title = dialogView.findViewById(R.id.popup_title);
        TextView popup_detail = dialogView.findViewById(R.id.popup_detail);
        ImageView popup_image = dialogView.findViewById(R.id.popup_image);

        popup_title.setText("톨 증정");
        popup_detail.setText("10톨을 받았어요!");
        popup_image.setImageResource(R.drawable.profile);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.TOP);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해
        int width = dm.widthPixels; //디바이스 화면 너비
        int height = dm.heightPixels; //디바이스 화면 높이

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.copyFrom(alertDialog.getWindow().getAttributes());
        params.width = 200;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
       // alertDialog.getWindow().setAttributes(params);
        alertDialog.show();


        //최근문서 바꾸기
        Map<String,Object> payMap = new HashMap<>();
        payMap.put(FirebaseID.recentDocument,order);

        firestore.collection(FirebaseID.myPage)
                .document(id)
                .collection(FirebaseID.tmpData)
                .document("recentPayNum")
                .set(payMap);


    }


}
