package com.abbsolute.ma_livu.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Home.GuestBook.GuestBookFragment;
import com.abbsolute.ma_livu.R;

public class HomeFragment extends Fragment {

    private View view;
    private Button go_Todo;
    private Button go_GuestBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);

        go_GuestBook = (Button)view.findViewById(R.id.go_GuestBook);
        go_GuestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).setFragment(4);
            }
        });
        go_Todo=view.findViewById(R.id.go_Todo);
        go_Todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).setFragment(100);
            }
        });
        return view;
    }
}
