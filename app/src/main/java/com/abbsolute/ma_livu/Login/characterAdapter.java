package com.abbsolute.ma_livu.Login;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.abbsolute.ma_livu.R;

import static com.abbsolute.ma_livu.R.drawable.sky_line;

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
        TextView textView=(TextView)v.findViewById(R.id.char_txt);
        imageView.setImageResource(images[position]);


        String text;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position ==0){
                    String text="뫄뫄를 선택했습니다.";
                    Log.d("캐릭터", String.valueOf(position));
                    textView.setText(text);
                }else if(position==1){
                    String text="므므를 선택했습니다.";
                    textView.setText(text);
                }else{
                    String text=("믜믜를 선택했습니다.");
                    textView.setText(text);
                }
            }

        });

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}
