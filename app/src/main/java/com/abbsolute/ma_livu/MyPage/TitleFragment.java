package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.content.ContentValues.TAG;

public class TitleFragment extends Fragment implements View.OnClickListener {

    /*파이어베이스 변수*/
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference myPageRef;
    private static String email;

    /*칭호 islocked 변수*/
    private ArrayList<Boolean> TODOIslocked = new ArrayList<Boolean>(Arrays.asList(true,false,false,true,false,false,true,false,true,false,false,false,false,true,false));
    private ArrayList<Boolean> attendanceIslocked = new ArrayList<Boolean>(Arrays.asList(true,true,false));
    private ArrayList<Boolean> todayIslocked = new ArrayList<Boolean>(Arrays.asList(true));
    private ArrayList<Boolean> roomIslocked = new ArrayList<Boolean>(Arrays.asList(true,false,false));

    private View view;
    private TODOtitleFragment TODOFragment;
    private attendanceFragment attendanceFragment;
    private todayFragment todayFragment;
    private roomFragment roomFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private TextView tdBtn,attendanceBtn,todayBtn,roomBtn,editTitle,repTitle;
    private Button editFinishBtn;
    private Bundle bundle;
    private static int repTitleIndex,category;
    private static String str_nickname;
    private static boolean editFinish = true;//초기화 기본화면으로
    private ImageView titleImage;

    public TitleFragment(){};
    public TitleFragment(String email){
        this.email = email;
        Log.d("email",email);

        /*대표칭호 정보 myPage firestore에서 가져와서 category,index 변수에 저장*/
        //TODO: 데이터 가져오는걸 onCreateView나 onCreate에서 하면 적용이 다른 함수들보다 느리게 됨 스레드문제인가?
        firestore.collection(FirebaseID.myPage).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                category  = Integer.valueOf((shot.get(FirebaseID.titleCategory).toString()));
                                repTitleIndex = Integer.valueOf((shot.get(FirebaseID.index).toString()));
                                Log.d("Titlecategory",String.valueOf(category));
                                Log.d("Titleindex",String.valueOf(repTitleIndex));
                                Log.d("TitleFragment", "대표칭호,인덱스 Get 완료");

                            } else {
                                Log.d("TitleFragment", "No such document");
                            }
                        } else {
                            Log.d("TitleFragment", "get failed with ", task.getException());
                        }
                    }
                });

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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.title,container,false);

        myPageRef = firestore.collection(FirebaseID.myPage).document(email);

        TODOFragment = new TODOtitleFragment();
        attendanceFragment = new attendanceFragment();
        todayFragment = new todayFragment();
        roomFragment = new roomFragment();

        repTitle = view.findViewById(R.id.representationTitle);
        tdBtn = view.findViewById(R.id.tdBtn);
        attendanceBtn = view.findViewById(R.id.attendanceBtn);
        todayBtn = view.findViewById(R.id.todayBtn);
        roomBtn = view.findViewById(R.id.roomBtn);
        editFinishBtn = view.findViewById(R.id.editFinish);
        editTitle = view.findViewById(R.id.editTitle);
        titleImage = view.findViewById(R.id.image);

        editFinishBtn.setVisibility(View.GONE);//초기화면은 편집아닌 일반화면이니까 완료버튼숨기기

        tdBtn.setSelected(true);//최초실행 fragment는 TODO로

        bundle = new Bundle(2);

        if(repTitle.getText().toString().equals("")){//대표칭호 설정 안되어있을 때
            bundle.putString("title","null");
        }else {
            bundle.putString("title", repTitle.getText().toString());
            Log.d("Titleeee",repTitle.getText().toString());
        }
        bundle.putBoolean("edit",false);

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        TODOFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();

        /* 각 버튼 setOnClickListener해주기 */
        tdBtn.setOnClickListener(this);
        attendanceBtn.setOnClickListener(this);
        todayBtn.setOnClickListener(this);
        roomBtn.setOnClickListener(this);
        editTitle.setOnClickListener(this);
        editFinishBtn.setOnClickListener(this);

        //category,index정보 불러와서 대표 칭호 setText
        setRepTitle(category,repTitleIndex);

        //파이어스토어 저장
        saveFirebaseIsLocked();

        return view;
    }
    public void setRepTitle(int category, int repTitleIndex){
        Log.d("setTitle","setRepTitle메소드 접속");
        titleList titleList = new titleList();

        //arrays.xml에서 각 title array불러오기
        TypedArray title_todo_image = getResources().obtainTypedArray(R.array.title_todo_image);
        TypedArray title__attendance_image = getResources().obtainTypedArray(R.array.title__attendance_image);

        String strReptitle;

        switch (category){
            case 1:
                strReptitle = titleList.getTODO_titleList(repTitleIndex);
                repTitle.setText(strReptitle);
                Drawable todoTitleImage = title_todo_image.getDrawable(repTitleIndex);
                titleImage.setImageDrawable(todoTitleImage);
                break;
            case 2:
                strReptitle = titleList.getAttendance_titleList(repTitleIndex);
                repTitle.setText(strReptitle);
                Drawable atTitleImage = title__attendance_image.getDrawable(repTitleIndex);
                titleImage.setImageDrawable(atTitleImage);
                break;
            case 3:
                strReptitle = titleList.getToday_titleList(repTitleIndex);
                repTitle.setText(strReptitle);
                break;
            case 4:
                strReptitle = titleList.getRoom_titleList(repTitleIndex);
                repTitle.setText(strReptitle);
                break;
        }
    }

    public void dataSet(String representationTitle,int repTitleIndex,int category){//DataListener 메소드
        this.repTitleIndex = repTitleIndex;
        this.category = category;
        setRepTitle(category,repTitleIndex);
    }

    public void onClick(View v){
        fragmentTransaction = fm.beginTransaction();

        Bundle bundle2 = new Bundle(4);
        if(repTitle.getText().toString().equals("")){//대표칭호 설정 안되어있을 때
            bundle2.putString("title","null");
        }else {
            bundle2.putString("title", repTitle.getText().toString());
            bundle2.putInt("repIndex",repTitleIndex);
            bundle2.putInt("category",category);
        }

        switch (v.getId()){
            case R.id.tdBtn:
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력,즉 edit데이터 false넘겨주면 됨
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
            case R.id.attendanceBtn://출석
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력, 즉 edit데이터 false넘겨주면 됨
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
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력 , 즉 edit데이터 false넘겨주면 됨
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
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력 ,즉 edit데이터 false넘겨주면 됨
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
                //지워주고 시작, 안지워주고 하면 화면 겹침
                fragmentTransaction.remove(fm.findFragmentById(R.id.frameLayout));
                editFinish = false;
                TODOFragment = new TODOtitleFragment();//fragment새로 호출
                bundle2.putBoolean("edit",true);
                TODOFragment.setArguments(bundle2);
                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                todayBtn.setSelected(false);
                roomBtn.setSelected(false);
                editFinishBtn.setVisibility(v.VISIBLE); //완료버튼 보이기
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
                editFinishBtn.setVisibility(v.GONE);//완료버튼 숨기기
                fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();

                //TODO: 대표칭호 선택 될 때 파이어스토어 디비 업데이트 - 완료
                myPageRef.update(FirebaseID.titleCategory,category);
                myPageRef.update(FirebaseID.index,repTitleIndex);

                break;
        }

    }

    //각 카테고리 isLocked여부, 대표칭호 category,index 파이어베이스에 저장하는 메소드
    public void saveFirebaseIsLocked(){
        if(firebaseAuth.getCurrentUser() != null){
            Map<String, Object> myPageTitle = new HashMap<>();
            //isLocked Custom Class 호출
            isLocked isLocked = new isLocked(1,TODOIslocked,attendanceIslocked,todayIslocked,roomIslocked,category,repTitleIndex);

            //email을 문서이름으로 해서 firestore에 저장
            firestore.collection(FirebaseID.myPage)
                    .document(email).set(isLocked, SetOptions.merge());
        }
    }

}
