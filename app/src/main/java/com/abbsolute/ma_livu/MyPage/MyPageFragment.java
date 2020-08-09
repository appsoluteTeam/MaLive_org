package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MyPageFragment extends Fragment {
    private View view;
    private MyPageDataListener dataListener;
    private Button btnMyPage_informationSet,btnMyPage_title,btnMyPage_pay,btnMyPage_active,btnMyPage_friend;
    private TextView nickname,textView_email;


    /*파이어베이스 변수*/
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference myPageRef;
    private static String email;
    private static String str_nickname;

    public MyPageFragment(){};

    /*login2Activity에서 데이터 받음*/
    public MyPageFragment(String email){
        this.email = email;
        Log.d("email",email);

        /*대표칭호 정보 myPage firestore에서 가져와서 category,index 변수에 저장*/
        //TODO: 데이터 가져오는걸 onCreateView나 onCreate에서 하면 적용이 다른 함수들보다 느리게 됨 스레드문제인가?

        /*user firestore에서 닉네임 정보 가져오기 */
        firestore.collection(FirebaseID.user).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                str_nickname  = shot.get(FirebaseID.Nickname).toString();
                                Log.d("TitleFragment", "user nickname Get 완료");

                            } else {
                                Log.d("TitleFragment", "No such document");
                            }
                        } else {
                            Log.d("TitleFragment", "get failed with ", task.getException());
                        }
                    }
                });
    }

    /*HomeActivity에 데이터 보내기 위해 필요한 코드*/
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof MyPageDataListener){
            dataListener = (MyPageDataListener) context;
        }else{
            throw new RuntimeException(context.toString() + "must implement DataListener");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* myPage fragment 처음 */
        view = inflater.inflate(R.layout.fragment_mypage,container,false);

        /* 정보설정*/
        btnMyPage_informationSet = view.findViewById(R.id.btnMyPage_informationSet);

        /* 칭호, 결제, 활동, 친구 아이디값 찾기 */
        btnMyPage_title = view.findViewById(R.id.btnMyPage_title);
        btnMyPage_pay = view.findViewById(R.id.btnMyPage_pay);
        btnMyPage_active = view.findViewById(R.id.btnMyPage_active);
        btnMyPage_friend = view.findViewById(R.id.btnMyPage_friend);

        /*대표칭호,email findViewByID*/
        nickname = view.findViewById(R.id.nickname);
        textView_email = view.findViewById(R.id.email);

        /*닉네임 ,email 설정*/
        nickname.setText(str_nickname);
        textView_email.setText(email);

        return view;
    }

    public void onClick(){
        /* HomeActivity에 어떤 카테고리를 눌렀는지 데이터 전달 */
        /* 0:칭호 , 1:결제, 2:활동 , 3:친구, 4:정보설정 */
        switch (view.getId()){
            case R.id.btnMyPage_title://칭호
                dataListener.myPageDataSet(0);
                break;
            case R.id.btnMyPage_pay://결제
                dataListener.myPageDataSet(1);
                break;
            case R.id.btnMyPage_active://활동
                dataListener.myPageDataSet(2);
                break;
            case R.id.btnMyPage_friend://친구
                dataListener.myPageDataSet(3);
                break;
            case R.id.btnMyPage_informationSet://정보 설정
                dataListener.myPageDataSet(4);
                break;
        }
    }
}
