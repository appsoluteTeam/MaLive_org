package com.abbsolute.ma_livu.ToDoList;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.abbsolute.ma_livu.Fragments.HelpFragment;
import com.abbsolute.ma_livu.GooeyMenu;
import com.abbsolute.ma_livu.R;
import com.abbsolute.ma_livu.TabPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ToDoMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CHANNEL_ID = "101" ;
    private static final String TAG = "FCM";

    private static int WRITE_RESULT = 100;
    private ToDoFragment toDoFragment;
    private ToDoNotiFragment toDoNotiFragment;
    private HelpFragment helpFragment;
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    ToDoAdapter toDoAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    private GooeyMenu mGooeyMenu;//밑에 네이버와 비슷한 모양의 UI
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_activity_main);


        /////
       // new AlarmHATT(getApplicationContext()).Alarm();
//        createNotificationChannel();
        ///
        toDoAdapter=new ToDoAdapter();
        //===데이터 불러오기
        SharedPreferences pf=getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk=pf.getBoolean("chk",false);
        toDoAdapter.getCheckState(chk);
        Toast.makeText(getApplicationContext(), ""+chk, Toast.LENGTH_SHORT).show();
        ///
        ToDoAppHelper.openDatabase(getApplicationContext(), "todo.db", 15);
        getDays();//디데이 알림을 구현하려고 시도 한 코드
/*        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("내가 할 일");*/
        final FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        toDoFragment=new ToDoFragment();
        fragmentTransaction.add(R.id.main_frame,toDoFragment).commit();

/*        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
        ////////
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        String msg = task.getResult().getToken();
                        Log.d(TAG, msg);
                        Toast.makeText(ToDoMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        //도움말기능
        ImageView button=findViewById(R.id.help_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpFragment helpFragment=new HelpFragment();
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,helpFragment).commit();
            }
        });
        ///
        viewPager=findViewById(R.id.main_pager);
        tabLayout=findViewById(R.id.main_tab);//하단 탭
        TabPagerAdapter tabPagerAdapter=new TabPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(tabPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Community"));
        tabLayout.addTab(tabLayout.newTab().setText("Title"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(0).setText("Home");
                tabLayout.getTabAt(1).setText("Community");
                tabLayout.getTabAt(2).setText("Title");
                tabLayout.getTabAt(3).setText("Profile");
                switch(position){
                    case 0:

                        tabLayout.getTabAt(0).setText("Home");

                        break;
                    case 1:
                        tabLayout.getTabAt(1).setText("Community");

                        break;
                    case 2:
                        tabLayout.getTabAt(2).setText("Title");

                        break;
                    case 3:
                        tabLayout.getTabAt(3).setText("Profile");

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
      //  mGooeyMenu = (GooeyMenu) findViewById(R.id.gooey_menu);
       // mGooeyMenu.setOnMenuListener(this);

    }//Oncreate()
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        int id=menuItem.getItemId();
        if(id==R.id.nav_home){
            Toast.makeText(getApplicationContext(), "클릭!", Toast.LENGTH_SHORT).show();
            onFragmentSelected(0,null);
        }else if(id==R.id.nav_notification){
            onFragmentSelected(1,null);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void repaint(){
        final FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        toDoFragment=new ToDoFragment();
        fragmentTransaction.replace(R.id.main_frame,toDoFragment).commit();
    }





    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    //알림 채널 생성 코드
    /*private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }*/
    public void onFragmentSelected(int pos, Bundle bundle) {
        Fragment cur = null;
        if (pos == 0) {
            cur = new ToDoFragment();
            toDoFragment = (ToDoFragment)cur;
        } else if (pos == 1) {
            cur=new ToDoNotiFragment();
            toDoNotiFragment =(ToDoNotiFragment)cur;
        }else if(pos==2){
           cur=new HelpFragment();
           helpFragment=(HelpFragment)cur;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, cur).commit();
    }
    //디데이 알림 기능(수정필요!)
    public void getDays() {
        Log.d("alarm2"," method start");
        //Toast.makeText(getApplicationContext(),"getDays실행!!!!!",Toast.LENGTH_SHORT).show();

        //Calendar c = Calendar.getInstance();
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


        //Toast.makeText(getApplicationContext(), ""+y+" "+m+" "+d, Toast.LENGTH_SHORT).show();
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


        for (int i = 0; i < toDoInfos.size(); i++) {
            String dates = toDoInfos.get(i).getDates();//d-day 등록한 날짜
            String dDay = toDoInfos.get(i).getdDay();
            String contents = toDoInfos.get(i).getContent();

            String[] res = dDay.split("-");
            String year = res[0];
            String month = res[1];
            String day = res[2];

            int int_month = Integer.parseInt(month);
            int int_day = Integer.parseInt(day);

            //1월부터 9월일 때 앞에 0 붙여주기 ex)01월 이런식
            //day도 마찬가지
            /*if(int_month > 0 && int_month < 10){
                month = "0" + month;
            }
            if(int_day > 0 && int_day < 10){
                day = "0" + day;
            }*/

            Log.d("d-day",year + "," + month + "," + day);
            Log.d("c-day",y + "," + m + "," + d);
            //y,m,d는 현재 시간
            //year,month,day는 d-day

            //d-day는 한자리 수일 때 앞에 0이 안붙고 c-day는 한자리 수일 때 앞에 0이 붙어서 둘이 일치할수가 없음!
            if (y.equals(year) && m.equals(month) && d.equals(day)) {
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
                calendar.set(Calendar.HOUR,10);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);

                //  Preference에 설정한 값 저장
                /////데이터 저장
                //                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                //                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences preferences = getSharedPreferences("daily_alarm", MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                editor.commit();

                AlarmNotification(calendar,contents);

            }

        }
    }//getDays()

    void AlarmNotification(Calendar calendar,String contents){
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, ToDoDeviceBootReceiver.class);
        Intent intent = new Intent(ToDoMainActivity.this, ToDoAlarmReceiver.class);
        intent.putExtra("alarmContents",contents);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 30, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

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
