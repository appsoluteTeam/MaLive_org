package com.abbsolute.ma_livu.Home.ToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    ArrayList<ToDoInfo> arrayList = new ArrayList<>();
    BringToDoData[] toDoDataArray;
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
            //  this.writeDates=v.findViewById(R.id.write_date);//작성 날짜
            //
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
        SharedPreferences sharedPreferences = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        final String id = sharedPreferences.getString("email_id", "");
        firestore.collection("ToDoList").document(id + " ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                String content = (String) snapshot.getData().get("contents"+position);
                                final String detailContent = (String) snapshot.getData().get("detailContents"+position);
                                String dates = (String) snapshot.getData().get("dates"+position);
                                String dDay = (String) snapshot.getData().get("dDates"+position);
                                holder.Contents.setText(content);
                                holder.ContentsDetail.setText(detailContent);
                                // holder.writeDates.setText(toDoInfo.getDates());
                                if (dDay != null) {
                                    holder.dDays.setText(dDay);
                                }
                                if (position >= 1) {
                                    int pos = position;
                                    if (pos > 0)
                                        pos--;
                                    String tmp = (String) snapshot.getData().get("dDates" + pos);
                                    ;
                                    if (tmp.equals(dDay)) {
                                        holder.dDays.setVisibility(View.GONE);
                                    }
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
                                //고정 할 일 데이터는 뒷배경 회색으로, 카테고리는 흰색
                                try {
                                    if(snapshot.exists()){
                                        final String color = (String) snapshot.getData().get("color"+position);
                                        int backs = Integer.parseInt(color);
                                        holder.Contents.setBackgroundResource(backs);
                                        holder.ContentsDetail.setBackgroundResource(backs);
                                    }
                                } catch (Resources.NotFoundException e) {
                                    e.printStackTrace();
                                }
                                ///할 일 완료 체크여부 설정하기

                            }
                        }
                    }
                });
        ///체크데이터 불러오기
        final SharedPreferences pf = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk1 = pf.getBoolean("chk" + position, false);
        holder.checkToDo.setChecked(chk1);
        holder.checkToDo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ///데이터 저장
                SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(context, "" + isChecked, Toast.LENGTH_SHORT).show();
                editor.putBoolean("chk" + position, isChecked);
                editor.commit();
            }
        });
    }//bindHolder끝


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
