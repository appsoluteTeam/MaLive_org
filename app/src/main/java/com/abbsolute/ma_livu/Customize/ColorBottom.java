package com.abbsolute.ma_livu.Customize;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abbsolute.ma_livu.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thebluealliance.spectrum.SpectrumPalette;

public class ColorBottom extends BottomSheetDialogFragment implements SpectrumPalette.OnColorSelectedListener {

    private ColorBottomListener mListener;
    private SpectrumPalette spectrumPalette;
    @ColorInt
    int curColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customize_color_bottom_frag, container, false);

        //TODO: 현재 컬러를 받아와야함
        spectrumPalette = v.findViewById(R.id.palette);

        Button saveBtt = v.findViewById(R.id.save_color_btt);
        Button cancelBtt = v.findViewById(R.id.cancel_color_btt);
        spectrumPalette.setOnColorSelectedListener(this);
        saveBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: unity color저장!
                dismiss();
            }
        });
        cancelBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: unity color hisotry 저장
                dismiss();
            }
        });

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
    public void onColorSelected(@ColorInt int color) {

        this.curColor = color;
        mListener.onButtonClicked(Integer.toHexString(curColor).toUpperCase());
    }

    public interface ColorBottomListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ColorBottomListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
