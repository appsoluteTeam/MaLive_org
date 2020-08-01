package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.deleteFixData;

public class ToDoFixListAdapter extends RecyclerView.Adapter<ToDoFixListAdapter.ViewHolder> {
    static ArrayList<ToDoFixInfo> arrayList=new ArrayList<>();
    static Context context;
    static String text;
    static String fixDate;
    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView fixToDoTextView;
        protected TextView fixPeriodTextView;

        boolean flag=false;// 고정리스트 눌렀을때 회색->흰색, 흰색->회색

        public ViewHolder(View v){
            super(v);
            final LinearLayout linearLayout=v.findViewById(R.id.fix_list_layout);
            fixToDoTextView=v.findViewById(R.id.todo);
            fixPeriodTextView=v.findViewById(R.id.todo_date);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag==false)
                    {
                        linearLayout.setBackgroundColor(Color.GRAY);
                        SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        String upDateText=fixToDoTextView.getText().toString();
                        editor.putString("upDateToDo",upDateText);
                        editor.commit();
                        flag=true;
                    }else if(flag==true) {
                        linearLayout.setBackgroundColor(Color.WHITE);
                        flag = false;
                    }
                }
            });
        }
    }
    public void getFixContext(Context context){
        this.context=context;
    }


    public void setFixItem(ArrayList<ToDoFixInfo> fixInfos){
        arrayList=fixInfos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_fix_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ToDoFixInfo toDoFixInfo=arrayList.get(position);
        holder.fixToDoTextView.setText(toDoFixInfo.getFixToDo());//고정 할 일
        holder.fixPeriodTextView.setText(toDoFixInfo.getFixPeriod());//고정 할 일 주기

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}