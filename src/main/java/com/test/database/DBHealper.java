package com.test.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHealper extends SQLiteOpenHelper {

    public static String NAME = "name";
    public static String ID = "id";
    public static String EMAIL = "email";
    public static String TABLE_NAME = "mytable";
    public static String DB_NAME = "MyDatabase";



    public DBHealper(Context context){
        super(context, DB_NAME,null, 1);

    }



    public DBHealper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHealper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME+" ("
                + ID + " integer primary key autoincrement,"
                + NAME + " text,"
                + EMAIL + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
