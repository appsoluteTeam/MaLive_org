package com.abbsolute.ma_livu.MyPage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/* 결제 창 fragment */

public class payFragment extends Fragment {
    private View view;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView mRecyclerView = null;
    private RecyclerPayAdapter mAdapter = null;
    private ArrayList<payItemListView> mList;
    private LinearLayoutManager layoutManager;
    private static String email;
    private Button btn_pay_sort,btn_back; //정렬버튼
    private LinearLayout layout_pay_sort;
    private TextView totalPay;
    public static Stack<Fragment> fragmentStack;

    private String recentBalance;
    private long recentPayDocumentNum;

    //fragment 관련 변수
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    //라디오 버튼 관련
    private RadioButton pay_sort_deposit,pay_sort_withdraw,pay_sort_total;
    private RadioGroup radioGroup;

    public payFragment() {
    }

    public payFragment(String email) {
        this.email = email;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mypage_pay, container, false);

        btn_pay_sort = view.findViewById(R.id.btn_pay_sort);
        layout_pay_sort = view.findViewById(R.id.layout_pay_sort);
        mRecyclerView = view.findViewById(R.id.pay_recyclerView);
        btn_back = view.findViewById(R.id.btn_back);
        totalPay = view.findViewById(R.id.totalPay);

        fragmentStack = HomeActivity.fragmentStack;
        fm = getFragmentManager();

        //초기화면 정렬버튼 안보이게
        layout_pay_sort.setVisibility(view.GONE);

        //버튼 클릭 리스너
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fm.beginTransaction();
                switch (v.getId()) {
                    case R.id.btn_pay_sort: //정렬 버튼
                        layout_pay_sort.setVisibility(v.VISIBLE);
                        mRecyclerView.setBackgroundColor(Color.parseColor("#F5F5F5"));
                        radioSet();
                        break;
                    case R.id.btn_back:
                        Fragment nextFragment = fragmentStack.pop();
                        fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
                        break;
                }
            }
        };

        /* 각 버튼 setOnClickListener해주기 */
        btn_pay_sort.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);

        getRecentPayDocument();

        return view;
    }

    public void getRecentPayDocument(){
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
                            getRecentBalance(recentPayDocumentNum);
                        } else {
                        }
                    }
                });
    }

    //가장 최근 문서의 잔액 알아내기
    public void getRecentBalance(final long recentPayDocumentNum){
        Log.d("getRecentBalance","접근완료");
        if(recentPayDocumentNum == 0){
            recentBalance = "0";
            totalPay.setText(recentBalance+"톨");
            Log.d("recentBalance final", recentBalance);
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
                                    totalPay.setText(recentBalance+"톨");
                                    Log.d("recentBalance final", recentBalance);
                                }
                            }
                        }
                    });

        }


    }
    public void onStart() {//fragment 처음
        super.onStart();

        callRecycler(0);
        //리사이클러뷰 관련 변수
        mList = new ArrayList<payItemListView>();

        mAdapter = new RecyclerPayAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        layoutManager = new LinearLayoutManager(getActivity());

        //역순 출력
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void radioSet(){

        //정렬화면 보이게
        layout_pay_sort.setVisibility(view.VISIBLE);

        //라디오버튼
        pay_sort_deposit = (RadioButton)view.findViewById(R.id.pay_sort_deposit);
        pay_sort_withdraw = (RadioButton)view.findViewById(R.id.pay_sort_withdraw);
        pay_sort_total = (RadioButton)view.findViewById(R.id.pay_sort_total);


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
                layout_pay_sort.setVisibility(view.GONE);
                mRecyclerView.setBackgroundColor(Color.WHITE);

                if(i == R.id.pay_sort_deposit){
                    callRecycler(1);
                }else if(i == R.id.pay_sort_withdraw){
                    callRecycler(2);
                }else if(i == R.id.pay_sort_total){
                    callRecycler(0);
                }

            }
        };

        pay_sort_deposit.setOnClickListener(radioButtonClickListener);
        pay_sort_withdraw.setOnClickListener(radioButtonClickListener);
        pay_sort_total.setOnClickListener(radioButtonClickListener);

        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);


    }

    public void callRecycler(int n){
        switch (n){
            case 0://전체정렬
                firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.pay)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        mList.clear();
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            Map<String, Object> shot = snapshot.getData();
                                            String amount = String.valueOf(shot.get(FirebaseID.amount));
                                            String balance = String.valueOf(shot.get(FirebaseID.balance));
                                            String title = String.valueOf(shot.get(FirebaseID.pay_title));
                                            String date = String.valueOf(shot.get(FirebaseID.pay_date));
                                            String inout = String.valueOf(shot.get(FirebaseID.inout));
                                            String pay_time = String.valueOf(shot.get(FirebaseID.pay_time));
                                            String order = String.valueOf(shot.get(FirebaseID.order));

                                            Log.d("amount", amount);
                                            payItemListView payItemListView = new payItemListView(date, title, inout, amount, balance,pay_time,order);
                                            mList.add(payItemListView);
                                        }
                                        mAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                                    }
                                }
                            }
                        });
                break;
            case 1://입금
                firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.pay)
                        .whereEqualTo(FirebaseID.inout,"입금")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        mList.clear();
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            Map<String, Object> shot = snapshot.getData();
                                            String amount = String.valueOf(shot.get(FirebaseID.amount));
                                            String balance = String.valueOf(shot.get(FirebaseID.balance));
                                            String title = String.valueOf(shot.get(FirebaseID.pay_title));
                                            String date = String.valueOf(shot.get(FirebaseID.pay_date));
                                            String inout = String.valueOf(shot.get(FirebaseID.inout));
                                            String pay_time = String.valueOf(shot.get(FirebaseID.pay_time));
                                            String order = String.valueOf(shot.get(FirebaseID.order));

                                            Log.d("amount", amount);
                                            payItemListView payItemListView = new payItemListView(date, title, inout, amount, balance, pay_time,order);
                                            mList.add(payItemListView);
                                        }
                                        mAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                                    }
                                }
                            }
                        });
                break;
            case 2://출금
                firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.pay)
                        .whereEqualTo(FirebaseID.inout,"출금")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        mList.clear();
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            Map<String, Object> shot = snapshot.getData();
                                            String amount = String.valueOf(shot.get(FirebaseID.amount));
                                            String balance = String.valueOf(shot.get(FirebaseID.balance));
                                            String title = String.valueOf(shot.get(FirebaseID.pay_title));
                                            String date = String.valueOf(shot.get(FirebaseID.pay_date));
                                            String inout = String.valueOf(shot.get(FirebaseID.inout));
                                            String pay_time = String.valueOf(shot.get(FirebaseID.pay_time));
                                            String order = String.valueOf(shot.get(FirebaseID.order));


                                            Log.d("amount", amount);
                                            payItemListView payItemListView = new payItemListView(date, title, inout, amount, balance,pay_time,order);
                                            mList.add(payItemListView);
                                        }
                                        mAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                                    }
                                }
                            }
                        });
                break;
        }
    }
}
