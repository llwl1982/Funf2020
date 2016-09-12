package edu.mit.media.funf.util;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import edu.mit.media.funf.storage.FileArchive;

/**
 * Created by Rocky on 16/9/12.
 */
public class DBUtils {

    public synchronized static void deleteDBFile(FileArchive archive) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        // TODO: add check to make sure this is not empty
        File dbFile = new File(db.getPath());
        DatabaseManager.getInstance().closeDatabase();
        if (archive.add(dbFile)) {
            dbFile.delete();
        }
    }

    public synchronized static long insertOrThrow(String table, String nullColumnHack, ContentValues values) throws SQLException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        return db.insertOrThrow(table, nullColumnHack, values);
    }
}
