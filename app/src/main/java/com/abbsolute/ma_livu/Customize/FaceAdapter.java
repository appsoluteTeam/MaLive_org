package com.abbsolute.ma_livu.Customize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.FaceViewHolder> {

    public FaceAdapter(UnityItem items[]) {
        this.items = items;
    }

//    public ItemClickListener itemClickListener;
    private UnityItem[] items;

    @NonNull
    @Override
    public FaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customize_face_item_view,parent,false);
        FaceViewHolder faceViewHolder = new FaceViewHolder(view);
        return faceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FaceViewHolder holder, int position) {
        //   holder.faceImageView.setImageResource(items[position]);
        //TODO: 표정 이미지 추가
        holder.itemTextView.setText(items[position].getName());
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
    public UnityItem getItem(int pos) {
        return items[pos];
    }

    public class FaceViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView faceImageView;
        TextView itemTextView;
        public FaceViewHolder(@NonNull View itemView) {
            super(itemView);
            faceImageView = itemView.findViewById(R.id.face_image);
            itemTextView = itemView.findViewById(R.id.face_text);
            this.itemView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (itemClickListener != null) {
//                        itemClickListener.onItemClick(view, items[getAdapterPosition()]);
//                    }
                }
            });
        }
    }

//    public void setItemClickListener(ItemClickListener itemClickListener){
//        this.itemClickListener = itemClickListener;
//    }
//    public interface ItemClickListener {
//        void onItemClick(View view, UnityItem item);
//    }
}
