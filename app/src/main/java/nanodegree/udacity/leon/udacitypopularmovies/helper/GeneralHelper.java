package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieTrailerModel;
import nanodegree.udacity.leon.udacitypopularmovies.provider.MovieInfoProviderContract;

public class GeneralHelper {
    private static final String LOG_TAG = GeneralHelper.class.getSimpleName();

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void markAsFavorite(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, 1).commit();

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, key);
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, 1);

        contentResolver.update(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI,
                contentValues, MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID,
                new String[]{key});
    }


    public static void cancelFavoriteStatus(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, 0).commit();

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, key);
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, 0);

        contentResolver.update(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI,
                contentValues, MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID,
                new String[]{key});
    }

    public static int getFavoriteStatus(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static ArrayList<MediumMovieInfoModel> getAllFavoriteMediumMovieInfoAsArrayList(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        ArrayList<MediumMovieInfoModel> mediumMovieInfoModelArrayList = new ArrayList<>();

        String selection = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS + " = ?";
        String[] selectionArgs = new String[]{"1"};
        String orderBy = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE;
        Cursor cursor = contentResolver.query(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, null,
                selection, selectionArgs, orderBy);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long movieId = cursor.getLong(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID));
                String movieOriginalTitle = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE));
                String movieImageUrl = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL));
                String moviePlotSynopsis = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS));
                float movieUserRating = cursor.getFloat(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING));
                String movieReleaseDate = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE));
                double moviePopularity = cursor.getDouble(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY));
                MediumMovieInfoModel mediumMovieInfoModel = new MediumMovieInfoModel(
                        movieId, movieOriginalTitle, movieImageUrl, moviePlotSynopsis,
                        movieUserRating, movieReleaseDate, moviePopularity);
                mediumMovieInfoModelArrayList.add(mediumMovieInfoModel);
                cursor.moveToNext();
            }
        }

        return mediumMovieInfoModelArrayList;
    }

    /**
     * Method is to get all the json data from the input URL as String.
     * <improvement>: there should be URL check methods
     *
     * @param defaultUrl
     * @return
     */
    public static String getAllJsonDataAsStringFromAPI(URL defaultUrl) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        try {
            urlConnection = (HttpURLConnection) defaultUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
//                Log.v(LOG_TAG, "inputStream - getAllJsonDataAsStringFromAPI(): " + inputStream.toString());
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

//            Log.v(LOG_TAG, "moviesJsonStr - getAllJsonDataAsStringFromAPI(): " + moviesJsonStr);
        return moviesJsonStr;
    }

/*    public static void updateDatabaseMovieInfo(Context context, ArrayList<MediumMovieInfoModel> mediumMovieInfoModelArrayList) {
        for (int i = 0; i < mediumMovieInfoModelArrayList.size(); i++) {
            if (checkMovieInfoStored(context, mediumMovieInfoModelArrayList.get(i))) {
                updateMovieInfo(context, mediumMovieInfoModelArrayList.get(i));
            } else {
                insertMovieInfo(context, mediumMovieInfoModelArrayList.get(i));
            }
        }
    }*/

    public static boolean checkMovieInfoStored(Context context, MediumMovieInfoModel movieInfo) {
        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "movieInfo, checkMovieInfoStored - Line86, DatabaseHelper: " + movieInfo.toString());
        // Specify the result column projection. Return the minimum set of columns required to satisfy your requirements
        String[] resultColumns = new String[]{MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID};

        // Specify the where clause that will limit your results
        String selection = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " =?";

        // Replace these with valid SQL statements as necessary
        String selectionArgs[] = new String[]{Long.toString(movieInfo.getMovieId())};
        String orderBy = "";

        Cursor resultCursor = contentResolver.query(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, resultColumns, selection, selectionArgs, orderBy);

        try {
            if (resultCursor.getCount() > -1)
                return true;
            else return false;
        } finally {
            resultCursor.close();
        }
    }

    public static boolean checkFavoriteStatus(Context context, MediumMovieInfoModel movieInfo) {
        if (1 == GeneralHelper.getFavoriteStatus(context, Long.toString(movieInfo.getMovieId()), 0))
            return true;
        return false;
    }

    public static void insertMovieInfo(Context context, MediumMovieInfoModel movieInfo) {
        Log.v(LOG_TAG, "insertMovieInfo(Context context, MediumMovieInfoModel movieInfo) executed.");
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, movieInfo.getMovieId());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY, movieInfo.getMoviePopularity());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, checkFavoriteStatus(context, movieInfo));

        try {
            Uri insertedId = contentResolver.insert(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, contentValues);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
//        if (insertedId == null)
//            Log.v(LOG_TAG, "insertedId, insertMovieInfo(MediumMovieInfoModel movieInfo), is null");
//        Log.v(LOG_TAG, "insertedId, insertMovieInfo(MediumMovieInfoModel movieInfo), DatabaseHelper: " + insertedId.getEncodedPath());
    }

    public static void updateMovieInfo(Context context, MediumMovieInfoModel movieInfo) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, movieInfo.getMovieId());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE, movieInfo.getMovieOriginalTitle());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL, movieInfo.getMovieImageUrl());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS, movieInfo.getMoviePlotSynopsis());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING, movieInfo.getMovieUserRating());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE, movieInfo.getMovieReleaseDate());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY, movieInfo.getMoviePopularity());
        contentValues.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, checkFavoriteStatus(context, movieInfo));

        String selection = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(movieInfo.getMovieId())};

        int updateCount = contentResolver.update(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI,
                contentValues, selection, selectionArgs);
        Log.v(LOG_TAG, "updateCount, updateMovieInfo(MediumMovieInfoModel movieInfo), DatabaseHelper: " + updateCount);
    }

    public static void insertMovieTrailer(Context context, MovieTrailerModel movieTrailerModel) {
        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "insertMovieTrailer(MovieTrailerModel movieTrailerModel), DatabaseHelper executed.");
        if (!movieTrailerExists(context, movieTrailerModel)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL, movieTrailerModel.getMovieTrailerUrl());
            contentValues.put(MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID, movieTrailerModel.getMovieId());

            contentResolver.insert(MovieInfoProviderContract.MovieTrailerEntry.CONTENT_URI, contentValues);
        }
    }

    public static boolean movieTrailerExists(Context context, MovieTrailerModel movieTrailerModel) {
        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "movieTrailerExists(long movieId, String movieTrailerUrl), DatabaseHelper executed");
        String[] projection = {
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID,
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL
        };

        String selection = MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + " = ? AND " +
                MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL + " = ?";
        String[] selectionArgs = new String[]{Long.toString(movieTrailerModel.getMovieId()), movieTrailerModel.getMovieTrailerUrl()};

        Cursor resultCursor = contentResolver.query(MovieInfoProviderContract.MovieTrailerEntry.CONTENT_URI,
                projection, selection, selectionArgs, null);

        try {
            if (resultCursor.getCount() == 0)
                return false;
            else {
                return true;
            }
        } finally {
            resultCursor.close();
        }
    }

    public static void insertMovieReviews(Context context, MovieReviewModel movieReview) {
        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "insertMovieReviews(MovieReviewModel movieReview) , DatabaseHelper executed.");
        if (!movieReviewExists(context, movieReview)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_AUTHOR, movieReview.getReviewAuthor());
            contentValues.put(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_URL, movieReview.getReviewUrl());
            contentValues.put(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_CONTENT, movieReview.getReviewContent());
            contentValues.put(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID, movieReview.getMovieId());
            contentResolver.insert(MovieInfoProviderContract.MovieReviewEntry.CONTENT_URI, contentValues);
        }
    }

    public static boolean movieReviewExists(Context context, MovieReviewModel movieReview) {
        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "movieReviewExists(MovieReviewModel movieReview), DatabaseHelper executed");
        String[] projection = new String[]{
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_AUTHOR,
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_CONTENT,
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID
        };

        String selection = MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID + " = ? AND " +
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_AUTHOR + " = ? AND " +
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_CONTENT + " = ?";
        String[] selectionArgs = new String[]{Long.toString(movieReview.getMovieId()), movieReview.getReviewAuthor(), movieReview.getReviewContent()};

        Cursor cursor = contentResolver.query(MovieInfoProviderContract.MovieReviewEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        try {
            if (cursor.getCount() > -1)
                return true;
        } finally {
            cursor.close();
        }
        return false;
    }

    public static ArrayList<String> getMovieTrailerUrls(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL};
        String selection = MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_FOREIGN_KEY_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(movieId)};
        Cursor resultCursor = contentResolver.query(MovieInfoProviderContract.MovieTrailerEntry.CONTENT_URI,
                projection, selection, selectionArgs, null);

        if (resultCursor.getCount() > -1) {
            ArrayList<String> movieTrailerUrlArrayList = new ArrayList<>();
            resultCursor.moveToFirst();
            while (!resultCursor.isAfterLast()) {
                movieTrailerUrlArrayList.add(resultCursor.getString(resultCursor.getColumnIndex(MovieInfoProviderContract.MovieTrailerEntry.MOVIE_TRAILER_COLUMN_URL)));
                resultCursor.moveToNext();
            }
            return movieTrailerUrlArrayList;
        } else {
            return new ArrayList<String>();
        }
    }

    public static ArrayList<MovieReviewModel> getMovieReviews(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "getMovieReviews(long movieId), DatabaseHelper executed.");

        ArrayList<MovieReviewModel> movieReviewsArrayList = new ArrayList<>();
        String[] projection = new String[]{
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_AUTHOR,
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_CONTENT,
                MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_URL
        };

        String selection = MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_FOREIGN_KEY_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(movieId)};

        Cursor resultCursor = contentResolver.query(MovieInfoProviderContract.MovieReviewEntry.CONTENT_URI, projection, selection, selectionArgs, null);

        try {
            if (resultCursor.getCount() > 0) {
                resultCursor.moveToFirst();
                while (!resultCursor.isAfterLast()) {
                    String movieReviewAuthor = resultCursor.getString(resultCursor.getColumnIndex(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_AUTHOR));
                    String movieReviewContent = resultCursor.getString(resultCursor.getColumnIndex(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_CONTENT));
                    String movieReviewUrl = resultCursor.getString(resultCursor.getColumnIndex(MovieInfoProviderContract.MovieReviewEntry.MOVIE_REVIEW_COLUMN_URL));
                    MovieReviewModel movieReviewModel = new MovieReviewModel(movieId, movieReviewAuthor, movieReviewContent, movieReviewUrl);
                    movieReviewsArrayList.add(movieReviewModel);
                    resultCursor.moveToNext();
                }
            } else {
                Log.e(LOG_TAG, "resultCursor, getMovieReviews(Context context, long movieId), GeneralHelper is null.");
                return new ArrayList<MovieReviewModel>();
            }
        } finally {
            Log.v(LOG_TAG, "resultCursor.getCount(), getMovieReviews(Context context, long movieId), GeneralHelper: " + resultCursor.getCount());
            resultCursor.close();
        }

        Log.v(LOG_TAG, "movieReviewsArrayList, getMovieReviews(Context context, long movieId), GeneralHelper is Empty or not: " + movieReviewsArrayList.isEmpty());
        return movieReviewsArrayList;
    }

    public static ArrayList<MediumMovieInfoModel> getAllMediumMovieInfo(Context context, int orderByPopularityOrAveragedVoting, int sortDescOrAsc) {

        ContentResolver contentResolver = context.getContentResolver();
        Log.v(LOG_TAG, "getAllMediumMovieInfo(Context context, int sortedByPopularityOrAveragedVoting, int sortDescOrAsc), DatabaseHelper executed.");
        ArrayList<MediumMovieInfoModel> allMediumMovieInfoArrayList = new ArrayList<>();

        String projection[] = new String[]{
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID,
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE,
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL,
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS,
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING,
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE,
                MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY
        };

        String selection = null;
        String[] selectionArgs = null;

        String orderBy;
        switch (orderByPopularityOrAveragedVoting) {
            case GeneralConstants.MOVIE_SORTED_BY_AVERAGED_VOTING:
                orderBy = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING;
                switch (sortDescOrAsc) {
                    case GeneralConstants.MOVIE_SORTED_ASC:
                        orderBy += " ASC";
                        break;
                    default:
                        orderBy += " DESC";
                        break;
                }
                break;
            default:
                orderBy = MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY;
                switch (sortDescOrAsc) {
                    case GeneralConstants.MOVIE_SORTED_ASC:
                        orderBy += " ASC";
                        break;
                    default:
                        orderBy += " DESC";
                        break;
                }
                break;
        }
        orderBy += " LIMIT 20";

        Cursor cursor = contentResolver.query(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI, projection, selection, selectionArgs, orderBy);
        try {
            if (cursor.getCount() > -1) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long movieId = cursor.getLong(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID));
                    String movieOriginalTitle = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE));
                    String movieImageUrl = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL));
                    String moviePlotSynopsis = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS));
                    float movieUserRating = cursor.getFloat(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING));
                    String movieReleaseDate = cursor.getString(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE));
                    double moviePopularity = cursor.getDouble(cursor.getColumnIndex(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY));
                    MediumMovieInfoModel mediumMovieInfoModel = new MediumMovieInfoModel(
                            movieId, movieOriginalTitle, movieImageUrl, moviePlotSynopsis,
                            movieUserRating, movieReleaseDate, moviePopularity);
                    allMediumMovieInfoArrayList.add(mediumMovieInfoModel);
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return allMediumMovieInfoArrayList;
    }

    public static int getMovieInfoStoredCount(Context context) {
        ArrayList<MediumMovieInfoModel> mediumMovieInfoArrayList
                = getAllMediumMovieInfo(context, GeneralConstants.MOVIE_SORTED_BY_POPULARITY, GeneralConstants.MOVIE_SORTED_DESC);
        return mediumMovieInfoArrayList.size();
    }
}
