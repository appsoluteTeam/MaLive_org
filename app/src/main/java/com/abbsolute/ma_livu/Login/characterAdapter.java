package com.abbsolute.ma_livu.Login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.abbsolute.ma_livu.R;

public class characterAdapter extends PagerAdapter {

    private int[] images = {R.drawable.attendance1,
        R.drawable.attendance2,
        R.drawable.attendance3};
    private LayoutInflater inflater;
    private Context context;

    public characterAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.character_item, container, false);
        ImageView imageView = (ImageView)v.findViewById(R.id.char_img);

        imageView.setImageResource(images[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}
