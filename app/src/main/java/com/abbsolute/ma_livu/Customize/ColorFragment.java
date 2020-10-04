package com.abbsolute.ma_livu.Customize;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.R;
import com.thebluealliance.spectrum.SpectrumPalette;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ColorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorFragment extends Fragment implements SpectrumPalette.OnColorSelectedListener {


    private SpectrumPalette spectrumPalette;
    private ColorFragment colorFragment;
    @ColorInt
    int curColor;
    private HomeFragment homeFragment;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ColorFragment() {
        // Required empty public constructor
        colorFragment = this;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ColorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorFragment newInstance(String param1, String param2) {
        ColorFragment fragment = new ColorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.customize_color_fragment, container, false);
        homeFragment = (HomeFragment)getFragmentManager().findFragmentById(R.id.unity_frame);

        //TODO: 현재 컬러를 받아와야함
        spectrumPalette = v.findViewById(R.id.palette);
        Button saveBtt = v.findViewById(R.id.save_color_btt);
        Button cancelBtt = v.findViewById(R.id.cancel_color_btt);
        spectrumPalette.setOnColorSelectedListener(this);
        saveBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: unity color저장!
                homeFragment.saveColorChange(Integer.toHexString(curColor).toUpperCase());
                getActivity().getSupportFragmentManager().beginTransaction().remove(colorFragment).commit();
            }
        });
        cancelBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: unity color hisotry 저장
                homeFragment.cancelColorChange();
                getActivity().getSupportFragmentManager().beginTransaction().remove(colorFragment).commit();

            }
        });

        return v;
    }

    @Override
    public void onColorSelected(@ColorInt int color) {

        this.curColor = color;
    //    mListener.onColorClicked(Integer.toHexString(curColor).toUpperCase());
         homeFragment.AssignSkin(Integer.toHexString(curColor).toUpperCase());
    }

//    public interface ColorBottomListener {
//        void onColorClicked(String Color);
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        try {
//            mListener = (ColorBottom.ColorBottomListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement BottomSheetListener");
//        }
    }
}