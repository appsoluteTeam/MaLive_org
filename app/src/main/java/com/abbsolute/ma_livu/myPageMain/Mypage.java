package com.abbsolute.ma_livu.myPageMain;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.abbsolute.ma_livu.R;
import com.abbsolute.ma_livu.title.titleActivity;

import androidx.appcompat.app.AppCompatActivity;

public class Mypage extends AppCompatActivity {

    public DBHelper dbhelper;
    private SQLiteDatabase db;
    TextView cleanNum;
    TextView recylingNum;
    TextView washNum;
    TextView etcNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbhelper = new DBHelper(getApplicationContext(),DBHelper.TABLE_NAME,null,1);

        try{
            db = dbhelper.getReadableDatabase();
        }catch(SQLException e){
            db = dbhelper.getWritableDatabase();
        }

        cleanNum = findViewById(R.id.cleanNum);
        recylingNum = findViewById(R.id.recylingNum);
        washNum = findViewById(R.id.washNum);
        etcNum = findViewById(R.id.etcNum);

        //다시 불러올때 ! 디비 값 받아서 불러오깅, 최초일땐 db있는지 검사부터..
        boolean exist;
        exist = checkTable(db);

        if(exist == true) {
            String[] columns = {"_id", "name", "count"};
            Cursor cursor = db.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null, null);

            int[] dbCount = new int[4]; //우선은 4으로 나중에는 동적배열?써야한당
            int i = 0;

            while (cursor.moveToNext()) {
                dbCount[i] = cursor.getInt(2);
                i++;
            }
            cursor.close();

            cleanNum.setText(Integer.toString(dbCount[0]));
            recylingNum.setText(Integer.toString(dbCount[1]));
            washNum.setText(Integer.toString(dbCount[2]));
            etcNum.setText(Integer.toString(dbCount[3]));
        }
    }

    public boolean checkTable(SQLiteDatabase db){
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name ='"+ DBHelper.TABLE_NAME + "'";
        Cursor cursor = db.rawQuery(sql , null);
        cursor.moveToFirst();

        if(cursor.getCount()>0){//테이블존재한다
            return true;
        }else{
            return false;
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.clean:
                int num = Integer.parseInt(cleanNum.getText().toString()) + 1;
                db.execSQL("INSERT OR REPLACE INTO " + DBHelper.TABLE_NAME + "(" + DBHelper.COL_ID + ", " + DBHelper.COL_NAME + ", " + DBHelper.COL_COUNT +
                        ") VALUES (1,'clean'," + num +");");
                cleanNum.setText(Integer.toString(num));
                break;
            case R.id.recycling:
                int num2 = Integer.parseInt(recylingNum.getText().toString()) + 1;
                db.execSQL("INSERT OR REPLACE INTO " + DBHelper.TABLE_NAME + "(" + DBHelper.COL_ID + ", " + DBHelper.COL_NAME + ", " + DBHelper.COL_COUNT +
                        ") VALUES (2,'recycling'," + num2 +");");
                recylingNum.setText(Integer.toString(num2));
                break;
            case R.id.wash:
                int num3 = Integer.parseInt(washNum.getText().toString()) + 1;
                db.execSQL("INSERT OR REPLACE INTO " + DBHelper.TABLE_NAME + "(" + DBHelper.COL_ID + ", " + DBHelper.COL_NAME + ", " + DBHelper.COL_COUNT +
                        ") VALUES (3,'wash'," + num3 +");");
                washNum.setText(Integer.toString(num3));
                break;
            case R.id.etc:
                int num4 = Integer.parseInt(etcNum.getText().toString()) + 1;
                db.execSQL("INSERT OR REPLACE INTO " + DBHelper.TABLE_NAME + "(" + DBHelper.COL_ID + ", " + DBHelper.COL_NAME + ", " + DBHelper.COL_COUNT +
                        ") VALUES (4,'etc'," + num4 +");");
                etcNum.setText(Integer.toString(num4));
                break;

            case R.id.go:
                String[] columns = {"_id", "name", "count"};
                Cursor cursor = db.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null, null);

                int[] dbCount2 = new int[4]; //우선은 4으로 나중에는 동적배열?써야한당 arrayList나..
                int i = 0;

                while (cursor.moveToNext()) {
                    dbCount2[i] = cursor.getInt(2);
                    i++;
                }
                cursor.close();

                Intent intent = new Intent(getApplicationContext(),result.class);
                intent.putExtra("result",dbCount2);
                startActivity(intent);
                break;

            case R.id.gotitle:
               Intent intent2 = new Intent(getApplicationContext(), titleActivity.class);
               startActivity(intent2);
               break;



        }
    }



}
