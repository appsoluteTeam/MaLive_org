package com.abbsolute.ma_livu;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Activities.WriteActivity;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView ContentsDetail;
        protected TextView Contents;
        protected TextView writeDates;
        protected TextView dDays;

        ViewHolder(View v) {
            super(v);
            this.Contents = v.findViewById(R.id.todo_text);//내용
            this.ContentsDetail=v.findViewById(R.id.todo_text_detail);//상세내용
            this.writeDates=v.findViewById(R.id.write_date);//작성 날짜
            this.dDays=v.findViewById(R.id.d_date);//디데이

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
        holder.writeDates.setText(toDoInfo.getDates());
        if(toDoInfo.getdDay()!=null){
            holder.dDays.setText(toDoInfo.getdDay());
        }
        //수정
        holder.Contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, WriteActivity.class);
                intent.putExtra("modify",toDoInfo.getContent());
                context.startActivity(intent);
            }
        });
        holder.ContentsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,WriteActivity.class);
                intent.putExtra("modify",toDoInfo.getContent());
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
