package my.edu.tarc.communechat_v2.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Xeosz on 28-Oct-17.
 *
 * For future implementation,
 * This Database act as buffer for received MQTT Messages
 * MQTT Messages is stored here when App is killed
 * while the MQTTService and MessageService are running on the phone
 *
 * Update from 2nd generation seniors:
 * these things are done by 1st generation
 * we have no idea what are these
 * we will leave it here
 * maybe a talented one can find any value in these
 */

public class MqttMessageBuffer extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "MqttMessageBuffer";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Contacts table name
    private static final String TABLE_NAME = "messages";

    // Contacts Table Columns names
    private static final String KEY_CONTENT = "content";
    private static final String KEY_TIME = "time";
    private static final String KEY_TOPIC = "topic";

    public MqttMessageBuffer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                        + KEY_TOPIC + " TEXT, "
                        + KEY_CONTENT + " TEXT, "
                        + KEY_TIME + " INTEGER "
                        + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void push(String topic, String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TOPIC, topic);
        values.put(KEY_CONTENT, msg);
        values.put(KEY_TIME, (new Date().getTime() / 1000));

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String[]> pop() {
        ArrayList<String[]> result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "
                + KEY_TOPIC + ", "
                + KEY_CONTENT
                + " FROM " + TABLE_NAME
                + " ORDER BY " + KEY_TIME + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst())
            do {
                result.add(new String[]{cursor.getString(0), cursor.getString(1)});
            } while (cursor.moveToNext());
        clearTable();
        db.close();
        return result;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        db.close();
        if (count > 0)
            return false;
        return true;
    }

    private boolean isTableExists() {
        String sql = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        db.close();
        return cs.getInt(0) == 1;
    }

    private void clearTable() {
        if (this.isTableExists()) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, null, null);
            db.close();
        }
    }
}
