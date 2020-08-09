package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbsolute.ma_livu.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class todayFragment extends Fragment {
    private String[] todayTitleList;
    private boolean[] islocked;
    private String repTitle;
    private ConstraintLayout todayTitle1;
    private ConstraintLayout[] titleList;//todayTitle담음 현재는 한개지만 언제또 추가 될지 모르니깡
    private boolean isedit,editFinish;
    private int repIndex,category;
    private static int a = 0;
    private DataListener dataListener;
    public static boolean isClicked;

    /*Activity에 데이터 보내기 위해 필요한 코드*/
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof DataListener){
            dataListener = (DataListener)context;
        }else{
            throw new RuntimeException(context.toString() + "must implement DataListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            isedit = getArguments().getBoolean("edit");
            repTitle = getArguments().getString("title");
            repIndex = getArguments().getInt("repIndex");
            editFinish = getArguments().getBoolean("editFinish");
            category = getArguments().getInt("category");
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.today_title_fragment, container, false);
        islocked = new boolean[]{true};

        todayTitle1 = v.findViewById(R.id.todayTitle1);
        titleList = new ConstraintLayout[]{todayTitle1};

        todayTitleList = new String[]{"말리브 influencer"};

        TextView[] todayTitleIdList = new TextView[]{todayTitle1.findViewById(R.id.name)};
        ImageView[] todayImageIdList = new ImageView[]{todayTitle1.findViewById(R.id.image)};

        Drawable drawable = getResources().getDrawable(R.drawable.lock);

        for(int i = 0; i < titleList.length; i++){
            if(islocked[i] == true){//목표 달성할 시 칭호 부여
                todayTitleIdList[i].setText(todayTitleList[i]);
            }else{//목표 달성 못할 시 빈칸(null)
                todayTitleIdList[i].setText("");
                todayImageIdList[i].setImageDrawable(drawable);
            }
        }

        if(isedit == true) {
            if(category == 3) {//TODAY category 일때
                for (int i = 0; i < titleList.length; i++) {
                    if (i == repIndex) {//대표칭호 선택되어있던것만 흰바탕
                        titleList[i].setSelected(false);
                    } else {
                        titleList[i].setSelected(true);
                    }
                }
            }else{
                for (int i = 0; i < todayTitleList.length; i++) {
                    titleList[i].setSelected(true);
                }
            }

            isClicked = false;

            for(int i = 0; i < titleList.length; i++){
                final ConstraintLayout TL = titleList[i];
                final int index = i;

                if(islocked[i] == true) {//잠금 풀린것만 클릭할 수 있게 해야한다
                    titleList[i].setOnClickListener(new TextView.OnClickListener() {
                        public void onClick(View view) {
                            final int real = index;
                            isClicked = true;
                            titleList[a].setSelected(true);//전에꺼는 회색배경
                            TL.setSelected(false);//누른건 흰색으로 변환
                            a = real;
                            ((DataListener)dataListener).dataSet(todayTitleList[a],a,3);
                        }
                    });
                }
            }

        }
        return v;
    }

    public void onDetach(){
        super.onDetach();
        dataListener = null;
    }
}
