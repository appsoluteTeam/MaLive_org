package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

public class ToDoFragment extends Fragment implements OnToDoTextClick, refreshInterface,OnBackPressedListener {//ToDoList 추가, 삭제, 수정 클래스
    RecyclerView recyclerView;
    public ToDoAdapter toDoAdapter;

    private static int WRITE_RESULT = 100;
    String res;
    Bundle bundle;
    ArrayList<ToDoInfo> toDoInfos = new ArrayList<>();
    ArrayList<ToDoInfo> tmpArray = new ArrayList<>();
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
    private ToDoListCustomDialog customDialog;
    private FragmentTransaction transaction;
    LinearLayoutManager layoutManager;

    ///
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        recyclerView = view.findViewById(R.id.todo_recyclerview);
        getDays();//디데이 알림을 구현하려고 시도 한 코드
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        toDoAdapter = new ToDoAdapter();
        Button fab = view.findViewById(R.id.fab);//추가
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref2", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("modify", false);
                editor.commit();
                Bundle bundle = new Bundle();
                bundle.putInt("ToDoCount", toDoAdapter.getItemCount());
                ToDoWriteMainFragment toDoWriteMainFragment = new ToDoWriteMainFragment();
                toDoWriteMainFragment.setArguments(bundle);
                ////
                Log.d("보내기", toDoAdapter.getItemCount() + "");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, toDoWriteMainFragment);
                transaction.commit();
                //((HomeActivity) getActivity()).setFragment(101);//toDoWriteMainFragment로 화면전환
                //Intent intent = new Intent(getContext(), ToDoWriteMainFragment.class);
                //startActivityForResult(intent, WRITE_RESULT);
            }
        });
        ///파이어스토어에서 할 일을 불러옴
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");
        firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                toDoInfos.clear();
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String content = String.valueOf(shot.get("content"));
                                    String detailContent = String.valueOf(shot.get("detailContent"));
                                    String date = String.valueOf(shot.get("date"));
                                    String dDay = String.valueOf(shot.get("dDay"));
                                    String color = String.valueOf(shot.get("color"));
                                    int backgroundColor = Integer.parseInt(color);
                                    //String content, String detailContent, String dates, String dDay,int color
                                    ToDoInfo toDoInfo = new ToDoInfo(content, detailContent, date, dDay, backgroundColor);
                                    toDoInfos.add(toDoInfo);
                                }
                                tmpArray = toDoInfos;
                                Comparator<ToDoInfo> comparator = new Comparator<ToDoInfo>() {
                                    @Override
                                    public int compare(ToDoInfo o1, ToDoInfo o2) {
                                        return o1.getdDay().compareTo(o2.getdDay());
                                    }
                                };
                                Collections.sort(toDoInfos, comparator);
                                toDoAdapter.setItem(toDoInfos);
                                toDoAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(toDoAdapter);
                            }
                        }
                    }
                });
        ////
        toDoAdapter.GetContext(getContext(), this);
        toDoAdapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(toDoAdapter);
        recyclerView.setItemAnimator(null);

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
                    final String deleteData=toDoInfos.get(position).detailContent;
                    final String content=toDoInfos.get(position).content;
                    final String detailContentes=toDoInfos.get(position).detailContent;
                    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                    ///ToDoAppHelper.deleteData(getContext(), "todoInfo", position, toDoInfos.get(position));
                    final SharedPreferences pf = getContext().getSharedPreferences("pref", MODE_PRIVATE);
                    boolean chk = pf.getBoolean("chk" + detailContentes, false);
                    ///파이어베이스 db삭제
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                    final String id = sharedPreferences.getString("email_id", "");
                    if (chk == true) {
                        ///파이어스토어 달성횟수 업로드
                        Log.d("체크","체크됨");
                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo")
                                .document(deleteData + "")
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "삭제성공", Toast.LENGTH_SHORT).show();
                                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("total").document("sub")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult() != null) {
                                                                //전체 달성횟수-> 칭호
                                                                DocumentSnapshot totalCompleteSnapShot = task.getResult();
                                                                if (totalCompleteSnapShot.exists()) {
                                                                    Map<String,Object> map=totalCompleteSnapShot.getData();
                                                                    if (map.containsKey(content+"complete")
                                                                            ||map.containsKey("투두complete")) {
                                                                        long completeCount=0;
                                                                        if(map.containsKey(content+"complete"))
                                                                             completeCount = (long) totalCompleteSnapShot.getData().get(content + "complete");
                                                                        long completeCountForToDo=0;
                                                                        if(map.containsKey("투두complete"))
                                                                             completeCountForToDo=(long)totalCompleteSnapShot.getData().get("투두complete");
                                                                        completeCount++;
                                                                        completeCountForToDo++;
                                                                        HashMap<String, Object> data = new HashMap<>();
                                                                        if (content.equals("기타")) {
                                                                            data.put("투두complete", completeCountForToDo);
                                                                        } else {
                                                                            data.put(content + "complete", completeCount);
                                                                        }
                                                                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("total").document("sub").set(data, SetOptions.merge());
                                                                        ////
                                                                    } else {
                                                                        HashMap<String, Object> data = new HashMap<>();
                                                                        long completeCount = 1;
                                                                        if (content.equals("기타")) {
                                                                            data.put("투두complete", completeCount);
                                                                        } else {
                                                                            data.put(content + "complete", completeCount);
                                                                        }
                                                                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("total").document("sub").set(data, SetOptions.merge());
                                                                    }
                                                                } else {
                                                                    HashMap<String, Object> data = new HashMap<>();
                                                                    long completeCount = 1;
                                                                    if (content.equals("기타")) {
                                                                        data.put("투두complete", completeCount);
                                                                    } else {
                                                                        data.put(content + "complete", completeCount);
                                                                    }
                                                                    firestore.collection(FirebaseID.ToDoLists).document(id).collection("total").document("sub").set(data, SetOptions.merge());
                                                                }
                                                            }
                                                        }
                                                    }
                                                });//전체 달성횟수 데이터
                                        //// 월별 달성률 측정
                                        long systemTime = System.currentTimeMillis();
                                        SimpleDateFormat formatter = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                                        }
                                        final String date = formatter.format(systemTime);
                                        String[] dates = date.split("-");
                                        String year = dates[0];
                                        String month = dates[1];
                                        final String updateDate = year + "-" + month;
                                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("EveryMonth").document(updateDate)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult() != null) {
                                                                DocumentSnapshot completeMonthSnapshot = task.getResult();
                                                                if (completeMonthSnapshot.exists()) {
                                                                    Map<String,Object> map=completeMonthSnapshot.getData();
                                                                    if (map.containsKey(content+"complete")
                                                                    ||map.containsKey("투두complete")) {
                                                                        long completeMonthCount=0;
                                                                        if(map.containsKey(content+"complete"))//청소, 빨래, 쓰레기
                                                                            completeMonthCount = (long) completeMonthSnapshot.getData().get(content + "complete");
                                                                        long completeCountForToDo=0;
                                                                        if(map.containsKey("투두complete"))
                                                                            completeCountForToDo=(long)completeMonthSnapshot.getData().get("투두complete");
                                                                        Toast.makeText(getContext(), ""+completeCountForToDo, Toast.LENGTH_SHORT).show();
                                                                        completeMonthCount++;
                                                                        completeCountForToDo++;
                                                                        HashMap<String, Object> data = new HashMap<>();
                                                                        if (content.equals("기타")) {
                                                                            data.put("투두complete", completeCountForToDo);
                                                                        } else {
                                                                            data.put(content + "complete", completeMonthCount);
                                                                        }
                                                                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("EveryMonth").document(updateDate)
                                                                                .set(data, SetOptions.merge());
                                                                    } else {
                                                                        HashMap<String, Object> data = new HashMap<>();
                                                                        long completeMonthCount = 1;
                                                                        if (content.equals("기타")) {
                                                                            data.put("투두complete", completeMonthCount);
                                                                        } else {
                                                                            data.put(content + "complete", completeMonthCount);
                                                                        }
                                                                        firestore.collection(FirebaseID.ToDoLists).document(id).collection("EveryMonth").document(updateDate)
                                                                                .set(data, SetOptions.merge());
                                                                    }
                                                                }else{
                                                                    HashMap<String, Object> data = new HashMap<>();
                                                                    long completeMonthCount = 1;
                                                                    if (content.equals("기타")) {
                                                                        data.put("투두complete", completeMonthCount);
                                                                    } else {
                                                                        data.put(content + "complete", completeMonthCount);
                                                                    }
                                                                    firestore.collection(FirebaseID.ToDoLists).document(id).collection("EveryMonth").document(updateDate)
                                                                            .set(data, SetOptions.merge());
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                        SharedPreferences pfComplete = getContext().getSharedPreferences("pref", MODE_PRIVATE);
                                        SharedPreferences.Editor Checkeditor = pfComplete.edit();
                                        Checkeditor.putBoolean("chk" +detailContentes, false);
                                        Checkeditor.commit();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "삭제실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        toDoInfos.remove(position);
                        recyclerView.setAdapter(toDoAdapter);
                        refresh();
                    } else {
                        //todo: 삭제 시 java.lang.IndexOutOfBoundsException: Index: 0, Size: 0 오류 발생으로 수정해야함
                        Log.d("배열의 크기", tmpArray.size() + "");
                        View.OnClickListener leftListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo")
                                        .document(deleteData + "")
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "삭제성공", Toast.LENGTH_SHORT).show();
                                                refresh();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "삭제실패", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                customDialog.dismiss();
                            }//onClick
                        };
                        customDialog = new ToDoListCustomDialog(getContext(), "완료하지 않은 리스트입니다.",
                                "그래도 삭제하시겠습니까?", leftListener, rightListener);
                        customDialog.show();
                        toDoInfos.remove(position);
                        toDoAdapter.setItem(toDoInfos);
                        toDoAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(toDoAdapter);

                    }//chk false

                }//왼쪽으로 swipe
                if (direction == ItemTouchHelper.RIGHT) {
                    refresh();
                }

            }//onswiped
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);
        return view;
    }

    private View.OnClickListener rightListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "오른쪽 버튼 클릭", Toast.LENGTH_SHORT).show();
            customDialog.dismiss();
        }
    };

    //다시 그리기
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "On Resume!!!", Toast.LENGTH_SHORT).show();
        toDoInfos = new ArrayList<>();
        SharedPreferences pf = getContext().getSharedPreferences("pref2", Activity.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("email_id", "");
        firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                toDoInfos.clear();
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Map<String, Object> shot = snapshot.getData();
                                    String content = String.valueOf(shot.get("content"));
                                    String detailContent = String.valueOf(shot.get("detailContent"));
                                    String date = String.valueOf(shot.get("date"));
                                    String dDay = String.valueOf(shot.get("dDay"));
                                    String color = String.valueOf(shot.get("color"));
                                    int backgroundColor = Integer.parseInt(color);
                                    //String content, String detailContent, String dates, String dDay,int color
                                    ToDoInfo toDoInfo = new ToDoInfo(content, detailContent, date, dDay, backgroundColor);
                                    toDoInfos.add(toDoInfo);
                                }
                                tmpArray = toDoInfos;
                                Comparator<ToDoInfo> comparator = new Comparator<ToDoInfo>() {
                                    @Override
                                    public int compare(ToDoInfo o1, ToDoInfo o2) {
                                        return o1.getdDay().compareTo(o2.getdDay());
                                    }
                                };
                                Collections.sort(toDoInfos, comparator);
                                toDoAdapter.setItem(toDoInfos);
                            }//not null
                            toDoAdapter.notifyDataSetChanged();
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(toDoAdapter);
                            SharedPreferences pf = getContext().getSharedPreferences("pref2", MODE_PRIVATE);
                            int uploading = pf.getInt("upload", 0);
                            if (uploading == 1) {
                                refresh();
                                SharedPreferences tmp = getContext().getSharedPreferences("pref2", MODE_PRIVATE);
                                SharedPreferences.Editor editor = tmp.edit();
                                editor.putInt("upload", 0);
                                editor.commit();
                            }

                        }//successful


                    }
                });
        recyclerView.setHasFixedSize(true);
        toDoAdapter = new ToDoAdapter();
        layoutManager = new LinearLayoutManager(getActivity());
        toDoAdapter.GetContext(getContext(), this);
        toDoAdapter.setItem(toDoInfos);
        recyclerView.scrollToPosition(0);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(toDoAdapter);
        ((HomeActivity)getActivity()).setOnBackPressedListener(this);//뒤로가기 이벤트처리
    }


    //디데이 알림 기능(수정필요!)
    public void getDays() {
        Log.d("alarm2", " method start");
        // ArrayList<ToDoInfo> toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
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
    //뒤로가기 이벤트
    @Override
    public void onBackPressed() {
        Toast.makeText(getContext(),getContext().getClass().getName(),Toast.LENGTH_SHORT).show();
        ((HomeActivity)getActivity()).setCurrentScene(this);
    }
}

