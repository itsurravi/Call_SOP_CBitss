package com.codrox.messagetemplate.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codrox.messagetemplate.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                + TAG_STATUS + " VARCHAR, "
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
                + CALL_AUDIO + " TEXT, "
                + CALL_AUDIO_STATUS + " VARCHAR, "
                + CALL_STATUS + " VARCHAR);";

        String remark_table = "CREATE TABLE " + REMARKS_TABLE + "("
                + REMARKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NUMBER + " VARCHAR, "
                + REMARKS + " VARCHAR, "
                + REMARKS_STATUS + " VARCHAR, "
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
    public void insertTag(String num, String tag, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TAG_NUMBER, num);
        cv.put(TAG_NAME, tag);
        cv.put(TAG_STATUS, status);

        db.insert(TAG_TABLE, null, cv);
    }

    public Cursor readTag(String num) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TAG_TABLE + " WHERE " + TAG_NUMBER + "='" + num + "';";
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
        String sql = "SELECT * FROM " + CALL_TABLE + " WHERE " + CALL_ID + " IN (SELECT MAX(" + CALL_ID + ") FROM " + CALL_TABLE + " GROUP BY " + CALL_NUMBER + ");";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor readNumberData(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CALL_TABLE + " WHERE " + CALL_NUMBER + "='" + number + "';";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public boolean updateAudioStatus(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_AUDIO_STATUS, Constants.online);
        db.update(CALL_TABLE, contentValues, CALL_ID + "=?", new String[]{id});
        db.close();
        return true;
    }

    public boolean updateAudioPath(String id, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_AUDIO, path);
        db.update(CALL_TABLE, contentValues, CALL_ID + "=?", new String[]{id});
        db.close();
        return true;
    }

    public void updateCallName(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_NAME, name);

        db.update(CALL_TABLE, cv, CALL_NUMBER + "=?", new String[]{number});
    }

    public void updateCallText(String id, String name, String msg, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_MSG, msg);
        cv.put(CALL_NAME, name);
        cv.put(CALL_STATUS, status);

        db.update(CALL_TABLE, cv, CALL_ID + "=?", new String[]{id});
        db.close();
    }

    public void updateCallWap(String id, String name, String msg, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CALL_WAP, msg);
        cv.put(CALL_STATUS, status);
        cv.put(CALL_NAME, name);
        db.update(CALL_TABLE, cv, CALL_ID + "=?", new String[]{id});
        db.close();
    }

    //Templates Functions
    public void insertTemplate(String message, String title, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TEMP_MSG, message);
        cv.put(TEMP_TITLE, title);
        cv.put(TEMP_CAT, category);

        db.insert(TEMP_TABLE, null, cv);
        db.close();
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

        db.update(TEMP_TABLE, cv, TEMP_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteTemp(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEMP_TABLE, TEMP_ID + "=?", new String[]{id});
        db.close();
    }

    //Remarks Table
    public void insertRemark(String number, String remark, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NUMBER, number);
        cv.put(REMARKS, remark);
        cv.put(REMARKS_DATE, date);
        cv.put(REMARKS_STATUS, status);

        db.insert(REMARKS_TABLE, null, cv);
        db.close();
    }

    public Cursor readRemarks(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + REMARKS_TABLE + " WHERE " + NUMBER + "='" + number + "';";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    //unsynced data
    public Cursor getUnsyncedCalls() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CALL_TABLE + " WHERE " + CALL_AUDIO_STATUS + "='" + Constants.offline + "';";
        Log.d("mydatao", sql);
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedRemarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + REMARKS_TABLE + " WHERE " + REMARKS_STATUS + "='" + Constants.offline + "';";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Cursor getUnsyncedTags() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TAG_TABLE + " WHERE " + TAG_STATUS + "='" + Constants.offline + "';";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public boolean updateRemarks(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REMARKS_STATUS, Constants.online);
        db.update(REMARKS_TABLE, contentValues, REMARKS_ID + "=?", new String[]{id});
        db.close();
        return true;
    }

    public boolean updateTags(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_STATUS, Constants.online);
        db.update(TAG_TABLE, contentValues, TAG_ID + "=?", new String[]{id});
        db.close();
        return true;
    }

    public void deleteOldRecord(String currentDate) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String date = getDate(currentDate);
            String sql = "select * from " + CALL_TABLE + " where " + CALL_DATE + "<" + date+";";
            Log.d("SQL_QUERY", sql);
            Cursor c = db.rawQuery(sql, null);

            List<String> list = new ArrayList<>();
            c.moveToFirst();

            do {
                try
                {
                    list.add(c.getString(c.getColumnIndex(CALL_NUMBER)));
                    deleteFile(c.getString(c.getColumnIndex(CALL_AUDIO)));
                }
                catch(Exception e)
                {

                }
            }
            while (c.moveToNext());

            c.close();

            Set<String> set = new HashSet<>(list);

            List<String> uniqueNumbers = new ArrayList<>();

            uniqueNumbers.addAll(set);

            db.delete(CALL_TABLE, CALL_DATE + " < "+date,null);

            for (String num : uniqueNumbers) {
                db.delete(REMARKS_TABLE, NUMBER + "=?", new String[]{num});
                db.delete(TAG_TABLE, TAG_NUMBER + "=?", new String[]{num});
            }

            db.close();
        } catch (Exception e) {
            Log.d("DataDeletion", e.getMessage());
        }
    }

    private void deleteFile(String fileName) {
        File f = new File(fileName);
        if(f.exists())
        {
            f.delete();
        }
    }

    private String getDate(String currentDate) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MMM/yyyy");
        try {
            Calendar c = Calendar.getInstance();
            Date date2 = new Date(Long.parseLong(currentDate));
            c.setTime(date2);
            c.add(Calendar.DAY_OF_MONTH, -30);

            String t = myFormat.format(c.getTime());

            Date d = myFormat.parse(t);

            long m = d.getTime();

            return String.valueOf(m);

        } catch (Exception e) {
            return null;
        }
    }
}
