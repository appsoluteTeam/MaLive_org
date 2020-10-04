package com.abbsolute.ma_livu.Customize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.FaceViewHolder> {

    public FaceAdapter(UnityItem[] items,int selectedPos) {
        this.items = items;
        this.selectedPos = selectedPos;
        faceViewHolders = new ArrayList<>();
    }

//    public ItemClickListener itemClickListener;
    private UnityItem[] items;
    private int selectedPos;
    private List<FaceViewHolder> faceViewHolders;

    @NonNull
    @Override
    public FaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customize_face_item_view,parent,false);
        FaceViewHolder faceViewHolder = new FaceViewHolder(view);
        faceViewHolders.add(faceViewHolder);
        return faceViewHolder;
    }

    public void setItemViewSelected(int pos){
        FaceViewHolder prevItem = faceViewHolders.get(selectedPos);
        prevItem.faceImageView.setBorderWidth(0);


        FaceViewHolder selectedItem = faceViewHolders.get(pos);
        prevItem.faceImageView.setBorderWidth(2);
    }


    @Override
    public void onBindViewHolder(@NonNull FaceViewHolder holder, int position) {
        //   holder.faceImageView.setImageResource(items[position]);
        //TODO: 표정 이미지 추가
        //  holder.itemTextView.setText(items[position].getName());
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
    public UnityItem getItem(int pos) {
        return items[pos];
    }

    public void setBorder(int pos){

    }

    public class FaceViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView faceImageView;
 //       TextView itemTextView;
        public FaceViewHolder(@NonNull View itemView) {
            super(itemView);
            faceImageView = itemView.findViewById(R.id.face_image);
       //     itemTextView = itemView.findViewById(R.id.face_text);
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
