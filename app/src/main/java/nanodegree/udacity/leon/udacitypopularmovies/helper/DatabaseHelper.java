package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private Context context;

    public static final String DATABASE_NAME = "movie_info_database";
    public static final String MEDIUM_MOVIE_INFO_TABLE_NAME = "stored_movie_info_table";
    //    private static final String UPDATED_TABLE_NAME = "updated_movie_info";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_ORIGINAL_TITLE = "movie_original_title";
    public static final String MOVIE_IMAGE_URL = "movie_image_url";
    public static final String MOVIE_PLOT_SYNOPSIS = "movie_plot_synopsis";
    public static final String MOVIE_USER_RATING = "movie_rating";
    public static final String MOVIE_RELEASE_DATE = "movie_date";
    public static final String MOVIE_POPULAIRY = "movie_popularity";
    public static final String MOVIE_FAVORITE_STATUS = "movie_favorite_status";
    public static final int versioin = 1;

    /**
     * Fields, together with Movie_ID, to create Movie Trailer table
     */
    public static final String MOVIE_TRAILER_URL_TABLE_NAME = "movie_trailer_url_table";
    public static final String MOVIE_TRAILER_URL_ID = "movie_trailer_url_id";
    public static final String MOVIE_TRAILER_URL = "movie_trailer_url";
    public static final String MOVIE_TRAILER_URL_FOREIGN_KEY_ID = "movie_trailer_url_foreign_key_id";


    /**
     * Fields, together with Movie_ID, to create Movie Review table
     */
    public static final String MOVIE_REVIEW_TABLE_NAME = "movie_review_url_table";
    public static final String MOVIE_REVIEW_ID = "movie_review_id";
    public static final String MOVIE_REVIEW_AUTHOR = "movie_review_author";
    public static final String MOVIE_REVIEW_CONTENT = "movie_review_content";
    public static final String MOVIE_REVIEW_URL = "movie_review_url";
    public static final String MOVIE_REVIEW_URL_FOREIGN_KEY_ID = "movie_review_url_foreign_key_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versioin);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMediumMovieInfoTableQuery = "CREATE TABLE " + MEDIUM_MOVIE_INFO_TABLE_NAME + " (" + MOVIE_ID + " LONG PRIMARY KEY, " +
                MOVIE_ORIGINAL_TITLE + " TEXT, " +
                MOVIE_IMAGE_URL + " TEXT, " +
                MOVIE_PLOT_SYNOPSIS + " TEXT, " +
                MOVIE_USER_RATING + " REAL, " +
                // todo: Date type might be better
                MOVIE_RELEASE_DATE + " TEXT, " +
                MOVIE_POPULAIRY + " REAL, " +
                MOVIE_FAVORITE_STATUS + " BOOLEAN)";
        Log.v(LOG_TAG, "createMediumMovieInfoTableQuery: " + createMediumMovieInfoTableQuery);
        db.execSQL(createMediumMovieInfoTableQuery);

        String createMovieTrailerUrlBasedOnMovieIdQuery
                = "CREATE TABLE " + MOVIE_TRAILER_URL_TABLE_NAME +
                " (" + MOVIE_TRAILER_URL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOVIE_TRAILER_URL + " TEXT, " +
                MOVIE_TRAILER_URL_FOREIGN_KEY_ID + " LONG, " +
                "FOREIGN KEY (" + MOVIE_TRAILER_URL_FOREIGN_KEY_ID + ") " +
                "REFERENCES " + MEDIUM_MOVIE_INFO_TABLE_NAME + "(" + MOVIE_ID + "))";
        Log.v(LOG_TAG, "createMovieTrailerUrlBasedOnMovieIdQuery: " + createMovieTrailerUrlBasedOnMovieIdQuery);
        db.execSQL(createMovieTrailerUrlBasedOnMovieIdQuery);

        String createMovieReviewBasedOnMovieIdQuery
                = "CREATE TABLE " + MOVIE_REVIEW_TABLE_NAME +
                " (" + MOVIE_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOVIE_REVIEW_AUTHOR + " TEXT, " +
                MOVIE_REVIEW_CONTENT + " TEXT, " +
                MOVIE_REVIEW_URL + " TEXT, " +
                MOVIE_REVIEW_URL_FOREIGN_KEY_ID + " LONG, " +
                "FOREIGN KEY (" + MOVIE_REVIEW_URL_FOREIGN_KEY_ID + ") " +
                "REFERENCES " + MEDIUM_MOVIE_INFO_TABLE_NAME + "(" + MOVIE_ID + "))";
        db.execSQL(createMovieReviewBasedOnMovieIdQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String updateDatabaseQuery = "DROP TABLE IF EXISTS " + MEDIUM_MOVIE_INFO_TABLE_NAME;
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
        String checkStoredQuery = "SELECT " + MOVIE_ID + " FROM " + MEDIUM_MOVIE_INFO_TABLE_NAME +
                " WHERE " + MOVIE_ID + " = ?";
        Log.v(LOG_TAG, "checkStoredQuery: " + checkStoredQuery);
        Cursor cursor = db.rawQuery(checkStoredQuery, new String[]{Long.toString(movieInfo.getMovieId())});
        try {
            while (cursor.moveToNext()) {
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    public void insertMovieInfo(MediumMovieInfoModel movieInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MOVIE_ID, movieInfo.getMovieId());
            contentValues.put(MOVIE_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
            contentValues.put(MOVIE_IMAGE_URL, movieInfo.getMovieImageUrl());
            contentValues.put(MOVIE_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
            contentValues.put(MOVIE_USER_RATING, movieInfo.getMovieUserRating());
            contentValues.put(MOVIE_RELEASE_DATE, movieInfo.getMovieReleaseDate());
            contentValues.put(MOVIE_POPULAIRY, movieInfo.getMoviePopularity());
            contentValues.put(MOVIE_FAVORITE_STATUS, checkFavoriteStatus(movieInfo));
            db.insert(MEDIUM_MOVIE_INFO_TABLE_NAME, null, contentValues);
        } finally {
            db.close();
        }
    }

    public void updateMovieInfo(MediumMovieInfoModel movieInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MOVIE_ID, movieInfo.getMovieId());
            contentValues.put(MOVIE_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
            contentValues.put(MOVIE_IMAGE_URL, movieInfo.getMovieImageUrl());
            contentValues.put(MOVIE_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
            contentValues.put(MOVIE_USER_RATING, movieInfo.getMovieUserRating());
            contentValues.put(MOVIE_RELEASE_DATE, movieInfo.getMovieReleaseDate());
            contentValues.put(MOVIE_POPULAIRY, movieInfo.getMoviePopularity());
            contentValues.put(MOVIE_FAVORITE_STATUS, checkFavoriteStatus(movieInfo));

            db.update(MEDIUM_MOVIE_INFO_TABLE_NAME, contentValues, MOVIE_ID + " = ? ",
                    new String[]{Long.toString(movieInfo.getMovieId())});
        } finally {
            db.close();
        }
    }

    public boolean checkFavoriteStatus(MediumMovieInfoModel movieInfo) {
        if (1 == GeneralHelper.getFavoriteStatus(context, Long.toString(movieInfo.getMovieId()), 0))
            return true;
        return false;
    }

    public ArrayList<MediumMovieInfoModel> getAllMediumMovieInfo(int sortedByPopularityOrAveragedVoting, int sortDescOrAsc) {
        Log.v(LOG_TAG, "getAllMediumMovieInfo(int sortedByPopularityOrAveragedVoting, int sortDescOrAsc), DatabaseHelper executed.");
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            ArrayList<MediumMovieInfoModel> allMediumMovieInfoArrayList = new ArrayList<>();
            String getAllMovieInfoQuery = "SELECT * FROM " + MEDIUM_MOVIE_INFO_TABLE_NAME;

            switch (sortedByPopularityOrAveragedVoting) {
                case GeneralConstants.MOVIE_SORTED_BY_AVERAGED_VOTING:
                    getAllMovieInfoQuery += " ORDER BY " + MOVIE_USER_RATING;
                    break;
                default:
                    getAllMovieInfoQuery += " ORDER BY " + MOVIE_POPULAIRY;
            }

            switch (sortDescOrAsc) {
                case GeneralConstants.MOVIE_SORTED_ASC:
                    getAllMovieInfoQuery += " ASC";
                    break;
                default:
                    getAllMovieInfoQuery += " DESC";
            }

            getAllMovieInfoQuery += " LIMIT 20";
            Log.v(LOG_TAG, "getAllMovieInfoQuery, DatabaseHelper: " + getAllMovieInfoQuery);
            Cursor cursor = db.rawQuery(getAllMovieInfoQuery, null);
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long movieId = cursor.getLong(cursor.getColumnIndex(MOVIE_ID));
                    String movieOriginalTitle = cursor.getString(cursor.getColumnIndex(MOVIE_ORIGINAL_TITLE));
                    String movieImageUrl = cursor.getString(cursor.getColumnIndex(MOVIE_IMAGE_URL));
                    String moviePlotSynopsis = cursor.getString(cursor.getColumnIndex(MOVIE_PLOT_SYNOPSIS));
                    float movieUserRating = cursor.getFloat(cursor.getColumnIndex(MOVIE_USER_RATING));
                    String movieReleaseDate = cursor.getString(cursor.getColumnIndex(MOVIE_RELEASE_DATE));
                    double moviePopularity = cursor.getDouble(cursor.getColumnIndex(MOVIE_POPULAIRY));
                    MediumMovieInfoModel mediumMovieInfoModel = new MediumMovieInfoModel(
                            movieId, movieOriginalTitle, movieImageUrl, moviePlotSynopsis,
                            movieUserRating, movieReleaseDate, moviePopularity);
                    allMediumMovieInfoArrayList.add(mediumMovieInfoModel);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
            return allMediumMovieInfoArrayList;
        } finally {
            db.close();
        }
    }

    public int getMovieInfoStoredCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + MEDIUM_MOVIE_INFO_TABLE_NAME;
            Cursor cursor = db.rawQuery(query, null);
            return cursor.getCount();
        } finally {
            db.close();
        }
    }

    public void insertMovieTrailer(long movieId, String movieTrailerUrl) {
        if (!movieTrailerExists(movieId, movieTrailerUrl)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MOVIE_TRAILER_URL, movieTrailerUrl);
            contentValues.put(MOVIE_TRAILER_URL_FOREIGN_KEY_ID, movieId);
            try {
                db.insert(MOVIE_TRAILER_URL_TABLE_NAME, null, contentValues);
            } finally {
                db.close();
            }
        }
    }

    public boolean movieTrailerExists(long movieId, String movieTrailerUrl) {
        Log.v(LOG_TAG, "movieTrailerExists(long movieId, String movieTrailerUrl), DatabaseHelper executed");
        SQLiteDatabase db = this.getReadableDatabase();
        String checkMovieTrailerDuplicationRawQuery = "SELECT * FROM " + MOVIE_TRAILER_URL_TABLE_NAME +
                " WHERE " + MOVIE_TRAILER_URL_FOREIGN_KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(checkMovieTrailerDuplicationRawQuery, new String[]{Long.toString(movieId)});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String movieTrailerUrlFromDatabase = cursor.getString(cursor.getColumnIndex(MOVIE_TRAILER_URL));
                    if (movieTrailerUrl.equals(movieTrailerUrlFromDatabase)) {
                        return true;
                    }
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return false;
    }

    public void insertMovieReviews(long movieId, MovieReviewModel movieReview) {
        if (!movieReviewExists(movieId, movieReview)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MOVIE_REVIEW_AUTHOR, movieReview.getReviewAuthor());
            contentValues.put(MOVIE_REVIEW_URL, movieReview.getReviewUrl());
            contentValues.put(MOVIE_REVIEW_CONTENT, movieReview.getReviewContent());
            contentValues.put(MOVIE_REVIEW_URL_FOREIGN_KEY_ID, movieId);
            try {
                db.insert(MOVIE_REVIEW_TABLE_NAME, null, contentValues);
            } finally {
                db.close();
            }
        }
    }

    public boolean movieReviewExists(long movieId, MovieReviewModel movieReview) {
        Log.v(LOG_TAG, "movieReviewExists(long movieId, MovieReviewModel movieReview), DatabaseHelper executed");
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String checkMovieReviewDuplicationRawQuery = "SELECT * FROM " + MOVIE_REVIEW_TABLE_NAME +
                    " WHERE " + MOVIE_REVIEW_URL_FOREIGN_KEY_ID + " = ?";
            Cursor cursor = db.rawQuery(checkMovieReviewDuplicationRawQuery, new String[]{Long.toString(movieId)});
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String movieReviewAuthorFromDatabase = cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_AUTHOR));
                        if ((movieReview.getReviewAuthor()).equals(movieReviewAuthorFromDatabase)) {
                            String movieReviewUrlFromDatabase = cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_URL));
                            if ((movieReview.getReviewUrl()).equals(movieReviewUrlFromDatabase)) {
                                String movieReviewContentFromDatabase = cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_CONTENT));
                                if ((movieReview.getReviewContent()).equals(movieReviewContentFromDatabase)) {
                                    return true;
                                }
                            }
                        }
                        cursor.moveToNext();
                    }
                }
            } finally {
                cursor.close();
            }
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<String> getMovieTrailerUrlsBasedOnMovieId(long movieId) {
        ArrayList<String> movieTrailerUrlArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String getMovieTrailerUrlsRawQuery = "SELECT * FROM " + MOVIE_TRAILER_URL_TABLE_NAME +
                    " WHERE " + MOVIE_TRAILER_URL_FOREIGN_KEY_ID + " = ?";
            Log.v(LOG_TAG, "getMovieTrailerUrlsRawQuery, DatabaseHelper: " + getMovieTrailerUrlsRawQuery);
            Cursor cursor = db.rawQuery(getMovieTrailerUrlsRawQuery, new String[]{Long.toString(movieId)});
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String movieTrailerUrl = cursor.getString(cursor.getColumnIndex(MOVIE_TRAILER_URL));
                        Log.v(LOG_TAG, "movieTrailerUrl, getMovieTrailerUrlsBasedOnMovieId(), DatabaseHelper: " + movieTrailerUrl);
                        movieTrailerUrlArrayList.add(movieTrailerUrl);
                        cursor.moveToNext();
                    }
                }
            } finally {
                cursor.close();
            }
            return movieTrailerUrlArrayList;
        } finally {
            db.close();
        }
    }

    public ArrayList<MovieReviewModel> getMovieReviewsBasedOnMovieId(long movieId) {
        ArrayList<MovieReviewModel> movieReviewsArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String getMovieReviewsQuery = "SELECT * FROM " + MOVIE_REVIEW_TABLE_NAME +
                    " WHERE " + MOVIE_REVIEW_URL_FOREIGN_KEY_ID + " = " + movieId;
            Log.v(LOG_TAG, "getMovieReviewsQuery, DatabaseHelper: " + getMovieReviewsQuery);
            Cursor cursor = db.rawQuery(getMovieReviewsQuery, null);
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String reviewContent = cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_CONTENT));
                        String reviewAuthor = cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_AUTHOR));
                        String reviewUrl = cursor.getString(cursor.getColumnIndex(MOVIE_REVIEW_URL));
                        MovieReviewModel movieReviewModel = new MovieReviewModel(reviewAuthor, reviewContent, reviewUrl);
                        movieReviewsArrayList.add(movieReviewModel);
                        cursor.moveToNext();
                    }
                }
            } finally {
                cursor.close();
            }
            return movieReviewsArrayList;
        } finally {
            db.close();
        }
    }
}
