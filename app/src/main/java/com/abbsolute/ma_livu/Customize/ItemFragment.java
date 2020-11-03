package com.abbsolute.ma_livu.Customize;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.Home.HomeFragment;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Comment;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment implements AccessoryAdapter.ItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AccessoryAdapter itemRecyclerAdapter;
    private HomeFragment homeFragment;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    UnityItem[] sample = {
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

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
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
        View v = inflater.inflate(R.layout.customize_fragment_item, container, false);
        recyclerView = v.findViewById(R.id.item_recycler_view);
        layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        itemRecyclerAdapter = new AccessoryAdapter(sample);
        itemRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(itemRecyclerAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        SnapToBlock snapToBlock = new SnapToBlock(8);
        snapToBlock.attachToRecyclerView(recyclerView);
        homeFragment = (HomeFragment) getFragmentManager().findFragmentById(R.id.unity_frame);

        return v; // Inflate the layout for this fragment
    }

    @Override
    public void onItemClick(View view, UnityItem item) {
        Toast.makeText(getContext(),"item 클릭됨",Toast.LENGTH_LONG).show();
        //  mListener.onButtonClicked(item.getName());
        homeFragment.AssignEquipment(item);
        saveItem(item);
    }

    public void saveItem(UnityItem item){
        String id = firebaseAuth.getCurrentUser().getEmail();
        DocumentReference db = firestore.collection("Customize")
                .document(id);
        db.update("items", FieldValue.arrayUnion(item.getItemMap()));
    }

}