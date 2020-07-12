package com.abbsolute.ma_livu.Community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.abbsolute.ma_livu.HomeActivity;
import com.abbsolute.ma_livu.Login.LoginActivity;
import com.abbsolute.ma_livu.R;

public class HotCommuActivity extends AppCompatActivity {

    private Button btn_more_text;
    private Toolbar hot_commu_toolbar;
    private SearchView hot_commu_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_commu);

        hot_commu_toolbar = (Toolbar)findViewById(R.id.hot_commu_toolbar);
        setSupportActionBar(hot_commu_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본텍스트 보여주기 x

        hot_commu_search =findViewById(R.id.hot_commu_search);
        hot_commu_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HotCommuActivity.this,query,Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        btn_more_text=(Button)findViewById(R.id.btn_more_text);
        btn_more_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotCommuActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });
    }
}
