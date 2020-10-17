package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.abbsolute.ma_livu.R;

import org.w3c.dom.Text;

public class ToDoListCustomDialog extends Dialog {
    private TextView titleView;
    private TextView contentView;
    private Button leftButton;
    private Button rightButton;
    private String title;
    private String content;

    private View.OnClickListener leftClickListener;
    private View.OnClickListener rightClickListener;
    public ToDoListCustomDialog(@NonNull Context context, String title,
                                String content, View.OnClickListener leftListener,
                                View.OnClickListener rightListener) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.title=title;
        this.content=content;
        this.leftClickListener=leftListener;
        this.rightClickListener=rightListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams ipWindow=new WindowManager.LayoutParams();
        ipWindow.flags=WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        ipWindow.dimAmount=0.8f;
        getWindow().setAttributes(ipWindow);

        setContentView(R.layout.todolist_custom_dialog);

        titleView=findViewById(R.id.txt_title);
        contentView=findViewById(R.id.txt_content);
        leftButton=findViewById(R.id.btn_left);
        rightButton=findViewById(R.id.btn_right);

        titleView.setText(title);
        contentView.setText(content);

        //클릭 이벤트 셋팅
        if(leftClickListener!=null&&rightClickListener!=null){
            leftButton.setOnClickListener(leftClickListener);
            rightButton.setOnClickListener(rightClickListener);
        }else if(leftClickListener!=null&&rightClickListener==null){
            leftButton.setOnClickListener(leftClickListener);
        }else if(leftClickListener==null&&rightClickListener!=null){
            rightButton.setOnClickListener(rightClickListener);
        }


    }
}
