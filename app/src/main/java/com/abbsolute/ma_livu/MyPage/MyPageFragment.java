package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.R;

public class MyPageFragment extends Fragment {
    private View view;
    private MyPageDataListener dataListener;
    private Button btnMyPage_title,btnMyPage_pay,btnMyPage_active,btnMyPage_friend;
    @Nullable
    @Override

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

        /* 칭호, 결제, 활동, 친구 아이디값 찾기 */
        btnMyPage_title = view.findViewById(R.id.btnMyPage_title);
        btnMyPage_pay = view.findViewById(R.id.btnMyPage_pay);
        btnMyPage_active = view.findViewById(R.id.btnMyPage_active);
        btnMyPage_friend = view.findViewById(R.id.btnMyPage_friend);

        return view;
    }

    public void onClick(){
        /* HomeActivity에 어떤 카테고리를 눌렀는지 데이터 전달 */
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
        }
    }
}
