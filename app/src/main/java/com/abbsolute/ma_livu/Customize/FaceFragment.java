package com.abbsolute.ma_livu.Customize;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.R;
import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.azoft.carousellayoutmanager.DefaultChildSelectionListener;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceFragment extends Fragment {

    UnityItem[] items = {
            new UnityItem(0,"모자",UnityItem.Face,0),
            new UnityItem(1,"헤드셋",UnityItem.Face,0),
            new UnityItem(3,"가방",UnityItem.Face,0),
            new UnityItem(4,"신발",UnityItem.Face,0),
            new UnityItem(5,"옷",UnityItem.Face,0),
            new UnityItem(6,"장갑",UnityItem.Face,0),
            new UnityItem(7,"짜장면",UnityItem.Face,0),
            new UnityItem(8,"귀도리",UnityItem.Face,0),
    };
    private FaceAdapter faceAdapter;
    private FaceFragment faceFragment;
    private HomeFragment homeFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FaceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceFragment newInstance(String param1, String param2) {
        FaceFragment fragment = new FaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        faceFragment = this;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.customize_face_fragment,container,false);
        homeFragment = (HomeFragment)getFragmentManager().findFragmentById(R.id.unity_frame);
        faceAdapter = new FaceAdapter(items);
        RecyclerView faceRecyclerview = v.findViewById(R.id.face_recyclerview);

        initRecyclerView(faceRecyclerview, new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false), faceAdapter);

        Button saveBtt = v.findViewById(R.id.save_color_btt);
        Button cancelBtt = v.findViewById(R.id.cancel_color_btt);
        saveBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.moveCameraToInit();
                getActivity().getSupportFragmentManager().beginTransaction().remove(faceFragment).commit();
            }
        });
        cancelBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                homeFragment.moveCameraToInit();
                getActivity().getSupportFragmentManager().beginTransaction().remove(faceFragment).commit();
            }
        });

        return v;
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager, final FaceAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(1);

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(adapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
        // enable center post touching on item and item click listener
        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
                final int position = recyclerView.getChildLayoutPosition(v);
                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }, recyclerView, layoutManager);

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    //       final int value = adapter.mPosition[adapterPosition];
/*
                    adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
                    adapter.notifyItemChanged(adapterPosition);
*/
                }

            }
        });
    }
}