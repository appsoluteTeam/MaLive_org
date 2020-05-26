package com.appsolute.org.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    ArrayList<ToDoInfo> arrayList = new ArrayList<>();
    OnItemClickListner listener;
    Context context;
    private static int UPDATE_RESULT=101;
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
            this.Contents = v.findViewById(R.id.todo_text);
            this.ContentsDetail=v.findViewById(R.id.todo_text_detail);
            this.writeDates=v.findViewById(R.id.write_date);
            this.dDays=v.findViewById(R.id.d_date);
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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ToDoInfo toDoInfo = arrayList.get(position);
        holder.Contents.setText(toDoInfo.getContent());
        holder.ContentsDetail.setText(toDoInfo.getDetailContent());
        holder.writeDates.setText(toDoInfo.getDates());
        if(toDoInfo.getdDay()!=null){
            holder.dDays.setText(toDoInfo.getdDay());
        }
        holder.Contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
