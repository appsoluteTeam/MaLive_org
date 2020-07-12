package com.abbsolute.ma_livu.title;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abbsolute.ma_livu.R;
import com.abbsolute.ma_livu.titleFragment.TODOtitleFragment;
import com.abbsolute.ma_livu.titleFragment.attendanceFragment;
import com.abbsolute.ma_livu.titleFragment.roomFragment;
import com.abbsolute.ma_livu.titleFragment.todayFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class titleActivity extends AppCompatActivity
    implements DataListener {
    private TODOtitleFragment TODOFragment;
    private com.abbsolute.ma_livu.titleFragment.attendanceFragment attendanceFragment;
    private com.abbsolute.ma_livu.titleFragment.todayFragment todayFragment;
    private com.abbsolute.ma_livu.titleFragment.roomFragment roomFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private TextView tdBtn,attendanceBtn,todayBtn,roomBtn,upRepTitle;
    private Button repTitle,editFinishBtn;
    private Bundle bundle;
    private int repTitleIndex,category;//대표칭호 인덱스 TODO인지 출석인지 투데인지도 if문으로 해서 index 설정해줘야할듯
    private static boolean editFinish = true;//초기화 기본화면으로

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);

        TODOFragment = new TODOtitleFragment();
        attendanceFragment = new attendanceFragment();
        todayFragment = new todayFragment();
        roomFragment = new roomFragment();

        repTitle = findViewById(R.id.representationTitle);
        tdBtn = findViewById(R.id.tdBtn);
        attendanceBtn = findViewById(R.id.attendanceBtn);
        todayBtn = findViewById(R.id.todayBtn);
        roomBtn = findViewById(R.id.roomBtn);
        editFinishBtn = findViewById(R.id.editFinish);
        upRepTitle = findViewById(R.id.upRepTitle);

        //초기화면은 편집아닌 일반화면이니까 완료버튼숨기기
        editFinishBtn.setVisibility(View.GONE);

        //최초실행 fragment는 TODO로 초기화
        tdBtn.setSelected(true);

        bundle = new Bundle(2);

        if(repTitle.getText().toString().equals("")){//대표칭호 설정 안되어있을 때
            Log.d("isNULL?","Yesss");
            bundle.putString("title","null");
        }else {//대표 칭호 설정 되어 있을 때
            bundle.putString("title", repTitle.getText().toString());
        }
        bundle.putBoolean("edit",false);

        fm = getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        TODOFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();
    }

    public void dataSet(String representationTitle,int repTitleIndex,int category){//DataListener 메소드
        repTitle.setText(representationTitle);
        upRepTitle.setText(representationTitle);
        this.repTitleIndex = repTitleIndex;
        this.category = category;
    }

    public void onClick(View v){
        fragmentTransaction = fm.beginTransaction();

        Bundle bundle2 = new Bundle(4);
        if(repTitle.getText().toString().equals("")){//대표칭호 설정 안되어있을 때
            Log.d("isNULL?","Yesss");
            bundle2.putString("title","null");
        }else {
            bundle2.putString("title", repTitle.getText().toString());
            bundle2.putInt("repIndex",repTitleIndex);
            bundle2.putInt("category",category);
        }

        switch (v.getId()){
            case R.id.tdBtn:
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(v.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    attendanceFragment.isClicked = false;
                    todayFragment.isClicked = false;
                    editFinishBtn.setVisibility(v.VISIBLE);
                }
                TODOFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();

                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                roomBtn.setSelected(false);
                todayBtn.setSelected(false);

                break;
            case R.id.attendanceBtn://출석 버튼
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(v.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    TODOFragment.isClicked = false;
                    todayFragment.isClicked = false;
                    editFinishBtn.setVisibility(v.VISIBLE);
                }
                attendanceFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout, attendanceFragment).commitAllowingStateLoss();

                attendanceBtn.setSelected(true);
                tdBtn.setSelected(false);
                todayBtn.setSelected(false);
                roomBtn.setSelected(false);

                break;
            case R.id.todayBtn:
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(v.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    TODOFragment.isClicked = false;
                    attendanceFragment.isClicked = false;
                    editFinishBtn.setVisibility(v.VISIBLE);
                }
                todayFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout, todayFragment).commitAllowingStateLoss();

                todayBtn.setSelected(true);
                tdBtn.setSelected(false);
                attendanceBtn.setSelected(false);
                roomBtn.setSelected(false);

                break;
            case R.id.roomBtn:
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력 //즉 edit데이터 false넘겨주면 됨
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(v.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    TODOFragment.isClicked = false;
                    attendanceFragment.isClicked = false;
                    todayFragment.isClicked = false;
                    editFinishBtn.setVisibility(v.VISIBLE);
                }
                roomFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout, roomFragment).commitAllowingStateLoss();

                roomBtn.setSelected(true);
                todayBtn.setSelected(false);
                tdBtn.setSelected(false);
                attendanceBtn.setSelected(false);

                break;
            case R.id.editTitle://편집버튼
                //fragment 지워주고 시작, 안지워주고 하면 편집화면 겹침
                fragmentTransaction.remove(fm.findFragmentById(R.id.frameLayout));
                editFinish = false;
                TODOFragment = new TODOtitleFragment();
                bundle2.putBoolean("edit",true);
                TODOFragment.setArguments(bundle2);

                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                todayBtn.setSelected(false);
                roomBtn.setSelected(false);

                //편집완료 버튼 활성화
                editFinishBtn.setVisibility(v.VISIBLE);

                fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();
                break;
            case R.id.editFinish://완료 버튼
                fragmentTransaction.remove(fm.findFragmentById(R.id.frameLayout));
                editFinish = true;
                TODOFragment = new TODOtitleFragment();
                bundle2.putBoolean("edit",false);
                TODOFragment.setArguments(bundle2);

                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                roomBtn.setSelected(false);
                todayBtn.setSelected(false);

                //편집완료 버튼 비활성화
                editFinishBtn.setVisibility(v.GONE);

                fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();
                break;
        }

    }
}
