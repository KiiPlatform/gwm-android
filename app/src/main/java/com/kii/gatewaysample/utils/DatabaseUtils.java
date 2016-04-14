package com.kii.gatewaysample.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteClosable;
import android.os.Build;

public class DatabaseUtils {
    public static void closeQuietly(SQLiteClosable input) {
        try {
            if (input != null) {
                input.releaseReference();
            }
        } catch (Exception ignore) {
        }
    }
    @SuppressWarnings("deprecation")
    public static void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    cursor.deactivate();
                } else {
                    cursor.close();
                }
            }
        } catch (Exception ignore) {
        }
    }
}
