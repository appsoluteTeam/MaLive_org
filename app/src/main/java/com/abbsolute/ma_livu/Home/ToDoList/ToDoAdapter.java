package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    ArrayList<ToDoInfo> arrayList = new ArrayList<>();
    CheckBox[] checkBoxes=new CheckBox[arrayList.size()];
    Context context;
    private static int UPDATE_RESULT=101;
    // CheckBox checkBox;
    boolean check=false;
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView ContentsDetail;
        protected TextView Contents;
        // protected TextView writeDates;
        protected TextView dDays;
        protected FrameLayout toDoFrame;
        protected FrameLayout toDoDetailFrame;
        protected CheckBox checkToDo;//할 일 체크 하는 오른쪽 라디오 버튼
        public ViewHolder(View v) {
            super(v);
            this.Contents = v.findViewById(R.id.todo_text);//내용
            this.ContentsDetail=v.findViewById(R.id.todo_text_detail);//상세내용
            //  this.writeDates=v.findViewById(R.id.write_date);//작성 날짜
            checkToDo=v.findViewById(R.id.chk_btn);
            this.dDays=v.findViewById(R.id.d_date);//디데이
            this.toDoFrame=v.findViewById(R.id.todo_frame);
            this.toDoDetailFrame=v.findViewById(R.id.todo_detail_frame);
        }
    }
    public void addItem(ToDoInfo info) {
        arrayList.add(info);
    }
    public void setItem(ArrayList<ToDoInfo> arrayList) {
        this.arrayList = arrayList;
    }
    public void GetContext(Context context){
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todolist, parent, false);

        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ToDoInfo toDoInfo = arrayList.get(position);
        holder.Contents.setText(toDoInfo.getContent());
        holder.ContentsDetail.setText(toDoInfo.getDetailContent());
        // holder.writeDates.setText(toDoInfo.getDates());
        if(toDoInfo.getdDay()!=null){
            holder.dDays.setText(toDoInfo.getdDay());
        }
        if(position>=1){
            ToDoInfo tmp=arrayList.get(position-1);
            if(tmp.getdDay().equals(toDoInfo.getdDay())){
                holder.dDays.setVisibility(View.GONE);
            }
        }
        //수정
        holder.Contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ToDoWriteFragment.class);
                intent.putExtra("modify",toDoInfo.getContent());
                context.startActivity(intent);
            }
        });
        //고정 할 일 데이터는 뒷배경 회색으로, 카테고리는 흰색
        try{
            holder.Contents.setBackgroundResource(toDoInfo.getColor());
            holder.ContentsDetail.setBackgroundResource(toDoInfo.getColor());
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }
        ///할 일 완료 체크
       holder.checkToDo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
               if(isChecked==false){
                   SharedPreferences.Editor editor = pref.edit();
                   editor.putBoolean("chk",true);
                   editor.commit();
               }else{
                   SharedPreferences.Editor editor = pref.edit();
                   editor.putBoolean("chk",false);
                   editor.commit();
               }
           }
       });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
