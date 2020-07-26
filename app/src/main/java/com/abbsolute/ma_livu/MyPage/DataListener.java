package com.abbsolute.ma_livu.MyPage;

//TODOtitleFragment, attendanceFragment, roomFragment, todayFragment 가 TitleFragment에게 정보전달하기 위해 필요한 listener

public interface DataListener {
    void dataSet(String title, int index, int category);
}
