package com.abbsolute.ma_livu.myPageMain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    final static String DB_NAME = "win.db";
    public final static String TABLE_NAME = "win_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_COUNT = "count";

    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,null,1);
    }

    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COL_ID + " integer primary key, "+
                COL_NAME + " TEXT, " + COL_COUNT + " integer);";
        Log.d("sqlLOG",sql);
        db.execSQL(sql);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
