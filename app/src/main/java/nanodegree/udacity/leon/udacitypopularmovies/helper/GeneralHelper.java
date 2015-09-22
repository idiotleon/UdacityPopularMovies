package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
<<<<<<<HEAD
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
=======
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
>>>>>>>e6cce583ad40b3ac8aa5321a49158327f94244a9

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
<<<<<<<HEAD
import java.net.URL;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieTrailerModel;
import nanodegree.udacity.leon.udacitypopularmovies.provider.MovieInfoProviderContract;
=======
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;
>>>>>>>e6cce583ad40b3ac8aa5321a49158327f94244a9

public class GeneralHelper {
    private static final String LOG_TAG = GeneralHelper.class.getSimpleName();

    public static final boolean FAVORITE_STATUS_TRUE = true;
    public static final int FAVORITE_STATUS_TRUE_STATUS_CODE = 1;
    public static final boolean FAVORITE_STATUS_FALSE = false;
    public static final int FAVORITE_STATUS_FALSE_STATUS_CODE = 0;

    private static final String LOG_TAG = GeneralHelper.class.getSimpleName();

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void markAsFavorite(Context context, MediumMovieInfoModel mediumMovieInfoModel) {
        String key = Long.toString(mediumMovieInfoModel.getMovieId());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, FAVORITE_STATUS_TRUE_STATUS_CODE).commit();

        changeFavoriteStatusCode(context, mediumMovieInfoModel, FAVORITE_STATUS_TRUE);
    }


    public static void cancelFavoriteStatus(Context context, MediumMovieInfoModel mediumMovieInfoModel) {
        String key = Long.toString(mediumMovieInfoModel.getMovieId());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, FAVORITE_STATUS_FALSE_STATUS_CODE).commit();

        changeFavoriteStatusCode(context, mediumMovieInfoModel, FAVORITE_STATUS_FALSE);
    }

    public static void changeFavoriteStatusCode(Context context, MediumMovieInfoModel mediumMovieInfoModel, boolean status) {
        String movieId = Long.toString(mediumMovieInfoModel.getMovieId());
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues updatedValue = new ContentValues();
        // The only row that has to be updated
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_FAVORITE_STATUS, status);
        // The other rows remain the same
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID, mediumMovieInfoModel.getMovieId());
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ORIGINAL_TITLE, mediumMovieInfoModel.getMovieOriginalTitle());
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_IMAGE_URL, mediumMovieInfoModel.getMovieImageUrl());
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_PLOT_SYNOPSIS, mediumMovieInfoModel.getMoviePlotSynopsis());
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_USER_RATING, mediumMovieInfoModel.getMovieUserRating());
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_RELEASE_DATE, mediumMovieInfoModel.getMovieReleaseDate());
        updatedValue.put(MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_POPULARITY, mediumMovieInfoModel.getMoviePopularity());
/*        int updateCount = contentResolver.update(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI,
                updatedValue, MovieInfoProviderContract.GeneralMovieInfoEntry.MOVIE_COLUMN_ID + " = ?",
                new String[]{movieId});*/
        Uri updateUri = Uri.parse(MovieInfoProviderContract.GeneralMovieInfoEntry.CONTENT_URI + "/" + movieId);
        int updateCount = contentResolver.update(updateUri,
                updatedValue, null, null);
        Log.v(LOG_TAG, "updateCount, changeFavoriteStatusCode(Context context, String key, int statusCode): " + updateCount);
        Log.v(LOG_TAG, "key, changeFavoriteStatusCode(Context context, String key, int statusCode): " + movieId);
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

    <<<<<<<HEAD
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
        Log.v(LOG_TAG, "allMediumMovieInfoArrayList.size(), getAllMediumMovieInfo(Context context, " +
                "int orderByPopularityOrAveragedVoting, int sortDescOrAsc): " + allMediumMovieInfoArrayList.size());
        return allMediumMovieInfoArrayList;
    }

    public static int getMovieInfoStoredCount(Context context) {
        ArrayList<MediumMovieInfoModel> mediumMovieInfoArrayList
                = getAllMediumMovieInfo(context,
                GeneralConstants.MOVIE_SORTED_BY_POPULARITY,
                GeneralConstants.MOVIE_SORTED_DESC);
        return mediumMovieInfoArrayList.size();
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    =======

    /**
     * JSON parsing method for udacity_popular_movie info
     *
     * @param moviesJsonStr
     * @return
     * @throws JSONException
     */
    public static ArrayList<MovieInfoModel> parseJsonDataForMovieInfo(String moviesJsonStr) throws JSONException, MalformedURLException {

        final String OWN_RESULTS = "results";
        final String OWN_MOVIE_ID = "id";
        final String OWN_ORIGINAL_TITLE = "original_title";
        final String OWN_MOVIE_PLOT_SYNOPSIS = "overview";
        final String OWN_MOVIE_USER_RATING = "vote_average";
        final String OWN_RELEASE_DATE = "release_date";
        final String OWN_POSTER_PATH = "poster_path";

        // base URL for poster images
        final String BASE_POSTER_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

        Long movieId;
        String movieOriginalTitle;
        String moviePlotSynopsis;
        String movieUserRating;
        String movieReleaseDate;
        String moviePosterUrl;
        ArrayList<String> movieTrailerUrlArrayList;
        ArrayList<MovieReviewModel> movieReviewArrayList;

        JSONObject moviesJsonObject = new JSONObject(moviesJsonStr);
        JSONArray moviesJsonObjectArray = moviesJsonObject.getJSONArray(OWN_RESULTS);

        ArrayList<MovieInfoModel> moviesInfoAsArrayList = new ArrayList<MovieInfoModel>();
        for (int i = 0; i < moviesJsonObjectArray.length(); i++) {

            JSONObject itemJson = moviesJsonObjectArray.getJSONObject(i);

            movieId = itemJson.getLong(OWN_MOVIE_ID);
//            Log.v(LOG_TAG, "MOVIE_ID, parseJsonDataForMovieInfo(): " + movieId);
            movieOriginalTitle = itemJson.getString(OWN_ORIGINAL_TITLE);
//            Log.v(LOG_TAG, "MOVIE_ORIGINAL_TITLE, parseJsonDataForMovieInfo(): " + movieOriginalTitle);
            moviePlotSynopsis = itemJson.getString(OWN_MOVIE_PLOT_SYNOPSIS);
//            Log.v(LOG_TAG, "MOVIE_PLOT_SYNOPSIS, parseJsonDataForMovieInfo(): " + moviePlotSynopsis);
            movieUserRating = itemJson.getString(OWN_MOVIE_USER_RATING);
//            Log.v(LOG_TAG, "MOVIE_USER_RATING, parseJsonDataForMovieInfo(): " + movieUserRating);
            movieReleaseDate = itemJson.getString(OWN_RELEASE_DATE);
//            Log.v(LOG_TAG, "MOVIE_RELEASE_DATE, parseJsonDataForMovieInfo(): " + movieReleaseDate);
            moviePosterUrl = BASE_POSTER_IMAGE_URL + itemJson.getString(OWN_POSTER_PATH);
//            Log.v(LOG_TAG, "MOVIE_POSTER_IMAGE_URL, parseJsonDataForMovieInfo(): " + moviePosterUrl);

/*            movieTrailerUrlArrayList = parseJsonDataForMovieTrailerUrl(movieId);
            movieReviewArrayList = parseJsonDataForMovieReview(movieId);*/

            MediumMovieInfoModel movieModelWithoutTrailerOrReviews = new MediumMovieInfoModel(movieId, movieOriginalTitle, moviePosterUrl, moviePlotSynopsis, movieUserRating, movieReleaseDate);
            moviesInfoAsArrayList.add(movieModelWithoutTrailerOrReviews);
        }
//            Log.v(LOG_TAG, "moviesInfoAsArrayList - parseJsonDataForMovieInfo(): " + moviesInfoAsArrayList.toString());
//            Log.v(LOG_TAG, "moviesInfoAsArrayList.size() - parseJsonDataForMovieInfo(): " + moviesInfoAsArrayList.size());

        return moviesInfoAsArrayList;
    }

    /**
     * For each specific udacity_popular_movie id, this method will get all the "key"s from API/JSON data, when combined with base Youtube URL, return
     * an ArrayList of all trailer urls, which can be played directly
     *
     * @param movieId
     * @return
     * @throws MalformedURLException
     * @throws JSONException
     */

    public static ArrayList<String> parseJsonDataForMovieTrailerUrl(Long movieId) throws MalformedURLException, JSONException {


        // base API URL for fetching trailer id
        final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/udacity_popular_movie/";
        // base Youtube URL for displaying trailer
        final String BASE_YOUTUBE_URL = "http://www.youtube.com/v/";
        final String PARAM_VIDEO = "/videos?";
        final String OWN_RESULTS = "results";
        final String OWN_KEY = "key";

        String movieTrailerAPIUrl = BASE_API_TRAILER_URL + movieId.toString() + PARAM_VIDEO + GeneralConstants.PARAM_API_KEY + "=" + GeneralConstants.API_KEY;
//            Log.v(LOG_TAG, "movieTrailerAPIUrl - MainActivity: " + movieTrailerAPIUrl);
        URL movieTrailerAPIURL = new URL(movieTrailerAPIUrl);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieTrailerAPIURL), Line325: " + getAllJsonDataAsStringFromAPI(movieTrailerAPIURL));
        JSONObject movieTrailerAllJsonDataObject = new JSONObject(getAllJsonDataAsStringFromAPI(movieTrailerAPIURL));

        JSONArray movieTrailerInfoJsonArray = movieTrailerAllJsonDataObject.getJSONArray(OWN_RESULTS);
//            Log.v(LOG_TAG, "movieTrailerInfoJsonArray: " + movieTrailerInfoJsonArray);

        ArrayList<String> movieTrailerUrlArrayList = new ArrayList<>();
        for (int i = 0; i < movieTrailerInfoJsonArray.length(); i++) {
            JSONObject itemJson = movieTrailerInfoJsonArray.getJSONObject(i);
            String key = itemJson.getString(OWN_KEY);
//                Log.v(LOG_TAG, "key: " + key);
            String url = BASE_YOUTUBE_URL + key;
//                Log.v(LOG_TAG, "url: " + url);
            movieTrailerUrlArrayList.add(url);
        }
        return movieTrailerUrlArrayList;
    }

    public static ArrayList<MovieReviewModel> parseJsonDataForMovieReview(Long movieId) throws MalformedURLException, JSONException {

        // base API URL for fetching reviews
        final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/udacity_popular_movie/";
        final String PARAM_REVIEWS = "/reviews?";

        final String OWN_RESULTS = "results";
        final String OWN_AUTHOR = "author";
        final String OWN_CONTENT = "content";
        final String OWN_URL = "url";

        String movieReviewAPIUrl = BASE_API_TRAILER_URL + movieId.toString() + PARAM_REVIEWS + GeneralConstants.PARAM_API_KEY + "=" + GeneralConstants.API_KEY;
//            Log.v(LOG_TAG, "movieReviewAPIUrl - MainActivity, Line428: " + movieReviewAPIUrl);
        URL movieReviewAPIURL = new URL(movieReviewAPIUrl);
        String allJsonData = getAllJsonDataAsStringFromAPI(movieReviewAPIURL);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieReviewAPIURL), Line431: " + allJsonData);
        JSONObject movieReviewAllJsonDataObject = new JSONObject(allJsonData);
//        Log.v(LOG_TAG, "movieReviewAllJsonDataObject, Line433: " + movieReviewAllJsonDataObject);
        JSONArray movieTrailerInfoJsonArray = movieReviewAllJsonDataObject.getJSONArray(OWN_RESULTS);
//            Log.v(LOG_TAG, "movieReviewInfoJsonArray: " + movieTrailerInfoJsonArray);

        ArrayList<MovieReviewModel> movieReviewArrayList = new ArrayList<>();
        for (int i = 0; i < movieTrailerInfoJsonArray.length(); i++) {
            JSONObject itemJson = movieTrailerInfoJsonArray.getJSONObject(i);
            String author = itemJson.getString(OWN_AUTHOR);
//                Log.v(LOG_TAG, "author: " + author);
            String content = itemJson.getString(OWN_CONTENT);
//                Log.v(LOG_TAG, "content: " + content);
            String url = itemJson.getString(OWN_URL);
//                Log.v(LOG_TAG, "review url: " + url);
            MovieReviewModel movieReviewModel = new MovieReviewModel(author, content, url);
            movieReviewArrayList.add(movieReviewModel);
        }
        return movieReviewArrayList;
    }

    >>>>>>>e6cce583ad40b3ac8aa5321a49158327f94244a9
}
