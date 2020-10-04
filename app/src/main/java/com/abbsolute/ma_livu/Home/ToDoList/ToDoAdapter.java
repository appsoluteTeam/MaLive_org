package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    ArrayList<ToDoInfo> arrayList = new ArrayList<>();
    // CheckBox[] checkBoxes = new CheckBox[arrayList.size()];
    Context context;
    private static int UPDATE_RESULT = 101;
    ToDoInfo toDoInfo = null;
    // CheckBox checkBox;
    boolean check = false;
    private OnToDoTextClick onCallBack;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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
            this.ContentsDetail = v.findViewById(R.id.todo_text_detail);//상세내용
            this.checkToDo = v.findViewById(R.id.chk_btn);
            this.dDays = v.findViewById(R.id.d_date);//디데이
            this.toDoFrame = v.findViewById(R.id.todo_frame);
            this.toDoDetailFrame = v.findViewById(R.id.todo_detail_frame);
        }
    }


    public void setItem(ArrayList<ToDoInfo> arrayList) {
        this.arrayList = arrayList;
    }

    public void GetContext(Context context, OnToDoTextClick listener) {
        this.context = context;
        this.onCallBack = listener;
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
        final ToDoInfo toDoInfo=arrayList.get(position);
        final String detailContent=toDoInfo.getDetailContent();
        String dDay=toDoInfo.getdDay();

        //고정 할 일 데이터는 뒷배경 회색으로, 카테고리는 흰색
        try {
            int backs=arrayList.get(position).color;
            if(backs==2131231008){
                String email=firebaseAuth.getCurrentUser().getEmail();
                firestore.collection(FirebaseID.ToDoLists).document(email).collection("FixToDo")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult()!=null){
                                        for(DocumentSnapshot snapshot:task.getResult()){
                                            Map<String,Object> data=snapshot.getData();
                                            String detail=(String)data.get("todo");
                                            String periods=(String)data.get("period");
                                            if(detail.equals(detailContent)){
                                                String category=toDoInfo.getContent();
                                                String contentText=category+"\n"+periods;
                                                holder.Contents.setText(contentText);
                                                holder.dDays.setVisibility(View.VISIBLE);
                                                holder.dDays.setText("고정리스트");
                                            }
                                        }
                                    }
                                }
                            }
                        });
                holder.Contents.setText(toDoInfo.getContent());
                holder.ContentsDetail.setText(toDoInfo.getDetailContent());
            }else if(backs==2131231007){
                holder.Contents.setText(toDoInfo.getContent());
                holder.ContentsDetail.setText(toDoInfo.getDetailContent());
                if (dDay != null) {
                    holder.dDays.setText(dDay);
                    holder.dDays.setTextColor(Color.BLACK);
                }
                if (position >= 1) {
                    int pos = position;
                    if (pos > 0)
                        pos--;
                    String tmp = arrayList.get(pos).dDay;
                    ;
                    if (tmp.equals(dDay)) {
                        holder.dDays.setVisibility(View.GONE);
                    }
                }
            }
            holder.Contents.setBackgroundResource(backs);
            holder.ContentsDetail.setBackgroundResource(backs);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        //수정
        holder.Contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendData = detailContent;
                SharedPreferences sharedPreferences = context.getSharedPreferences("pref2", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("modify", true);
                editor.putString("modifyContent", sendData);
                editor.putInt("pos",position);
                editor.commit();
                onCallBack.onClick(3);//수정하기 이벤트처리

            }
        });

        ///체크데이터 불러오기
        final SharedPreferences pf = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk1 = pf.getBoolean("chk" + toDoInfo.getDetailContent(), false);
        holder.checkToDo.setChecked(chk1);
        holder.checkToDo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ///데이터 저장
                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(context, "" + isChecked, Toast.LENGTH_SHORT).show();
                editor.putBoolean("chk" + toDoInfo.getDetailContent(), isChecked);
                editor.commit();
            }
        });
    }//bindHolder끝


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
