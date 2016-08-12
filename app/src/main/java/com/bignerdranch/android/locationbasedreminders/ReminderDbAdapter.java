package com.bignerdranch.android.locationbasedreminders;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by donita on 04-07-2016.
 */
public class ReminderDbAdapter
{
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "LocationBasedReminders";
    private static final String SQLITE_TABLE = "Reminder";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;
    private static final String REMINDER_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN, contact VARCHAR, image VARCHAR, type VARCHAR, note VARCHAR );";

    private static final String LOCATION_DATABASE_CREATE="CREATE TABLE IF NOT EXISTS Location( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "reminderid INTEGER, title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN );";
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(REMINDER_DATABASE_CREATE);
         //   db.execSQL(LOCATION_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public ReminderDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public ReminderDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public Cursor fetchAllReminders() {
        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {
                "_id", "title", "name", "address", "date","latitude","longitude","status","type","note"},
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchOneReminder(int ReminderId){

        Cursor c = mDb.rawQuery("SELECT * FROM " + SQLITE_TABLE + " where _id = '"+ReminderId+"'", null);
        if (c != null ) {
         c.moveToFirst();
        }
        return c;
      /*  Cursor mCursor=  mDb.query(SQLITE_TABLE, new String[] {
                        "_id", "title", "name", "address", "date","latitude","longitude","status"},
                "_id =?", new String[]{"'"+ReminderId+"'"}, null, null, null);
        if (mCursor != null) {
            Log.e("data","cursor is not null");
            //mCursor.moveToFirst();
        }
        return mCursor;*/

    }
   /* public Cursor fetchGeneralReminders(){
        Cursor cursor=mDb.rawQuery("SELECT * FROM " + "Location" + " where status = 'false'",null);
        if (cursor!=null){
          //  cursor.moveToFirst();
            Log.e("notnull","cur not null general"+cursor.moveToFirst());
        }
        return cursor;
    }*/

    public Cursor fetchSpecificReminders(){
        Cursor cursor=mDb.rawQuery("SELECT * FROM " + "Reminder" + " WHERE status = 'false' and name !=''",null);
        if (cursor!=null){
           // cursor.moveToFirst();
            Log.e("notnull","cur not null specific"+cursor.moveToFirst());
        }
        return cursor;
    }

    public void deleteReminder(int ReminderId ){
        open();
        mDb.execSQL("DELETE FROM Reminder WHERE _id ='"+ReminderId+"';");
        close();
    }
    public void markAsDone(int ReminderId){
        open();
        mDb.execSQL("UPDATE Reminder SET status='true' WHERE _id='"+ReminderId+"';");
        close();
    }
    public void undo(int ReminderId){
        open();
        mDb.execSQL("UPDATE Reminder SET status='false' WHERE _id='"+ReminderId+"';");
        close();
    }

}
