package com.abbsolute.ma_livu.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbsolute.ma_livu.Community.CommuWriteActivity;
import com.abbsolute.ma_livu.FirebaseID;
import com.abbsolute.ma_livu.HomeActivity;
import com.abbsolute.ma_livu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup2Activity extends AppCompatActivity {

    private EditText et_nickname;
    private Button btn_next2;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        et_nickname=(EditText)findViewById(R.id.et_nickname);
        btn_next2=(Button)findViewById(R.id.btn_next2);

        btn_next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null){
                    Map<String,Object> userMap = new HashMap<>();
                    userMap.put(FirebaseID.Nickname,et_nickname.getText().toString());
                    firestore.collection(FirebaseID.user).document(email).set(userMap, SetOptions.merge());
                    Intent intent = new Intent(Signup2Activity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}
