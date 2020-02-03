package com.example.a2in1.api.feeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.a2in1.models.FacebookPost;

import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database = this.getWritableDatabase();

    private static final String TABLE_NAME = "socialFeeds";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_SITE = "site";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_HASHTAGS = "hashtags";

    private static final String DATABASE_NAME = "feeds.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT, site TEXT, message TEXT, image TEXT, hashtags TEXT)");
    }

    // Drops and recreates the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Selects all the data in the database
    public Cursor getAll(){
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        return cursor;
    }

    public Cursor getAllOfSite(String site){
        return database.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_SITE + " = ?", new String[]{site});
//        return cursor;
    }

    public FacebookPost[] getAllFacebook(){
        int amount = getAllOfSite("Facebook").getCount();

        FacebookPost[] posts = new FacebookPost[amount];

        String[] msg = getColumnItem("Facebook","message");
        String[] img = getColumnItem("Facebook","image");
        String[] hashtags = getColumnItem("Facebook","hashtags");

        for(int i = 0; i < amount; i++){

            String tags = "None";

            if (!hashtags[i].equals("None")){
                tags="";
                String[] nonNoneTags = hashtags[i].split("[\\s,]");
                for (String x:nonNoneTags) {
                    tags+= x + " ";
                }
            }
            posts[i] = new FacebookPost(msg[i],img[i],tags);
        }

        return posts;
    }

    private String[] getColumnItem(String site,String nameColumn){
        Cursor cursor = database.rawQuery("select " + nameColumn + " from "+ TABLE_NAME + " where " +  COLUMN_SITE + " = ?",new String[]{site});

        String[] item = new String[cursor.getCount()];

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            item[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(nameColumn));
            cursor.moveToNext();
        }
        return item;
    }

    // Update the given column id's data
    public boolean updateData(String id, String site, String message, String image, String hashtags){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE, site);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_HASHTAGS, hashtags);

        database.update(TABLE_NAME,values,"COLUMN_ID = ?",new String[]{id});

        return true;
    }

    // Delete data of a given id
    public Integer deleteData(String id){
        return database.delete(TABLE_NAME,"id = ?", new String[]{id});
    }

    // Insert into the database
    public boolean insertIntoDB(String site, String message, String image, String hashtags){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE, site);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_HASHTAGS, hashtags);

        long result = database.insert(TABLE_NAME,null,values);

        if (result == -1){
            Log.d("DB","Insert FAILED: " + message + " " + image + " " + hashtags);

            return false;
        }
        else {
            Log.d("DB","Inserted: " + message + " " + image + " " + hashtags);
            return true;

        }
    }

    public void emptyDB(){
        database.delete(TABLE_NAME,null,null);
        Log.d("DB",TABLE_NAME + " Table deleted");
    }
}