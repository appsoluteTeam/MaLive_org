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

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
    private static ArrayList<Boolean> TODOIslocked;
    private ArrayList<Boolean> attendanceIslocked = new ArrayList<Boolean>(Arrays.asList(true,true,false));
    private ArrayList<Boolean> todayIslocked = new ArrayList<Boolean>(Arrays.asList(true));
    private ArrayList<Boolean> roomIslocked = new ArrayList<Boolean>(Arrays.asList(true,false,false,true,true,true,false));

    private View view;
    private TODOtitleFragment TODOFragment;
    private attendanceFragment attendanceFragment;
    private todayFragment todayFragment;
    private roomFragment roomFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private TextView tdBtn,attendanceBtn,todayBtn,roomBtn,editTitle,repTitle;
    private Button editFinishBtn,btn_back;
    private Bundle bundle;
    private static int repTitleIndex,category;
    private static String str_nickname;
    private static long clean_complete, trash_complete, todo_complete, wash_complete;
    private static boolean editFinish = true;//초기화 기본화면으로
    private ImageView titleImage;
    public static Stack<Fragment> fragmentStack;
    public static Boolean[] TODOList = new Boolean[15];
    public static int count = 0;

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

        /*칭호 획득 여부 가져오기*/
        firestore.collection(FirebaseID.ToDoLists).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                clean_complete = (long) shot.get(FirebaseID.clean_complete);
                                wash_complete = (long) shot.get(FirebaseID.wash_complete);
                                trash_complete = (long) shot.get(FirebaseID.trash_complete);
                                todo_complete = (long) shot.get(FirebaseID.todo_complete);

                                Log.d("TitleFragment", "todo 가져오기 완료");
                                Log.d("clean_complete",shot.get(FirebaseID.clean_complete).toString());
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
        btn_back = view.findViewById(R.id.btn_back);

        //back버튼 적용 위해 stack에 담아둔 fragment
        fragmentStack = HomeActivity.fragmentStack;

        editFinishBtn.setVisibility(View.GONE);//초기화면은 편집아닌 일반화면이니까 완료버튼숨기기

        tdBtn.setSelected(true);//최초실행 fragment는 TODO로

        bundle = new Bundle(2);

        if(repTitle.getText().toString().equals("")){//대표칭호 설정 안되어있을 때
            bundle.putString("title","null");
        }else {
            bundle.putString("title", repTitle.getText().toString());
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
        btn_back.setOnClickListener(this);

        //category,index정보 불러와서 대표 칭호 setText
        setRepTitle(category,repTitleIndex);

        //칭호 획득 여부 판단
        setTitleIslocked(clean_complete,wash_complete,trash_complete,todo_complete);
        TODOIslocked = new ArrayList<Boolean>(Arrays.asList(TODOList));

        //파이어스토어 저장
        saveFirebaseIsLocked();

        return view;
    }

    //칭호 획득 여부 판단, 횟수에 따라서 달라짐
    public void setTitleIslocked(long clean_complete,long wash_completete, long trash_complete, long todo_complete){
        countCleanTitleIsLocked(clean_complete);
        countWashTitleIsLocked(wash_completete);
        countTrashTitleIsLocked(trash_complete);
        countTodoTitleIsLocked(todo_complete);
    }

    //count 에 따라 열렸는지 안열렸는지 체크
    public void countCleanTitleIsLocked(long clean_complete){
        if(clean_complete >= 5 && clean_complete < 10){
            setCleanTitleIsLocked(true,false,false,false);
        }else if(clean_complete >= 10 && clean_complete < 30){
            setCleanTitleIsLocked(true,true,false,false);
        }else if(clean_complete >= 30 && clean_complete < 50){
            setCleanTitleIsLocked(true,true,true,false);
        }else if(clean_complete >= 50){
            setCleanTitleIsLocked(true,true,true,true);
        }else if(clean_complete < 5){
            setCleanTitleIsLocked(false,false,false,false);
        }
    }

    public void setCleanTitleIsLocked(boolean first,boolean second, boolean third, boolean fourth){
        TODOList[0] = first;
        TODOList[1] = second;
        TODOList[2] = third;
        TODOList[3] = fourth;

        Log.d("setCleanTitle",TODOList[0].toString());
    }

    public void countWashTitleIsLocked(long wash_complete){
        if(wash_complete >= 5 && wash_complete < 10){
            setWashTitleIsLocked(true,false,false,false);
        }else if(wash_complete >= 10 && wash_complete < 30){
            setWashTitleIsLocked(true,true,false,false);
        }else if(wash_complete >= 30 && wash_complete < 50){
            setWashTitleIsLocked(true,true,true,false);
        }else if(wash_complete >= 50){
            setWashTitleIsLocked(true,true,true,true);
        }else if(wash_complete < 5){
            setWashTitleIsLocked(false,false,false,false);
        }
    }

    public void setWashTitleIsLocked(boolean first,boolean second, boolean third, boolean fourth){
        TODOList[4] = first;
        TODOList[5] = second;
        TODOList[6] = third;
        TODOList[7] = fourth;
    }

    public void countTrashTitleIsLocked(long trash_complete){
        if(trash_complete >= 4 && trash_complete < 8){
            setTrashTitleIsLocked(true,false,false,false);
        }else if(trash_complete >= 8 && trash_complete < 20){
            setTrashTitleIsLocked(true,true,false,false);
        }else if(trash_complete >= 20 && trash_complete < 40){
            setTrashTitleIsLocked(true,true,true,false);
        }else if(trash_complete >= 40){
            setTrashTitleIsLocked(true,true,true,true);
        }else if(trash_complete < 4){
            setTrashTitleIsLocked(false,false,false,false);
        }
    }

    public void setTrashTitleIsLocked(boolean first,boolean second, boolean third, boolean fourth){
        TODOList[8] = first;
        TODOList[9] = second;
        TODOList[10] = third;
        TODOList[11] = fourth;
    }

    public void countTodoTitleIsLocked(long todo_complete){
        if(todo_complete >= 10 && todo_complete < 20){
            setTodoTitleIsLocked(true,false,false);
        }else if(todo_complete >= 20 && todo_complete < 40){
            setTodoTitleIsLocked(true,true,false);
        }else if(todo_complete >= 40){
            setTodoTitleIsLocked(true,true,true);
        }else if(todo_complete < 10){
            setTodoTitleIsLocked(false,false,false);
        }
    }

    public void setTodoTitleIsLocked(boolean first,boolean second, boolean third){
        TODOList[12] = first;
        TODOList[13] = second;
        TODOList[14] = third;
    }

    public void setRepTitle(int category, int repTitleIndex){
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

            case R.id.btn_back:
                Fragment nextFragment = fragmentStack.pop();
                fragmentTransaction.replace(R.id.main_frame, nextFragment).commit();
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
