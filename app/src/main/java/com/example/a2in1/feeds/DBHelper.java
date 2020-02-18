package com.example.a2in1.feeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.a2in1.models.FacebookPost;
import com.example.a2in1.models.TwitterPost;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database = this.getWritableDatabase();

    private static final String TABLE_NAME = "socialFeeds";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_SITE = "site";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_POSTEDBY = "postedby";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_HASHTAGS = "hashtags";
    private static final String COLUMN_LINK = "link";

    private static final String DATABASE_NAME = "feeds.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT, site TEXT, message TEXT, image TEXT, hashtags TEXT, link TEXT, postedBy TEXT)");
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
    }

    public FacebookPost[] getAllFacebook(){
        int amount = getAllOfSite("Facebook").getCount();

        FacebookPost[] posts = new FacebookPost[amount];

        String[] msg = getColumnItem("Facebook","message");
        String[] img = getColumnItem("Facebook","image");
        String[] hashtags = getColumnItem("Facebook","hashtags");
        String[] link = getColumnItem("Facebook","link");

        for(int i = 0; i < amount; i++){

            String tags = "None";

            if (!hashtags[i].equals("None")){
                tags="";
                String[] nonNoneTags = hashtags[i].split("[\\s,]");
                for (String x:nonNoneTags) {
                    tags+= x + " ";
                }
            }
            posts[i] = new FacebookPost(msg[i],img[i],tags,link[i]);
        }

        return posts;
    }

    public TwitterPost[] getAllTwitterTimeline(){
        int amount = getAllOfSite("TwitterTimeline").getCount();

        TwitterPost[] posts = new TwitterPost[amount];

        String[] msg = getColumnItem("TwitterTimeline","message");
        String[] nameOfUser = getColumnItem("TwitterTimeline","postedBy");
        String[] link = getColumnItem("TwitterTimeline","link");

        for(int i = 0; i < amount; i++){
            posts[i] = new TwitterPost(msg[i],"","",link[i],nameOfUser[i]);
        }

        return posts;
    }
    public TwitterPost[] getAllTwitter(){
        int amount = getAllOfSite("Twitter").getCount();

        TwitterPost[] posts = new TwitterPost[amount];

        String[] msg = getColumnItem("Twitter","message");
        String[] img = getColumnItem("Twitter","image");
        String[] hashtags = getColumnItem("Twitter","hashtags");
        String[] link = getColumnItem("Twitter","link");

        for(int i = 0; i < amount; i++){

            String tags = "None";

            if (!hashtags[i].equals("None")){
                tags="";
                String[] nonNoneTags = hashtags[i].split("[\\s,]");
                for (String x:nonNoneTags) {
                    tags+= x + " ";
                }
            }
            posts[i] = new TwitterPost(msg[i],img[i],tags,link[i]);
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
    public boolean updateData(String id, String site, String message, String image, String hashtags, String link){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE, site);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_HASHTAGS, hashtags);
        values.put(COLUMN_LINK, link);

        database.update(TABLE_NAME,values,"COLUMN_ID = ?",new String[]{id});

        return true;
    }

    // Update the given column id's data
    public boolean updateData2(String id, String site, String message, String image, String link){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE, site);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_LINK, link);

        database.update(TABLE_NAME,values,"COLUMN_ID = ?",new String[]{id});

        return true;
    }

    // Delete data of a given id
    public Integer deleteData(String id){
        return database.delete(TABLE_NAME,"id = ?", new String[]{id});
    }

    // Deletes the data of either site from the database
    public Integer deleteSiteData(String site){
        return database.delete(TABLE_NAME,"site = ?", new String[]{site});
    }

    // Insert into the database
    public boolean insertIntoDB(String site, String message, String image, String hashtags, String link){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE, site);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_HASHTAGS, hashtags);
        values.put(COLUMN_LINK, link);

        long result = database.insert(TABLE_NAME,null,values);

        if (result == -1){
            Log.d("DB","Insert FAILED: " + message + " " + image + " " + hashtags + " "+ link);

            return false;
        }
        else {
            Log.d("DB","Inserted Successful: " + message + " " + image + " " + hashtags + " "+ link);
            return true;
        }
    }

    // Insert into the database without Hashtags
    public boolean insertIntoDB2(String site, String message, String postedBy, String link){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE, site);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_POSTEDBY, postedBy);
        values.put(COLUMN_LINK, link);

        long result = database.insert(TABLE_NAME,null,values);

        if (result == -1){
            Log.d("DB","Insert FAILED: " + message + " " + postedBy + " "+ link);

            return false;
        }
        else {
            Log.d("DB","Inserted Successful: " + message + " " + postedBy + " "+ link);
            return true;
        }
    }

    public void emptyDB(){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
        Log.d("DB",TABLE_NAME + " Table deleted");
    }
}