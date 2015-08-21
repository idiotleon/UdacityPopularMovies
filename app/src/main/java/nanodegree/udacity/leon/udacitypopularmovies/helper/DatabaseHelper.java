package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie_info_database";
    private static final String TABLE_NAME = "movie_info";
    private static final String MOVIE_ID = "movie_id";
    private static final String MOVIE_ORIGINAL_TITLE = "movie_original_title";
    private static final String MOVIE_IMAGE_URL = "movie_image_url";
    private static final String MOVIE_PLOT_SYNOPSIS = "movie_plot_synopsis";
    private static final String MOVIE_USER_RATING = "movie_rating";
    private static final String MOVIE_RELEASE_DATE = "movie_date";
    private static final String MOVIE_TRAILER_URL_JSON_STRING = "movie_trailer_url_json_string";
    private static final String MOVIE_REVIEW_JSON_STRING = "movie_review_json_string";
    private static final String MOVIE_FAVORITE_STATUS = "movie_favorite_status";
    private static final int versioin = 1;


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, versioin);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" + MOVIE_ID + " LONG PRIMARY KEY " +
                MOVIE_ORIGINAL_TITLE + " TEXT " +
                MOVIE_IMAGE_URL + " TEXT " +
                MOVIE_PLOT_SYNOPSIS + " TEXT " +
                MOVIE_USER_RATING + " FLOAT " +
                // todo: Date type might be better
                MOVIE_RELEASE_DATE + " TEXT " +
                MOVIE_TRAILER_URL_JSON_STRING + " TEXT " +
                MOVIE_REVIEW_JSON_STRING + " TEXT " +
                MOVIE_FAVORITE_STATUS + " BOOLEAN)";
        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String updateDatabaseQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(updateDatabaseQuery);
    }


}
