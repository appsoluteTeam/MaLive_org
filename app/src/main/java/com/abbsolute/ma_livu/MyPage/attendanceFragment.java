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

/*출석 칭호 fragment*/

public class attendanceFragment extends Fragment {
    private String[] attendanceTitleList;
    private boolean[] islocked;
    private String repTitle;
    private ConstraintLayout atTitle1,atTitle2,atTitle3;
    private ConstraintLayout[] titleList;//atTitle1,atTitle2,atTitle3담음
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

        attendanceTitleList = new String[]{"말리브새내기","말리브정든내기","말리브껌딱지"};

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
                             Bundle savedInstanceState){
        View v =  inflater.inflate(R.layout.attendance_title_fragment,container,false);
        islocked = new boolean[]{true,true,false};

        atTitle1 = v.findViewById(R.id.attendanceTitle1);
        atTitle2 = v.findViewById(R.id.attendanceTitle2);
        atTitle3 = v.findViewById(R.id.attendanceTitle3);

        titleList = new ConstraintLayout[]{atTitle1,atTitle2,atTitle3};

        TextView[] atTitleIdList = new TextView[]{atTitle1.findViewById(R.id.name),atTitle2.findViewById(R.id.name),atTitle3.findViewById(R.id.name)};
        ImageView[] atImageIdList = new ImageView[]{atTitle1.findViewById(R.id.image),atTitle2.findViewById(R.id.image),atTitle3.findViewById(R.id.image)};

        Drawable lock_title = getResources().getDrawable(R.drawable.lock_title);

        Drawable[] attendanceImage = new Drawable[]{getResources().getDrawable(R.drawable.attendance1),getResources().getDrawable(R.drawable.attendance2),getResources().getDrawable(R.drawable.attendance3)};

        for(int i = 0; i < attendanceTitleList.length; i++){
            if(islocked[i] == true){//목표 달성할 시 칭호 부여
                titleList[i].setSelected(false);//흰색
                atTitleIdList[i].setText(attendanceTitleList[i]);
                atImageIdList[i].setImageDrawable(attendanceImage[i]);
            }else{//목표 달성 못할 시 빈칸(null)
                titleList[i].setSelected(true);//회색
                atTitleIdList[i].setText("");
                atImageIdList[i].setImageDrawable(lock_title);
            }
        }

        if(isedit == true) {

            if(category == 2) {//TODO category 일때
                for (int i = 0; i < titleList.length; i++) {
                    if (i == repIndex) {//대표칭호 선택되어있던것만 흰바탕
                        titleList[i].setSelected(false);
                    } else {
                        titleList[i].setSelected(true);
                    }
                }
            }else{
                for (int i = 0; i < attendanceTitleList.length; i++) {
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
                            titleList[a].setSelected(true);//전에꺼는 회색으로
                            TL.setSelected(false);//현재누른거 흰색으로 변환
                            a = real;
                            ((DataListener)dataListener).dataSet(attendanceTitleList[a],a,2);
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
