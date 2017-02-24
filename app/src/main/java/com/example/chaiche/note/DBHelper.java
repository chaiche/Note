package com.example.chaiche.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chaiche on 2017/2/13.
 */

public class DBHelper {


    SQLiteDatabase db;
    static final String db_name = "Info";
    private String tb_name = "";

    public DBHelper(Context context, String name) {
        tb_name = name;
        db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
    }

    public void open_tb(String createTable){
        String tmp = "CREATE TABLE IF NOT EXISTS " + tb_name + createTable;
        db.execSQL(tmp);
    }

    public void addData(ContentValues cv) {
        db.insert(tb_name, null, cv);
    }

    public Cursor getData(){

        Cursor c = db.rawQuery("SELECT * FROM "+tb_name, null);
        return c;
    }

    public void deleteData(int id){
        db.delete(tb_name, "_id" + "=" + id, null);
    }

    public void deleteTable(String tb){
        db.delete(tb,null,null);
    }


    public void closedb(){
        if(db.isOpen()) {
            db.close();
        }
    }
    public void update(int id,ContentValues cv){
        db.update(tb_name, cv, "_id" + "=" + id, null);
    }
}
