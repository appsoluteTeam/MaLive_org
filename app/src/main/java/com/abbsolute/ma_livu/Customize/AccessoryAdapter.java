package com.abbsolute.ma_livu.Customize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.R;

public class AccessoryAdapter extends RecyclerView.Adapter<AccessoryAdapter.ItemViewBinder> {

    public AccessoryAdapter(UnityItem[] items) {
        this.items = items;
    }

    public ItemClickListener itemClickListener;
    private UnityItem[] items;
    private RecyclerView parent;

    @NonNull
    @Override
    public ItemViewBinder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customize_item_view, parent, false);

        int width = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        view.getLayoutParams().width = width / 4; /// THIS LINE WILL DIVIDE OUR VIEW INTO NUMBERS OF PARTS
        ItemViewBinder itemViewBinder = new ItemViewBinder(view);

        return itemViewBinder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewBinder holder, int position) {
        //   holder.itemImageView.setImageResource(items[position]);
        holder.itemTextView.setText(items[position].getName());

    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ItemViewBinder extends RecyclerView.ViewHolder {

        ImageView itemImageView;
        TextView itemTextView;

        public ItemViewBinder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, items[getAdapterPosition()]);
                    }
                }
            });
        }
    }

    public UnityItem getItem(int pos) {
        return items[pos];
    }


    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, UnityItem item);
    }
}
