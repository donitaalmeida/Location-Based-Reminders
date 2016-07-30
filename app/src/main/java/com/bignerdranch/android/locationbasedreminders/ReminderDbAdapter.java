package com.bignerdranch.android.locationbasedreminders;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import java.util.Date;

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
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS Reminder( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title VARCHAR, name VARCHAR, address VARCHAR, date VARCHAR, latitude REAL, longitude REAL, status BOOLEAN );";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
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
                "_id", "title", "name", "address", "date","latitude","longitude","status"},
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
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

}
