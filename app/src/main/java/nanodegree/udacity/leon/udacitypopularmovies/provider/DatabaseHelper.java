package nanodegree.udacity.leon.udacitypopularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private Context context;

    public static final String DATABASE_NAME = "movie_info.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createMediumMovieInfoTableQuery = "CREATE TABLE " + MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME +
                " (" + MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " LONG PRIMARY KEY, " +
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL + " TEXT, " +
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS + " TEXT, " +
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING + " REAL, " +
                // todo: Date type might be better
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE + " TEXT, " +
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY + " REAL, " +
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS + " BOOLEAN DEFAULT FALSE)";
        Log.v(LOG_TAG, "createMediumMovieInfoTableQuery: " + createMediumMovieInfoTableQuery);
        db.execSQL(createMediumMovieInfoTableQuery);

        final String createMovieTrailerTableQuery
                = "CREATE TABLE " + MovieInfoProviderContract.MovieTrailerEntry.TABLE_NAME +
                " (" + MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL + " TEXT, " +
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + ") " +
                "REFERENCES " + MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME +
                "(" + MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + "))";
        Log.v(LOG_TAG, "createMovieTrailerTableQuery: " + createMovieTrailerTableQuery);
        db.execSQL(createMovieTrailerTableQuery);

        final String createMovieReviewTableQuery
                = "CREATE TABLE " + MovieInfoProviderContract.MovieReviewEntry.TABLE_NAME +
                " (" + MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_AUTHOR + " TEXT, " +
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_CONTENT + " TEXT, " +
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_URL + " TEXT, " +
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID + ") " +
                "REFERENCES " + MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME +
                "(" + MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + "))";
        Log.v(LOG_TAG, "createMovieReviewTableQuery: " + createMovieReviewTableQuery);
        db.execSQL(createMovieReviewTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String updateGeneralMovieInfoTableQuery = "DROP TABLE IF EXISTS " + MovieInfoProviderContract.GeneralMovieInfoEntry.TABLE_NAME;
        String updateMovieTrailerTableQuery = "DROP TABLE IF EXISTS " + MovieInfoProviderContract.MovieTrailerEntry.TABLE_NAME;
        String updateMovieReviewTableQuery = "DROP TABLE IF EXISTS " + MovieInfoProviderContract.MovieReviewEntry.TABLE_NAME;
        db.execSQL(updateGeneralMovieInfoTableQuery);
        db.execSQL(updateMovieTrailerTableQuery);
        db.execSQL(updateMovieReviewTableQuery);
    }
}
