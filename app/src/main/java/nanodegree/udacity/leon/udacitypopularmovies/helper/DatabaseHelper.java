package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private Context context;

    private static final String DATABASE_NAME = "movie_info_database";
    private static final String STORED_TABLE_NAME = "stored_movie_info";
    //    private static final String UPDATED_TABLE_NAME = "updated_movie_info";
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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versioin);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStoredTableQuery = "CREATE TABLE " + STORED_TABLE_NAME + " (" + MOVIE_ID + " LONG PRIMARY KEY " +
                MOVIE_ORIGINAL_TITLE + " TEXT " +
                MOVIE_IMAGE_URL + " TEXT " +
                MOVIE_PLOT_SYNOPSIS + " TEXT " +
                MOVIE_USER_RATING + " FLOAT " +
                // todo: Date type might be better
                MOVIE_RELEASE_DATE + " TEXT " +
                MOVIE_TRAILER_URL_JSON_STRING + " TEXT " +
                MOVIE_REVIEW_JSON_STRING + " TEXT " +
                MOVIE_FAVORITE_STATUS + " BOOLEAN)";
        Log.v(LOG_TAG, "createStoredTableQuery: " + createStoredTableQuery);
        db.execSQL(createStoredTableQuery);

//        String createUpdatedTableQuery = "CREATE TABLE " + UPDATED_TABLE_NAME + " (" + MOVIE_ID + " LONG PRIMARY KEY " +
//                MOVIE_ORIGINAL_TITLE + " TEXT " +
//                MOVIE_IMAGE_URL + " TEXT " +
//                MOVIE_PLOT_SYNOPSIS + " TEXT " +
//                MOVIE_USER_RATING + " FLOAT " +
//                MOVIE_RELEASE_DATE + " TEXT " +
//                MOVIE_TRAILER_URL_JSON_STRING + " TEXT " +
//                MOVIE_REVIEW_JSON_STRING + " TEXT)";
//        Log.v(LOG_TAG, "createUpdatedTableQuery: " + createUpdatedTableQuery);
//        db.execSQL(createUpdatedTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String updateDatabaseQuery = "DROP TABLE IF EXISTS " + STORED_TABLE_NAME;
        db.execSQL(updateDatabaseQuery);
    }

    public void updateTableData(MovieInfoModel movieInfo) {
        if (checkMovieInfoStored(movieInfo)) updateMovieInfo(movieInfo);
        else insertMovieInfo(movieInfo);
    }

    public boolean checkMovieInfoStored(MovieInfoModel movieInfo) {
        Log.v(LOG_TAG, "movieInfo, checkMovieInfoStored - Line81, DatabaseHelper: " + movieInfo.toString());
        SQLiteDatabase db = this.getReadableDatabase();
        String checkStoredQuery = "SELECT " + MOVIE_ID + " FROM " + STORED_TABLE_NAME + " WHERE " + MOVIE_ID + " = " + movieInfo.getMovieId().toString();
        Log.v(LOG_TAG, "checkStoredQuery: " + checkStoredQuery);
        Cursor cursor = db.rawQuery(checkStoredQuery, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public void insertMovieInfo(MovieInfoModel movieInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID, movieInfo.getMovieId());
        contentValues.put(MOVIE_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MOVIE_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MOVIE_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MOVIE_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MOVIE_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MOVIE_TRAILER_URL_JSON_STRING, GsonizeData(movieInfo.getMovieTrailerUrlArrayList()));
        contentValues.put(MOVIE_REVIEW_JSON_STRING, GsonizeData(movieInfo.getMovieReviewArrayList()));
        contentValues.put(MOVIE_FAVORITE_STATUS, CheckFavoriteStatus(movieInfo));
        db.insert(STORED_TABLE_NAME, null, contentValues);
    }

    public void updateMovieInfo(MovieInfoModel movieInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID, movieInfo.getMovieId());
        contentValues.put(MOVIE_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MOVIE_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MOVIE_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MOVIE_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MOVIE_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MOVIE_TRAILER_URL_JSON_STRING, GsonizeData(movieInfo.getMovieTrailerUrlArrayList()));
        contentValues.put(MOVIE_REVIEW_JSON_STRING, GsonizeData(movieInfo.getMovieReviewArrayList()));
        contentValues.put(MOVIE_FAVORITE_STATUS, CheckFavoriteStatus(movieInfo));
        db.update(STORED_TABLE_NAME, contentValues, MOVIE_ID + " = ? ", new String[]{Long.toString(movieInfo.getMovieId())});
    }

    public String GsonizeData(ArrayList<?> arrayList) {
        Gson gson = new Gson();
        String gsonizedData = gson.toJson(arrayList);
        Log.v(LOG_TAG, "gsonizedData: " + gsonizedData);
        return gsonizedData;
    }

    public boolean CheckFavoriteStatus(MovieInfoModel movieInfo) {
        if (1 == GeneralHelper.getFavoriteStatus(context, movieInfo.getMovieId().toString(), 0))
            return true;
        return false;
    }

    public ArrayList<MovieInfoModel> getAllMovieInfo() {
        Log.v(LOG_TAG, "getAllMovieInfo() executed.");
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MovieInfoModel> allMovieInfoArrayList = new ArrayList<>();

        String getAllMovieInfoQuery = "SELECT * FROM " + STORED_TABLE_NAME;
        Cursor cursor = db.rawQuery(getAllMovieInfoQuery, null);
        while (!cursor.isAfterLast()) {
            Gson gson = new Gson();
            MovieInfoModel movieInfo = new MovieInfoModel();
            movieInfo.setMovieId(cursor.getLong(cursor.getColumnIndex(MOVIE_ID)));
            movieInfo.setMovieOriginalTitle(cursor.getString(cursor.getColumnIndex(MOVIE_ORIGINAL_TITLE)));
            movieInfo.setMoviePlotSynopsis(cursor.getString(cursor.getColumnIndex(MOVIE_PLOT_SYNOPSIS)));
            movieInfo.setMovieUserRating(cursor.getString(cursor.getColumnIndex(MOVIE_USER_RATING)));
            movieInfo.setMovieReleaseDate(cursor.getString(cursor.getColumnIndex(MOVIE_RELEASE_DATE)));
            ArrayList<String> movieTrailerUrlArrayList = gson.fromJson(cursor.getString(cursor.getColumnIndex(MOVIE_TRAILER_URL_JSON_STRING)), new TypeToken<ArrayList<String>>() {
            }.getType());
            movieInfo.setMovieTrailerUrlArrayList(movieTrailerUrlArrayList);
            ArrayList<MovieReviewModel> movieReviewArrayList = gson.fromJson(cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_JSON_STRING)), new TypeToken<ArrayList<MovieReviewModel>>() {
            }.getType());

            movieInfo.setMovieReviewArrayList(movieReviewArrayList);
            allMovieInfoArrayList.add(movieInfo);
        }
        return allMovieInfoArrayList;
    }

    public ArrayList<MovieInfoModel> getAllMovieInfoOrderByUserRating() {
        Log.v(LOG_TAG, "getAllMovieInfoOrderByUserRating() executed.");
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MovieInfoModel> allMovieInfoOrderByUserRatingArrayList = new ArrayList<>();

        String getAllMovieInfoOrderByUserRatingQuery = "SELECT * FROM " + STORED_TABLE_NAME + " ORDER BY " + MOVIE_USER_RATING;
        Log.v(LOG_TAG, "getAllMovieInfoOrderByUserRatingQuery: " + getAllMovieInfoOrderByUserRatingQuery);
        Cursor cursor = db.rawQuery(getAllMovieInfoOrderByUserRatingQuery, null);
        while (!cursor.isAfterLast()) {
            Gson gson = new Gson();
            MovieInfoModel movieInfo = new MovieInfoModel();
            movieInfo.setMovieId(cursor.getLong(cursor.getColumnIndex(MOVIE_ID)));
            movieInfo.setMovieOriginalTitle(cursor.getString(cursor.getColumnIndex(MOVIE_ORIGINAL_TITLE)));
            movieInfo.setMoviePlotSynopsis(cursor.getString(cursor.getColumnIndex(MOVIE_PLOT_SYNOPSIS)));
            movieInfo.setMovieUserRating(cursor.getString(cursor.getColumnIndex(MOVIE_USER_RATING)));
            movieInfo.setMovieReleaseDate(cursor.getString(cursor.getColumnIndex(MOVIE_RELEASE_DATE)));
            ArrayList<String> movieTrailerUrlArrayList = gson.fromJson(cursor.getString(cursor.getColumnIndex(MOVIE_TRAILER_URL_JSON_STRING)), new TypeToken<ArrayList<String>>() {
            }.getType());
            movieInfo.setMovieTrailerUrlArrayList(movieTrailerUrlArrayList);
            ArrayList<MovieReviewModel> movieReviewArrayList = gson.fromJson(cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_JSON_STRING)), new TypeToken<ArrayList<MovieReviewModel>>() {
            }.getType());

            movieInfo.setMovieReviewArrayList(movieReviewArrayList);
            allMovieInfoOrderByUserRatingArrayList.add(movieInfo);
        }
        return allMovieInfoOrderByUserRatingArrayList;
    }


//    public void updateData(Integer id, ) {
//        insertDataIntoUpdatedTable();
//        leftJoinUpdatedTableAndStoredTable();
//        changeNameOfLeftJoinTable();
//    }
//
//    public void insertDataIntoUpdatedTable() {
//
//    }
//
//    /**
//     * Method will left join updated table and stored table based on movie_id
//     */
//    public void leftJoinUpdatedTableAndStoredTable() {
//
//    }
//
//    /**
//     * Method that will change the name of table newly generated via SQL left join into 'stored_movie_info'
//     */
//    public void changeNameOfLeftJoinTable() {
//
//    }
}
