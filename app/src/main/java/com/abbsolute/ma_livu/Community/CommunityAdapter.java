package com.abbsolute.ma_livu.Community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CustomViewHolder> {

    private ArrayList<bringData> arrayList;

    public CommunityAdapter(ArrayList<bringData> arrayList){
        this.arrayList =arrayList;
    }

    @NonNull
    @Override
    public CommunityAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.CustomViewHolder holder, int position) {
        holder.community_category.setText(arrayList.get(position).getCategory());
        holder.community_title.setText(arrayList.get(position).getTitle());

    }

    @Override
    public int getItemCount()  {
        return (arrayList !=null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView community_category;
        TextView community_title;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.community_category = itemView.findViewById(R.id.commu_category);
            this.community_title = itemView.findViewById(R.id.commu_title);

        }
    }
}
