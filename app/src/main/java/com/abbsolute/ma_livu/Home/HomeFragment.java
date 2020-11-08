package com.abbsolute.ma_livu.Home;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.Alarm.PopupFragment;
import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
//체크
//import com.abbsolute.ma_livu.Customize.ColorFragment;
import com.abbsolute.ma_livu.Customize.ColorFragment;
import com.abbsolute.ma_livu.Customize.FaceFragment;
import com.abbsolute.ma_livu.Customize.ItemFragment;
import com.abbsolute.ma_livu.Customize.UnityItem;
import com.abbsolute.ma_livu.Firebase.FirebaseID;

import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.unity3d.player.UnityPlayer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private View view;
    private Button go_Todo;
    private Button go_GuestBook;

    private ImageButton colorBtt;
    private ImageButton accessoryBtt;
    private ImageButton expressionBtt;
    private static String email;
    private long atCount;

    //출석체크 관련 변수
    private String beforeCalendar;
//    private LinearLayout layout_checkin;
//    private Button at_close_button;

    //파이어스토에 저장하기 위한 변수
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // 유니티
    protected UnityPlayer mUnityPlayer;
    private FrameLayout fl_forUnity;
    public HomeFragment(){}

    public HomeFragment(String email){
        HomeFragment.email = email;
    }

//    // FCM Topic test
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FirebaseMessaging.getInstance().subscribeToTopic("FCM");
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUnityPlayer = new UnityPlayer(getActivity());

        view = inflater.inflate(R.layout.fragment_home,container,false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
  //      ((HomeActivity)getActivity()).setOnBackPressedListener(this);

//        layout_checkin = view.findViewById(R.id.layout_checkin);
//        at_close_button = (Button) view.findViewById(R.id.at_close_button);

        attendance_check();

        go_Todo=view.findViewById(R.id.go_Todo);
        go_Todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).setFragment(100);
            }
        });
        LinearLayout floatingBtns = view.findViewById(R.id.customize_floating_buttons);
        /*
        Button goCustomize = view.findViewById(R.id.go_customize);
        goCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUnityPlayer.UnitySendMessage("SceneManager", "LoadCustomizeScene","");
                floatingBtns.setVisibility(View.VISIBLE);
            }
        });
*/
        //customize listners
        colorBtt = floatingBtns.findViewById(R.id.btn_fragment);
        accessoryBtt = floatingBtns.findViewById(R.id.accessory_btn);
        expressionBtt = floatingBtns.findViewById(R.id.expression_btn);
        colorBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ColorBottom bottomSheet = new ColorBottom();
//                bottomSheet.show(getActivity().getSupportFragmentManager(), "bottomSheet");
                ColorFragment colorFragment = new ColorFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.customize_frame,colorFragment).commit();
            }
        });
        accessoryBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     ItemBottom bottomSheet = new ItemBottom();
                ItemFragment itemFragment = new ItemFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.customize_frame,itemFragment).commit();
           //     bottomSheet.show(getFragmentManager(), "bottomSheet");

            }
        });
        expressionBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FaceFragment faceFragment = new FaceFragment();
                moveCameraToFace();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.customize_frame,faceFragment).commit();

            }
        });

// 체크!!
//         여기서부터 유니티
        this.fl_forUnity = view.findViewById(R.id.fl_forUnity);
        this.fl_forUnity.addView(mUnityPlayer.getView(),
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);//First fix Line

//        at_close_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layout_checkin.setVisibility(View.GONE);
//            }
//        });



        return view;
    }

    public void AssignSkin(String color){
        mUnityPlayer.UnitySendMessage("쌀알1", "AssignSkin", color);
    }

    public void cancelColorChange() {
        mUnityPlayer.UnitySendMessage("쌀알1", "cancleSkin", "");
    }

    public void saveColorChange(String color){
        mUnityPlayer.UnitySendMessage("쌀알1", "saveSkin", color);
    }

    public void moveCameraToFace(){
        mUnityPlayer.UnitySendMessage("쌀알1", "MoveCameraToFace", "");
    }
    public void moveCameraToInit(){
        mUnityPlayer.UnitySendMessage("쌀알1", "MoveCameraToInit", "");
    }

    public void AssignEquipment(UnityItem unityItem){
        Gson gson = new Gson();
        String json = gson.toJson(unityItem);
        Log.d("whiy",json);
        mUnityPlayer.UnitySendMessage("쌀알1", "AssignEquipment", json);
    }

    /*  출석체크 메소드    */
    public void attendance_check(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("time",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //현재 접속 년원일 구하기
        Calendar cal = Calendar.getInstance();
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String date = Integer.toString(cal.get(Calendar.DATE));

        //현재 접속 년월일 String형으로
        String currentCalendar = year + "-" + month + "-" + date;

        String emailBeforeCalendar = "beforeCalendar-" + email; //sharedPreference에 email값과 함께 저장하기 위해 만든 String형 key값

        beforeCalendar = sharedPreferences.getString(emailBeforeCalendar,"null");

        Log.d("currenCalendar",currentCalendar);
        Log.d("beforeCalendar",beforeCalendar);

        if(beforeCalendar.equals(currentCalendar)) {//둘의 날짜가 같으면 중복인것임...
//            layout_checkin.setVisibility(View.GONE);
            Toast.makeText(getContext(),"마지막 접속 하고 하루 안지났음!",Toast.LENGTH_LONG).show();
        }else{//두 값이 다르면 중복이 아님 -> 즉 출석체크 해줘야한다.
            //todo: 출첵 팝업창(dialog)띄어주고(완료), 파이어스토어 저장(완료)
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                public void run() { // 시간 지난 후 실행할 코딩
//                    layout_checkin.setVisibility(View.VISIBLE);

                    // 출석 팝업 띄우기
                    Bundle bundle = new Bundle();
                    bundle.putString("popup", "popup_checkin");
                    PopupFragment e = PopupFragment.getInstance();
                    e.setArguments(bundle);
                    e.show(getFragmentManager(), "dialog");
                }
            }, 3000); // 0.3초후
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
                                    atCount = (long)shot.get(FirebaseID.attendanceCount);
                                    atCount += 1;

                                    //출석체크 +1 추가한 값 저장
                                   firestore.collection(FirebaseID.Attendance).document(email).update(FirebaseID.attendanceCount,atCount);
                                    Log.d("HomeFragment", "at count get 완료");
                                } else {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put(FirebaseID.attendanceCount, 1);
                                    firestore.collection(FirebaseID.Attendance).document(email).set(data, SetOptions.merge());
                                    Log.d("HomeFragment", "No such document");
                                }
                            } else {
                                Log.d("HomeFragment", "get failed with ", task.getException());
                            }
                        }
                    });

            Toast.makeText(getContext(), "출석체크 완료!", Toast.LENGTH_SHORT).show();
        }


        //sharedPreference에 마지막 접속시간 저장
        //commit해야지 sharedPreference에 저장 완료
        editor.putString(emailBeforeCalendar,currentCalendar);
        editor.commit();
    }

    // 유니티 함수
    @Override
    public void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }
    @Override
    public void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
