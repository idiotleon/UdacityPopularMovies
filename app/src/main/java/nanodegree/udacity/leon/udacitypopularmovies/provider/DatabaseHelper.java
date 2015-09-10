package nanodegree.udacity.leon.udacitypopularmovies.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private Context context;

    private static final String DATABASE_NAME = "movie_info.db";
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
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULAIRY + " REAL, " +
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
        // Get the Content Resolver
        ContentResolver contentResolver = context.getContentResolver();

        // Specify the result column projection. Return the minimum set of columns required to satisfy your requirements
        String[] resultColumns = new String[]{
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID
        };

        // Specify the where clause that will limit your results
        String where = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " =?";

        // Replace these with valid SQL statements as necessary
        String whereArgs[] = new String[]{Long.toString(movieInfo.getMovieId())};
        String orderBy = "DESC";

        Cursor resultCursor = contentResolver.query(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, resultColumns, where, whereArgs, orderBy);

        try {
            if (resultCursor.getCount() > 0)
                return true;
            else return false;
        } finally {
            resultCursor.close();
        }
    }

    public void insertMovieInfo(MediumMovieInfoModel movieInfo) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, movieInfo.getMovieId());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULAIRY, movieInfo.getMoviePopularity());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, checkFavoriteStatus(movieInfo));

        Uri insertedId = contentResolver.insert(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, contentValues);
        Log.v(LOG_TAG, "insertedId, insertMovieInfo(MediumMovieInfoModel movieInfo), DatabaseHelper: " + insertedId.toString());
    }

    public void updateMovieInfo(MediumMovieInfoModel movieInfo) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, movieInfo.getMovieId());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULAIRY, movieInfo.getMoviePopularity());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, checkFavoriteStatus(movieInfo));

        String where = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " = ?";
        String[] whereArgs = new String[]{Long.toString(movieInfo.getMovieId())};

        int updateCount = contentResolver.update(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, contentValues, where, whereArgs);
        Log.v(LOG_TAG, "updateCount, updateMovieInfo(MediumMovieInfoModel movieInfo), DatabaseHelper: " + updateCount);
    }

    public boolean checkFavoriteStatus(MediumMovieInfoModel movieInfo) {
        if (1 == GeneralHelper.getFavoriteStatus(context, Long.toString(movieInfo.getMovieId()), 0))
            return true;
        return false;
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
        ContentResolver contentResolver = context.getContentResolver();

        String[] resultColumns = {
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_ID,
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL
        };

        String where = MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + " = ?";
        String[] whereArgs = new String[]{Long.toString(movieId)};

        Cursor resultCursor = contentResolver.query(MovieInfoProviderContract.MovieTrailerEntry.CONTENT_URI, resultColumns, where, whereArgs, null);
        try {
            if (resultCursor.getCount() == 0)
                return false;
            else {
                resultCursor.moveToFirst();
                while (!resultCursor.isAfterLast()) {
                    if (movieTrailerUrl == resultCursor.getString(resultCursor.getColumnIndex(MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL)))
                        return true;
                }
            }
        } finally {
            resultCursor.close();
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

    public ArrayList<MediumMovieInfoModel> getAllMediumMovieInfo(int sortedByPopularityOrAveragedVoting, int sortDescOrAsc) {
        Log.v(LOG_TAG, "getAllMediumMovieInfo(int sortedByPopularityOrAveragedVoting, int sortDescOrAsc), DatabaseHelper executed.");
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            ArrayList<MediumMovieInfoModel> allMediumMovieInfoArrayList = new ArrayList<>();
            String getAllMovieInfoQuery = "SELECT * FROM " + GENERAL_MOVIE_INFO_TABLE_NAME;

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
            String query = "SELECT * FROM " + GENERAL_MOVIE_INFO_TABLE_NAME;
            Cursor cursor = db.rawQuery(query, null);
            return cursor.getCount();
        } finally {
            db.close();
        }
    }
}
