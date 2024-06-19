    package com.example.ech.notes;
    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import androidx.annotation.Nullable;

    public class DatabaseHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "ECH.db";
        public static final String TABLE1_NAME = "noteTable";
        public static final String COL_0 = "ID";
        public static final String COL_1 = "MESSAGE";



        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME,null,5);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE1_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT , MESSAGE TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists "+TABLE1_NAME);
            onCreate(db);
        }
        public void insertNotes( String msg){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,msg);
            db.insert(TABLE1_NAME,null,contentValues);
        }
        public Cursor getAllNotes(){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from "+TABLE1_NAME,null);
            return res;
        }
    public Integer deleteData (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE1_NAME, "ID = ?",new String[] {String.valueOf(id)});
    }
    }