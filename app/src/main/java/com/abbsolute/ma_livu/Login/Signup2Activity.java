package com.abbsolute.ma_livu.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.abbsolute.ma_livu.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signup2Activity extends AppCompatActivity {

    private EditText et_nickname;
    private Button btn_next2;
    private String nickname="";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        et_nickname=(EditText)findViewById(R.id.et_nickname);

    }
}
