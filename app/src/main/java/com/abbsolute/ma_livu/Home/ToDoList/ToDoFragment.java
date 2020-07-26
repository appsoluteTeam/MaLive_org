package com.abbsolute.ma_livu.Home.ToDoList;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ToDoFragment extends Fragment {//ToDoList 추가, 삭제, 수정 클래스
    RecyclerView recyclerView;
    public ToDoAdapter toDoAdapter;

    private static int WRITE_RESULT = 100;
    String res;
    Bundle bundle;
    ArrayList<ToDoInfo> toDoInfos;
    LinearLayout linearLayout;
    TextView Contents;
    private int UPDATE_OK = 5;
    //멤버변수
    int Year, Month, Day;
    int Hour, Min;
    AlarmManager alarmManager;
    //CheckBox checkBox;
    ///


    ///
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        recyclerView = view.findViewById(R.id.todo_recyclerview);
        /// todo: 데이터 베이스 open
        ToDoAppHelper.openDatabase(getContext(), "todo.db", 16);
        getDays();//디데이 알림을 구현하려고 시도 한 코드
//  fragmentTransaction.add(R.id.contentMain,FirstFragment.newInstance());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // layoutManager.setReverseLayout(true);
        // layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        toDoAdapter = new ToDoAdapter();
        FragmentManager fragmentManager=getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Button fab = view.findViewById(R.id.fab);//추가
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).setFragment(101);//toDoWriteFragment로 화면전환
                //Intent intent = new Intent(getContext(), ToDoWriteMainFragment.class);
                //startActivityForResult(intent, WRITE_RESULT);
            }
        });
        toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
        Comparator<ToDoInfo> comparator=new Comparator<ToDoInfo>() {
            @Override
            public int compare(ToDoInfo o1, ToDoInfo o2) {
                return o1.getdDay().compareTo(o2.getdDay());
            }
        };
        Collections.sort(toDoInfos,comparator);
        toDoAdapter.setItem(toDoInfos);
        toDoAdapter.GetContext(getContext());
        toDoAdapter.notifyDataSetChanged();
        recyclerView.setItemAnimator(null);

        //밀어서 할일 삭제
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction== ItemTouchHelper.LEFT){
                    final int position = viewHolder.getAdapterPosition();
                    ToDoAppHelper.deleteData(getContext(), "todoInfo", position, toDoInfos.get(position));
                    toDoInfos.remove(position);
                    toDoAdapter.notifyItemRemoved(position);
                }else if(direction== ItemTouchHelper.RIGHT){
                    toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
                    // toDoAdapter.clearData();
                    toDoAdapter.setItem(toDoInfos);
                    recyclerView.setAdapter(toDoAdapter);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);

        return view;
    }
    //다시 그리기
    @Override
    public void onResume() {
        super.onResume();
        toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
        Comparator<ToDoInfo> comparator=new Comparator<ToDoInfo>() {
            @Override
            public int compare(ToDoInfo o1, ToDoInfo o2) {
                return o1.getdDay().compareTo(o2.getdDay());
            }
        };
        Collections.sort(toDoInfos,comparator);
        // toDoAdapter.clearData();
        toDoAdapter.setItem(toDoInfos);
        recyclerView.setAdapter(toDoAdapter);
    }
    //작성하기 내용 보여주기
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_RESULT) {
            if (resultCode == RESULT_OK) {
                toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
                Comparator<ToDoInfo> comparator=new Comparator<ToDoInfo>() {
                    @Override
                    public int compare(ToDoInfo o1, ToDoInfo o2) {
                        return o1.getdDay().compareTo(o2.getdDay());
                    }
                };
                Collections.sort(toDoInfos,comparator);
                toDoAdapter.setItem(toDoInfos);
                recyclerView.setAdapter(toDoAdapter);
            }
        }
    }///onActivityResult
    //디데이 알림 기능(수정필요!)
    public void getDays() {
        Log.d("alarm2"," method start");
        ArrayList<ToDoInfo> toDoInfos = ToDoAppHelper.selectTodoInfo("todoInfo");
        Log.d("toDoInfoSize",Integer.toString(toDoInfos.size()));
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



        Log.d("currentTime",y + "," + m + "," + d);
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
            String[] days=dDay.split("월");
            String year = res[0];
            String month = res[1];
            String day = days[1];



            Log.d("d-day",year + "," + month + "," + day);
            Log.d("c-day",y + "," + m + "," + d);
            //y,m,d는 현재 시간
            //year,month,day는 d-day

            //d-day는 한자리 수일 때 앞에 0이 안붙고 c-day는 한자리 수일 때 앞에 0이 붙어서 둘이 일치할수가 없음!
            if (y.equals(year) && m.equals(month) && d.equals(day)) {
                flag += 1;
                Log.d("correct","날짜일치...");
                int sendYear = Integer.parseInt(year);
                int sendMonth = Integer.parseInt(month);
                int sendDay = Integer.parseInt(day);
                //선택한 날짜와 시간으로 알람 설정
                //GregorianCalendar calendar = new GregorianCalendar(sendYear, sendMonth, sendDay, 18, 44);
//알람시간에 AlarmActivity 실행되도록.

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                //UTC시간으로 지정시
                calendar.set(Calendar.DAY_OF_YEAR,sendYear);
                calendar.set(Calendar.DAY_OF_MONTH,sendMonth);
                calendar.set(Calendar.DATE,sendDay);
                calendar.set(Calendar.HOUR,8);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,alarm_second);
                alarm_second += 3;//하루에 여러개 일 때 3초 간격

                //  Preference에 설정한 값 저장
                /////데이터 저장
                //                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                //                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences preferences = getActivity().getSharedPreferences("daily_alarm", MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                editor.commit();

                AlarmNotification(calendar,contents,flag);

            }

        }
    }//getDays()

    void AlarmNotification(Calendar calendar,String contents,int flag){
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = getActivity().getPackageManager();
        ComponentName receiver = new ComponentName(getContext(), ToDoDeviceBootReceiver.class);
        Intent intent = new Intent(getContext(), ToDoAlarmReceiver.class);
        intent.putExtra("alarmContents",contents);
        intent.putExtra("notificationId",flag);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), flag, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        if(dailyNotify){//사용자가 매일 알람을 허용해놨을 때
            if(am != null){
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


}

