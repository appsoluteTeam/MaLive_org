package com.abbsolute.ma_livu.MyPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerPostAdapter extends RecyclerView.Adapter<RecyclerPostAdapter.ViewHolder> {
    private ArrayList<postItemListView> arrayList;

    public RecyclerPostAdapter(ArrayList<postItemListView> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 최초로 만들어내는 역할
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypost_recycler_item, parent, false);
        RecyclerPostAdapter.ViewHolder holder = new RecyclerPostAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerPostAdapter.ViewHolder holder, final int position) { //각 아이템에 대한 매칭
        holder.post_category.setText(arrayList.get(position).getPost_category());
        holder.post_name.setText(arrayList.get(position).getPost_name());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView post_category;
        private TextView post_name;

        public ViewHolder(View view){
            super(view);
            this.post_category = view.findViewById(R.id.post_category);
            this.post_name = view.findViewById(R.id.post_name);
        }

    }
}
