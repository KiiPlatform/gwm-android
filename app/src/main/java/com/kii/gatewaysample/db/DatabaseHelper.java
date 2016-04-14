package com.kii.gatewaysample.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kii.gatewaysample.GatewaySampleApplication;
import com.kii.gatewaysample.db.dao.OnboardedNodesDao;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "com.kii.gatewaysample.db";
    public static final int DATABASE_VERSION = 1;

    private static final DatabaseHelper helper = new DatabaseHelper();
    public static DatabaseHelper getInstance() {
        return helper;
    }

    private DatabaseHelper() {
        super(GatewaySampleApplication.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OnboardedNodesDao.SQL_CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
