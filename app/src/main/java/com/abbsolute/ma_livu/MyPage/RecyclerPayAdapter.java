package com.abbsolute.ma_livu.MyPage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abbsolute.ma_livu.Home.GuestBook.CommentAdapter;
import com.abbsolute.ma_livu.Home.GuestBook.CommentItem;
import com.abbsolute.ma_livu.Home.GuestBook.OnItemClick;
import com.abbsolute.ma_livu.R;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerPayAdapter extends RecyclerView.Adapter<RecyclerPayAdapter.ViewHolder> {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<payItemListView> arrayList;
    private OnItemClick callback;


    public RecyclerPayAdapter(ArrayList<payItemListView> arrayList, OnItemClick listener) {
        this.arrayList = arrayList;
        this.callback = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 최초로 만들어내는 역할
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        RecyclerPayAdapter.ViewHolder holder = new RecyclerPayAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) { //각 아이템에 대한 매칭
        holder.pay_date.setText(arrayList.get(position).getPay_date());
        holder.pay_title.setText(arrayList.get(position).getPay_title());
        holder.pay_time_deposit_withdrawal.setText(arrayList.get(position).getPay_time_deposit_withdrawal());
        holder.amount.setText(arrayList.get(position).getAmount());
        holder.balance.setText(arrayList.get(position).getBalance());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView pay_date;
        private TextView pay_title;
        private TextView pay_time_deposit_withdrawal;
        private TextView amount;
        private TextView balance;

        public ViewHolder(View view){
            super(view);
            this.pay_date = view.findViewById(R.id.pay_date);
            this.pay_title = view.findViewById(R.id.pay_title);
            this.pay_time_deposit_withdrawal = view.findViewById(R.id.pay_time_deposit_withdrawal);
            this.amount = view.findViewById(R.id.amount);
            this.balance = view.findViewById(R.id.balance);
        }

    }
}
