package nanodegree.udacity.leon.udacitypopularmovies.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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

import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

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
    }

    public static void cancelFavoriteStatus(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, 0).commit();
    }

    public static int getFavoriteStatus(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
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

            MovieInfoModel movieModelWithoutTrailerOrReviews = new MovieInfoModel(movieId, movieOriginalTitle, moviePosterUrl, moviePlotSynopsis, movieUserRating, movieReleaseDate);
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

}
