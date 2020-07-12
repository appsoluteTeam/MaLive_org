package com.abbsolute.ma_livu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
            Log.d("AT11","데이터전달완료");
        }
     /*   if(editFinish == true){
            Log.d("AT22","받아오기 성공");
            if(isClicked == true){
                Log.d("ATL",attendanceTitleList[a]);
            }else{

            }
        }
     */
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

        Drawable drawable = getResources().getDrawable(R.drawable.lock);

        for(int i = 0; i < attendanceTitleList.length; i++){
            if(islocked[i] == true){//목표 달성할 시 칭호 부여
                atTitleIdList[i].setText(attendanceTitleList[i]);
            }else{//목표 달성 못할 시 빈칸(null)
                atTitleIdList[i].setText("");
                atImageIdList[i].setImageDrawable(drawable);
            }
        }

        if(isedit == true) {
            //refresh();
            Log.d("ATeditISTRUE","editTRUE");

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
            /*if (repTitle.equals("null")) {//대표칭호 없어서 null이거나 이 카테고리에 없다면 다 회색초기화, 클릭하는것만 흰색으로 바뀌게
                Log.d("repTitle", "null임");
                for (int i = 0; i < attendanceTitleList.length; i++) {
                    titleList[i].setSelected(true);
                }
            } else {//대표칭호 설정되어있다.
                Log.d("대표칭호 설정된 repTitle",repTitle);
                if(category == 2) {//TODO category 일때
                    for (int i = 0; i < titleList.length; i++) {
                        if (i == repIndex) {//대표칭호 선택되어있던것만 흰바탕
                            titleList[i].setSelected(false);
                        } else {
                            titleList[i].setSelected(true);
                        }
                    }
                }
            }
            */

            isClicked = false;

            for(int i = 0; i < titleList.length; i++){
                final ConstraintLayout TL = titleList[i];
                final int index = i;

                if(islocked[i] == true) {//잠금 풀린것만 클릭할 수 있게 해야함;;;;;;;
                    titleList[i].setOnClickListener(new TextView.OnClickListener() {
                        public void onClick(View view) {
                            final int real = index;
                            isClicked = true;
                            titleList[a].setSelected(true);//전에꺼는 회색으로.? 대체 이게 무슨코드냐구 ㅜ
                            TL.setSelected(false);//흰색으로 변환 but 다른거 누르면 전에꺼 회색으로 변해야한다.......전에꺼 몇번째인지 (이거 어케알암)저장?
                            a = real;
                            ((DataListener)dataListener).dataSet(attendanceTitleList[a],a,2);
                        }
                    });
                }
            }

            if(editFinish == true){
                Log.d("editFinish","받아오기 성공");
                if(isClicked == true){
                   // dataListener.dataSet(attendanceTitleList[a],a,1,true);
                }else{

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
