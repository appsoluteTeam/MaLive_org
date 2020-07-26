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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_category_list, parent, false);
        return new ViewHolder(itemView);
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
                }
                else if(text.equals("빨래하기")){
                    Toast.makeText(context, "빨래하기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","빨래");
                    editor.commit();
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
                }
                else if(text.equals("쓰레기")){
                    Toast.makeText(context, "쓰레기 이미지 클릭!", Toast.LENGTH_SHORT).show();
                    editor.putString("toDo","쓰레기");
                    editor.commit();
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
                                        data.put(FirebaseID.trash, newTrashNum);
                                        firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                        Toast.makeText(context, "쓰레기 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    newtrashNum[0]="1";
                                    Map<String, Object> data = new HashMap<>();
                                    //data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid());
                                    data.put(FirebaseID.trash, newtrashNum[0]);
                                    firestore.collection(FirebaseID.ToDoLists).document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                                    Toast.makeText(context, "쓰레기 칭호 카운트 업!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
