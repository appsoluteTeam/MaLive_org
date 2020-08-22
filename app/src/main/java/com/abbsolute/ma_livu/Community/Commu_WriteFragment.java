package com.abbsolute.ma_livu.Community;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.MainActivity;
import com.abbsolute.ma_livu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.w3c.dom.Text;
import java.util.HashMap;
import java.util.Map;

public class Commu_WriteFragment extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // 작성자UID를 가져오기 위해서 선언
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // 파이어스토어를 사용하기 위해서 선언

    //카테고리 클릭
    private TextView category_eat;
    private TextView category_do;
    private TextView category_how;
    private String category;

    //작성한 글
    private EditText et_title;
    private EditText et_content;

    //버튼
    private TextView btn_commu_upload;
    private ImageButton btn_back;
    private ImageButton btn_image;

    //날짜 받아오기
    private SimpleDateFormat dateform;
    private Calendar date;

    //사진
    private static final int IMAGE_REQUEST_CODE = 1888;
    private static final int REQUESTED_PERMISSION = 1002;
    private ImageView image_test;
    public CommunityAdapter adapter;
    private RecyclerView recycler_community;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<bringData> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commu_write_fragment, container, false);

        et_title = view.findViewById(R.id.et_title);
        et_content = view.findViewById(R.id.et_content);

        category_eat = (TextView) view.findViewById(R.id.category_eat);
        category_do = (TextView) view.findViewById(R.id.category_do);
        category_how = (TextView) view.findViewById(R.id.category_how);


        //카테고리 선택되었을 때 클릭리스너
        TextView.OnClickListener categoryListener = new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.category_eat: //뭐 먹지 카테고리 선택
                        category = "what_eat";
                        category_eat.setBackgroundResource(R.drawable.categort_select);
                        category_do.setBackgroundResource(R.drawable.categort_basic);
                        category_how.setBackgroundResource(R.drawable.categort_basic);
                        break;
                    case R.id.category_do: //뭐 하지 카테고리 선택
                        category = "what_do";
                        category_do.setBackgroundResource(R.drawable.categort_select);
                        category_eat.setBackgroundResource(R.drawable.categort_basic);
                        category_how.setBackgroundResource(R.drawable.categort_basic);
                        break;
                    case R.id.category_how: //어떻게 하지 카테고리 선택
                        category = "how_do";
                        category_how.setBackgroundResource(R.drawable.categort_select);
                        category_eat.setBackgroundResource(R.drawable.categort_basic);
                        category_do.setBackgroundResource(R.drawable.categort_basic);
                        break;
                }
            }
        };

        category_eat.setOnClickListener(categoryListener);
        category_do.setOnClickListener(categoryListener);
        category_how.setOnClickListener(categoryListener);


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

                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.documentID, firebaseAuth.getCurrentUser().getUid()); // FirebaseID 라는 클래스에서 선언한 필드이름에 , 사용자 UID를 저장
                    data.put(FirebaseID.category, category);
                    data.put(FirebaseID.title, et_title.getText().toString()); // title 이란 필드이름으로 작성한 제목 저장
                    data.put(FirebaseID.content, et_content.getText().toString());
                    data.put(FirebaseID.commu_date, dateform.format(date.getTime()));

                    // 저장 위치 변경
                    firestore.collection(FirebaseID.Community).document(category)
                            .collection("sub_Community").document(et_title.getText().toString())
                            .set(data, SetOptions.merge());
                }
                ((HomeActivity) getActivity()).setFragment(50);
            }
        });


        //사진 업로드드 눌렀을 때
        btn_image = (ImageButton) view.findViewById(R.id.btn_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        //뒤로가기 버튼 눌렀을 때
        btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).setFragment(50);
            }
        });
        return view;

//        if (ContextCompat.checkSelfPermission(
//                getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                //showInContextUI();
//            } else {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUESTED_PERMISSION);
//            }
//        }
    }

    //사진 올리기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),image);
                    image_test=(ImageView)view.findViewById(R.id.image_test);
                    image_test.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
