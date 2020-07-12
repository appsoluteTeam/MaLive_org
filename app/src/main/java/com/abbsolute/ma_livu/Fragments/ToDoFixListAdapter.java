package com.abbsolute.ma_livu.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class ToDoFixListAdapter extends RecyclerView.Adapter<ToDoFixListAdapter.ViewHolder> {
    ArrayList<ToDoFixInfo> arrayList=new ArrayList<>();
    public class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView fixToDoTextView;
        protected TextView fixPeriodTextView;
        public ViewHolder(View v){
            super(v);
            fixToDoTextView=v.findViewById(R.id.todo);
            fixPeriodTextView=v.findViewById(R.id.todo_date);
        }
    }
    public void addFixItem(ToDoFixInfo fixInfo){
        arrayList.add(fixInfo);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_fix_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoFixInfo info=arrayList.get(position);
        holder.fixToDoTextView.setText(info.getFixToDo());
        holder.fixPeriodTextView.setText(info.getFixPeriod());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
