package com.abbsolute.ma_livu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GooeyMenu.GooeyMenuInterface{

    private static final String CHANNEL_ID = "101" ;
    private static final String TAG = "FCM";
    private AppBarConfiguration mAppBarConfiguration;

    private ToDoFragment toDoFragment;
    private NotiFragment notiFragment;
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
        setContentView(R.layout.activity_main);


        /////
       // new AlarmHATT(getApplicationContext()).Alarm();
//        createNotificationChannel();
        ///
        getDays();//디데이 알림을 구현하려고 시도 한 코드
        toDoAdapter=new ToDoAdapter();
        //===데이터 불러오기
        SharedPreferences pf=getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk=pf.getBoolean("chk",false);
        toDoAdapter.getCheckState(chk);
        Toast.makeText(getApplicationContext(), ""+chk, Toast.LENGTH_SHORT).show();
        ///
        AppHelper.openDatabase(getApplicationContext(), "todo.db", 14);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("내가 할 일");
        final FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        toDoFragment=new ToDoFragment();
        fragmentTransaction.add(R.id.main_frame,toDoFragment).commit();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        mGooeyMenu = (GooeyMenu) findViewById(R.id.gooey_menu);
        mGooeyMenu.setOnMenuListener(this);

    }
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

    /*public class AlarmHATT{
        private Context context;
        AlarmHATT(Context context){
            this.context=context;
        }
        public void Alarm(){
            AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(MainActivity.this,BroadcastD.class);

            PendingIntent sender=PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
            Calendar calendar=Calendar.getInstance();

            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),18,40,0);

            am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);
        }
    }*/
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void menuOpen() {

    }

    @Override
    public void menuClose() {
        showToast( "Menu Close");
    }
    public void repaint(){
        final FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        toDoFragment=new ToDoFragment();
        fragmentTransaction.replace(R.id.main_frame,toDoFragment).commit();
    }


    @Override
    public void menuItemClicked(int menuNumber) {// 1 청소 2 요리 3 빨래 4 책상정리 5 설거지
        showToast( "Menu item clicked : " + menuNumber);
        if(menuNumber==1){
            long systemTime = System.currentTimeMillis();
            SimpleDateFormat formatter= null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            }
            String date=formatter.format(systemTime);
            String dDate="디데이";
            ToDoInfo toDoInfo=new ToDoInfo("청소","집안청소",date,dDate);
            AppHelper.insertData("todoInfo",toDoInfo);
            repaint();//화면 다시 그리기
        }else if(menuNumber==2) {
            long systemTime = System.currentTimeMillis();
            SimpleDateFormat formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            }
            String date = formatter.format(systemTime);
            String dDate = "디데이";
            ToDoInfo toDoInfo = new ToDoInfo("요리", "자취생간편요리", date, dDate);
            AppHelper.insertData("todoInfo", toDoInfo);
            repaint();
        }else if(menuNumber==3){
            long systemTime = System.currentTimeMillis();
            SimpleDateFormat formatter= null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            }
            String date=formatter.format(systemTime);
            String dDate="디데이";
            ToDoInfo toDoInfo=new ToDoInfo("빨래","",date,dDate);
            AppHelper.insertData("todoInfo",toDoInfo);
            repaint();
        }else if(menuNumber==4){
            long systemTime = System.currentTimeMillis();
            SimpleDateFormat formatter= null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            }
            String date=formatter.format(systemTime);
            String dDate="디데이";
            ToDoInfo toDoInfo=new ToDoInfo("책상정리","",date,dDate);
            AppHelper.insertData("todoInfo",toDoInfo);
            repaint();
        }else if(menuNumber==5){
            long systemTime = System.currentTimeMillis();
            SimpleDateFormat formatter= null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            }
            String date=formatter.format(systemTime);
            String dDate="디데이";
            ToDoInfo toDoInfo=new ToDoInfo("설거지","",date,dDate);
            AppHelper.insertData("todoInfo",toDoInfo);
            repaint();
        }

    }
    private void showToast(String msg){
        if(mToast!=null){
            mToast.cancel();
        }
        mToast= Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
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
            cur=new NotiFragment();
            notiFragment=(NotiFragment)cur;
        }else if(pos==2){
           cur=new HelpFragment();
           helpFragment=(HelpFragment)cur;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, cur).commit();
    }
    //디데이 알림 기능(수정필요!)
    public void getDays() {
        Calendar c = Calendar.getInstance();
        ArrayList<ToDoInfo> toDoInfos = AppHelper.selectTodoInfo("todoInfo");
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        }
        String date = formatter.format(systemTime);
        String[] r = date.split("-");
        String y = r[0];
        String m = r[1];
        String d = r[2];
        // Toast.makeText(getContext(), ""+y+" "+m+" "+d, Toast.LENGTH_SHORT).show();
        //디데이 알림 체크
        for (int i = 0; i < toDoInfos.size(); i++) {
            String dates = toDoInfos.get(i).getDates();
            //Toast.makeText(getContext(), "" + dates, Toast.LENGTH_SHORT).show();
            String[] res = dates.split("-");
            String year = res[0];
            String month = res[1];
            String day = res[2];
            if (y.equals(year) && m.equals(month) && d.equals(day)) {
                int sendYear = Integer.parseInt(year);
                int sendMonth = Integer.parseInt(month);
                int sendDay = Integer.parseInt(day);
                //선택한 날짜와 시간으로 알람 설정
                GregorianCalendar calendar = new GregorianCalendar(sendYear, sendMonth, sendDay, 18, 44);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//알람시간에 AlarmActivity 실행되도록.
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 30, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

        }
    }//getDays()
}
