package com.abbsolute.ma_livu.Community;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.io.Files.getFileExtension;

public class Commu_WriteFragment extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // 작성자UID를 가져오기 위해서 선언
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // 파이어스토어를 사용하기 위해서 선언
    private StorageReference storageReference=FirebaseStorage.getInstance().getReference();

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
    private ArrayList<Uri> image_list = new ArrayList<Uri>();
    private int image_turn=0;
    private ArrayList<String> image_temp = new ArrayList<String>();

    public Commu_WriteFragment() {}

    public Commu_WriteFragment(String email) {
        this.email = email;
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

        img1=view.findViewById(R.id.commu_img1);
        img2=view.findViewById(R.id.commu_img2);
        img3=view.findViewById(R.id.commu_img3);
        img4=view.findViewById(R.id.commu_img4);
        img5=view.findViewById(R.id.commu_img5);

        //사진 업로드드 눌렀을 때
        btn_image = (ImageButton) view.findViewById(R.id.btn_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
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
                    data.put(FirebaseID.category, category); //카테고리
                    data.put(FirebaseID.title, et_title.getText().toString()); // 제목
                    data.put(FirebaseID.content, et_content.getText().toString()); //내용
                    data.put(FirebaseID.commu_date, dateform.format(date.getTime())); // 작성시간
                    data.put(FirebaseID.Nickname,str_nickname); // 작성자 닉네임
                    data.put(FirebaseID.commu_like_count, like_count); //좋아요
                    data.put(FirebaseID.commu_save_count, save_count); //스크랩수
                    data.put(FirebaseID.commu_comment_count, comment_count); //덧글수

                    //파이어 스토리지 사진 올리기
                    for(Uri image:image_list){
                        uploadFile(image,image_turn);
                        image_turn++;
                    }

                    // 저장 위치 변경
                    firestore.collection(FirebaseID.Community).document(category)
                            .collection("sub_Community").document(et_title.getText().toString())
                            .set(data, SetOptions.merge());
                }
                ((HomeActivity) getActivity()).setFragment(50);
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

    }

    //사진 셋팅하기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE  && data != null && data.getData() != null) {
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
                for(int i = 0; i < 5; i++)
                {
                    if(i<clipData.getItemCount()){
                        urione =  clipData.getItemAt(i).getUri();
                        image_list.add(urione);
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
                            case 3:
                                img4.setImageURI(urione);
                            case 4:
                                img5.setImageURI(urione);
                        }
                    }
                }
            }else if(image !=null){
                img1.setImageURI(image);
            }
        }
    }


    private void uploadFile(Uri image, final int idx) {
            final StorageReference sRef = storageReference.child(
                        FirebaseID.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(String.valueOf(image)));
                sRef.putFile(image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //creating the upload object to store uploaded image details
                                        ImageUpload upload = new ImageUpload(uri.toString());
                                        final Map<String, Object> data = new HashMap<>();
                                        data.put("Uri"+idx,upload.getUrl());

                                        //adding an upload to firebase database
                                        firestore.collection(FirebaseID.Community).document(category)
                                                .collection("sub_Community").document(et_title.getText().toString())
                                                .set(data, SetOptions.merge());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }


    }

