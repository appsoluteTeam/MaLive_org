package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Community.bringData;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ToDoFragment extends Fragment implements OnToDoTextClick, refreshInterface {//ToDoList 추가, 삭제, 수정 클래스
    RecyclerView recyclerView;
    public ToDoAdapter toDoAdapter;

    private static int WRITE_RESULT = 100;
    String res;
    Bundle bundle;
    ArrayList<ToDoInfo> toDoInfos = new ArrayList<>();
    LinearLayout linearLayout;
    TextView Contents;
    private int UPDATE_OK = 5;
    //멤버변수
    int Year, Month, Day;
    int Hour, Min;
    AlarmManager alarmManager;
    //CheckBox checkBox;
    ///
//파이버베이스 인증 변수
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FragmentTransaction fragmentTransaction;
    private String TAG = "";

    ///
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        recyclerView = view.findViewById(R.id.todo_recyclerview);
        getDays();//디데이 알림을 구현하려고 시도 한 코드
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        toDoAdapter = new ToDoAdapter();
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Button fab = view.findViewById(R.id.fab);//추가
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref2", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("modify", false);
                editor.commit();
                ((HomeActivity) getActivity()).setFragment(101);//toDoWriteFragment로 화면전환
                //Intent intent = new Intent(getContext(), ToDoWriteMainFragment.class);
                //startActivityForResult(intent, WRITE_RESULT);
            }
        });
        ///파이어스토어에서 할 일을 불러옴
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");
        firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                toDoInfos.clear();
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    String cnt = (String) snapshot.getData().get("Count");
                                    int siz = Integer.parseInt(cnt);
                                    for (int i = 0; i <= siz; i++) {
                                        String content = (String) snapshot.getData().get("contents" + i);
                                        String detailContent = (String) snapshot.getData().get("detailContents" + i);
                                        String dates = (String) snapshot.getData().get("dates" + i);
                                        String dDay = (String) snapshot.getData().get("dDates" + i);
                                        String color = (String) snapshot.getData().get("color" + i);
                                        int backgrounds = Integer.parseInt(color);
                                        ToDoInfo toDoInfo = new ToDoInfo(content, detailContent, dates, dDay, backgrounds);
                                        toDoInfos.add(toDoInfo);
                                    }
                                    toDoAdapter.setItem(toDoInfos);

                                }
                                recyclerView.setAdapter(toDoAdapter);
                                toDoAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        ////
        toDoAdapter.GetContext(getContext(), this);
        toDoAdapter.notifyDataSetChanged();
        recyclerView.setItemAnimator(null);
        ///뒤로가기 버튼 이벤트 처리
        Button back = view.findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).setFragment(0);//뒤로가기 누르면 homeFragment로 이동
            }
        });
        //밀어서 할일 삭제
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    final int position = viewHolder.getAdapterPosition();
                    ///ToDoAppHelper.deleteData(getContext(), "todoInfo", position, toDoInfos.get(position));
                    ///파이어베이스 db삭제
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                    final String id = sharedPreferences.getString("email_id", "");
                    firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                                    SharedPreferences pf = getContext().getSharedPreferences("pref", MODE_PRIVATE);
                                    boolean chk = pf.getBoolean("chk" + position, false);
                                    if (chk == true) {
                                        DocumentSnapshot snapshot = task.getResult();
                                        String content = "";
                                        String detailContent = "";
                                        String dates = "";
                                        String dDay = "";
                                        String colors = "";
                                        String count = "";
                                        if (snapshot.exists()) {
                                            count = (String) snapshot.getData().get("Count");
                                            int cnt = Integer.parseInt(count);
                                            if (cnt >= 1)
                                                cnt--;
                                            count = Integer.toString(cnt);
                                            if(position==0){
                                                firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo")
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error deleting document", e);
                                                            }
                                                        });

                                            }else{
                                                Map<String, Object> updates = new HashMap<>();
                                                int moveIdx = position + 1;
                                                //ex) 2번 지우면 3->2
                                                Map<String, Object> data = new HashMap<>();
                                                content = (String) snapshot.getData().get("contents" + moveIdx);
                                                detailContent = (String) snapshot.getData().get("detailContents" + moveIdx);
                                                dates = (String) snapshot.getData().get("dates" + moveIdx);
                                                dDay = (String) snapshot.getData().get("dDates" + moveIdx);
                                                colors = (String) snapshot.getData().get("color" + moveIdx);
                                                data.put("contents" + position, content);
                                                data.put("detailContents" + position, detailContent);
                                                data.put("dates" + position, dates);
                                                data.put("dDates" + position, dDay);
                                                data.put("color" + position, colors);
                                                data.put("Count", count);
                                                firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo").set(data, SetOptions.merge());
                                                ////
                                                updates.put("contents" + moveIdx, FieldValue.delete());
                                                updates.put("detailContents" + moveIdx, FieldValue.delete());
                                                updates.put("dates" + moveIdx, FieldValue.delete());
                                                updates.put("dDates" + moveIdx, FieldValue.delete());
                                                updates.put("color" + moveIdx, FieldValue.delete());
                                                updates.put("Count", count);
                                                firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo").update(updates);
                                                ////
                                                if (!count.equals("0"))
                                                    refresh();
                                            }
                                        }


                                        SharedPreferences pfComplete = getContext().getSharedPreferences("pref", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pfComplete.edit();
                                        editor.putBoolean("chk" + position, false);
                                        editor.commit();
                                    }//chk true
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("AlertDialog Title");
                                        builder.setMessage("AlertDialog Content");
                                        builder.setPositiveButton("예",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(getContext(), "예를 선택했습니다.", Toast.LENGTH_LONG).show();
                                                        DocumentSnapshot snapshot = task.getResult();
                                                        String content = "";
                                                        String detailContent = "";
                                                        String dates = "";
                                                        String dDay = "";
                                                        String colors = "";
                                                        String count = "";
                                                        if (snapshot.exists()) {
                                                            count = (String) snapshot.getData().get("Count");
                                                            int cnt = Integer.parseInt(count);
                                                            if (cnt >= 1)
                                                                cnt--;
                                                            count = Integer.toString(cnt);
                                                            if(position==0){
                                                                firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo")
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w(TAG, "Error deleting document", e);
                                                                            }
                                                                        });
                                                            }else{
                                                                Map<String, Object> updates = new HashMap<>();
                                                                int moveIdx = position + 1;
                                                                //ex) 2번 지우면 3->2
                                                                Map<String, Object> data = new HashMap<>();
                                                                content = (String) snapshot.getData().get("contents" + moveIdx);
                                                                detailContent = (String) snapshot.getData().get("detailContents" + moveIdx);
                                                                dates = (String) snapshot.getData().get("dates" + moveIdx);
                                                                dDay = (String) snapshot.getData().get("dDates" + moveIdx);
                                                                colors = (String) snapshot.getData().get("color" + moveIdx);
                                                                data.put("contents" + position, content);
                                                                data.put("detailContents" + position, detailContent);
                                                                data.put("dates" + position, dates);
                                                                data.put("dDates" + position, dDay);
                                                                data.put("color" + position, colors);
                                                                data.put("Count", count);
                                                                firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo").set(data, SetOptions.merge());
                                                                ////
                                                                updates.put("contents" + moveIdx, FieldValue.delete());
                                                                updates.put("detailContents" + moveIdx, FieldValue.delete());
                                                                updates.put("dates" + moveIdx, FieldValue.delete());
                                                                updates.put("dDates" + moveIdx, FieldValue.delete());
                                                                updates.put("color" + moveIdx, FieldValue.delete());
                                                                updates.put("Count", count);
                                                                firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo").update(updates);
                                                                ////
                                                                if (!count.equals("0"))
                                                                    refresh();
                                                            }
                                                        }


                                                    }
                                                });
                                        builder.setNegativeButton("아니오",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(getContext(), "아니오를 선택했습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                        builder.show();
                                    }


                                }
                            });
                    ///파이어스토어 달성횟수 업로드
                    firestore.collection("ToDoList").document(id + " ToDo")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult() != null) {
                                            DocumentSnapshot snapshot = task.getResult();
                                            if (snapshot.exists()) {
                                                SharedPreferences pf = getContext().getSharedPreferences("pref", MODE_PRIVATE);
                                                boolean chk = pf.getBoolean("chk" + position, false);
                                                if (chk == true) {
                                                    String content = (String) snapshot.getData().get("contents" + position);
                                                    SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("pref3", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                                                    int cnt = 0;
                                                    if (sharedPreferences1 != null) {
                                                        cnt = sharedPreferences1.getInt(content + "Complete", 1);
                                                        cnt++;
                                                        editor.putInt(content + "Complete", cnt);
                                                        editor.commit();
                                                    } else {
                                                        cnt = 1;
                                                        editor.putInt(content + "Complete", cnt);
                                                        editor.commit();
                                                    }
                                                    HashMap<String, Object> data = new HashMap<>();
                                                    if (content.equals("기타")) {
                                                        String detail = (String) snapshot.getData().get("detailContents" + (position + 1));
                                                        String c = Integer.toString(cnt);
                                                        data.put(detail + "complete", c);
                                                    } else {
                                                        String c = Integer.toString(cnt);
                                                        data.put(content + "complete", c);
                                                    }
                                                    firestore.collection(FirebaseID.ToDoLists).document(id).set(data, SetOptions.merge());
                                                }
                                            }

                                        }
                                    }
                                }
                            });
                    toDoInfos.remove(position);
                    toDoAdapter.notifyItemRemoved(position);

                }//왼쪽으로 swipe

            }//onswiped
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);

        return view;
    }
    //다시 그리기

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");
        firestore.collection(FirebaseID.ToDoLists).document(id + " ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    toDoInfos.clear();
                                    String cnt = (String) snapshot.getData().get("Count");
                                    int siz = Integer.parseInt(cnt);
                                    Toast.makeText(getContext(), cnt, Toast.LENGTH_SHORT).show();
                                    for (int i = 0; i <= siz; i++) {
                                        String content = (String) snapshot.getData().get("contents" + i);
                                        String detailContent = (String) snapshot.getData().get("detailContents" + i);
                                        String dates = (String) snapshot.getData().get("dates" + i);
                                        String dDay = (String) snapshot.getData().get("dDates" + i);
                                        String color = (String) snapshot.getData().get("color" + i);
                                        int backgrounds = Integer.parseInt(color);
                                        ToDoInfo toDoInfo = new ToDoInfo(content, detailContent, dates, dDay, backgrounds);
                                        toDoInfos.add(toDoInfo);
                                    }
                                    toDoAdapter.setItem(toDoInfos);

                                }
                                recyclerView.setAdapter(toDoAdapter);
                                toDoAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                });
        ////
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    //디데이 알림 기능(수정필요!)
    public void getDays() {
        Log.d("alarm2", " method start");
        ArrayList<ToDoInfo> toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
        Log.d("toDoInfoSize", Integer.toString(toDoInfos.size()));
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date = formatter.format(systemTime);
        String[] r = date.split("-");
        String y = r[0];
        String m = r[1];
        String d = r[2];
        Log.d("currentTime", y + "," + m + "," + d);
        //디데이 알림 체크
        /*
        try {
            String toDoInfocheck = toDoInfos.get(0).content;
            String Ddaycheck = toDoInfos.get(0).dDay;
            Log.d("todoInfo","content = " + toDoInfocheck + "dDay = " + Ddaycheck);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        int alarm_second = 0;
        int flag = 0;

        for (int i = 0; i < toDoInfos.size(); i++) {
            String dates = toDoInfos.get(i).getDates();//d-day 등록한 날짜

            String dDay = toDoInfos.get(i).getdDay();
            String contents = toDoInfos.get(i).getContent();

            String[] res = dDay.split("년");
            String[] days = dDay.split("월");
            String year = res[0];
            String month = res[1];
            String day = days[1];


            Log.d("d-day", year + "," + month + "," + day);
            Log.d("c-day", y + "," + m + "," + d);
            //y,m,d는 현재 시간
            //year,month,day는 d-day

            //d-day는 한자리 수일 때 앞에 0이 안붙고 c-day는 한자리 수일 때 앞에 0이 붙어서 둘이 일치할수가 없음!
            if (y.equals(year) && m.equals(month) && d.equals(day)) {
                flag += 1;
                Log.d("correct", "날짜일치...");
                int sendYear = Integer.parseInt(year);
                int sendMonth = Integer.parseInt(month);
                int sendDay = Integer.parseInt(day);
                //선택한 날짜와 시간으로 알람 설정
                //GregorianCalendar calendar = new GregorianCalendar(sendYear, sendMonth, sendDay, 18, 44);
//알람시간에 AlarmActivity 실행되도록.

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                //UTC시간으로 지정시
                calendar.set(Calendar.DAY_OF_YEAR, sendYear);
                calendar.set(Calendar.DAY_OF_MONTH, sendMonth);
                calendar.set(Calendar.DATE, sendDay);
                calendar.set(Calendar.HOUR, 8);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, alarm_second);
                alarm_second += 3;//하루에 여러개 일 때 3초 간격

                //  Preference에 설정한 값 저장
                /////데이터 저장
                //                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                //                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences preferences = getActivity().getSharedPreferences("daily_alarm", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("nextNotifyTime", (long) calendar.getTimeInMillis());
                editor.commit();

                AlarmNotification(calendar, contents, flag);

            }

        }
    }//getDays()

    void AlarmNotification(Calendar calendar, String contents, int flag) {
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = getActivity().getPackageManager();
        ComponentName receiver = new ComponentName(getContext(), ToDoDeviceBootReceiver.class);
        Intent intent = new Intent(getContext(), ToDoAlarmReceiver.class);
        intent.putExtra("alarmContents", contents);
        intent.putExtra("notificationId", flag);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), flag, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        if (dailyNotify) {//사용자가 매일 알람을 허용해놨을 때
            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //특정시간에 한번만 동작한다.
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    //    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    //    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
        ////
    }

    //투두리스트 수정하기 이벤트처리
    @Override
    public void onClick(int go) {
        if (go == 3) {
            ((HomeActivity) getActivity()).setFragment(101);// 투두 메인 작성화면으로 이동
        }
    }

    @Override
    public void refresh() {
        // 리사이클러뷰 새로고침 메소드
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }
}

