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

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class TODOtitleFragment extends Fragment {

    /*파이어베이스 변수*/
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    private String[] cleanTitleList,washTitleList,trashTitleList,todoTitleList;
    private String[] totalTitleList;
    private ConstraintLayout[] titleList;
    private ConstraintLayout cleanTitle1,cleanTitle2,cleanTitle3,cleanTitle4;
    private ConstraintLayout washTitle1,washTitle2,washTitle3,washTitle4;
    private ConstraintLayout trashTitle1,trashTitle2,trashTitle3,trashTitle4;
    private ConstraintLayout todoTitle1,todoTitle2,todoTitle3;

    private Boolean[] clean,wash,trash,todo; //각 목표 달성여부 우선은 다 false로 둠
    private String repTitle;
    private Boolean isedit,isNull=false,editFinish;
    public static boolean isClicked = false;
    private static int a = 0;
    private DataListener dataListener;
    private int repIndex,category;

    /*Activity에 데이터 보내기 위해 필요한 코드* 이제는 필요없음 TO-DO fragment에 데이터 보내야한다 */
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof DataListener){
            dataListener = (DataListener)context;
        }else{
            throw new RuntimeException(context.toString() + "must implement DataListener");
        }
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            repTitle = getArguments().getString("title");
            isedit = getArguments().getBoolean("edit");
            editFinish = getArguments().getBoolean("editFinish");
            repIndex = getArguments().getInt("repIndex");
            category = getArguments().getInt("category");
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View v =  inflater.inflate(R.layout.todo_title_fragment,container,false);

        cleanTitleList = new String[]{"인간돌돌이", "인간빗자루", "인간청소기", "50회는아직안정함"};
        washTitleList = new String[]{"다듬이", "빨래판", "드럼세탁기", "스타일러"};
        trashTitleList = new String[]{"5L","10L","50L","100L"};
        todoTitleList = new String[]{"todo1","todo2","todo3"};

        totalTitleList = new String[]{"인간돌돌이", "인간빗자루", "인간청소기", "50회는아직안정함","다듬이", "빨래판", "드럼세탁기", "스타일러",
               "5L","10L","50L","100L","todo1","todo2","todo3"};


        clean = new Boolean[4];
        wash = new Boolean[4];
        trash = new Boolean[4];
        todo = new Boolean[3];

        for(int i = 0; i < 4; i++){
            clean[i] = TitleFragment.TODOList[i];
        }
        for(int i = 4; i < 8; i++){
            wash[i-4] = TitleFragment.TODOList[i];
        }
        for(int i = 8; i < 12; i++){
            trash[i-8] = TitleFragment.TODOList[i];
        }
        for(int i = 12; i < 15; i++){
            todo[i-12] = TitleFragment.TODOList[i];
        }

        Boolean[] islocked = TitleFragment.TODOList;

        //setSelected는 default가 false -> 흰색 배경
        //true -> 회색배경
        cleanTitle1 = v.findViewById(R.id.cleanTitle1);
        cleanTitle2 = v.findViewById(R.id.cleanTitle2);
        cleanTitle3 = v.findViewById(R.id.cleanTitle3);
        cleanTitle4 = v.findViewById(R.id.cleanTitle4);

        washTitle1 = v.findViewById(R.id.washTitle1);
        washTitle2 = v.findViewById(R.id.washTitle2);
        washTitle3 = v.findViewById(R.id.washTitle3);
        washTitle4 = v.findViewById(R.id.washTitle4);

        trashTitle1 = v.findViewById(R.id.trashTitle1);
        trashTitle2 = v.findViewById(R.id.trashTitle2);
        trashTitle3 = v.findViewById(R.id.trashTitle3);
        trashTitle4 = v.findViewById(R.id.trashTitle4);

        todoTitle1 = v.findViewById(R.id.todoTitle1);
        todoTitle2 = v.findViewById(R.id.todoTitle2);
        todoTitle3 = v.findViewById(R.id.todoTitle3);

       titleList = new ConstraintLayout[]{cleanTitle1,cleanTitle2,cleanTitle3,cleanTitle4,washTitle1,washTitle2,washTitle3,washTitle4,
               trashTitle1,trashTitle2,trashTitle3,trashTitle4,todoTitle1,todoTitle2,todoTitle3};


        if(isedit == true) {
            if(category == 1) {//TODO category 일때
                for (int i = 0; i < titleList.length; i++) {
                    if (i == repIndex) {//대표칭호 선택되어있던것만 흰바탕
                        titleList[i].setSelected(false);
                    } else {
                        titleList[i].setSelected(true);
                    }
                }
            }else{
                for (int i = 0; i < titleList.length; i++) {
                    titleList[i].setSelected(true);//모두 회색 배경
                }
            }

            isClicked = false;

            for(int i = 0; i < titleList.length; i++){
                final ConstraintLayout TL = titleList[i];
                final int index = i;

                if(islocked[i] == true) {//잠금 풀린것만 클릭할 수 있게 헤야한다
                    titleList[i].setOnClickListener(new TextView.OnClickListener() {
                        public void onClick(View view) {
                            final int real = index;
                            isClicked = true;
                            titleList[a].setSelected(true);//전에꺼는 회색
                            TL.setSelected(false);//현재 흰색으로 변환
                            a = real;
                            ((DataListener)dataListener).dataSet(totalTitleList[a],a,1);
                        }
                    });
                }
            }
        }


        /*
            title,image findViewById해준 후 배열에 할당
         */

        TextView[] cleanTitleIdList = new TextView[]{cleanTitle1.findViewById(R.id.name),cleanTitle2.findViewById(R.id.name),cleanTitle3.findViewById(R.id.name),cleanTitle4.findViewById(R.id.name)};
        TextView[] washTitleIdList = new TextView[]{washTitle1.findViewById(R.id.name),washTitle2.findViewById(R.id.name),washTitle3.findViewById(R.id.name),washTitle4.findViewById(R.id.name)};
        TextView[] trashTitleIdList = new TextView[]{trashTitle1.findViewById(R.id.name),trashTitle2.findViewById(R.id.name),trashTitle3.findViewById(R.id.name),trashTitle4.findViewById(R.id.name)};
        TextView[] todoTitleIdList = new TextView[]{todoTitle1.findViewById(R.id.name),todoTitle2.findViewById(R.id.name),todoTitle3.findViewById(R.id.name)};


        ImageView[] cleanImageIdList = new ImageView[]{cleanTitle1.findViewById(R.id.image),cleanTitle2.findViewById(R.id.image),cleanTitle3.findViewById(R.id.image),cleanTitle4.findViewById(R.id.image)};
        ImageView[] washImageIdList = new ImageView[]{washTitle1.findViewById(R.id.image),washTitle2.findViewById(R.id.image),washTitle3.findViewById(R.id.image),washTitle4.findViewById(R.id.image)};
        ImageView[] trashImageIdList = new ImageView[]{trashTitle1.findViewById(R.id.image),trashTitle2.findViewById(R.id.image),trashTitle3.findViewById(R.id.image),trashTitle4.findViewById(R.id.image)};
        ImageView[] todoImageIdList = new ImageView[]{todoTitle1.findViewById(R.id.image),todoTitle2.findViewById(R.id.image),todoTitle3.findViewById(R.id.image)};

        //TODO: 아이콘 적용
        Drawable lock_title = getResources().getDrawable(R.drawable.lock_title);
        //각 칭호 아이콘
        Drawable[] cleanImage = new Drawable[]{getResources().getDrawable(R.drawable.clean1),getResources().getDrawable(R.drawable.clean2),getResources().getDrawable(R.drawable.clean3),getResources().getDrawable(R.drawable.clean4)};
        Drawable[] washImage = new Drawable[]{getResources().getDrawable(R.drawable.wash1),getResources().getDrawable(R.drawable.wash2),getResources().getDrawable(R.drawable.wash3),getResources().getDrawable(R.drawable.wash4)};
        Drawable[] trashImage = new Drawable[]{getResources().getDrawable(R.drawable.trash1),getResources().getDrawable(R.drawable.trash2),getResources().getDrawable(R.drawable.trash3),getResources().getDrawable(R.drawable.trash4)};
        Drawable[] todoImage = new Drawable[]{getResources().getDrawable(R.drawable.todo1),getResources().getDrawable(R.drawable.todo2),getResources().getDrawable(R.drawable.todo3)};


        for(int i = 0; i < clean.length; i++){

            if(clean[i] == true){//목표 달성할 시 칭호 부여
                cleanTitleIdList[i].setText(cleanTitleList[i]);
                cleanImageIdList[i].setImageDrawable(cleanImage[i]);
            }else{//목표 달성 못할 시 빈칸(null)
                cleanTitleIdList[i].setText("");
                cleanImageIdList[i].setImageDrawable(lock_title);
            }
        }

        for(int i = 0; i < wash.length; i++){
            if(wash[i] == true){
                washTitleIdList[i].setText(washTitleList[i]);
                washImageIdList[i].setImageDrawable(washImage[i]);
            }else{
                washTitleIdList[i].setText("");
                washImageIdList[i].setImageDrawable(lock_title);
            }
        }

        for(int i = 0; i < todo.length; i++){
            if(todo[i] == true){
                todoTitleIdList[i].setText(todoTitleList[i]);
                todoImageIdList[i].setImageDrawable(todoImage[i]);
            }else{
                todoTitleIdList[i].setText("");
                todoImageIdList[i].setImageDrawable(lock_title);
            }
        }


        for(int i = 0; i < trash.length; i++) {
            if (trash[i] == true) {
                trashTitleIdList[i].setText(trashTitleList[i]);
                trashImageIdList[i].setImageDrawable(trashImage[i]);
            } else {
                trashTitleIdList[i].setText("");
                trashImageIdList[i].setImageDrawable(lock_title);
            }
        }
        return v;
    }

    public void onDetach(){
        super.onDetach();
        dataListener = null;
    }

}
