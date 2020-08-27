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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.abbsolute.ma_livu.Home.ToDoList.ToDoAppHelper.deleteFixData;

public class ToDoFixRemoveListAdapter extends RecyclerView.Adapter<ToDoFixRemoveListAdapter.ViewHolder> {
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
        protected ImageButton removingBtn;//삭제 버튼
        protected LinearLayout removeLayout;//삭제리스트 레이아웃
        public ViewHolder(View v){
            super(v);
            fixToDoTextView=v.findViewById(R.id.todo_remove);
            fixPeriodTextView=v.findViewById(R.id.todo_date_remove);
            removingBtn=v.findViewById(R.id.removingButton);
            removeLayout=v.findViewById(R.id.fix_remove_layout);
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
        View itemView = inflater.inflate(R.layout.todo_fix_remove_lists, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ToDoFixInfo toDoFixInfo=arrayList.get(position);
        String chkStr=toDoFixInfo.getFixPeriod();
        if(chkStr==null){
           holder.removeLayout.setVisibility(View.GONE);
        }
        holder.fixToDoTextView.setText(toDoFixInfo.getFixToDo());//고정 할 일
        holder.fixPeriodTextView.setText(toDoFixInfo.getFixPeriod());//고정 할 일 주기
        holder.removingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deleteData=arrayList.get(position).fixToDo;
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,arrayList.size());
               // deleteFixData("fixToDoInfo",toDoFixInfo.getFixToDo());
                SharedPreferences sharedPreferences=context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                final String id=sharedPreferences.getString("email_id","");
                firestore.collection(FirebaseID.ToDoLists).document(id).collection("FixToDo")
                        .document(deleteData)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}