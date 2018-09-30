package my.edu.tarc.communechat_v2.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.communechat_v2.model.Contact;

/**
 * Created by Xeosz on 27-Sep-17.
 */

public class ContactDBHandler extends SQLiteOpenHelper {
    // Database Name
    public static final String DATABASE_NAME = "contactsManager";
    // Contacts table name
    //Main contact list table
    public static final String TABLE_CONTACTS = "contacts";

    //cache storage
    public static final String TABLE_CACHE = "cacheContacts";

    //cache storage
    public static final String TABLE_FRIEND_REQUEST = "friendRequests";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Contacts Table Columns names
    private static final String KEY_ID = "uid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_STATUS = "status";
    private static final String KEY_LAST_ONLINE = "last_online";
    private static final String KEY_PH_NO = "phone_number";

    private static String CREATE_CONTACTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_USERNAME + " TEXT, "
                    + KEY_NICKNAME + " TEXT, "
                    + KEY_GENDER + " INTEGER, "
                    + KEY_STATUS + " TEXT, "
                    + KEY_LAST_ONLINE + " INTEGER, "
                    + KEY_PH_NO + " TEXT"
                    + ");";
    private static String CREATE_CACHE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CACHE + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_USERNAME + " TEXT, "
                    + KEY_NICKNAME + " TEXT, "
                    + KEY_GENDER + " INTEGER, "
                    + KEY_STATUS + " TEXT, "
                    + KEY_LAST_ONLINE + " INTEGER, "
                    + KEY_PH_NO + " TEXT"
                    + ");";
    private static String CREATE_FRIENDREQUEST_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_FRIEND_REQUEST + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_USERNAME + " TEXT, "
                    + KEY_NICKNAME + " TEXT, "
                    + KEY_GENDER + " INTEGER, "
                    + KEY_STATUS + " TEXT, "
                    + KEY_LAST_ONLINE + " INTEGER, "
                    + KEY_PH_NO + " TEXT"
                    + ");";

    public ContactDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // + KEY_IMAGE + " BLOB"
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_CACHE_TABLE);
        sqLiteDatabase.execSQL(CREATE_FRIENDREQUEST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_REQUEST);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    // Adding new contact
    public void addContact(Contact contact, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (tableName == TABLE_CONTACTS || tableName == TABLE_FRIEND_REQUEST) {
            values.put(KEY_ID, contact.getUid());
            values.put(KEY_NICKNAME, contact.getNickname());
            values.put(KEY_STATUS, contact.getStatus());
        }

        if (tableName == TABLE_CACHE) {
            values.put(KEY_ID, contact.getUid());
            values.put(KEY_USERNAME, contact.getUsername());
            values.put(KEY_NICKNAME, contact.getNickname());
            values.put(KEY_GENDER, contact.getGender());
            values.put(KEY_STATUS, contact.getStatus());
            values.put(KEY_LAST_ONLINE, contact.getLast_online());
            //values.put(KEY_IMAGE, contact.getPhoneNumber());
            //values.put(KEY_PH_NO, contact.getPhone_number());
        }
        // Inserting Row
        db.insert(tableName, null, values);
        db.close(); // Closing database connection
    }

    public boolean isContactExists(String uid, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName,
                new String[]{KEY_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(uid)}, null, null, null, null);
        if (cursor.getCount() > 0
                && cursor.moveToFirst()) {
                return true;
        }
        return false;
    }

    // Getting single contact
    public Contact getContact(String id, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(tableName,
                new String[]{KEY_ID, KEY_USERNAME, KEY_NICKNAME, KEY_GENDER, KEY_STATUS, KEY_LAST_ONLINE, KEY_PH_NO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Contact contact = new Contact();
        if (cursor != null && cursor.moveToFirst()) {
            if (!cursor.isNull(0))
                contact.setUid(cursor.getInt(0));                       //id
            if (!cursor.isNull(1))
                contact.setUsername(cursor.getString(1));                 //username
            if (!cursor.isNull(2))
                contact.setNickname(cursor.getString(2));                  //nickname
            if (!cursor.isNull(3))
                contact.setGender(cursor.getInt(3));                    //gender
            if (!cursor.isNull(4))
                contact.setStatus(cursor.getString(4));                    //status
            if (!cursor.isNull(5))
                contact.setLast_online(cursor.getInt(5));                      //last online
            if (!cursor.isNull(6))
                contact.setPhone_number(cursor.getString(6));                     //phone number
        }
        db.close();
        // return contact
        return contact;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts(String tableName) {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT " + KEY_ID + ", " + KEY_USERNAME + ", " + KEY_NICKNAME + ", " + KEY_GENDER + ", " + KEY_STATUS + ", "
                + KEY_LAST_ONLINE + ", " + KEY_PH_NO + " FROM " + tableName;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst())
            do {
                Contact contact = new Contact();
                if (!cursor.isNull(0))
                    contact.setUid(cursor.getInt(0));                       //id
                if (!cursor.isNull(1))
                    contact.setUsername(cursor.getString(1));                 //username
                if (!cursor.isNull(2))
                    contact.setNickname(cursor.getString(2));                  //nickname
                if (!cursor.isNull(3))
                    contact.setGender(cursor.getInt(3));                    //gender
                if (!cursor.isNull(4))
                    contact.setStatus(cursor.getString(4));                    //status
                if (!cursor.isNull(5))
                    contact.setLast_online(cursor.getInt(5));                      //last online
                if (!cursor.isNull(6))
                    contact.setPhone_number(cursor.getString(6));                     //phone number
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        db.close();
        // return contact list
        return contactList;
    }

    // Getting contacts Count
    public int getContactsCount(String tableName) {
        String countQuery = "SELECT  COUNT(*) FROM '" + tableName + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        db.close();
        // return count
        return cursor.getInt(0);
    }
    // Updating single contact
    public int updateSingleContact(Contact contact, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getUid());
        if (!contact.getUsername().isEmpty() || contact.getUsername() != null)
            values.put(KEY_USERNAME, contact.getUsername());
        if (!contact.getNickname().isEmpty() || contact.getNickname() != null)
            values.put(KEY_NICKNAME, contact.getNickname());
        if (contact.getGender() != 0)
            values.put(KEY_GENDER, contact.getGender());
        if (!contact.getStatus().isEmpty() || contact.getStatus() != null)
            values.put(KEY_STATUS, contact.getStatus());
        if (contact.getLast_online() != 0)
        values.put(KEY_LAST_ONLINE, contact.getLast_online());
        //values.put(KEY_IMAGE, contact.getImage());
        if (contact.getPhone_number() != null || !contact.getPhone_number().isEmpty())
        values.put(KEY_PH_NO, contact.getPhone_number());

        // updating row
        return db.update(tableName, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getUid()) });
    }

    public void updateContacts(ArrayList<Contact> contacts, String tableName) {
        for (Contact contact : contacts) {
            updateSingleContact(contact, tableName);
        }
    }

    // Deleting single contact
    public void deleteContact(Contact contact, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getUid()) });
        db.close();
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }
        for (String table : tables) {
            clearTable(table);
        }
    }

    private boolean isTableExists(String tableName) {
        String sql = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        db.close();
        return cs.getInt(0) == 1;
    }

    public void clearTable(String tableName) {
        if (this.isTableExists(tableName)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(tableName, null, null);
            db.close();
        }
    }

}
