package com.abbsolute.ma_livu.Home;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoAdapter;
import com.abbsolute.ma_livu.Home.ToDoList.ToDoInfo;
import com.abbsolute.ma_livu.MyPage.RecyclerPayAdapter;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter_final extends RecyclerView.Adapter<ToDoAdapter_final.ViewHolder> {

    private ArrayList<ToDoList_Info> arrayList;

    public ToDoAdapter_final(ArrayList<ToDoList_Info> arrayList) {
            this.arrayList = arrayList;
    }
 //   public ToDoAdapter_final(){}

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView todo_date;
        private TextView detail_date;
        private TextView todo_category;
        private TextView todo_detail;
        private CheckBox check;

        public ViewHolder(View view){
            super(view);
            this.todo_date = view.findViewById(R.id.todo_date);
            this.detail_date = view.findViewById(R.id.detail_date);
            this.todo_category = view.findViewById(R.id.todo_category);
            this.todo_detail = view.findViewById(R.id.todo_detail);
            this.check = view.findViewById(R.id.btn_todo_check);
        }

    }

    @NonNull
    @Override
    public ToDoAdapter_final.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 최초로 만들어내는 역할
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.todo_custom_list, parent, false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_custom_list, parent, false);
        ToDoAdapter_final.ViewHolder holder = new ToDoAdapter_final.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoAdapter_final.ViewHolder holder, final int position) { //각 아이템에 대한 매칭

            Log.d("position",Integer.toString(position));

            //이미 같은 날짜가 존재하면 날짜 표시 안해주기
            if(position != 0){
                if(arrayList.get(position - 1).getTodo_date().equals(arrayList.get(position).getTodo_date())){
                    holder.todo_date.setText("");
                }else{
                    holder.todo_date.setText(arrayList.get(position).getTodo_date());
                }
            }else{
                holder.todo_date.setText(arrayList.get(position).getTodo_date());
            }

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String id = firebaseAuth.getCurrentUser().getEmail();

        firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo").document(arrayList.get(position).getDetail_date())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                boolean check = (boolean)shot.get("check");
                                Log.d("check",Integer.toString(position) + Boolean.toString(check));
                                holder.check.setChecked(check);
                            }
                        }
                    }
                });

        //체크박스 클릭 시 listener
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String todo_date = arrayList.get(position).getTodo_date();
                String detail_date = arrayList.get(position).getDetail_date();
                String todo_category = arrayList.get(position).getTodo_category();
                String todo_detail = arrayList.get(position).getTodo_datail();

                if(isChecked == true){
                    holder.check.setChecked(true);

                    //firebase 업데이트
                    DocumentReference data = firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo").document(arrayList.get(position).getDetail_date());
                    data.update("check", true);

                    //ArrayList 업데이트
//                    ToDoList_Info toDoInfo = new ToDoList_Info(todo_date,detail_date,todo_category,todo_detail,true);
//                    arrayList.set(position,toDoInfo);

                }else {
                    holder.check.setChecked(false);

                    //firebase 업데이트
                    DocumentReference data = firestore.collection(FirebaseID.ToDoLists).document(id).collection("ToDo").document(arrayList.get(position).getDetail_date());
                    data.update("check",false);

                    //arrayList 업데이트
//                    ToDoList_Info toDoInfo = new ToDoList_Info(todo_date,detail_date,todo_category,todo_detail,false);
//                    arrayList.set(position,toDoInfo);

                }
            }
        });



            holder.detail_date.setText(arrayList.get(position).getDetail_date());
            holder.todo_category.setText(arrayList.get(position).getTodo_category());
            holder.todo_detail.setText(arrayList.get(position).getTodo_datail());
    }

    @Override
    public int getItemCount() {
            return (arrayList != null ? arrayList.size() : 0);
            }

}
