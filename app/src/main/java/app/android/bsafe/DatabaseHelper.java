package app.android.bsafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DatabaseName = "contacts.db";
    private static final String TableName = "contacts_table";
    private static final String COL_1 = "PHONE_NUMBER";

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DatabaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TableName + "(PHONE_NUMBER TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        onCreate(db);
    }

    boolean AddData(String PhoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,PhoneNumber);

        long result = db.insert(TableName,null,contentValues);
        return result != -1;
    }

    Cursor ViewData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TableName,null);
        return cursor;
    }

    boolean EditData(String phone_id,String phoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1,phoneNumber);
        db.update(TableName,contentValues,"PHONE_NUMBER = ?",new String[] {phone_id});
        return true;
    }

    Integer DeletaData(String phoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int temp =  db.delete(TableName,"PHONE_NUMBER = ?",new String[]{phoneNumber});
        Log.d("CHECK_TAG",Integer.toString(temp));
        return temp;
    }
    boolean checkAlreadyExist(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT " + COL_1 + " FROM " + TableName + " WHERE " + COL_1 + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{name});
        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
            return false;
    }
}
