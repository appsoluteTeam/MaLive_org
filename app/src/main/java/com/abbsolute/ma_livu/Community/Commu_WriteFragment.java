package com.abbsolute.ma_livu.Community;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.MainActivity;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.w3c.dom.Text;
import java.util.HashMap;
import java.util.Map;

public class Commu_WriteFragment extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // 작성자UID를 가져오기 위해서 선언
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // 파이어스토어를 사용하기 위해서 선언

    private TextView category_eat;
    private TextView category_do;
    private TextView category_how;
    private String category;
    private EditText et_title;
    private EditText et_content;

    private Button btn_commu_upload;

    //날짜 받아오기
    private SimpleDateFormat dateform;
    private Calendar date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commu_write_fragment,container,false);

        et_title= view.findViewById(R.id.et_title);
        et_content= view.findViewById(R.id.et_content);

        category_eat=(TextView)view.findViewById(R.id.category_eat);
        category_do=(TextView)view.findViewById(R.id.category_do);
        category_how=(TextView)view.findViewById(R.id.category_how);

        //카테고리 선택되었을 때 클릭리스너
        category_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category ="what_eat";
                category_eat.setBackgroundResource(R.drawable.categort_select);
                category_do.setBackgroundResource(R.drawable.categort_basic);
                category_how.setBackgroundResource(R.drawable.categort_basic);

            }
        });
        category_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category ="what_do";
                category_do.setBackgroundResource(R.drawable.categort_select);
                category_eat.setBackgroundResource(R.drawable.categort_basic);
                category_how.setBackgroundResource(R.drawable.categort_basic);

            }
        });
        category_how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category ="how_do";
                category_how.setBackgroundResource(R.drawable.categort_select);
                category_do.setBackgroundResource(R.drawable.categort_basic);
                category_eat.setBackgroundResource(R.drawable.categort_basic);
            }
        });


        //저장하기 버튼을 눌렀을 때 파이어스토어로 저장
        btn_commu_upload = view.findViewById(R.id.btn_commu_upload);
        btn_commu_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {

                    // 게시글 작성 시간 받아오기
                    long now = System.currentTimeMillis();
                    dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    date = Calendar.getInstance();

                    Map<String,Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID,firebaseAuth.getCurrentUser().getUid()); // FirebaseID 라는 클래스에서 선언한 필드이름에 , 사용자 UID를 저장
                    data.put(FirebaseID.category,category);
                    data.put(FirebaseID.title,et_title.getText().toString()); // title 이란 필드이름으로 작성한 제목 저장
                    data.put(FirebaseID.content,et_content.getText().toString());
                    data.put(FirebaseID.commu_date, dateform.format(date.getTime()));
                  
                    // 저장 위치 변경
                    firestore.collection(FirebaseID.Community).document(category)
                            .collection("sub_Community").document(et_title.getText().toString())
                            .set(data, SetOptions.merge());
                }
                ((HomeActivity)getActivity()).setFragment(50);
            }
        });
        return view;
    }

}
