package com.abbsolute.ma_livu.Alarm;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.abbsolute.ma_livu.R;

import org.w3c.dom.Text;

public class PopupFragment extends DialogFragment implements View.OnClickListener{

    public static final String TAG_EVENT_DIALOG = "dialog_event";

    public PopupFragment() { }
    public static PopupFragment getInstance() {
        PopupFragment e = new PopupFragment();
        return e;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_title, container);

        Bundle bundle = getArguments();
        String popupKey = bundle.getString("popup");
        switch (popupKey){
            case "popup_title" :
                v = inflater.inflate(R.layout.popup_title, container);
                break;
            case "popup_gettoll":
                v = inflater.inflate(R.layout.popup_gettoll, container);
                int gettoll = bundle.getInt("toll");
                TextView toll = (TextView) v.findViewById(R.id.toll);
                toll.setText(String.valueOf(gettoll));
                break;
            case "popup_checkin":
                v = inflater.inflate(R.layout.popup_checkin, container);
                break;
        }

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(180, 250);

        Button btn_popup_close = (Button) v.findViewById(R.id.btn_popup_close);
        btn_popup_close.setOnClickListener(this);
     return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
