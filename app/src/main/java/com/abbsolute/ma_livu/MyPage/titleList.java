package com.abbsolute.ma_livu.MyPage;

/* 대표칭호 리스트 */

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.abbsolute.ma_livu.R;

public class titleList {
    private String[] TODO_titleList = {"인간돌돌이", "인간빗자루", "인간청소기", "50회는아직안정함","다듬이", "빨래판", "드럼세탁기", "스타일러",
           "5L","10L","50L","100L","초보계획러","중급계획러","프로계획러"};
    private String[] today_titleList = {"말리브 influencer"};
    private String[] attendance_titleList = {"말리브새내기","말리브정든내기","말리브껌딱지"};
    private String[] room_titleList = {"금","은","동","미니멀리스트","살림뉴비","살림부자","맥시멈리스트"};

  //  Drawable drawable = Resources.
    public String getTODO_titleList(int index) {
        return TODO_titleList[index];
    }
    public String getToday_titleList(int index) {
        return today_titleList[index];
    }
    public String getAttendance_titleList(int index) {
        return attendance_titleList[index];
    }
    public String getRoom_titleList(int index) {
        return room_titleList[index];
    }

}
