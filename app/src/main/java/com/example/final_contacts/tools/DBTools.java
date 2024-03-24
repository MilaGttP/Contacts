package com.example.final_contacts.tools;

import static com.example.final_contacts.tools.DBTools.ContactsNames.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.final_contacts.data.Contact;


public class DBTools extends SQLiteOpenHelper {

    public enum ContactsNames {
        contacts,
        _id,
        name,
        surname,
        patronymic,
        phone_number,
        description
    }

    public DBTools(@Nullable Context context) {
        super(context, "contacts", null, 1);
        createContactsTable();
    }

    private SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createContactsTable() {
        db = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s TEXT" +
                ")";
        sql = String.format(sql, contacts, _id, name, surname, patronymic, phone_number, description);
        db.execSQL(sql);
    }

    public void saveContact(Contact contact) {
        db = getWritableDatabase();
        db.beginTransaction();
        try {
            String sql = "INSERT INTO %s(%s, %s, %s, %s, %s) VALUES('%s', '%s', '%s', '%s', '%s')";
            sql = String.format(sql, contacts, name, surname, patronymic, phone_number, description,
                    contact.getName(), contact.getSurname(), contact.getPatronymic(), contact.getPhoneNumber(), contact.getDescription());
            Log.d("TAG", "saveContact: sql " + sql);
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("TAG", "Error saving contact", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Cursor findAllContactsCursor() {
        db = getReadableDatabase();
        String sql = "select * from " + contacts;
        Log.d("TAG", "findAllContactsCursor: sql is " + sql);
        return db.rawQuery(sql, new String[]{});
    }

    public Contact findContactById(int id) {
        db = this.getReadableDatabase();

        String[] projection = {
                String.valueOf(_id),
                String.valueOf(name),
                String.valueOf(surname),
                String.valueOf(patronymic),
                String.valueOf(phone_number),
                String.valueOf(description)
        };

        String selection = _id + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                String.valueOf(contacts),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Contact contact = null;

        if (cursor.moveToFirst()) {
            String nameVar = cursor.getString(cursor.getColumnIndexOrThrow(String.valueOf(name)));
            String surnameVar = cursor.getString(cursor.getColumnIndexOrThrow(String.valueOf(surname)));
            String patronymicVar = cursor.getString(cursor.getColumnIndexOrThrow(String.valueOf(patronymic)));
            String phoneNumberVar = cursor.getString(cursor.getColumnIndexOrThrow(String.valueOf(phone_number)));
            String descriptionVar = cursor.getString(cursor.getColumnIndexOrThrow(String.valueOf(description)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                contact = new Contact(id, nameVar, surnameVar, patronymicVar, phoneNumberVar, descriptionVar);
            }
        }
        cursor.close();

        return contact;
    }

    public void clearContactsTable() {
        getWritableDatabase().execSQL(
                "drop table if exists " + contacts,
                new Object[]{}
        );
        createContactsTable();
    }

    public void deleteContactById(int id) {
        db = getWritableDatabase();
        String sql = "delete from %s where %s=%d";
        sql = String.format(sql, contacts, _id, id);
        db.execSQL(sql);
    }
}
