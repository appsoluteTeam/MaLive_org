package com.abbsolute.ma_livu.MyPage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Login.LoginActivity;
import com.abbsolute.ma_livu.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/* 정보설정 fragment */

public class informationSetFragment extends Fragment implements View.OnClickListener{
    private View view;
    private TextView btn_accountWithdraw,btn_logout;
    private LinearLayout layout_fragment_information_set,information_noti;
    private TextView approve,notiContents,cancel;
    private String notiContents_withdraw, notiContents_logout,approve_withdraw, approve_logout;
    private static int approveNum;
    private static String email;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static Stack<Fragment> fragmentStack;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private Button btn_back;

    public informationSetFragment(){}
    public informationSetFragment(String email){
        informationSetFragment.email = email;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information_set, container, false);


        /*fragement설정*/
        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

        layout_fragment_information_set = view.findViewById(R.id.layout_fragment_information_set);
        btn_accountWithdraw = view.findViewById(R.id.btn_accountWithdraw); //회원탈퇴
        information_noti = view.findViewById(R.id.information_noti);
        approve = view.findViewById(R.id.approve);
        btn_logout = view.findViewById(R.id.btn_logout);
        notiContents = view.findViewById(R.id.notiContents);
        cancel = view.findViewById(R.id.cancel);
        btn_back = view.findViewById(R.id.btn_back);

        notiContents_withdraw = getString(R.string.withdrawWarning);
        notiContents_logout = getString(R.string.logoutWarning);
        approve_withdraw = getString(R.string.accountWithdraw);
        approve_logout = getString(R.string.logout);

        information_noti.setVisibility(View.GONE);

        /* 각 버튼 setOnClickListener해주기 */
        btn_accountWithdraw.setOnClickListener(this);
        approve.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        cancel.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        //back버튼 적용 위해 stack에 담아둔 fragment
        fragmentStack = HomeActivity.fragmentStack;


        return view;
    }

    public void onClick(View v){
        switch(v.getId()){

            case R.id.btn_accountWithdraw://탈퇴버튼
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#F5F5F5"));
                notiContents.setText(notiContents_withdraw);
                approve.setText(approve_withdraw);
                information_noti.setVisibility(View.VISIBLE);
                approveNum = 1;//탈퇴
                break;
            case R.id.btn_logout://로그아웃
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#F5F5F5"));
                notiContents.setText(notiContents_logout);
                approve.setText(approve_logout);
                information_noti.setVisibility(View.VISIBLE);
                approveNum = 2;
                break;
            case R.id.approve:
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#FFFFFF"));
                final Intent loginHome = new Intent(getContext(), LoginActivity.class);
                if(approveNum == 1){//탈퇴 승인
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //firestore user 디비 삭제
                    firestore.collection(FirebaseID.user).document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    //firestore TODOList 디비 삭제
                    //todo:수정필요
                    firestore.collection(FirebaseID.ToDoLists).document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    //firestore MyPage 디비 삭제
                    firestore.collection(FirebaseID.myPage).document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    //firestore Attendance 디비 삭제
                    firestore.collection(FirebaseID.Attendance).document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    //firestore AlarmFragment 디비 삭제
                    firestore.collection("AlarmFragment").document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    //내가 쓴 글 지우기
                    final String[] communityCategory = {"how_do","what_do","what_eat"};

                    for(int i = 0; i < communityCategory.length; i++) {
                        final String Category2 = communityCategory[i];
                        final ArrayList<String> Title = new ArrayList<String>();

                        // 내가 쓴 글 불러와서 삭제
                        deleteMyPost(Category2);
                        deleteMyComment(Category2);
                        // 댓글 단 글 불러오기
                       // bringMyCommentPost(Category2, Title);
                    }

                    //Authentication 계정삭제
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("delete OKAY","okay");
                                        startActivity(loginHome);
                                    }
                                }
                            });

                    //startActivity(loginHome);
                }else if(approveNum == 2){//로그아웃
                    SharedPreferences sharedPreferences=getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("email_id");
                    editor.commit();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(loginHome);
                }
                break;
            case R.id.cancel:
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#FFFFFF"));

                information_noti.setVisibility(view.GONE);

                break;
            case R.id.btn_back:
                Fragment nextFragment = fragmentStack.pop();
                fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
                break;
        }
    }

    public void deleteMyPost(final String Category2){
        firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //String category = Category2;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idDelete = document.getId();
                                firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community").document(idDelete)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void deleteMyComment(final String Category2){
        firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 댓글 단 글 받아오기
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    final String documentName = document.getId();
                                    firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community").document(documentName).collection("Community_comment")
                                            .whereEqualTo("email", email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                String deleteComment = snapshot.getId();
                                                                Log.d("deleteComment",deleteComment);
                                                                firestore.collection(FirebaseID.Community).document(Category2).collection("sub_Community").document(documentName).collection("Community_comment")
                                                                        .document(deleteComment)
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }
}
