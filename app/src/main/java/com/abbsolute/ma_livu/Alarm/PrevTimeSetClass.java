package com.abbsolute.ma_livu.Alarm;

import java.util.Date;

public class PrevTimeSetClass {
    public static final int SEC = 60;

    public static final int MIN = 60;

    public static final int HOUR = 24;

    public static final int DAY = 30;

    public static final int MONTH = 12;

    public static String formatTimeString(Date date){
        long curTime = System.currentTimeMillis();

        long regTime = date.getTime();

        long diffTime = (curTime - regTime) / 1000;



        String msg = null;

        if (diffTime < SEC) {

            // sec

            msg = "방금 전";

        } else if ((diffTime /= SEC) < MIN) {

            // min

            msg = diffTime + "분 전";

        } else if ((diffTime /= MIN) < HOUR) {

            // hour

            msg = (diffTime) + "시간 전";

        } else if ((diffTime /= HOUR) < DAY) {

            // day

            msg = (diffTime) + "일 전";

        } else if ((diffTime /= DAY) < MONTH) {

            // day

            msg = (diffTime) + "달 전";

        } else {

            msg = (diffTime) + "년 전";

        }
        return msg;
    }
}
