package com.abbsolute.ma_livu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import static java.sql.Types.NULL;

public class AppHelper {//db 쿼리문 다루는 클래스 delete, select, update, insert
    private static final String TAG = "AppHelper";
    private static SQLiteDatabase database;
    public static RequestQueue requestQueue;
    private static String createTableToDoInfoSql="create table if not exists todoInfo"+
            "("+
            "_id integer PRIMARY KEY autoincrement, " +
            "content text, "+
            "detailcontent text," +
            "dates text, "+
            "d_day text "+
            ")";
    private static String SQL="drop table todoInfo";
    public static void openDatabase(Context context, String databaseName, int version) {
        println("openDatabase 호출됨");
        // 헬퍼이용
        DatabaseHelper helper = new DatabaseHelper(context, databaseName, null, version);
        database = helper.getWritableDatabase();
        createTable(database, "todoInfo");
        // requestMovieList 안에서 나머지도 요청하도록 하였음.
        //  requestMovieList(database);
    }

    private static void createTable(SQLiteDatabase db, String tableName) {
        println("createTable 호출됨 : " + tableName);

        if (db != null) {
            if (tableName.equals("todoInfo")) {
                db.execSQL(createTableToDoInfoSql);
                println("movieInfo 테이블 생성 요청됨.");
            }
        } else {
            println("데이터베이스가 없습니다. 먼저 만들어 주세요.");
        }
    }
    public static void insertData(String tableName, ToDoInfo toDoInfo) {
        if (database != null) {
            String sql = "insert into " + tableName + "(content,detailcontent,dates,d_day) " +
                    "values(?, ?,?,?)";
            Object[] params = {toDoInfo.content,toDoInfo.detailContent,toDoInfo.dates,toDoInfo.dDay};
            database.execSQL(sql, params);
            println("할일 정보들 추가함.");
        } else {
            println("먼저 데이터베이스를 오픈하세요.");
        }
    }
    public static void updateData(Context context,String tableName,ToDoInfo toDoInfo,String contents){
        DatabaseHelper helper = new DatabaseHelper(context, "todo.db", null, 14);
        database = helper.getWritableDatabase();
        String sql="update "+ tableName +" set content='"+toDoInfo.getContent()+"',detailcontent='"+toDoInfo.getDetailContent()+"',dates='"+toDoInfo.getDates()+"'" +
                ",d_day='"+toDoInfo.getdDay()+"'"+
                 " where content='"+contents+"';";
        database.execSQL(sql);
    }
    public static ArrayList<ToDoInfo> deleteData(Context context, String tableName, int id, ToDoInfo toDoInfo){
        ArrayList<ToDoInfo> toDoInfos=new ArrayList<>();
        if(database!=null){
            id++;
            String tmp=Integer.toString(id);
            // 헬퍼이용
            DatabaseHelper helper = new DatabaseHelper(context, "todo.db", null, 14);
            database = helper.getWritableDatabase();
            String sql="delete from "+ tableName +" where content='" + toDoInfo.getContent() + "';";
            database.execSQL(sql);
            println(sql);
            Cursor cursor = database.rawQuery(sql, null);
            println("조회된 데이터 개수 : " + cursor.getCount());
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String content=cursor.getString(0);
                String detailContent=cursor.getString(1);
                String dates=cursor.getString(2);
                String dDay=cursor.getString(3);
                toDoInfos.add(new ToDoInfo(content,detailContent,dates,dDay));
            }
            cursor.close();
        }
        return toDoInfos;
    }
    public static ArrayList<ToDoInfo> selectTodoInfo(String tableName) {
        println("selectToDoInfo() 호출됨.");
        ArrayList<ToDoInfo> toDoInfos=new ArrayList<>();
        if (database != null) {
            String sql = "select content,detailcontent,dates,d_day " +
                    "from " + tableName;
            Log.d("Database",sql);
            Cursor cursor = database.rawQuery(sql, null);
            println("조회된 데이터 개수 : " + cursor.getCount());
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String content = cursor.getString(0);
                String detailContent = cursor.getString(1);
                String dates=cursor.getString(2);
                String dDay=cursor.getString(3);
                toDoInfos.add(new ToDoInfo(content,detailContent,dates,dDay));
            }
            cursor.close();
        }
        return toDoInfos;
    }
    public static void println(String data) {
        Log.d(TAG, data);
    }
    static class DatabaseHelper extends SQLiteOpenHelper{
        Context context;
        public DatabaseHelper(@Nullable Context context, @Nullable String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.context = context;

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context);
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("onCreate() 호출됨");
            sameTasks(db);
        }

        private void sameTasks(SQLiteDatabase db) {
            createTable(db, "todoInfo");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("onUpgrade() 호출됨: " + oldVersion + "," + newVersion);
            if (newVersion > oldVersion) {
                db.execSQL("drop table if exists todoInfo");
                println("테이블 삭제함");
                sameTasks(db);
            }
        }
    }
}
