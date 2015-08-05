//package nanodegree.udacity.leon.udacitypopularmovies;
//
//import android.graphics.Movie;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//
///**
// * This class is mainly for building connections, fetch and parse JSON data.
// * The only input needed is the API key of developers.
// * The method doInBackground() only execute 2 setters, to set movieArrayList and posterImageUrlsArrayList.
// * If one need values of two arraylists, getters will be provided to return the 2 arraylists.
// * <p>
// * <Incomplete Part>:
// * Settings for different sorting.
// */
//
//public class BuildConnection extends AsyncTask<String, Void, ArrayList<MovieModel>> {
//
//    private final String LOG_TAG = BuildConnection.class.getSimpleName();
//
//    private String APIKey;
//    private ArrayList<MovieModel> moviesInfoAsArrayList;
//    private String moviesJsonStr;
//    private String movieId;
//
//    private URL defaultUrl;
//
//    private String moviesJsonDataAsString;
//    ArrayList<String> posterImageUrlArrayList;
//    private String movieOriginalTitle;
//    private String moviePlotSynopsis;
//    private String movieUserRating;
//    private String movieReleaseDate;
//
//    // final Strings to build URL to fetch movie info
//    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
//    final String QUERY_PARAM = "sort_by";
//    final String SETTINGS_PARAM_POPULARITY_DESC = "popularity.desc";
//    // parameter options for settings
//    final String SETTINGS_PARAM_HIGHESTRATED_DESC = "vote_average.desc";
//    final String SETTINGS_PARAM_POPULARITY_ASC = "popularity.aesc";
//    final String SETTINGS_PARAM_HIGHESTRATED_ASC = "vote_average.asc";
//
//    // parameters for parsing data of poster images
//    final String BASE_MOVIES_POSTERIMAGE_API_URL = "http://api.themoviedb.org/3/movie/";
//    final String IMAGE_PARAM = "/images?";
//
//    // base url for poster images
//    final String BASE_POSTERIMAGE_URL = "http://image.tmdb.org/t/p/w500/";
//
//    // API Parameter for building URL
//    final String API_KEY_PARAM = "api_key";
//
//    // This method is responsible for fetching all the JSON data from the URL defined by parameter of the method,
//    // no matter the URL is for movie info or poster image URLs.
//    public String getAllJsonDataAsStringFromAPI(URL defaultUrl) {
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String moviesJsonStr = null;
//        try {
//            urlConnection = (HttpURLConnection) defaultUrl.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            InputStream inputStream = urlConnection.getInputStream();
////            Log.v(LOG_TAG, "inputStream - getAllJsonDataAsStringFromAPI(): " + inputStream.toString());
//            StringBuffer buffer = new StringBuffer();
//            if (inputStream == null) {
//                return null;
//            }
//
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line + "\n");
//            }
//
//            if (buffer.length() == 0) {
//                return null;
//            }
//            moviesJsonStr = buffer.toString();
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (urlConnection != null) urlConnection.disconnect();
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    Log.e(LOG_TAG, "Error closing stream", e);
//                }
//            }
//        }
//
////        Log.v(LOG_TAG, "moviesJsonStr - getAllJsonDataAsStringFromAPI(): " + moviesJsonStr);
//        return moviesJsonStr;
//    }
//
//    /*
//         Method doInBackground() will only call 2 setters, returning nothing.
//         Method doInBackground()  will set moviesInfoArrayList(as an ArrayList of class MovieModel), and
//         posterImageURLsArrayList(as an ArrayList of Strings), both of which will be returned by simple getters.
//
//         Bodies of all method called will be defined outside the method doInBackground().
//         But what's the final purpose of doInBackground() method? What should be returned finally?
//     */
//    @Override
//    protected ArrayList<MovieModel> doInBackground(String... params) {
//
//        // APIKey for later use (to build poster image URLs), although I did not find a better solution
//        APIKey = params[0];
//
//        Uri defaultUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
//                .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_POPULARITY_DESC)
//                .appendQueryParameter(API_KEY_PARAM, params[0])
//                .build();
//
//        // try...catch... block in the constructor?! What will happen if there is MalformedURLException
//        try {
//            defaultUrl = new URL(defaultUri.toString());
////            Log.v(LOG_TAG, "defaultUrl is: " + defaultUrl.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        moviesJsonStr = getAllJsonDataAsStringFromAPI(defaultUrl);
////        Log.v(LOG_TAG, "moviesJsonStr - all JSON data of movies info: " + moviesJsonStr);
//
//        try {
//            setMoviesInfoArrayList(parseMovieJsonData(moviesJsonStr));
//            setPosterImageUrl();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        return moviesInfoAsArrayList;
//    }
//
//    // Setter for the ArrayList<MovieModel> of Movies Info
//    public void setMoviesInfoArrayList(ArrayList<MovieModel> moviesInfoAsArrayList) throws JSONException {
//        this.moviesInfoAsArrayList = moviesInfoAsArrayList;
//
//        for (int i = 0; i < moviesInfoAsArrayList.size(); i++) {
//            Log.v(LOG_TAG, "getMovieId(), setMoviesInfoArrayList() - setMoviesInfoArrayList()" + moviesInfoAsArrayList.get(i).getMovieId().toString());
//        }
//    }
//
//    // Parsing JSON data to acquire all movie info for details page
//    public ArrayList<MovieModel> parseMovieJsonData(String moviesJsonStr) throws JSONException {
//
//        final String OWN_RESULTS = "results";
//        final String OWN_MOVIE_ID = "id";
//        final String OWN_ORIGINAL_TITLE = "original_title";
//        final String OWN_MOVIE_PLOT_SYNOPSIS = "overview";
//        final String OWN_MOVIE_USER_RATING = "vote_average";
//        final String OWN_RELEASE_DATE = "release_date";
//
//        JSONObject moviesJsonObject = new JSONObject(moviesJsonStr);
//        JSONArray moviesJsonObjectArray = moviesJsonObject.getJSONArray(OWN_RESULTS);
//
//        ArrayList<MovieModel> moviesInfoAsArrayList = new ArrayList<MovieModel>();
//        for (int i = 0; i < moviesJsonObjectArray.length(); i++) {
//
//            JSONObject itemJson = moviesJsonObjectArray.getJSONObject(i);
//
//            movieId = itemJson.getString(OWN_MOVIE_ID);
////            Log.v(LOG_TAG, "movieId - parseMovieJsonData(): " + movieId);
//            movieOriginalTitle = itemJson.getString(OWN_ORIGINAL_TITLE);
////            Log.v(LOG_TAG, "movieOriginalTitle - parseMovieJsonData(): " + movieOriginalTitle);
//            moviePlotSynopsis = itemJson.getString(OWN_MOVIE_PLOT_SYNOPSIS);
////            Log.v(LOG_TAG, "moviePlotSynopsis - parseMovieJsonData(): " + moviePlotSynopsis);
//            movieUserRating = itemJson.getString(OWN_MOVIE_USER_RATING);
////            Log.v(LOG_TAG, "movieUserRating - parseMovieJsonData(): " + movieUserRating);
//            movieReleaseDate = itemJson.getString(OWN_RELEASE_DATE);
////            Log.v(LOG_TAG, "movieReleaseDate - parseMovieJsonData(): " + movieReleaseDate);
//
//            MovieModel movieSimple = new MovieModel(movieId, movieOriginalTitle, moviePlotSynopsis, movieUserRating, movieReleaseDate);
//            moviesInfoAsArrayList.add(movieSimple);
//        }
//        Log.v(LOG_TAG, "moviesInfoAsArrayList - parseMovieJsonData(): " + moviesInfoAsArrayList.toString());
//        return moviesInfoAsArrayList;
//    }
//
//    // Poster Images Fetched
//    // Setter for  ArrayList<String> of posterImagesURL
//    public void setPosterImageUrl() throws JSONException, MalformedURLException {
//        String tempMovieId;
//
//        Log.v(LOG_TAG, "moviesInfoAsArrayList - setPosterImageUrl(): " + moviesInfoAsArrayList.toString());
//
//        for (int i = 0; i < moviesInfoAsArrayList.size(); i++) {
//            tempMovieId = moviesInfoAsArrayList.get(i).getMovieId();
//            String posterImageUrlAsString = BASE_MOVIES_POSTERIMAGE_API_URL + tempMovieId + IMAGE_PARAM + API_KEY_PARAM + "=" + APIKey;
////            Log.v(LOG_TAG, "posterImageUrlAsString: " + posterImageUrlAsString);
//            URL posterImageUrl = new URL(posterImageUrlAsString);
//            // parsing JSON data to get a poster image URL arraylist.
//            // The element of the arraylist can be the class, or simply strings.
//            parsePosterImageUrlsJsonData(getAllJsonDataAsStringFromAPI(posterImageUrl));
//        }
//    }
//
//    /**
//     * Parsing JSON data of poster image URLs
//     *
//     * @param posterImageJsonDataAsString is an ArrayList whose elements consist by final poster image urls (one url for one movieId)
//     * @throws JSONException
//     */
//    public void parsePosterImageUrlsJsonData(String posterImageJsonDataAsString) throws JSONException {
//
//        final String OWN_POSTERS = "posters";
//        final String OWN_FILE_PATH = "file_path";
//
//        JSONObject posterImageJsonData = new JSONObject(posterImageJsonDataAsString);
//        JSONArray posterImageJsonArray = posterImageJsonData.getJSONArray(OWN_POSTERS);
//
//        for (int i = 0; i < posterImageJsonArray.length(); i++) {
//
//            JSONObject itemJson = posterImageJsonArray.getJSONObject(i);
//
//            String posterImageUrlFilePath = itemJson.getString(OWN_FILE_PATH);
//
//            String posterImageUrl = BASE_POSTERIMAGE_URL + posterImageUrlFilePath;
//
//            moviesInfoAsArrayList.get(i).setMovieImageUrl(posterImageUrl);
//        }
//
////        Log.v(LOG_TAG, "posterImageUrlArrayList - parsePosterImageUrlsJsonData(): " + posterImageUrlArrayList.toString());
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<MovieModel> movieModels) {
//        super.onPostExecute(movieModels);
//    }
//}
