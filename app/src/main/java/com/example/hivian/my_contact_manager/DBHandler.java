package com.example.hivian.my_contact_manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hivian on 1/24/17.
 */

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DB_VERSION = 1;
    // Database Name
    private static final String DB_NAME = "DB";
    // Contacts table name
    private static final String CONTACTS_TABLE = "contacts";
    private static final String SMS_TABLE = "sms";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_SMS_HEADER = "sms_header";
    private static final String KEY_SMS_CONTENT = "sms_content";
    private static final String KEY_CONTACT_ID = "contact_id";
    private static final String KEY_SMS_TYPE = "sms_type";
    private static final String CONTACTS_TABLE_CREATE =
            "CREATE TABLE " + CONTACTS_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_IMAGE + " BLOB, " + KEY_NAME + " TEXT, " + KEY_PHONE + " TEXT, " +
                    KEY_EMAIL + " TEXT, " + KEY_ADDRESS + " TEXT)";
    private static final String SMS_TABLE_CREATE =
            "CREATE TABLE " + SMS_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_SMS_HEADER + " TEXT, " +  KEY_SMS_CONTENT + " TEXT, " +
                    KEY_CONTACT_ID + " INTEGER," + KEY_SMS_TYPE + " INTEGER)";


    DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONTACTS_TABLE_CREATE);
        db.execSQL(SMS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
        // Creating tables again
        onCreate(db);
    }

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE, contact.getImage());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        db.insert(CONTACTS_TABLE, null, values);
        db.close();
    }

    // Adding new sms
    public void addSms(SmsContent sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SMS_HEADER, sms.getHeader());
        values.put(KEY_SMS_CONTENT, sms.getContent());
        values.put(KEY_CONTACT_ID, sms.getContactId());
        values.put(KEY_SMS_TYPE, sms.getType());
        db.insert(SMS_TABLE, null, values);
        db.close();
    }

    // Getting one contact
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]
                { KEY_ID, KEY_IMAGE, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS },
                KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        Contact contact = new Contact(cursor.getBlob(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5));
        contact.setId(cursor.getInt(0));
        cursor.close();
        db.close();
        return contact;
    }

    // Getting one contact by name
    public Contact getContactByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]
                        { KEY_ID, KEY_IMAGE, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS },
                KEY_NAME + "=?", new String[] { name }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        Contact contact = new Contact(cursor.getBlob(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5));
        contact.setId(cursor.getInt(0));
        cursor.close();
        db.close();
        return contact;
    }

    // Getting one contact by phone
    public Contact getContactByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]
                        { KEY_ID, KEY_IMAGE, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS },
                KEY_PHONE + "=?", new String[] { phone }, null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        Contact contact = new Contact(cursor.getBlob(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5));
        contact.setId(cursor.getInt(0));
        cursor.close();
        db.close();
        return contact;
    }

    // Getting one sms by contact id
    public List<SmsContent> getSmsByContactId(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SMS_TABLE, new String[]
                        { KEY_ID, KEY_SMS_HEADER, KEY_SMS_CONTENT, KEY_CONTACT_ID, KEY_SMS_TYPE },
                KEY_CONTACT_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        List<SmsContent> allSms = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                SmsContent sms = new SmsContent();
                sms.setId(Integer.parseInt(cursor.getString(0)));
                sms.setHeader(cursor.getString(1));
                sms.setContent(cursor.getString(2));
                sms.setContactId(cursor.getInt(3));
                sms.setType(cursor.getInt(4));
                allSms.add(sms);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allSms;
    }

    // Getting all contacts
    public List<Contact> getAllContacts() {
        String selectQuery = "SELECT * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Contact> allContacts = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setImage(cursor.getBlob(1));
                contact.setName(cursor.getString(2));
                contact.setPhone(cursor.getString(3));
                contact.setEmail(cursor.getString(4));
                contact.setAddress(cursor.getString(5));
                allContacts.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allContacts;
    }

    // Getting all sms
    public List<SmsContent> getAllSms() {
        String selectQuery = "SELECT * FROM " + SMS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<SmsContent> allSms = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                SmsContent sms = new SmsContent();
                sms.setId(Integer.parseInt(cursor.getString(0)));
                sms.setHeader(cursor.getString(1));
                sms.setContent(cursor.getString(2));
                sms.setContactId(cursor.getInt(3));
                sms.setType(cursor.getInt(4));
                allSms.add(sms);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allSms;
    }

    // Getting all sms from contact
    public List<SmsContent> getAllSmsFromContact(int id) {
        String selectQuery = "SELECT * FROM " + SMS_TABLE + " WHERE " +
                KEY_CONTACT_ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<SmsContent> allSms = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                SmsContent sms = new SmsContent();
                sms.setId(Integer.parseInt(cursor.getString(0)));
                sms.setHeader(cursor.getString(1));
                sms.setContent(cursor.getString(2));
                sms.setContactId(cursor.getInt(3));
                sms.setType(cursor.getInt(4));
                allSms.add(sms);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allSms;
    }

    // Getting contacts count
    public Integer getContactsCount() {
        String countQuery = "SELECT * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    // Checking duplicates in table
    public boolean isDuplicate(DBHandler db, String name) {
        List<Contact> contacts = db.getAllContacts();
        for (Contact cont : contacts) {
            if (cont.getName().equals(name))
                return true;
        }
        return false;
    }

    // Updating a contact
    public Integer updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE, contact.getImage());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        return db.update(CONTACTS_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    // Updating a sms
    public Integer updateSms(SmsContent sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SMS_HEADER, sms.getHeader());
        values.put(KEY_SMS_CONTENT, sms.getContent());
        values.put(KEY_CONTACT_ID, sms.getContactId());
        values.put(KEY_SMS_TYPE, sms.getType());
        return db.update(SMS_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(sms.getId())});
    }

    // Deleting a contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CONTACTS_TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    // Deleting a contact
    public void deleteSms(SmsContent sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SMS_TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(sms.getId()) });
        db.close();
    }

    // Deleting all contacts
    public void deleteAllContacts(DBHandler db) {
        List<Contact> contacts = db.getAllContacts();

        for (Contact cont : contacts) {
            db.deleteContact(cont);
        }
    }

    // Deleting all sms
    public void deleteAllSms(DBHandler db) {
        List<SmsContent> allSms = db.getAllSms();

        for (SmsContent sms : allSms) {
            db.deleteSms(sms);
        }
    }

}
