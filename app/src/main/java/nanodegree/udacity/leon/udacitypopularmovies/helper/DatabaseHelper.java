package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie_info_database";
    private static final String TABLE_NAME = "movie_info";
    private static final String MOVIE_ID = "movie_id";
    private static final String MOVIE_ORIGINAL_TITLE = "movie_id";
    private static final String MOVIE_IMAGE_URL = "movie_id";
    private static final String MOVIE_PLOT_SYNOPSIS = "movie_id";
    private static final String MOVIE_USER_RATING = "movie_id";
    private static final String MOVIE_RELEASE_DATE = "movie_id";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
