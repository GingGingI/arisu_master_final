package com.chkk.arisong_arisu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tlqkf on 2017-10-30.
 */

public class saveData  {

    private static final String DATABASE_NAME = "first_DB";
    private static final String DATABASE_TABLE = "userSetting";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE="CREATE TABLE userSetting" +
            " (    ID    INTEGER PRIMARY    KEY AUTOINCREMENT, TEXTSIZE INTEGER,   PUSH INTEGER,    API INTEGER,    AROUND INTEGER)";


    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        /**
         *
         * @param db         The database.
         * @param oldVersion The old database version.
         * @param newVersion The new database version.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public void open() throws SQLException {

        mDbHelper = new DatabaseHelper(mCtx);

        mDb = mDbHelper.getWritableDatabase();
    }

    public saveData(Context ctx) {
        this.mCtx = ctx;
    }

    public void close() {
        mDbHelper.close();
    }

    public long insert(int textSize, int push, int API, int around) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("TEXTSIZE", textSize);
        insertValues.put("PUSH", push);
        insertValues.put("API", API);
        insertValues.put("AROUND", around);
        return mDb.insert(DATABASE_TABLE, null, insertValues);
    }

    public long updatetextSize(String id,int textSize){

        ContentValues updateValues = new ContentValues();
        updateValues.put("TEXTSIZE", textSize);
        return mDb.update(DATABASE_TABLE, updateValues, "ID" + "=?", new String[]{id});
    }
    public long updatetextAPI(String id,int API){

        ContentValues updateValues = new ContentValues();
        updateValues.put("API", API);
        return mDb.update(DATABASE_TABLE, updateValues, "ID" + "=?", new String[]{id});
    }
    public long updatetextpush(String id,int push){

        ContentValues updateValues = new ContentValues();
        updateValues.put("PUSH", push);
        return mDb.update(DATABASE_TABLE, updateValues, "ID" + "=?", new String[]{id});
    }
    public long updatearound(String id,int around){

        ContentValues updateValues = new ContentValues();
        updateValues.put("AROUND", around);
        return mDb.update(DATABASE_TABLE, updateValues, "ID" + "=?", new String[]{id});
    }
    public Cursor AllRows() {
        return mDb.query(DATABASE_TABLE, null, null, null, null, null, null);
    }

    public boolean ChkDB(){
        mDb = mDbHelper.getReadableDatabase();

        Cursor cursor = mDb.rawQuery("Select * from userSetting", null);

        if (cursor.moveToFirst()) {
            return true;
        }else{
            return false;
        }
    }
}