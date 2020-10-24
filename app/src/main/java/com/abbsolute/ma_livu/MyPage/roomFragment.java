package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbsolute.ma_livu.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

/* 방 꾸미기 칭호 fragment */
public class roomFragment extends Fragment {
    private String[] roomTitleList;
    private boolean[] islocked;
    private String repTitle;
    private ConstraintLayout roomTitle1,roomTitle2,roomTitle3,roomTitle4;
    private ConstraintLayout[] titleList;
    private boolean isedit,editFinish;
    private int repIndex,category;
    private static int a = 0;
    private DataListener dataListener;
    public static boolean isClicked;

    /*Activity에 데이터 보내기 위해 필요한 코드*/
    //상단 Activity 거쳐서 TitleFragment에 데이터 전달
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
        View v = inflater.inflate(R.layout.room_title_fragment, container, false);
        islocked = new boolean[]{true,false,false,true,true,true,false};

        roomTitle1 = v.findViewById(R.id.roomTitle1);
        roomTitle2 = v.findViewById(R.id.roomTitle2);
        roomTitle3 = v.findViewById(R.id.roomTitle3);
        roomTitle4 = v.findViewById(R.id.roomTitle4);


        titleList = new ConstraintLayout[]{roomTitle1,roomTitle2,roomTitle3,roomTitle4};

        roomTitleList = new String[]{"미니멀리스트","살림뉴비","살림부자","맥시멈리스트"};

        TextView[] roomTitleIdList = new TextView[]{roomTitle1.findViewById(R.id.name), roomTitle2.findViewById(R.id.name), roomTitle3.findViewById(R.id.name), roomTitle4.findViewById(R.id.name)};
        ImageView[] roomImageIdList = new ImageView[]{roomTitle1.findViewById(R.id.image), roomTitle2.findViewById(R.id.image), roomTitle3.findViewById(R.id.image), roomTitle4.findViewById(R.id.image),};

        Drawable lock_title = getResources().getDrawable(R.drawable.lock_title);
        Drawable[] roomImage = new Drawable[]{getResources().getDrawable(R.drawable.room1), getResources().getDrawable(R.drawable.room2), getResources().getDrawable(R.drawable.room3), getResources().getDrawable(R.drawable.room4)};

        for(int i = 0; i < titleList.length; i++){
            if(islocked[i] == true){//목표 달성할 시 칭호 부여
                titleList[i].setSelected(false);//흰색
                roomTitleIdList[i].setText(roomTitleList[i]);
                roomImageIdList[i].setImageDrawable(roomImage[i]);
            }else{//목표 달성 못할 시 빈칸(null)
                titleList[i].setSelected(true);//회색
                roomTitleIdList[i].setText("");
                roomImageIdList[i].setImageDrawable(lock_title);
            }
        }

        if(isedit == true) {
            if(category == 4) {
                for (int i = 0; i < titleList.length; i++) {
                    if (i == repIndex) {//대표칭호 선택되어있던것만 흰바탕
                        titleList[i].setSelected(false);
                    } else {
                        titleList[i].setSelected(true);
                    }
                }
            }else{
                for (int i = 0; i < roomTitleList.length; i++) {
                    titleList[i].setSelected(true);
                }
            }

            isClicked = false;

            for(int i = 0; i < titleList.length; i++){
                final ConstraintLayout TL = titleList[i];
                final int index = i;

                if(islocked[i] == true) {////잠금 풀린것만 클릭할 수 있게 해야한다
                    titleList[i].setOnClickListener(new TextView.OnClickListener() {
                        public void onClick(View view) {
                            final int real = index;
                            isClicked = true;
                            titleList[a].setSelected(true);//전에꺼는 회색으로
                            TL.setSelected(false);//현재누른거 흰색으로 변환
                            a = real;
                            dataListener.dataSet(roomTitleList[a],a,4);
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
