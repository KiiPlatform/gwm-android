package com.kii.gatewaysample.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.kii.gatewaysample.db.DatabaseHelper;
import com.kii.gatewaysample.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

public class OnboardedNodesDao {

    public static class Endnode {
        public final String thingID;
        public final String vendorThingID;
        public Endnode(String thingID, String vendorThingID) {
            this.thingID = thingID;
            this.vendorThingID = vendorThingID;
        }
    }


    public static final String TABLE_NAME = "onboarded_nodes";
    public static final String SQL_CREATE_TABLE = "" +
            "create table onboarded_nodes (" +
            "    _id                INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    app_id             TEXT NOT NULL," +
            "    thing_id           TEXT NOT NULL," +
            "    vendor_thing_id    TEXT NOT NULL," +
            "    creation_time      INT NOT NULL" +
            ");";

    static final String SQL_SELECT_BY_APP = "select app_id, thing_id, vendor_thing_id from onboarded_nodes where app_id = ? order by creation_time desc";
    static final String SQL_INSERT = "insert into onboarded_nodes (app_id, thing_id, vendor_thing_id, creation_time) values (?, ?, ?, ?)";

    private final DatabaseHelper databaseHelper;
    public OnboardedNodesDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void insert(String appID, String thingID, String vendorThingID) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_INSERT);
        try {
            long now = System.currentTimeMillis();
            statement.bindString(1, appID);
            statement.bindString(2, thingID);
            statement.bindString(3, vendorThingID);
            statement.bindLong(4, now);
            statement.executeInsert();
        } finally {
            DatabaseUtils.closeQuietly(statement);
            DatabaseUtils.closeQuietly(db);
        }
    }
    public List<Endnode> selectByApp(String appID) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_BY_APP, new String[] {appID});
        List<Endnode> nodes = new ArrayList<Endnode>();
        try {
            boolean isEof = cursor.moveToFirst();
            while (isEof) {
                String thingID = cursor.getString(1);
                String vendorThingID = cursor.getString(2);
                Endnode node = new Endnode(thingID, vendorThingID);
                nodes.add(node);
                isEof = cursor.moveToNext();
            }
        } finally {
            DatabaseUtils.closeCursor(cursor);
            DatabaseUtils.closeQuietly(db);
        }
        return nodes;
    }
}
