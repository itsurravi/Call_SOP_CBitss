package com.codrox.messagetemplate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper {

    public final static String DB_NAME = "Call_DB";

    //Templates_Table
    public final static String TEMP_TABLE = "Templates";
    public final static String TEMP_ID = "id";
    public final static String TEMP_MSG = "message";
    public final static String TEMP_TITLE = "title";
    public final static String TEMP_CAT = "category";

    //CallData_Table
    public final static String CALL_TABLE = "Call_Data";
    public final static String CALL_ID = "id";
    public final static String CALL_NUMBER = "number";
    public final static String CALL_NAME = "name";
    public final static String CALL_MSG = "text_message";
    public final static String CALL_WAP = "whatsapp_message";
    public final static String CALL_DATE = "date";
    public final static String CALL_STATUS = "status";
    public final static String CALL_AUDIO = "audio";
    public final static String CALL_AUDIO_STATUS = "audio_status";

    //Remarks_Table
    public final static String REMARKS_TABLE = "remarks_table";
    public final static String REMARKS_ID = "id";
    public final static String REMARKS = "remarks";
    public final static String NUMBER = "number";
    public final static String REMARKS_DATE = "remarks_date";
    public final static String REMARKS_STATUS = "status";

    //Tags_Table
    public final static String TAG_TABLE = "tags_table";
    public final static String TAG_ID = "id";
    public final static String TAG_NUMBER = "number";
    public final static String TAG_NAME = "tag";
    public final static String TAG_STATUS = "status";

    private final static int DB_VERSION = 1;

    Context c;

    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tag_table = "CREATE TABLE " + TAG_TABLE + "("
                + TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TAG_NUMBER + " VARCHAR, "
                + TAG_NAME + " VARCHAR);";

        String sql1 = "CREATE TABLE " + TEMP_TABLE + "("
                + TEMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TEMP_CAT + " VARCHAR, "
                + TEMP_TITLE + " VARCHAR, "
                + TEMP_MSG + " VARCHAR);";

        String sql2 = "CREATE TABLE " + CALL_TABLE + "("
                + CALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CALL_NUMBER + " VARCHAR, "
                + CALL_NAME + " VARCHAR, "
                + CALL_MSG + " TEXT, "
                + CALL_WAP + " TEXT, "
                + CALL_DATE + " VARCHAR, "
                + CALL_AUDIO + " VARCHAR, "
                + CALL_AUDIO_STATUS + " VARCHAR, "
                + CALL_STATUS + " VARCHAR);";

        String remark_table = "CREATE TABLE " + REMARKS_TABLE + "("
                + REMARKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NUMBER + " VARCHAR, "
                + REMARKS + " VARCHAR, "
                + REMARKS_DATE + " VARCHAR);";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(remark_table);
        db.execSQL(tag_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Tag Functions
    public void insertTag(String num, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TAG_NUMBER, num);
        cv.put(TAG_NAME, tag);

        db.insert(TAG_TABLE, null, cv);
    }

    public Cursor readTag(String num) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TAG_TABLE+" WHERE "+TAG_NUMBER+"='"+num+"';";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    //CallStatus Functions
    public void insertCall(String number, String msg, String wap, String date, String status, String name,
                           String audio, String audio_status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_NUMBER, number);
        cv.put(CALL_MSG, msg);
        cv.put(CALL_WAP, wap);
        cv.put(CALL_DATE, date);
        cv.put(CALL_STATUS, status);
        cv.put(CALL_AUDIO, audio);
        cv.put(CALL_NAME, name);
        cv.put(CALL_AUDIO_STATUS, audio_status);

        db.insert(CALL_TABLE, null, cv);
    }

    public Cursor readAllCalls() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CALL_TABLE + ";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor readDistinctCalls() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+CALL_TABLE+" WHERE "+CALL_ID+" IN (SELECT MAX("+CALL_ID+") FROM "+CALL_TABLE+" GROUP BY "+CALL_NUMBER+");";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor readNumberData(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+CALL_TABLE+" WHERE "+CALL_NUMBER+"='"+number+"';";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public void updateCallName(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_NAME, name);

        db.update(CALL_TABLE, cv, CALL_NUMBER+"=?", new String[]{number});
    }

    public void updateCallText(String id, String name, String msg, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_MSG, msg);
        cv.put(CALL_NAME, name);
        cv.put(CALL_STATUS, status);

        db.update(CALL_TABLE, cv, CALL_ID+"=?", new String[]{id});
    }

    public void updateCallWap(String id, String name, String msg, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_WAP, msg);
        cv.put(CALL_STATUS, status);
        cv.put(CALL_NAME, name);
        db.update(CALL_TABLE, cv, CALL_ID+"=?", new String[]{id});
    }

    //Templates Functions
    public void insertTemplate(String message, String title, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TEMP_MSG, message);
        cv.put(TEMP_TITLE, title);
        cv.put(TEMP_CAT, category);

        db.insert(TEMP_TABLE, null, cv);
    }

    public Cursor readAllTemp() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TEMP_TABLE + ";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public void updateTemp(String id, String msg, String title, String cat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TEMP_MSG, msg);
        cv.put(TEMP_TITLE, title);
        cv.put(TEMP_CAT, cat);

        db.update(TEMP_TABLE, cv, TEMP_ID+"=?", new String[]{id});
    }

    public void deleteTemp(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEMP_TABLE, TEMP_ID+"=?", new String[]{id});
    }

    //Remarks Table
    public void insertRemark(String number, String remark, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NUMBER, number);
        cv.put(REMARKS, remark);
        cv.put(REMARKS_DATE, date);

        db.insert(REMARKS_TABLE, null, cv);
    }

    public Cursor readRemarks(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + REMARKS_TABLE + " WHERE "+NUMBER+"='"+number+"';";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public boolean updateStatus(int id,String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_STATUS, status);
        db.update(CALL_TABLE, contentValues, CALL_ID+ "=" + id, null);
        db.close();
        return true;
    }


    //unsynced data
    public Cursor getUnsyncedCalls() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CALL_TABLE + " WHERE " + CALL_AUDIO_STATUS + " = '"+Constants.offline+"';";
        Log.d("mydatao",sql);
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedRemarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + REMARKS_TABLE + " WHERE " + REMARKS_STATUS + " = "+Constants.offline+";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedTags() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TAG_TABLE + " WHERE " + TAG_STATUS + " = "+Constants.offline+";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}
