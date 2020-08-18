package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoCategoryAdapter extends RecyclerView.Adapter<ToDoCategoryAdapter.ViewHolder> {
    ArrayList<ToDoCategoryInfo> arrayList=new ArrayList<>();
    Context context;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView todoText;
        ViewHolder(View v){
            super(v);
            this.todoText=v.findViewById(R.id.todo_image_text);
        }
    }
    public void setItem(ArrayList<ToDoCategoryInfo> arrayList){
        this.arrayList=arrayList;
    }
    public void getCategoryContext(Context context){
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_category_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoCategoryInfo categoryInfo=arrayList.get(position);
        final String text=categoryInfo.getToDoText();
        holder.todoText.setText(text);
        holder.todoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                if(text.equals("청소하기")){
                    Toast.makeText(context, "청소하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo", "청소");
                    editor.commit();
                }//청소하기
                else if(text.equals("빨래하기")){
                    Toast.makeText(context, "빨래하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","빨래");
                    editor.commit();

                }//빨래하기
                else if(text.equals("쓰레기")){
                    Toast.makeText(context, "쓰레기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","쓰레기");
                    editor.commit();

                }//쓰레기
                else if(text.equals("기타")){
                    Toast.makeText(context, "기타 이미지 클릭", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","기타");
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
