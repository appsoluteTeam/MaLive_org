package com.abbsolute.ma_livu.MyPage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class informationSetFragment extends Fragment implements View.OnClickListener{
    private View view;
    private TextView btn_accountWithdraw,btn_logout;
    private LinearLayout layout_fragment_information_set,information_noti;
    private TextView approve,notiContents,cancel;
    private String notiContents_withdraw, notiContents_logout,approve_withdraw, approve_logout;
    private static int approveNum;
    private static String email;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public informationSetFragment(){}
    public informationSetFragment(String email){
        this.email = email;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 정보설정 fragment */
        view = inflater.inflate(R.layout.fragment_information_set, container, false);

        layout_fragment_information_set = view.findViewById(R.id.layout_fragment_information_set);
        btn_accountWithdraw = view.findViewById(R.id.btn_accountWithdraw); //회원탈퇴
        information_noti = view.findViewById(R.id.information_noti);
        approve = view.findViewById(R.id.approve);
        btn_logout = view.findViewById(R.id.btn_logout);
        notiContents = view.findViewById(R.id.notiContents);
        cancel = view.findViewById(R.id.cancel);

        notiContents_withdraw = getString(R.string.withdrawWarning);
        notiContents_logout = getString(R.string.logoutWarning);
        approve_withdraw = getString(R.string.accountWithdraw);
        approve_logout = getString(R.string.logout);

        information_noti.setVisibility(view.GONE);

        /* 각 버튼 setOnClickListener해주기 */
        btn_accountWithdraw.setOnClickListener(this);
        approve.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return view;
    }

    public void onClick(View v){
        switch(v.getId()){

            case R.id.btn_accountWithdraw://탈퇴버튼
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#F5F5F5"));
                notiContents.setText(notiContents_withdraw);
                approve.setText(approve_withdraw);
                information_noti.setVisibility(view.VISIBLE);
                approveNum = 1;//탈퇴
                break;
            case R.id.btn_logout:
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#F5F5F5"));
                notiContents.setText(notiContents_logout);
                approve.setText(approve_logout);
                information_noti.setVisibility(view.VISIBLE);
                approveNum = 2;
                break;
            case R.id.approve:
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#FFFFFF"));
                final Intent loginHome = new Intent(getContext(), LoginActivity.class);
                if(approveNum == 1){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //Authentication 계정삭제
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(loginHome);
                                    }
                                }
                            });

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
                }else if(approveNum == 2){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(loginHome);
                }
                break;
            case R.id.cancel:
                layout_fragment_information_set.setBackgroundColor(Color.parseColor("#FFFFFF"));
                information_noti.setVisibility(view.GONE);
                break;
        }
    }
}
