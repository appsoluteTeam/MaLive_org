package com.appsolute.org.todolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,new Date().toString(), Toast.LENGTH_SHORT).show();

//반복알람을 위해 알람이 울리면
//다시 새로운 알람을 설정

//알람관리자 소환
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i= new Intent(context,AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,20,intent,PendingIntent.FLAG_UPDATE_CURRENT);

//알람 설정(20초 후)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+20000,pendingIntent);
        }else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+20000,pendingIntent);
        }

    }
}
