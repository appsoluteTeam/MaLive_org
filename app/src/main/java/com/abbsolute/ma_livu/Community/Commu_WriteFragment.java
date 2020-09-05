package com.abbsolute.ma_livu.Community;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbsolute.ma_livu.BottomNavigation.HomeActivity;
import com.abbsolute.ma_livu.Firebase.FirebaseID;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Commu_WriteFragment extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // 작성자UID를 가져오기 위해서 선언
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // 파이어스토어를 사용하기 위해서 선언
    private StorageReference storage = FirebaseStorage.getInstance().getReference();

    //카테고리 클릭
    private TextView category_eat,category_do,category_how;
    private String category;

    //작성한 글
    private EditText et_title,et_content;
    private ImageView img1,img2,img3,img4,img5;
    private static String email;
    private static String str_nickname;

    //버튼
    private TextView btn_commu_upload;
    private ImageButton btn_back,btn_image;

    //날짜 받아오기
    private SimpleDateFormat dateform;
    private Calendar date;

    //사진
    private Uri image;
    private Uri urione;
    private static final int IMAGE_REQUEST_CODE = 1888;
    public CommunityAdapter adapter;
    private ArrayList<String> image_list = new ArrayList<String>();

    public Commu_WriteFragment() {}

    public Commu_WriteFragment(String email) {
        Commu_WriteFragment.email = email;
        Log.d("email",email);

        /*대표칭호 정보 myPage firestore에서 가져와서 category,index 변수에 저장*/
        //TODO: 데이터 가져오는걸 onCreateView나 onCreate에서 하면 적용이 다른 함수들보다 느리게 됨 스레드문제인가?

        /*user firestore에서 닉네임 정보 가져오기 */
        firestore.collection(FirebaseID.user).document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 컬렉션 내의 document에 접근
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> shot = document.getData();
                                str_nickname  = shot.get(FirebaseID.Nickname).toString();
                                Log.d("TitleFragment", "user nickname Get 완료");

                            } else {
                                Log.d("TitleFragment", "No such document");
                            }
                        } else {
                            Log.d("TitleFragment", "get failed with ", task.getException());
                        }
                    }
                });
    }



    private static int comment_count;
    private static int save_count;
    private static int like_count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commu_write_fragment, container, false);

        et_title = view.findViewById(R.id.et_title);
        et_content = view.findViewById(R.id.et_content);

        category_eat = view.findViewById(R.id.category_eat);
        category_do = view.findViewById(R.id.category_do);
        category_how = view.findViewById(R.id.category_how);


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

        img1=view.findViewById(R.id.commu_img1);
        img2=view.findViewById(R.id.commu_img2);
        img3=view.findViewById(R.id.commu_img3);
        img4=view.findViewById(R.id.commu_img4);
        img5=view.findViewById(R.id.commu_img5);

        //사진 업로드드 눌렀을 때
        btn_image = view.findViewById(R.id.btn_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        //저장하기 버튼을 눌렀을 때 파이어스토어로 저장
        btn_commu_upload = view.findViewById(R.id.btn_commu_upload);
        btn_commu_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {

                    like_count  = 0;
                    save_count = 0;
                    comment_count = 0;

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

                    data.put(FirebaseID.Nickname,str_nickname);
                    data.put(FirebaseID.Commu_image_URI,image_list);

                    // 좋아요, 저장, 댓글
                    data.put(FirebaseID.commu_like_count, like_count);
                    data.put(FirebaseID.commu_save_count, save_count);
                    data.put(FirebaseID.commu_comment_count, comment_count);


                    // 저장 위치 변경
                    firestore.collection(FirebaseID.Community).document(category)
                            .collection("sub_Community").document(et_title.getText().toString())
                            .set(data, SetOptions.merge());
                }
                ((HomeActivity) getActivity()).setFragment(50);
            }
        });

        //뒤로가기 버튼 눌렀을 때
        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).setFragment(50);
            }
        });
        return view;

    }

    //사진 올리기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE) {

            //기존 이미지 지우기
            img1.setImageResource(0);
            img2.setImageResource(0);
            img3.setImageResource(0);
            img4.setImageResource(0);
            img5.setImageResource(0);

            //데이터 가져오기
            image = data.getData();
            ClipData clipData = data.getClipData();

            if (data != null) {
                for(int i = 0; i < 3; i++)
                {
                    if(i<clipData.getItemCount()){
                        urione =  clipData.getItemAt(i).getUri();
                        image_list.add(urione.toString());
                        switch (i){
                            case 0:
                                img1.setImageURI(urione);
                                break;
                            case 1:
                                img2.setImageURI(urione);
                                break;
                            case 2:
                                img3.setImageURI(urione);
                                break;
                        }
                    }
                }
            }else if(image !=null){
                img1.setImageURI(image);
            }
        }
    }

    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        Log.d("Commu_WriteFragment", "image");
        if (image != null) {
            StorageReference riversRef = storage.child("images/rivers.jpg");
            riversRef.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        } else {
            Log.d("Commu_WriteFragment", "사진을 선택안했어용");
        }
    }
}
