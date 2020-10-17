package com.abbsolute.ma_livu.Community;

import android.content.ClipData;
import android.content.Context;
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
import android.widget.Toast;

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

import static com.google.common.io.Files.getFileExtension;

public class Commu_WriteFragment extends Fragment {

    private View view;
    private Context context;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // 작성자UID를 가져오기 위해서 선언
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // 파이어스토어를 사용하기 위해서 선언
    private StorageReference storageReference=FirebaseStorage.getInstance().getReference(); // 슽호리쥐~

    //카테고리 클릭
    private TextView category_eat,category_do,category_how;
    private String category;

    //작성한 글
    private EditText et_title,et_content;
    private ImageView img1,img2,img3,img4,img5;
    private ImageView[] img;
    private TextView[] expain;
    private ImageButton[] remove;
    private EditText commu_img_explain1,commu_img_explain2,commu_img_explain3,commu_img_explain4,commu_img_explain5;
    private ImageButton remove1,remove2,remove3,remove4,remove5;
    private static String email,str_nickname;
    private static int comment_count,save_count,like_count;

    //버튼
    private TextView btn_commu_upload;
    private ImageButton btn_back,btn_image;

    //날짜 받아오기
    private SimpleDateFormat dateform;
    private Calendar date;

    //사진 올리기에 필요한 변수들
    private Uri image,urione;
    private static final int IMAGE_REQUEST_CODE = 1888;
    public CommunityAdapter adapter;
    private ArrayList<Uri> image_list = new ArrayList<Uri>();
    private int image_turn=0;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commu_write_fragment, container, false);
        //하단 탭 바에있는 4개의 항목에 대해 이것을 수행하여 listener를 초기화한다
//        ((HomeActivity)getActivity()).setOnBackPressedListener(this);
        context=container.getContext();

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
        img = new ImageView[]{img1, img2, img3, img4, img5};
        commu_img_explain1=view.findViewById(R.id.commu_img_explain1);commu_img_explain1.setVisibility(view.INVISIBLE);
        commu_img_explain2=view.findViewById(R.id.commu_img_explain2);commu_img_explain2.setVisibility(view.INVISIBLE);
        commu_img_explain3=view.findViewById(R.id.commu_img_explain3);commu_img_explain3.setVisibility(view.INVISIBLE);
        commu_img_explain4=view.findViewById(R.id.commu_img_explain4);commu_img_explain4.setVisibility(view.INVISIBLE);
        commu_img_explain5=view.findViewById(R.id.commu_img_explain5);commu_img_explain5.setVisibility(view.INVISIBLE);
        expain = new TextView[]{commu_img_explain1, commu_img_explain2, commu_img_explain3, commu_img_explain4, commu_img_explain5};
        remove1=view.findViewById(R.id.remove1);remove1.setVisibility(view.INVISIBLE);
        remove2=view.findViewById(R.id.remove2);remove2.setVisibility(view.INVISIBLE);
        remove3=view.findViewById(R.id.remove3);remove3.setVisibility(view.INVISIBLE);
        remove4=view.findViewById(R.id.remove4);remove4.setVisibility(view.INVISIBLE);
        remove5=view.findViewById(R.id.remove5);remove5.setVisibility(view.INVISIBLE);
        remove= new ImageButton[]{remove1, remove2, remove3, remove4, remove5};


        //사진 업로드드 눌렀을 때
        btn_image = view.findViewById(R.id.btn_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image_list.size()== 5){
                    Toast.makeText(context, "사진은 최대 5장 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
                }
            }
        });

        ImageButton.OnClickListener ImageListener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.remove1:
                        image_list.remove(0);
                        break;
                    case R.id.remove2:
                        image_list.remove(1);
                        break;
                    case R.id.remove3:
                        image_list.remove(2);
                        break;
                    case R.id.remove4:
                        image_list.remove(3);
                        break;
                    case R.id.remove5:
                        image_list.remove(4);
                        break;
                }
                if(image_list.isEmpty()){
                    img1.setImageResource(0);
                    remove1.setVisibility(view.INVISIBLE);
                    commu_img_explain1.setVisibility(view.INVISIBLE);
                }else{
                    for(int i=0; i<image_list.size(); i++){
                        img[i].setImageURI(image_list.get(i));
                        remove[i].setVisibility(view.VISIBLE);
                        expain[i].setVisibility(view.VISIBLE);
                        if( i == (image_list.size()-1)) {
                            img[i+1].setImageResource(0);
                            remove[i+1].setVisibility(view.INVISIBLE);
                            expain[i+1].setVisibility(view.INVISIBLE);
                        }
                    }
                }
            }
        };

        remove1.setOnClickListener(ImageListener);
        remove2.setOnClickListener(ImageListener);
        remove3.setOnClickListener(ImageListener);
        remove4.setOnClickListener(ImageListener);
        remove5.setOnClickListener(ImageListener);


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
                    data.put(FirebaseID.Email, firebaseAuth.getCurrentUser().getEmail());//이메일
                    data.put(FirebaseID.category, category); //카테고리
                    data.put(FirebaseID.title, et_title.getText().toString()); // 제목
                    data.put(FirebaseID.content, et_content.getText().toString()); //내용
                    data.put(FirebaseID.commu_date, dateform.format(date.getTime())); // 작성시간
                    data.put(FirebaseID.Nickname,str_nickname); // 작성자 닉네임
                    data.put(FirebaseID.commu_like_count, like_count); //좋아요
                    data.put(FirebaseID.commu_save_count, save_count); //스크랩수
                    data.put(FirebaseID.commu_comment_count, comment_count); //덧글수
                    data.put("hot",false);//핫게시글 선정 여부

                    //파이어 스토리지 사진 올리기
                    for(Uri image:image_list){
                        uploadFile(image,image_turn);
                        image_turn++;
                    }

                    //이미지 설명 넣기
                    EditText[] commu_explain = {commu_img_explain1,commu_img_explain2,commu_img_explain3,commu_img_explain4,commu_img_explain5};
                    for(int i=0; i<5; i++){
                        if(!(commu_explain[i].getText().toString().equals(""))){
                            data.put((FirebaseID.commu_img_explain)+i,commu_explain[i].getText().toString());
                        }
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
        btn_back = view.findViewById(R.id.btn_back);
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
        if(requestCode == IMAGE_REQUEST_CODE ){
            image = data.getData();
            ClipData clipData = data.getClipData();
            if(clipData == null){
                urione =data.getData();
                image_list.add(urione);
                if (img1.getDrawable() == null) {
                    img1.setImageURI(urione);
                    commu_img_explain1.setVisibility(View.VISIBLE);
                    remove1.setVisibility(View.VISIBLE);
                }
            }else {
                for (int i = 0; i <clipData.getItemCount(); i++) {
                    if (i < clipData.getItemCount()) {
                        urione = clipData.getItemAt(i).getUri();
                        image_list.add(urione);
                        switch (i) {
                            case 0:
                                if (img1.getDrawable() == null) {
                                    img1.setImageURI(urione);
                                    commu_img_explain1.setVisibility(View.VISIBLE);
                                    remove1.setVisibility(View.VISIBLE);
                                    break;
                                }
                            case 1:
                                if (img2.getDrawable() == null) {
                                    img2.setImageURI(urione);
                                    commu_img_explain2.setVisibility(View.VISIBLE);
                                    remove2.setVisibility(View.VISIBLE);
                                    break;
                                }
                            case 2:
                                if (img3.getDrawable() == null) {
                                    img3.setImageURI(urione);
                                    commu_img_explain3.setVisibility(View.VISIBLE);
                                    remove3.setVisibility(View.VISIBLE);
                                    break;
                                }
                            case 3:
                                if (img4.getDrawable() == null) {
                                    img4.setImageURI(urione);
                                    commu_img_explain4.setVisibility(View.VISIBLE);
                                    remove4.setVisibility(View.VISIBLE);
                                    break;
                                }
                            case 4:
                                if (img5.getDrawable() == null) {
                                    img5.setImageURI(urione);
                                    commu_img_explain5.setVisibility(View.VISIBLE);
                                    remove5.setVisibility(View.VISIBLE);
                                }
                                break;
                        }
                    }else if (image != null) {
                        img1.setImageURI(image);
                    }
                }
            }
        }
    }


    //파이어 스토리지에 사진 올리기
    private void uploadFile(Uri image, final int idx) {
            final StorageReference sRef = storageReference.child(FirebaseID.STORAGE_PATH_UPLOADS+
                        et_title.getText().toString()+"/" + System.currentTimeMillis() + "." + getFileExtension(String.valueOf(image)));
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
                                        data.put(FirebaseID.Url+idx,upload.getUrl());

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



