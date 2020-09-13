package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.abbsolute.ma_livu.Home.ToDoList.ToDoAlarmReceiver;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ToDoDeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

                // on device boot complete, reset the alarm
                Intent alarmIntent = new Intent(context, ToDoAlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                //

                SharedPreferences sharedPreferences = context.getSharedPreferences("daily_alarm", MODE_PRIVATE);
                long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());


                Calendar current_calendar = Calendar.getInstance();
                Calendar nextNotifyTime = new GregorianCalendar();
                nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis));

                if (current_calendar.after(nextNotifyTime)) {
                    nextNotifyTime.add(Calendar.DATE, 1);
                }
       /*         Date currentDateTime = nextNotifyTime.getTime();
                String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                Toast.makeText(context.getApplicationContext(),"[재부팅후] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        */
                if (manager != null) {
                    manager.set(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),pendingIntent);
                }
            }
        }
    }
}