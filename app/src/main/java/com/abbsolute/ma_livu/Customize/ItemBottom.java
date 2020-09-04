package com.abbsolute.ma_livu.Customize;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.abbsolute.ma_livu.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ItemBottom extends BottomSheetDialogFragment implements AccessoryAdapter.ItemClickListener {

    private ItemBottomListener mListener;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AccessoryAdapter itemRecyclerAdapter;

    UnityItem sample[] = {
            new UnityItem(0,"모자",2,0),
            new UnityItem(1,"헤드셋",2,0),
            new UnityItem(2,"가방",2,0),
            new UnityItem(2,"신발",3,0),
            new UnityItem(2,"옷",4,0),
            new UnityItem(2,"장갑",5,0),
            new UnityItem(2,"짜장면",6,0),
            new UnityItem(3,"귀도리",2,0),
            new UnityItem(4,"목도리",2,0),
            new UnityItem(9,"코트",2,0),
            new UnityItem(10,"블레이저",3,0),
            new UnityItem(11,"첼시부츠",4,0),
            new UnityItem(12,"가디건",5,0),
            new UnityItem(13,"탕수육",3,0),
            new UnityItem(14,"치킨",3,0),
            new UnityItem(14,"치킨",3,0) ,
            new UnityItem(14,"치킨",3,0)
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.customize_accessory_bottom_frag,container,false);
        recyclerView = v.findViewById(R.id.item_recycler_view);
        layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        itemRecyclerAdapter = new AccessoryAdapter(sample);
        itemRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(itemRecyclerAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        SnapToBlock snapToBlock = new SnapToBlock(8);
       snapToBlock.attachToRecyclerView(recyclerView);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //뒤에
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    @Override
    public void onItemClick(View view, UnityItem item) {
        mListener.onButtonClicked(item.getName());
    }


    public interface ItemBottomListener{
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ItemBottomListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}