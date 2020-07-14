package com.abbsolute.ma_livu.ToDoList;
//할 일 어뎁터 클래스
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;
import com.abbsolute.ma_livu.Interfaces.OnItemClickListner;

import java.util.ArrayList;



public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    ArrayList<ToDoInfo> arrayList = new ArrayList<>();
    OnItemClickListner listener;
    CheckBox[] checkBoxes=new CheckBox[arrayList.size()];
    Context context;
    private static int UPDATE_RESULT=101;
   // CheckBox checkBox;
    boolean check=false;
    public void setOnItemClickListner(OnItemClickListner listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView ContentsDetail;
        protected TextView Contents;
       // protected TextView writeDates;
        protected TextView dDays;
        protected FrameLayout toDoFrame;
        protected FrameLayout toDoDetailFrame;
        public ViewHolder(View v) {
            super(v);
            this.Contents = v.findViewById(R.id.todo_text);//내용
            this.ContentsDetail=v.findViewById(R.id.todo_text_detail);//상세내용
          //  this.writeDates=v.findViewById(R.id.write_date);//작성 날짜
            this.dDays=v.findViewById(R.id.d_date);//디데이
            this.toDoFrame=v.findViewById(R.id.todo_frame);
            this.toDoDetailFrame=v.findViewById(R.id.todo_detail_frame);
        }

    }
    public void addItem(ToDoInfo info) {
        arrayList.add(info);
    }
    public void removeTask(int pos){
        arrayList.remove(pos);
        notifyDataSetChanged();
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
    public void getCheckState(boolean is){
        check=is;
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
        holder.Contents.setBackgroundResource(toDoInfo.getColor());
        holder.ContentsDetail.setBackgroundResource(toDoInfo.getColor());

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
