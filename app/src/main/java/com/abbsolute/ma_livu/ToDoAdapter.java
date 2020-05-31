package com.abbsolute.ma_livu;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
        protected CheckBox checkBox;
        ViewHolder(View v) {
            super(v);
            this.Contents = v.findViewById(R.id.todo_text);//내용
            this.ContentsDetail=v.findViewById(R.id.todo_text_detail);//상세내용
            this.writeDates=v.findViewById(R.id.write_date);//작성 날짜
            this.dDays=v.findViewById(R.id.d_date);//디데이
            this.checkBox=v.findViewById(R.id.check_complete);
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
        //복사하기(클립보드)
        holder.Contents.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String copyText=holder.Contents.getText().toString();
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("복사하기",copyText);
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });
        //수정
        holder.Contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,WriteActivity.class);
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
        ///체크데이터 불러오기
        SharedPreferences pf=context.getSharedPreferences("pref",Activity.MODE_PRIVATE);
        Boolean chk1=pf.getBoolean("chk"+position, false);
        holder.checkBox.setChecked(chk1);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ///데이터 저장
                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(context, ""+isChecked, Toast.LENGTH_SHORT).show();
                editor.putBoolean("chk"+position,isChecked);
                editor.commit();
            }
        });

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
