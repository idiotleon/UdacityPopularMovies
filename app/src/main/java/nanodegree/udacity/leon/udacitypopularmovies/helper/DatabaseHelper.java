package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.adapter.CustomGridViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.model.CompleteMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private Context context;

    public static final String DATABASE_NAME = "movie_info_database";
    public static final String MEDIUM_MOVIEINFO_TABLE_NAME = "stored_movie_info";
    //    private static final String UPDATED_TABLE_NAME = "updated_movie_info";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_ORIGINAL_TITLE = "movie_original_title";
    public static final String MOVIE_IMAGE_URL = "movie_image_url";
    public static final String MOVIE_PLOT_SYNOPSIS = "movie_plot_synopsis";
    public static final String MOVIE_USER_RATING = "movie_rating";
    public static final String MOVIE_RELEASE_DATE = "movie_date";
    public static final String MOVIE_FAVORITE_STATUS = "movie_favorite_status";
    public static final int versioin = 1;

    public static final String MOVIE_TRAILER_URL_JSON_STRING = "movie_trailer_url_json_string";
    public static final String MOVIE_REVIEW_JSON_STRING = "movie_review_json_string";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versioin);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMediumMovieInfoTableQuery = "CREATE TABLE " + MEDIUM_MOVIEINFO_TABLE_NAME + " (" + MOVIE_ID + " LONG PRIMARY KEY, " +
                MOVIE_ORIGINAL_TITLE + " TEXT, " +
                MOVIE_IMAGE_URL + " TEXT, " +
                MOVIE_PLOT_SYNOPSIS + " TEXT, " +
                MOVIE_USER_RATING + " FLOAT, " +
                // todo: Date type might be better
                MOVIE_RELEASE_DATE + " TEXT, " +
                MOVIE_FAVORITE_STATUS + " BOOLEAN)";
        Log.v(LOG_TAG, "createMediumMovieInfoTableQuery: " + createMediumMovieInfoTableQuery);
        db.execSQL(createMediumMovieInfoTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String updateDatabaseQuery = "DROP TABLE IF EXISTS " + MEDIUM_MOVIEINFO_TABLE_NAME;
        db.execSQL(updateDatabaseQuery);
    }

    public void updateDatabaseMovieInfo(ArrayList<MediumMovieInfoModel> mediumMovieInfoModelArrayList) {
        for (int i = 0; i < mediumMovieInfoModelArrayList.size(); i++) {
            if (checkMovieInfoStored(mediumMovieInfoModelArrayList.get(i))) {
                updateMovieInfo(mediumMovieInfoModelArrayList.get(i));
            } else {
                insertMovieInfo(mediumMovieInfoModelArrayList.get(i));
            }
        }
    }

    public boolean checkMovieInfoStored(MediumMovieInfoModel movieInfo) {
        Log.v(LOG_TAG, "movieInfo, checkMovieInfoStored - Line86, DatabaseHelper: " + movieInfo.toString());
        SQLiteDatabase db = this.getReadableDatabase();
        String checkStoredQuery = "SELECT " + MOVIE_ID + " FROM " + MEDIUM_MOVIEINFO_TABLE_NAME + " WHERE " + MOVIE_ID + " = " + movieInfo.getMovieId();
        Log.v(LOG_TAG, "checkStoredQuery: " + checkStoredQuery);
        Cursor cursor = db.rawQuery(checkStoredQuery, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public void insertMovieInfo(MediumMovieInfoModel movieInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID, movieInfo.getMovieId());
        contentValues.put(MOVIE_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MOVIE_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MOVIE_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MOVIE_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MOVIE_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MOVIE_FAVORITE_STATUS, CheckFavoriteStatus(movieInfo));
        db.insert(MEDIUM_MOVIEINFO_TABLE_NAME, null, contentValues);
    }

    public void updateMovieInfo(MediumMovieInfoModel movieInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID, movieInfo.getMovieId());
        contentValues.put(MOVIE_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MOVIE_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MOVIE_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MOVIE_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MOVIE_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MOVIE_FAVORITE_STATUS, CheckFavoriteStatus(movieInfo));
        db.update(MEDIUM_MOVIEINFO_TABLE_NAME, contentValues, MOVIE_ID + " = ? ", new String[]{Long.toString(movieInfo.getMovieId())});
    }

    public boolean CheckFavoriteStatus(MediumMovieInfoModel movieInfo) {
        if (1 == GeneralHelper.getFavoriteStatus(context, Long.toString(movieInfo.getMovieId()), 0))
            return true;
        return false;
    }

    public ArrayList<MediumMovieInfoModel> getAllMediumMovieInfo() {
        Log.v(LOG_TAG, "getAllMediumMovieInfo(), DatabaseHelper executed.");
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MediumMovieInfoModel> allMediumMovieInfoArrayList = new ArrayList<>();

        String getAllMovieInfoQuery = "SELECT * FROM " + MEDIUM_MOVIEINFO_TABLE_NAME;
        Cursor cursor = db.rawQuery(getAllMovieInfoQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MediumMovieInfoModel movieInfo = new MediumMovieInfoModel(
                    cursor.getLong(cursor.getColumnIndex(MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndex(MOVIE_ORIGINAL_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MOVIE_IMAGE_URL)),
                    cursor.getString(cursor.getColumnIndex(MOVIE_PLOT_SYNOPSIS)),
                    cursor.getString(cursor.getColumnIndex(MOVIE_USER_RATING)),
                    cursor.getString(cursor.getColumnIndex(MOVIE_RELEASE_DATE)));
            allMediumMovieInfoArrayList.add(movieInfo);
        }
        return allMediumMovieInfoArrayList;
    }


    public boolean checkMovieInfoStored(CompleteMovieInfoModel movieInfo) {
        Log.v(LOG_TAG, "movieInfo, checkMovieInfoStored - Line81, DatabaseHelper: " + movieInfo.toString());
        SQLiteDatabase db = this.getReadableDatabase();
        String checkStoredQuery = "SELECT " + MOVIE_ID + " FROM " + MEDIUM_MOVIEINFO_TABLE_NAME + " WHERE " + MOVIE_ID + " = " + movieInfo.getMovieId().toString();
        Log.v(LOG_TAG, "checkStoredQuery: " + checkStoredQuery);
        Cursor cursor = db.rawQuery(checkStoredQuery, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public void insertMovieInfo(CompleteMovieInfoModel movieInfo) {
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
        db.insert(MEDIUM_MOVIEINFO_TABLE_NAME, null, contentValues);
    }

    public void updateMovieInfo(CompleteMovieInfoModel movieInfo) {
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
        db.update(MEDIUM_MOVIEINFO_TABLE_NAME, contentValues, MOVIE_ID + " = ? ", new String[]{Long.toString(movieInfo.getMovieId())});
    }

    public String GsonizeData(ArrayList<?> arrayList) {
        Gson gson = new Gson();
        String gsonizedData = gson.toJson(arrayList);
        Log.v(LOG_TAG, "gsonizedData: " + gsonizedData);
        return gsonizedData;
    }

    public boolean CheckFavoriteStatus(CompleteMovieInfoModel movieInfo) {
        if (1 == GeneralHelper.getFavoriteStatus(context, movieInfo.getMovieId().toString(), 0))
            return true;
        return false;
    }


    public ArrayList<CompleteMovieInfoModel> getAllMovieInfoOrderByUserRating() {
        Log.v(LOG_TAG, "getAllMovieInfoOrderByUserRating() executed.");
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CompleteMovieInfoModel> allMovieInfoOrderByUserRatingArrayList = new ArrayList<>();

        String getAllMovieInfoOrderByUserRatingQuery = "SELECT * FROM " + MEDIUM_MOVIEINFO_TABLE_NAME + " ORDER BY " + MOVIE_USER_RATING;
        Log.v(LOG_TAG, "getAllMovieInfoOrderByUserRatingQuery: " + getAllMovieInfoOrderByUserRatingQuery);
        Cursor cursor = db.rawQuery(getAllMovieInfoOrderByUserRatingQuery, null);
        while (!cursor.isAfterLast()) {
            Gson gson = new Gson();
            CompleteMovieInfoModel movieInfo = new CompleteMovieInfoModel();
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
