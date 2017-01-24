package com.example.hivian.ft_hangouts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String CONTACTS_TABLE_CREATE =
            "CREATE TABLE " + CONTACTS_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_NAME + " TEXT, " + KEY_LAST_NAME + " TEXT, " + KEY_PHONE + " TEXT, " +
            KEY_EMAIL + " TEXT, " + KEY_ADDRESS + " TEXT)";

    DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONTACTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        // Creating tables again
        onCreate(db);
    }

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_LAST_NAME, contact.getLastName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        db.insert(CONTACTS_TABLE, null, values);
        db.close();
    }

    // Getting one contact
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]
                { KEY_ID, KEY_NAME, KEY_LAST_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS },
                KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        cursor.close();
        db.close();
        return contact;
    }

    // Getting all contacts
    public List<Contact> getAllContacts() {
        String selectQuery = "SELECT * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Contact> allContacts = new ArrayList<Contact>();

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setLastName(cursor.getString(2));
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

    // Getting contacts count
    public Integer getContactsCount() {
        String countQuery = "SELECT * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.close();
        db.close();
        return cursor.getCount();
    }

    // Updating a contact
    public Integer updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_LAST_NAME, contact.getLastName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        return db.update(CONTACTS_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    // Deleting a contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CONTACTS_TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

}
