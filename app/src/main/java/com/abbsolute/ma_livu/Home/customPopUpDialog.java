package com.abbsolute.ma_livu.Home;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class customPopUpDialog extends Dialog {
    public customPopUpDialog(@NonNull Context context) {
        super(context);
    }

    public customPopUpDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected customPopUpDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
