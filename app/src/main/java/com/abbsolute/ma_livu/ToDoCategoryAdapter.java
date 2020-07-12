package com.abbsolute.ma_livu;

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

import java.util.ArrayList;

public class ToDoCategoryAdapter extends RecyclerView.Adapter<ToDoCategoryAdapter.ViewHolder> {
    ArrayList<ToDoCategoryInfo> arrayList=new ArrayList<>();
    Context context;
    public class ViewHolder extends RecyclerView.ViewHolder{
        protected ImageView todoImg;
        protected TextView todoText;

        ViewHolder(View v){
            super(v);
            this.todoImg=v.findViewById(R.id.todo_image);
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
    public ToDoCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_category_list, parent, false);

        return new ToDoCategoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoCategoryInfo categoryInfo=arrayList.get(position);
        int img=categoryInfo.getToDoImage();
        final String text=categoryInfo.getToDoText();
        holder.todoImg.setImageResource(img);
        holder.todoText.setText(text);
        holder.todoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                if(text.equals("청소하기")){
                    Toast.makeText(context, "청소하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo", "청소");
                    editor.commit();
                }
                else if(text.equals("빨래하기")){
                    Toast.makeText(context, "빨래하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","빨래");
                    editor.commit();
                }
                else if(text.equals("쓰레기")){
                    Toast.makeText(context, "쓰레기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","쓰레기");
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
