package com.ascomp.database_prac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database1 extends SQLiteOpenHelper {

    static Context context;
    final String Database_name="";
    static String Table_User="user";

    private final String create_user_table= "CREATE TABLE "+ Table_User +
            "( id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name varchar(50) NOT NULL,"+
            "password varchar(50) NOT NULL)";


    public Database1(Context context) {
        super(context, "Database_prac", null, 1);
        this.context=context;
        Log.v("ttt", "Database Created");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_user_table);
        Log.v("tttt ", "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static Cursor getData(){
        SQLiteDatabase db= new Database1(context).getReadableDatabase();
         Cursor cursor= db.query(Table_User,null, null,null,null, null, null, null);
         return cursor;
    }
    public Long insertData(ContentValues values){
        SQLiteDatabase db=new Database1(context).getWritableDatabase();
        return db.insert(Table_User,null, values);
    }
     public 
}
