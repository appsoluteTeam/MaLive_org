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
import java.util.HashMap;
import java.util.Map;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    ArrayList<ToDoInfo> arrayList = new ArrayList<>();
    CheckBox[] checkBoxes = new CheckBox[arrayList.size()];
    Context context;
    private static int UPDATE_RESULT = 101;
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

    public void addItem(ToDoInfo info) {
        arrayList.add(info);
    }

    public void setItem(ArrayList<ToDoInfo> arrayList) {
        this.arrayList = arrayList;
    }

    public void GetContext(Context context, OnToDoTextClick listener) {
        this.context = context;
        this.onCallBack=listener;
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
        // holder.writeDates.setText(toDoInfo.getDates());
        if (toDoInfo.getdDay() != null) {
            holder.dDays.setText(toDoInfo.getdDay());
        }
        if (position >= 1) {
            ToDoInfo tmp = arrayList.get(position - 1);
            if (tmp.getdDay().equals(toDoInfo.getdDay())) {
                holder.dDays.setVisibility(View.GONE);
            }
        }
        //수정
        holder.Contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendData=toDoInfo.getDetailContent();
                SharedPreferences sharedPreferences=context.getSharedPreferences("pref2",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("modify",true);
                editor.putString("modifyContent",sendData);
                editor.commit();
                onCallBack.onClick(3);//수정하기 이벤트처리

            }
        });
        //고정 할 일 데이터는 뒷배경 회색으로, 카테고리는 흰색
        try {
            holder.Contents.setBackgroundResource(toDoInfo.getColor());
            holder.ContentsDetail.setBackgroundResource(toDoInfo.getColor());
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        ///할 일 완료 체크여부 설정하기
        ///체크데이터 불러오기
        SharedPreferences pf = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk1 = pf.getBoolean("chk" + position, false);
        holder.checkToDo.setChecked(chk1);
        if(chk1==true){//완료했다는 표시
            String todo=arrayList.get(position).getContent();
            String detailToDo=arrayList.get(position).getDetailContent();
            if(todo.equals("청소")){
                final String[] newCleaningNum = {""};
                final boolean[] flag = {false};
                DocumentReference documentReference=firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot=task.getResult();
                            String tmp= (String) snapshot.getData().get("cleaning");
                            int tmpNum=Integer.parseInt(tmp);
                            if(tmpNum>=1){
                                String cleaningNum= (String) snapshot.getData().get("cleaning");
                                Toast.makeText(context, cleaningNum, Toast.LENGTH_SHORT).show();
                                int tmpCleaningNum=0;
                                try {
                                    tmpCleaningNum=Integer.parseInt(cleaningNum)+1;
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                                String newCleaningNum=Integer.toString(tmpCleaningNum);
                                if(!newCleaningNum.equals("1")){
                                    Map<String, Object> data = new HashMap<>();
                                    //data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                    data.put(FirebaseID.cleaning, newCleaningNum);
                                    firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                    Toast.makeText(context, "청소 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                newCleaningNum[0]="1";
                                Map<String, Object> data = new HashMap<>();
                                // data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                data.put(FirebaseID.cleaning, newCleaningNum[0]);
                                firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                Toast.makeText(context, "청소 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }//청소하기 완료!!!
            else if(todo.equals("빨래")){
                final String[] newLaundryNum = {""};
                final boolean[] flag = {false};
                DocumentReference documentReference=firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot=task.getResult();
                            String tmp= (String) snapshot.getData().get("laundry");//횟수가 1이상인지 확인하는 코드
                            int tmpNum=Integer.parseInt(tmp);
                            if(tmpNum>=1){
                                String laundryNum= (String) snapshot.getData().get("laundry");
                                Toast.makeText(context, laundryNum, Toast.LENGTH_SHORT).show();
                                int tmpLaundryNum=0;
                                try{
                                    tmpLaundryNum=Integer.parseInt(laundryNum)+1;
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                                String newLaundryNum=Integer.toString(tmpLaundryNum);
                                if(!newLaundryNum.equals("1")){
                                    Map<String, Object> data = new HashMap<>();
                                    //data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                    data.put(FirebaseID.laundry, newLaundryNum);
                                    firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                    Toast.makeText(context, "빨래하기 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                newLaundryNum[0]="1";
                                Map<String, Object> data = new HashMap<>();
                                // data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                data.put(FirebaseID.laundry, newLaundryNum[0]);
                                firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                Toast.makeText(context, "빨래하기 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }//빨래하기 완료!!
            else if(todo.equals("쓰레기")){
                final String[] newtrashNum = {""};
                DocumentReference documentReference=firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot=task.getResult();
                            String tmp= (String) snapshot.getData().get("trash");//횟수가 1이상인지 확인하는 코드
                            int tmpNum=Integer.parseInt(tmp);
                            if(tmpNum>=1){
                                String trashNum= (String) snapshot.getData().get("trash");
                                Toast.makeText(context, trashNum, Toast.LENGTH_SHORT).show();
                                int tmpTrashNum=0;
                                try{
                                    tmpTrashNum=Integer.parseInt(trashNum)+1;
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                                String newTrashNum=Integer.toString(tmpTrashNum);
                                if(!newTrashNum.equals("1")){
                                    Map<String, Object> data = new HashMap<>();
                                    // data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                    data.put(FirebaseID.ToDoTrash, newTrashNum);
                                    firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                    Toast.makeText(context, "쓰레기 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                newtrashNum[0]="1";
                                Map<String, Object> data = new HashMap<>();
                                //data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                data.put(FirebaseID.ToDoTrash, newtrashNum[0]);
                                firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                Toast.makeText(context, "쓰레기 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                
            }//쓰레기 완료!!!
        }
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
