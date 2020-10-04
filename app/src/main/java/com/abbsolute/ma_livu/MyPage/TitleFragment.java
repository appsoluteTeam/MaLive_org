package com.abbsolute.ma_livu.MyPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
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
import com.abbsolute.ma_livu.Home.ToDoList.OnBackPressedListener;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.shadow.ShadowRenderer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import static android.content.Context.MODE_PRIVATE;

public class TitleFragment extends Fragment implements View.OnClickListener, OnBackPressedListener {

    private View view;

    /*sharedPreference*/
    private final String todoTitleFile = email + "-check_todoTitle_first";
    private final String attendanceTitleFile = email + "-check_attendanceTitle_first";

    /*파이어베이스 변수*/
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference myPageRef;
    private static String email;

    /*칭호 islocked 변수*/
    private static ArrayList<Boolean> TODOIslocked;
    private static ArrayList<Boolean> attendanceIslocked;

    private ArrayList<Boolean> todayIslocked = new ArrayList<Boolean>(Arrays.asList(true));
    private ArrayList<Boolean> roomIslocked = new ArrayList<Boolean>(Arrays.asList(true,false,false,true,true,true,false));

    //fragment 관련 변수들
    private TODOtitleFragment TODOFragment;
    private attendanceFragment attendanceFragment;
    private todayFragment todayFragment;
    private roomFragment roomFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    //값 받아오는 변수들
    private TextView tdBtn,attendanceBtn,todayBtn,roomBtn,editTitle,repTitle;
    private Button editFinishBtn,btn_back,newTODO,newAttendance,newBtn;
    private ImageView titleImage;
    private Bundle bundle;

    private static int repTitleIndex,category;
    private static String str_nickname;
    private static long clean_complete, trash_complete, todo_complete, wash_complete;
    private static long attendanceCount;
    public static int count = 0;
    private static boolean editFinish = true;//초기화 기본화면으로
    public static Stack<Fragment> fragmentStack;

    //pay관련 변수들
    private String recentBalance;
    private long recentPayDocumentNum;

    public static Boolean[] TODOList = new Boolean[15];
    public static Boolean[] attendanceList = new Boolean[3];


    //칭호 새로 획득 여부

    public static String eamilAttendanceComplete;

    public TitleFragment(){}

    public TitleFragment(final String email){
        TitleFragment.email = email;
        Log.d("email",email);


        /*대표칭호 정보 myPage firestore에서 가져와서 category,index 변수에 저장*/
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
                                category = 0;
                                repTitleIndex = 0;
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
                                str_nickname = "";
                                Log.d("TitleFragment", "No such document");
                            }
                        } else {
                            Log.d("TitleFragment", "get failed with ", task.getException());
                        }
                    }
                });

        /*칭호 획득 여부 가져오기*/
        //TODO:앱을 새로 킬 때 마다 업데이트 해줘야함 .. 메인에서 불러오기..?
        firestore.collection(FirebaseID.ToDoLists).document(email).collection("total").document("sub")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                /*
                                editor.putLong(emailCleanComplete,(long)shot.get(FirebaseID.clean_complete));
                                editor.putLong(emailWashComplete,(long)shot.get(FirebaseID.wash_complete));
                                editor.putLong(emailTrashComplete,(long)shot.get(FirebaseID.trash_complete));
                                editor.putLong(emailTodoComplete,(long)shot.get(FirebaseID.todo_complete));
                                editor.commit();
                                */

                                if(shot.get(FirebaseID.clean_complete)== null){
                                    clean_complete = 0;
                                }else{
                                    clean_complete = (long) shot.get(FirebaseID.clean_complete);

                                }

                                if(shot.get(FirebaseID.trash_complete)== null){
                                    trash_complete = 0;
                                }else{
                                    trash_complete = (long) shot.get(FirebaseID.trash_complete);

                                }

                                if(shot.get(FirebaseID.wash_complete)== null){
                                    wash_complete = 0;
                                }else{
                                    wash_complete = (long) shot.get(FirebaseID.wash_complete);

                                }

                                if(shot.get(FirebaseID.todo_complete)== null){
                                    todo_complete = 0;
                                }else{
                                    todo_complete = (long) shot.get(FirebaseID.todo_complete);

                                }
                                Log.d("TitleFragment", "todo 가져오기 완료");
                                Log.d("washComplte",Long.valueOf(wash_complete).toString());
                            } else {
                                clean_complete = 0;
                                wash_complete = 0;
                                trash_complete = 0;
                                todo_complete = 0;

                                Log.d("TitleFragment", "No such document");
                            }
                        } else {
                            Log.d("TitleFragment", "get failed with ", task.getException());
                        }
                    }
                });



        firestore.collection(FirebaseID.Attendance).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();

                                attendanceCount = (long)shot.get(FirebaseID.attendanceCount);

                                Log.d("TitleFragment", "attendance count 가져오기 완료");
                            } else {
                                attendanceCount = 0;
                                Log.d("TitleFragment", "No such document");
                            }
                        } else {
                            Log.d("TitleFragment", "get failed with ", task.getException());
                        }
                    }
                });

    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.title,container,false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
        ((HomeActivity)getActivity()).setOnBackPressedListener(this);
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

        newTODO = view.findViewById(R.id.newTODO);
        newAttendance = view.findViewById(R.id.newAttendance);

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

        /*칭호 획득 여부 판단*/
        //to-do
        setTodoTitleIslocked(clean_complete,wash_complete,trash_complete,todo_complete);
        TODOIslocked = new ArrayList<Boolean>(Arrays.asList(TODOList));

        //출석
        setAttendanceIslocked(attendanceCount);
        attendanceIslocked = new ArrayList<Boolean>(Arrays.asList(attendanceList));


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
              saveFirebaseIsLocked();
            }
        }, 5000);
        getRecentPayDocument();

        return view;
    }


    /* TODO: at,today,room 체크
    public boolean checkNewAtTitle(){
        if()
    }
    public boolean checkTodayTitle(){

    }
    public boolean checkRoomTitle(){

    }
    */
    //칭호 획득 여부 판단, 횟수에 따라서 달라짐
    public void setTodoTitleIslocked(long clean_complete,long wash_completete, long trash_complete, long todo_complete){
        countCleanTitleIsLocked(clean_complete);
        countWashTitleIsLocked(wash_completete);
        countTrashTitleIsLocked(trash_complete);
        countTodoTitleIsLocked(todo_complete);
    }


    //count 에 따라 to-do title 열렸는지 안열렸는지 체크
    public void countCleanTitleIsLocked(long clean_complete){
        if(clean_complete >= 5 && clean_complete < 10){
            setCleanTitleIsLocked(true,false,false,false);
            checkFirstGetTitle("TODO",0);
        }else if(clean_complete >= 10 && clean_complete < 30){
            setCleanTitleIsLocked(true,true,false,false);
            checkFirstGetTitle("TODO",1);
        }else if(clean_complete >= 30 && clean_complete < 50){
            setCleanTitleIsLocked(true,true,true,false);
            checkFirstGetTitle("TODO",2);
        }else if(clean_complete >= 50){
            setCleanTitleIsLocked(true,true,true,true);
            checkFirstGetTitle("TODO",3);
        }else if(clean_complete < 5){
            setCleanTitleIsLocked(false,false,false,false);
        }
    }

    public void setCleanTitleIsLocked(boolean first,boolean second, boolean third, boolean fourth){
        TODOList[0] = first;
        TODOList[1] = second;
        TODOList[2] = third;
        TODOList[3] = fourth;

    }

    public void countWashTitleIsLocked(long wash_complete){
        if(wash_complete >= 5 && wash_complete < 10){
            setWashTitleIsLocked(true,false,false,false);
            checkFirstGetTitle("TODO",4);
        }else if(wash_complete >= 10 && wash_complete < 30){
            setWashTitleIsLocked(true,true,false,false);
            checkFirstGetTitle("TODO",5);
        }else if(wash_complete >= 30 && wash_complete < 50){
            setWashTitleIsLocked(true,true,true,false);
            checkFirstGetTitle("TODO",6);
        }else if(wash_complete >= 50){
            setWashTitleIsLocked(true,true,true,true);
            checkFirstGetTitle("TODO",7);
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
            checkFirstGetTitle("TODO",8);
        }else if(trash_complete >= 8 && trash_complete < 20){
            setTrashTitleIsLocked(true,true,false,false);
            checkFirstGetTitle("TODO",9);
        }else if(trash_complete >= 20 && trash_complete < 40){
            setTrashTitleIsLocked(true,true,true,false);
            checkFirstGetTitle("TODO",10);
        }else if(trash_complete >= 40){
            setTrashTitleIsLocked(true,true,true,true);
            checkFirstGetTitle("TODO",11);
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
            checkFirstGetTitle("TODO",12);
        }else if(todo_complete >= 20 && todo_complete < 40){
            setTodoTitleIsLocked(true,true,false);
            checkFirstGetTitle("TODO",13);
        }else if(todo_complete >= 40){
            setTodoTitleIsLocked(true,true,true);
            checkFirstGetTitle("TODO",14);
        }else if(todo_complete < 10){
            setTodoTitleIsLocked(false,false,false);
        }
    }

    public void setTodoTitleIsLocked(boolean first,boolean second, boolean third){
        TODOList[12] = first;
        TODOList[13] = second;
        TODOList[14] = third;
    }

    public void setAttendanceIslocked(long attendanceCount){
        if(attendanceCount >= 15 && attendanceCount < 30){//새내기
            setAttendanceTitleIsLocked(true,false,false);
            checkFirstGetTitle("Attendance",0);
        }else if(attendanceCount >= 30 && attendanceCount < 100){//정든내기
            setAttendanceTitleIsLocked(true,true,false);
            checkFirstGetTitle("Attendance",1);
        }else if(attendanceCount >= 100){//껌딱지
            setAttendanceTitleIsLocked(true,true,true);
            checkFirstGetTitle("Attendance",2);
        }else if(attendanceCount < 15){
            setAttendanceTitleIsLocked(false,false,false);
        }
    }

    public void setAttendanceTitleIsLocked(boolean first, boolean second, boolean third){
        attendanceList[0] = first;
        attendanceList[1] = second;
        attendanceList[2] = third;
    }

    //최초로 얻은 칭호인지 check하는 메소드
    public void checkFirstGetTitle(final String category, final int index){
         /*
            0이면(아직 얻지않은거니까){//최초한번이니까
                칭호 얻은거 톨 얻게 해주고
                to-do 파란불보이게 하고
                해당건 1로 바꿔주기
            }else(1이면){
                to-do 파란불꺼주기
            }
          */
//         String keyname = category + "-" + Integer.valueOf(index).toString();
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(category,MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();


        if(category.equals("TODO")) {
            Log.d("here","TODO");
            newBtn = newTODO;
        }else if(category.equals("Attendance")){
            Log.d("here","AT");
            newBtn = newAttendance;
        }

        //false인지 확인
        firestore.collection(FirebaseID.myPage).document(email).collection("title").document("titleIsLocked")

                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();

                                Boolean lock = (Boolean)shot.get(category+"-" + Integer.toString(index));
                                Log.d("lock",lock.toString());
                                if(lock == false){ //최초로 칭호 얻음
                                    Log.d("false","false");
                                    newBtn.setVisibility(view.VISIBLE);

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            newBtn.setVisibility(view.INVISIBLE);
                                        }
                                    }, 1000000);

                                    //todo:getRecentPayDocument() 호출

                                //    firestore.collection(FirebaseID.myPage)
                                 //           .document(email)
                                 //           .collection("title")
                                 //           .document("titleIsLocked")
                                 //           .update(category+"-" + Integer.toString(index),true);
                                }
                            } else {}
                        } else {
                        }
                    }
                });
   //     boolean checkFisrt = sharedPreferences.getBoolean(keyname,false);

//
//
//        if(checkFisrt == false){    //최초 한 번 일 때
//            newBtn.setVisibility(view.VISIBLE);
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    newBtn.setVisibility(view.INVISIBLE);
//                }
//            }, 1000000);

            //톨 얻기
            //todo:getRecentPayDocument() 호출

           // editor.putBoolean(keyname,true);
           // editor.commit();
//        }
    }

    //가장 최근 문서 알아내기
    public void getRecentPayDocument(){
        firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.tmpData).document("recentPayNum")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                recentPayDocumentNum = Long.parseLong(shot.get(FirebaseID.recentDocument).toString());
                                Log.d("getRecentPayDocument 안",Long.valueOf(recentPayDocumentNum).toString());
                            } else {
                                recentPayDocumentNum = 0;
                            }
                            getRecentBalance(recentPayDocumentNum);
                        } else {
                        }
                    }
                });
    }

    //가장 최근 문서의 잔액 알아내기
    public void getRecentBalance(final long recentPayDocumentNum){
        Log.d("getRecentBalance","접근완료");
        if(recentPayDocumentNum == 0){
            recentBalance = "0";
            Log.d("recentBalance final", recentBalance);
            getToll(recentBalance,recentPayDocumentNum);
        }else{
            Log.d("else문","else");
            firestore.collection(FirebaseID.myPage).document(email).collection(FirebaseID.pay)
                    .whereEqualTo(FirebaseID.order,Long.valueOf(recentPayDocumentNum).toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    recentBalance = shot.get(FirebaseID.balance).toString();
                                    Log.d("recentBalance final", recentBalance);
                                    getToll(recentBalance,recentPayDocumentNum);
                                }
                            }
                        }
                    });

        }


    }

    //직전의 balance에 근거해서 톨 파이어스토어에 저장
    public void getToll(String recentBalance,long recentPayDocumentNum){

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        String format_time = format.format(calendar.getTime());


        String month = Integer.valueOf(calendar.get(Calendar.MONTH)).toString();
        String date = Integer.valueOf(calendar.get(Calendar.DATE)).toString();

        String today = month + "." + date;

        String hour = Integer.valueOf(calendar.get(Calendar.HOUR)).toString();
        String minute = Integer.valueOf(calendar.get(Calendar.MINUTE)).toString();

        String time = hour + ":" + minute;

        int amount = 400;//임시로
        int balance = Integer.parseInt(recentBalance) + amount;

        String order = String.valueOf(recentPayDocumentNum + 1);

        payItemListView payItemListView =
                new payItemListView(today,"칭호 획득 보상","입금","+400",Integer.valueOf(balance).toString(),time,order);

        //파이어스토어 저장
        firestore.collection(FirebaseID.myPage)
                .document(email)
                .collection(FirebaseID.pay)
                .document(format_time)
                .set(payItemListView,SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        //최근문서 바꾸기
        Map<String,Object> payMap = new HashMap<>();
        payMap.put(FirebaseID.recentDocument,order);

        firestore.collection(FirebaseID.myPage)
                .document(email)
                .collection(FirebaseID.tmpData)
                .document("recentPayNum")
                .set(payMap);


    }

    public void setRepTitle(int category, int repTitleIndex){
        titleList titleList = new titleList();

        //arrays.xml에서 각 title array불러오기
        TypedArray title_todo_image = getResources().obtainTypedArray(R.array.title_todo_image);
        TypedArray title_attendance_image = getResources().obtainTypedArray(R.array.title_attendance_image);
        TypedArray title_today_image = getResources().obtainTypedArray(R.array.title_today_image);
        TypedArray title_room_image = getResources().obtainTypedArray(R.array.title_room_image);
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
                Drawable atTitleImage = title_attendance_image.getDrawable(repTitleIndex);
                titleImage.setImageDrawable(atTitleImage);
                break;
            case 3:
                strReptitle = titleList.getToday_titleList(repTitleIndex);
                repTitle.setText(strReptitle);
                Drawable todayTitleImage = title_today_image.getDrawable(repTitleIndex);
                titleImage.setImageDrawable(todayTitleImage);
                break;
            case 4:
                strReptitle = titleList.getRoom_titleList(repTitleIndex);
                repTitle.setText(strReptitle);
                Drawable roomTitleImage = title_room_image.getDrawable(repTitleIndex);
                titleImage.setImageDrawable(roomTitleImage);
                break;
        }
    }

    public void dataSet(String representationTitle,int repTitleIndex,int category){//DataListener 메소드
        TitleFragment.repTitleIndex = repTitleIndex;
        TitleFragment.category = category;
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
                    editFinishBtn.setVisibility(View.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    com.abbsolute.ma_livu.MyPage.attendanceFragment.isClicked = false;
                    com.abbsolute.ma_livu.MyPage.todayFragment.isClicked = false;
                    editFinishBtn.setVisibility(View.VISIBLE);
                }
                TODOFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();

                //tdBtn 버튼 제외하고 모두 회색
                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                roomBtn.setSelected(false);
                todayBtn.setSelected(false);

                break;
            case R.id.attendanceBtn://출석
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력, 즉 edit데이터 false넘겨주면 됨
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(View.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    TODOtitleFragment.isClicked = false;
                    com.abbsolute.ma_livu.MyPage.todayFragment.isClicked = false;
                    editFinishBtn.setVisibility(View.VISIBLE);
                }

                //atBtn 버튼 제외하고 모두 회색
                attendanceBtn.setSelected(true);
                tdBtn.setSelected(false);
                todayBtn.setSelected(false);
                roomBtn.setSelected(false);

                attendanceFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout, attendanceFragment).commitAllowingStateLoss();
                break;
            case R.id.todayBtn:
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력 , 즉 edit데이터 false넘겨주면 됨
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(View.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    TODOtitleFragment.isClicked = false;
                    com.abbsolute.ma_livu.MyPage.attendanceFragment.isClicked = false;
                    editFinishBtn.setVisibility(View.VISIBLE);
                }

                //todayBtn버튼 제외하고 모두 회색
                todayBtn.setSelected(true);
                tdBtn.setSelected(false);
                attendanceBtn.setSelected(false);
                roomBtn.setSelected(false);

                todayFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout, todayFragment).commitAllowingStateLoss();
                break;
            case R.id.roomBtn:
                if(editFinish == true){//편집화면이 아니라면 정상화면 출력 ,즉 edit데이터 false넘겨주면 됨
                    bundle2.putBoolean("edit",false);
                    editFinishBtn.setVisibility(View.GONE);
                }else {//edit데이터 true넘겨줘야함
                    bundle2.putBoolean("edit",true);
                    TODOtitleFragment.isClicked = false;
                    com.abbsolute.ma_livu.MyPage.attendanceFragment.isClicked = false;
                    com.abbsolute.ma_livu.MyPage.todayFragment.isClicked = false;
                    editFinishBtn.setVisibility(View.VISIBLE);
                }

                //roomBtn버튼 제외하고 모두 회색
                roomBtn.setSelected(true);
                todayBtn.setSelected(false);
                tdBtn.setSelected(false);
                attendanceBtn.setSelected(false);

                roomFragment.setArguments(bundle2);
                fragmentTransaction.replace(R.id.frameLayout, roomFragment).commitAllowingStateLoss();
                break;
            case R.id.editTitle://편집버튼
                /* 전 화면 지워주고 시작, 안지워주고 하면 화면 겹침 */
                fragmentTransaction.remove(fm.findFragmentById(R.id.frameLayout));
                editFinish = false;

                TODOFragment = new TODOtitleFragment();//fragment새로 호출
                bundle2.putBoolean("edit",true);
                TODOFragment.setArguments(bundle2);

                //편집하기 눌렀을 때 최초화면 to-do fragment 이기 떄문에 tdBtn빼고 다 회색
                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                todayBtn.setSelected(false);
                roomBtn.setSelected(false);

                editFinishBtn.setVisibility(View.VISIBLE); //완료버튼 보이기

                fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();
                break;
            case R.id.editFinish://완료 버튼
                /* 전 화면 지워주고 시작, 안지워주고 하면 화면 겹침 */
                fragmentTransaction.remove(fm.findFragmentById(R.id.frameLayout));
                editFinish = true;

                TODOFragment = new TODOtitleFragment();
                bundle2.putBoolean("edit",false);
                TODOFragment.setArguments(bundle2);

                tdBtn.setSelected(true);
                attendanceBtn.setSelected(false);
                roomBtn.setSelected(false);
                todayBtn.setSelected(false);

                //대표칭호 선택 될 때 파이어스토어 디비 업데이트
                myPageRef.update(FirebaseID.titleCategory,category);
                myPageRef.update(FirebaseID.index,repTitleIndex);

                editFinishBtn.setVisibility(View.GONE);//완료버튼 숨기기
                fragmentTransaction.add(R.id.frameLayout,TODOFragment).commitAllowingStateLoss();
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
            //Map<String, Object> myPageTitle = new HashMap<>();
            //isLocked Custom Class 호출
            //isLocked isLocked = new isLocked(1,TODOIslocked,attendanceIslocked,todayIslocked,roomIslocked,category,repTitleIndex);

            //대표 칭호 정보
            Map<String,Object> repTitleInfo = new HashMap<>();
            repTitleInfo.put("category",category);
            repTitleInfo.put("index",repTitleIndex);

            //대표 칭호 정보 저장
            firestore.collection(FirebaseID.myPage).document(email)
                    .set(repTitleInfo, SetOptions.merge());


            //칭호 잠금 여부
            Map<String, Boolean> locked = new HashMap<>();

            for(int i = 0; i < TODOIslocked.size(); i++){
                locked.put("TODO-" + i,TODOIslocked.get(i));
            }
            for(int i = 0; i < attendanceIslocked.size(); i++){
                locked.put("Attendance-" + i,attendanceIslocked.get(i));
            }
            for(int i = 0; i < todayIslocked.size(); i++){
                locked.put("Today-" + i,todayIslocked.get(i));
            }
            for(int i = 0; i< roomIslocked.size(); i++){
                locked.put("Room-" + i, roomIslocked.get(i));
            }

            //칭호잠금여부 firestore 저장
            firestore.collection(FirebaseID.myPage).document(email)
                    .collection("title")
                    .document("titleIsLocked")
                    .set(locked, SetOptions.merge());
//                //myPageRef.set(isLocked);
//
//
//                //email을 문서이름으로 해서 firestore에 저장
//                firestore.collection(FirebaseID.myPage)
//                        .document(email).set(isLocked, SetOptions.merge());

        }
    }

    @Override
    public void onBackPressed() {
        ((HomeActivity)getActivity()).setFragment(2);
    }
}
