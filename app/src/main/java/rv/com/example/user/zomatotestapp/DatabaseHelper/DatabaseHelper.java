package rv.com.example.user.zomatotestapp.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 9/18/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "Zomato.db";
    public static int DATABASE_VERSION = 2;

    public static String RESTAURANT_TABLE = "restaurant_table";
    public static String RESTAURANT_ID_PK = "_id";
    public static String RESTAURANT_ID = "restaurant_id";
    public static String LOCALITY ="locality";
    public static String ADDRESS ="address";
    public static String SITE_URL ="siteurl";
    public static String LATITUDE ="latitude";
    public static String LONGITUDE ="longitude";
    public static String RESTAURANT_NAME ="restaurant_name";
    //public static String RESTAURANT_NAME ="restaurant_id";
    //public static String RESTAURANT_NAME ="restaurant_id";

    public static String Create_Restaurant_Table = " CREATE TABLE " + RESTAURANT_TABLE + " ( "
            + RESTAURANT_ID_PK + " integer primary key autoincrement, "
            + RESTAURANT_ID + " text, "
            + RESTAURANT_NAME + " text, "
            + LOCALITY + " text, "
            + ADDRESS + " text, "
            + SITE_URL + " text, "
            + LATITUDE + " text, "
            + LONGITUDE + " text " + " ) ";

    public static String Drop_Restaurant_Table = " Drop Table IF EXISTS " + RESTAURANT_TABLE;


    public static String COMMENTS_TABLE ="comments_table";
    public static String COMMENTS_TEXT ="comments_text";
    public static String COMMENTS_PK ="_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(Create_Restaurant_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Drop_Restaurant_Table);
        onCreate(db);
    }
}

