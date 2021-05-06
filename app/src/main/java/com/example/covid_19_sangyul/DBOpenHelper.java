package com.example.covid_19_sangyul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper {

    private static final String DATABASE_NAME = "sangyul.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

//    public static final String City = "City";
//    public static final String stdDay = "stdDay";
//    public static final String overFlowCnt = "overFlowCnt";
//    public static final String localOccCnt = "localOccCnt";
//    public static final String isolIngCnt = "isolIngCnt";
//    public static final String defCnt = "defCnt";
//    public static final String deathCnt = "deathCnt";

    //값 insert 구문
    public long insertColumn(String City, String stdDay, String overFlowCnt, String localOccCnt, String isolIngCnt, String defCnt, String deathCnt){
        ContentValues values = new ContentValues();
        values.put(DB.CreateDB.City, City);
        values.put(DB.CreateDB.stdDay, stdDay);
        values.put(DB.CreateDB.overFlowCnt, overFlowCnt);
        values.put(DB.CreateDB.localOccCnt, localOccCnt);
        values.put(DB.CreateDB.isolIngCnt, isolIngCnt);
        values.put(DB.CreateDB.defCnt, defCnt);
        values.put(DB.CreateDB.deathCnt, deathCnt);
        return mDB.insert(DB.CreateDB.TABLENAME, null, values);
    }
    
    //값 select 구문
    public Cursor selectColumns(){
        return mDB.query(DB.CreateDB.TABLENAME, null,null,null,null,null,null);
    }
    
    //값 update 구문
    public boolean updateColumn(long id, String City, String stdDay, String overFlowCnt, String localOccCnt, String isolIngCnt, String defCnt, String deathCnt){
        ContentValues values = new ContentValues();
        values.put(DB.CreateDB.City, City);
        values.put(DB.CreateDB.stdDay, stdDay);
        values.put(DB.CreateDB.overFlowCnt, overFlowCnt);
        values.put(DB.CreateDB.localOccCnt, localOccCnt);
        values.put(DB.CreateDB.isolIngCnt, isolIngCnt);
        values.put(DB.CreateDB.defCnt, defCnt);
        values.put(DB.CreateDB.deathCnt, deathCnt);
        return mDB.update(DB.CreateDB.TABLENAME, values, "_id=" + id, null) > 0; //특정 행(id)만 업데이트
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DB.CreateDB.TABLENAME, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(DB.CreateDB.TABLENAME, "_id="+id, null) > 0;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        //생성자
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //테이블 생성
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DB.CreateDB.CREATE_TABLE); //COVID-19 테이블 생성
        }

        //버전업그레이드 시 이전버전을 지우고 새 버전 생성
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DB.CreateDB.TABLENAME);
            onCreate(db);
        }
    }

    public DBOpenHelper(Context context){
        this.mCtx = context;
    }


    public DBOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase(); //DB를 읽고 쓸 수 있게 해줌
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM COVID19 ORDER BY " + sort + ";", null);
        return c;
    }
}
