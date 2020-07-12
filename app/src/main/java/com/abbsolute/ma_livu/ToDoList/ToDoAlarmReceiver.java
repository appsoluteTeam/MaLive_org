package com.abbsolute.ma_livu.ToDoList;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.abbsolute.ma_livu.R;
import com.abbsolute.ma_livu.ToDoList.ToDoMainActivity;

public class ToDoAlarmReceiver extends BroadcastReceiver {// 알람을 받는 클래스

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("alarmStartttttt","START!!!!!");

        String alarmContents = intent.getStringExtra("alarmContents");
        Log.d("alarmContents",alarmContents);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notiIntent = new Intent(context, ToDoMainActivity.class);

        notiIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notiIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//채널생성
            String channelName = "알람채널";
            String description = "정해진 시간에 알람합니다";
            int importance = NotificationManager.IMPORTANCE_HIGH;//소리와 알람메시지 같이 보여줌

            NotificationChannel channel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel("default", channelName, importance);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel.setDescription(description);
            }

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(alarmContents+"하는 날입니다!")
                .setContentText("완료하셨나용?")
                .setContentIntent(pendingI)
                .setSmallIcon(R.drawable.notification_icon);

        if(notificationManager!=null){
            notificationManager.notify(1234, builder.build());
        }

    }
}
