package com.abbsolute.ma_livu.titleFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbsolute.ma_livu.title.DataListener;
import com.abbsolute.ma_livu.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class TODOtitleFragment extends Fragment {

    private String[] cleanTitleList,washTitleList,dishTitleList,trashTitleList;
    private String[] totalTitleList;
    private ConstraintLayout[] titleList;
    private ConstraintLayout cleanTitle1,cleanTitle2,cleanTitle3,cleanTitle4;
    private ConstraintLayout washTitle1,washTitle2,washTitle3,washTitle4;
    private ConstraintLayout dishTitle1,dishTitle2,dishTitle3,dishTitle4;
    private ConstraintLayout trashTitle1,trashTitle2,trashTitle3,trashTitle4;

    private boolean[] clean,wash,dish,trash; //각 목표 달성여부 우선은 다 false로 둠
    private String repTitle;
    private Boolean isedit,isNull=false,editFinish;
    public static boolean isClicked = false;
    private static int a = 0;
    private DataListener dataListener;
    private int repIndex,category;

    /*Activity에 데이터 보내기 위해 필요한 코드*/
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
            Log.d("rightttt", "right");
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
        dishTitleList = new String[]{"수세미","퐁퐁","고무장갑","식기세척기"};
        trashTitleList = new String[]{"5L","10L","50L","100L"};

        totalTitleList = new String[]{"인간돌돌이", "인간빗자루", "인간청소기", "50회는아직안정함","다듬이", "빨래판", "드럼세탁기", "스타일러",
                "수세미","퐁퐁","고무장갑","식기세척기","5L","10L","50L","100L"};

        clean = new boolean[]{true,false,false,true};
        wash = new boolean[]{false,false,true,false};
        dish = new boolean[]{false,true,false,false};
        trash = new boolean[]{true,false,false,false};

        boolean[] islocked = new boolean[]{true,false,false,true,false,false,true,false,false,true,false,false,true,false,false,false};

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

        dishTitle1 = v.findViewById(R.id.dishTitle1);
        dishTitle2 = v.findViewById(R.id.dishTitle2);
        dishTitle3 = v.findViewById(R.id.dishTitle3);
        dishTitle4 = v.findViewById(R.id.dishTitle4);

        trashTitle1 = v.findViewById(R.id.trashTitle1);
        trashTitle2 = v.findViewById(R.id.trashTitle2);
        trashTitle3 = v.findViewById(R.id.trashTitle3);
        trashTitle4 = v.findViewById(R.id.trashTitle4);


        titleList = new ConstraintLayout[]{cleanTitle1,cleanTitle2,cleanTitle3,cleanTitle4,washTitle1,washTitle2,washTitle3,washTitle4,
                dishTitle1,dishTitle2,dishTitle3,dishTitle4,trashTitle1,trashTitle2,trashTitle3,trashTitle4};


        if(isedit == true) {
            Log.d("editISTRUE","editTRUE");
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

                if(islocked[i] == true) {//잠금 풀린것만 클릭할 수 있게 해야함;;;;;;;
                    titleList[i].setOnClickListener(new TextView.OnClickListener() {
                        public void onClick(View view) {
                            final int real = index;
                            isClicked = true;
                            titleList[a].setSelected(true);
                            TL.setSelected(false);
                            a = real;
                            ((DataListener)dataListener).dataSet(totalTitleList[a],a,1);
                        }
                    });
                }
            }
        }


        if(editFinish == true){
            Log.d("editFinish","받아오기 성공");
            if(isClicked == true){
               // dataListener.dataSet(totalTitleList[a],a,1,true);
            }else{

            }
        }

        TextView[] cleanTitleIdList = new TextView[]{cleanTitle1.findViewById(R.id.name),cleanTitle2.findViewById(R.id.name),cleanTitle3.findViewById(R.id.name),cleanTitle4.findViewById(R.id.name)};
        TextView[] washTitleIdList = new TextView[]{washTitle1.findViewById(R.id.name),washTitle2.findViewById(R.id.name),washTitle3.findViewById(R.id.name),washTitle4.findViewById(R.id.name)};
        TextView[] dishTitleIdList = new TextView[]{dishTitle1.findViewById(R.id.name),dishTitle2.findViewById(R.id.name),dishTitle3.findViewById(R.id.name),dishTitle4.findViewById(R.id.name)};
        TextView[] trashTitleIdList = new TextView[]{trashTitle1.findViewById(R.id.name),trashTitle2.findViewById(R.id.name),trashTitle3.findViewById(R.id.name),trashTitle4.findViewById(R.id.name)};

        ImageView[] cleanImageIdList = new ImageView[]{cleanTitle1.findViewById(R.id.image),cleanTitle2.findViewById(R.id.image),cleanTitle3.findViewById(R.id.image),cleanTitle4.findViewById(R.id.image)};
        ImageView[] washImageIdList = new ImageView[]{washTitle1.findViewById(R.id.image),washTitle2.findViewById(R.id.image),washTitle3.findViewById(R.id.image),washTitle4.findViewById(R.id.image)};
        ImageView[] dishImageIdList = new ImageView[]{dishTitle1.findViewById(R.id.image),dishTitle2.findViewById(R.id.image),dishTitle3.findViewById(R.id.image),dishTitle4.findViewById(R.id.image)};
        ImageView[] trashImageIdList = new ImageView[]{trashTitle1.findViewById(R.id.image),trashTitle2.findViewById(R.id.image),trashTitle3.findViewById(R.id.image),trashTitle4.findViewById(R.id.image)};


        Drawable drawable = getResources().getDrawable(R.drawable.lock);

        for(int i = 0; i < clean.length; i++){
            if(clean[i] == true){//목표 달성할 시 칭호 부여
                cleanTitleIdList[i].setText(cleanTitleList[i]);
            }else{//목표 달성 못할 시 빈칸(null)
                cleanTitleIdList[i].setText("");
                cleanImageIdList[i].setImageDrawable(drawable);
            }
        }

        for(int i = 0; i < wash.length; i++){
            if(wash[i] == true){
                washTitleIdList[i].setText(washTitleList[i]);
            }else{
                washTitleIdList[i].setText("");
                washImageIdList[i].setImageDrawable(drawable);
            }
        }

        for(int i = 0; i < dish.length; i++){
            if(dish[i] == true){
                dishTitleIdList[i].setText(dishTitleList[i]);
            }else{
                dishTitleIdList[i].setText("");
                dishImageIdList[i].setImageDrawable(drawable);
            }
        }

        for(int i = 0; i < trash.length; i++) {
            if (trash[i] == true) {
                trashTitleIdList[i].setText(trashTitleList[i]);
            } else {
                trashTitleIdList[i].setText("");
                trashImageIdList[i].setImageDrawable(drawable);
            }
        }
        return v;
    }

    public void onDetach(){
        super.onDetach();
        dataListener = null;
    }
}
