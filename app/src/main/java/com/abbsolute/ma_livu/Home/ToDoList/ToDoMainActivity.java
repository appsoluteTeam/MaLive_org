package com.abbsolute.ma_livu.Home.ToDoList;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

//import com.abbsolute.ma_livu.GooeyMenu;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class ToDoMainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "101" ;
    private static final String TAG = "FCM";

    private static int WRITE_RESULT = 100;
    private ToDoFragment toDoFragment;
    private ToDoNotiFragment toDoNotiFragment;
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    ToDoAdapter toDoAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    ///private GooeyMenu mGooeyMenu;//밑에 네이버와 비슷한 모양의 UI
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_activity_main);
        toDoAdapter=new ToDoAdapter();
        //===데이터 불러오기

        // todo: todoFragment 장착
        final FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        toDoFragment=new ToDoFragment();
        fragmentTransaction.add(R.id.main_frame,toDoFragment).commit();
        // todo: 파이어베이스 메시지 전송 코드
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
                      //  Toast.makeText(ToDoMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
      //  mGooeyMenu = (GooeyMenu) findViewById(R.id.gooey_menu);
       // mGooeyMenu.setOnMenuListener(this);

    }//Oncreate()


    // todo: 뒤로가기 이벤트
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
    //todo: firebase 메시지 보내는 기능(필요한 사람있으면 쓰세요)
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
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, cur).commit();
    }


}
