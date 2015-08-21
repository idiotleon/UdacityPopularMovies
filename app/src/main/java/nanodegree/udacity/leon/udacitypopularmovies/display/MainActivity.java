package nanodegree.udacity.leon.udacitypopularmovies.display;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.CustomGridViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.helper.DatabaseHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.moviedetail.MovieDetailsActivity;
import nanodegree.udacity.leon.udacitypopularmovies.helper.CommonConstants;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;


public class MainActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    /*
     *  ============================================================
     *  Variable to store API Key
     *  Please type your API key here
     *  ============================================================
     */
    final String API_KEY = "74684520f47c025a768d03e231efe89c";

    private GridView gridView;

    private CustomGridViewAdapter customGridViewAdapter;

    private ArrayList<MovieInfoModel> movieInfo;

    private ParsingForMovieInfo parsingForMovieInfo;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(MainActivity.this);

        if (savedInstanceState != null) {
            movieInfo = savedInstanceState.getParcelableArrayList(CommonConstants.MOVIE_SAVED_INSTANCE_STATE_MAIN_ACTIVITY);
            Log.v(LOG_TAG, "movieInfo - saveInstanceState: " + movieInfo);
            customSetContentView(movieInfo);
        } else if (dbHelper.getAllMovieInfo() != null) {
            movieInfo = dbHelper.getAllMovieInfo();
            customSetContentView(movieInfo);
            // For update (database) purpose
            parsingForMovieInfo = new ParsingForMovieInfo();
            parsingForMovieInfo.execute(API_KEY, "popularity");
        } else {
            parsingForMovieInfo = new ParsingForMovieInfo();
            parsingForMovieInfo.execute(API_KEY, "popularity");
        }
    }

    private void customSetContentView(ArrayList<MovieInfoModel> movieInfo) {
        if (GeneralHelper.isTablet(MainActivity.this)) {
            Log.v(LOG_TAG, "This is a tablet.");
            setContentView(R.layout.activity_main_tabletux);
            Bundle displayFragmentArgs = new Bundle();
            displayFragmentArgs.putParcelableArrayList(CommonConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER, movieInfo);
            DisplayFragment displayFragment = new DisplayFragment();
            displayFragment.setArguments(displayFragmentArgs);
            getFragmentManager().beginTransaction().
                    replace(R.layout.fragment_display_tabletux, displayFragment).commit();
        } else {
            Log.v(LOG_TAG, "This is a phone.");
            setContentView(R.layout.activity_main);
            gridView = (GridView) findViewById(R.id.gridview_mainactivity);
            customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), movieInfo);
            gridView.setAdapter(customGridViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MovieInfoModel clickedMovieInfo = (MovieInfoModel) gridView.getItemAtPosition(position);
                    Intent detailsIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                    detailsIntent.putExtra(CommonConstants.MOVIE_PARCEL, new MovieInfoModel(
                            clickedMovieInfo.getMovieId(),
                            clickedMovieInfo.getMovieOriginalTitle(),
                            clickedMovieInfo.getMovieImageUrl(),
                            clickedMovieInfo.getMoviePlotSynopsis(),
                            clickedMovieInfo.getMovieUserRating(),
                            clickedMovieInfo.getMovieReleaseDate(),
                            clickedMovieInfo.getMovieTrailerUrlArrayList(),
                            clickedMovieInfo.getMovieReviewArrayList()
                    ));
                    startActivity(detailsIntent);
                }
            });
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(CommonConstants.MOVIE_SAVED_INSTANCE_STATE_MAIN_ACTIVITY, movieInfo);
//        Log.v(LOG_TAG, "movieInfo - onSaveInstanceState: " + movieInfo);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // todo: methods should be improved based on databases
        if (id == R.id.sort_popularity_desc) {
            /**
             * todo:
             * I do not how to improve this part based on database.
             * There is no Popularity in MovieModel.
             * Should I add popularity in the model and parse it in JSON parsing part,
             * or simply use WebAPI each time for such sort?
             */
            ParsingForMovieInfo parsingForMovieInfo = new ParsingForMovieInfo();
            parsingForMovieInfo.execute(API_KEY, "popularity");
            return true;
        }
        if (id == R.id.sort_highest_rating_desc) {
//            ParsingForMovieInfo parsingForMovieInfo = new ParsingForMovieInfo();
//            parsingForMovieInfo.execute(API_KEY, "highestrating");
            movieInfo = dbHelper.getAllMovieInfoOrderByUserRating();
            customSetContentView(movieInfo);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ParsingForMovieInfo extends AsyncTask<String, Void, ArrayList<MovieInfoModel>> {

        private final String LOG_TAG = ParsingForMovieInfo.class.getSimpleName();

        private ArrayList<MovieInfoModel> moviesInfoAsArrayList;
        private String moviesJsonStr;
        private URL defaultUrl;

        // base API URL to fetch movie info
        final String BASE_API_MOVIE_INFO_URL = "http://api.themoviedb.org/3/discover/movie?";
        // base URL for poster images
        final String BASE_POSTER_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

        final String QUERY_PARAM = "sort_by";
        final String SETTINGS_PARAM_POPULARITY_DESC = "popularity.desc";
        // parameter options for settings
        final String SETTINGS_PARAM_HIGHESTRATED_DESC = "vote_average.desc";

        // API Parameter for building URL
        final String PARAM_API_KEY = "api_key";

        /**
         * Method doInBackground() will only call 2 setters, returning null.
         * Method doInBackground()  will set moviesInfoArrayList(as an ArrayList of class MovieInfoModel), and
         * posterImageURLsArrayList(as an ArrayList of Strings), both of which will be returned by simple getters,
         * within onPostExecute() method.
         *
         * @param params
         * @return
         */
        @Override
        protected ArrayList<MovieInfoModel> doInBackground(String... params) {

            Uri defaultUri;

            if (params[1].toLowerCase() == "highestrating") {
                defaultUri = Uri.parse(BASE_API_MOVIE_INFO_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_HIGHESTRATED_DESC)
                        .appendQueryParameter(PARAM_API_KEY, params[0])
                        .build();
            } else {
                defaultUri = Uri.parse(BASE_API_MOVIE_INFO_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_POPULARITY_DESC)
                        .appendQueryParameter(PARAM_API_KEY, params[0])
                        .build();
            }

            try {
                defaultUrl = new URL(defaultUri.toString());
//                Log.v(LOG_TAG, "defaultUrl is: " + defaultUrl.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            moviesJsonStr = getAllJsonDataAsStringFromAPI(defaultUrl);
//            Log.v(LOG_TAG, "moviesJsonStr - all JSON data of movies info: " + moviesJsonStr);

            try {
                try {
                    ArrayList<MovieInfoModel> movieInfo = parseJsonDataForMovieInfo(moviesJsonStr);
                    setMoviesInfoArrayList(movieInfo);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movieInfo;
        }

        /**
         * Setter method for movie info
         *
         * @param moviesInfoAsArrayList
         * @throws JSONException
         */
        public void setMoviesInfoArrayList(ArrayList<MovieInfoModel> moviesInfoAsArrayList) throws JSONException {
            this.moviesInfoAsArrayList = moviesInfoAsArrayList;

//            for (int i = 0; i < moviesInfoAsArrayList.size(); i++) {
//                Log.v(LOG_TAG, "getMovieId(), setMoviesInfoArrayList() - setMoviesInfoArrayList()" + moviesInfoAsArrayList.get(i).getMovieId().toString());
//            }
        }

        /**
         * Getter to provide moviesInfoAsArrayList (the final data after JSON parsing), return null if no parsing occurred.
         * For savedInstanceState() purpose
         *
         * @return
         */
        public ArrayList<MovieInfoModel> getMoviesInfoArrayList() {
            if (moviesInfoAsArrayList != null) {
                Log.v(LOG_TAG, "moviesInfoAsArrayList is returned");
                return moviesInfoAsArrayList;
            } else {
                Log.e(LOG_TAG, "moviesInfoAsArrayList is null, please execute(API, 'sort-starndards') first.");
                return null;
            }
        }

        /**
         * Method is to get all the json data from the input URL as String.
         * <improvement>: there should be URL check methods
         *
         * @param defaultUrl
         * @return
         */
        public String getAllJsonDataAsStringFromAPI(URL defaultUrl) {
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
         * JSON parsing method for movie info
         *
         * @param moviesJsonStr
         * @return
         * @throws JSONException
         */
        public ArrayList<MovieInfoModel> parseJsonDataForMovieInfo(String moviesJsonStr) throws JSONException, MalformedURLException {

            final String OWN_RESULTS = "results";
            final String OWN_MOVIE_ID = "id";
            final String OWN_ORIGINAL_TITLE = "original_title";
            final String OWN_MOVIE_PLOT_SYNOPSIS = "overview";
            final String OWN_MOVIE_USER_RATING = "vote_average";
            final String OWN_RELEASE_DATE = "release_date";
            final String OWN_POSTER_PATH = "poster_path";

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
//            Log.v(LOG_TAG, "MOVIE_ID - parseJsonDataForMovieInfo(): " + MOVIE_ID);
                movieOriginalTitle = itemJson.getString(OWN_ORIGINAL_TITLE);
//            Log.v(LOG_TAG, "MOVIE_ORIGINAL_TITLE - parseJsonDataForMovieInfo(): " + MOVIE_ORIGINAL_TITLE);
                moviePlotSynopsis = itemJson.getString(OWN_MOVIE_PLOT_SYNOPSIS);
//            Log.v(LOG_TAG, "MOVIE_PLOT_SYNOPSIS - parseJsonDataForMovieInfo(): " + MOVIE_PLOT_SYNOPSIS);
                movieUserRating = itemJson.getString(OWN_MOVIE_USER_RATING);
//            Log.v(LOG_TAG, "MOVIE_USER_RATING - parseJsonDataForMovieInfo(): " + MOVIE_USER_RATING);
                movieReleaseDate = itemJson.getString(OWN_RELEASE_DATE);
//            Log.v(LOG_TAG, "MOVIE_RELEASE_DATE - parseJsonDataForMovieInfo(): " + MOVIE_RELEASE_DATE);
                moviePosterUrl = BASE_POSTER_IMAGE_URL + itemJson.getString(OWN_POSTER_PATH);

                movieTrailerUrlArrayList = parseJsonDataForMovieTrailerUrl(movieId);

                movieReviewArrayList = parseJsonDataForMovieReview(movieId);

                MovieInfoModel movieSimple = new MovieInfoModel(movieId, movieOriginalTitle, moviePosterUrl, moviePlotSynopsis, movieUserRating, movieReleaseDate, movieTrailerUrlArrayList, movieReviewArrayList);
                moviesInfoAsArrayList.add(movieSimple);
            }
//            Log.v(LOG_TAG, "moviesInfoAsArrayList - parseJsonDataForMovieInfo(): " + moviesInfoAsArrayList.toString());
//            Log.v(LOG_TAG, "moviesInfoAsArrayList.size() - parseJsonDataForMovieInfo(): " + moviesInfoAsArrayList.size());

            return moviesInfoAsArrayList;
        }

        /**
         * For each specific movie id, this method will get all the "key"s from API/JSON data, when combined with base Youtube URL, return
         * an ArrayList of all trailer urls, which can be played directly
         *
         * @param movieId
         * @return
         * @throws MalformedURLException
         * @throws JSONException
         */
        public ArrayList<String> parseJsonDataForMovieTrailerUrl(Long movieId) throws MalformedURLException, JSONException {

            // base API URL for fetching trailer id
            final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
            // base Youtube URL for displaying trailer
            final String BASE_YOUTUBE_URL = "http://www.youtube.com/v/";
            final String PARAM_VIDEO = "/videos?";
            final String OWN_RESULTS = "results";
            final String OWN_KEY = "key";

            String movieTrailerAPIUrl = BASE_API_TRAILER_URL + movieId.toString() + PARAM_VIDEO + PARAM_API_KEY + "=" + API_KEY;
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

        public ArrayList<MovieReviewModel> parseJsonDataForMovieReview(Long movieId) throws MalformedURLException, JSONException {

            // base API URL for fetching reviews
            final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
            final String PARAM_REVIEWS = "/reviews?";

            final String OWN_RESULTS = "results";
            final String OWN_AUTHOR = "author";
            final String OWN_CONTENT = "content";
            final String OWN_URL = "url";

            String movieReviewAPIUrl = BASE_API_TRAILER_URL + movieId.toString() + PARAM_REVIEWS + PARAM_API_KEY + "=" + API_KEY;
//            Log.v(LOG_TAG, "movieReviewAPIUrl - MainActivity, Line355: " + movieReviewAPIUrl);
            URL movieReviewAPIURL = new URL(movieReviewAPIUrl);
            String allJsonData = getAllJsonDataAsStringFromAPI(movieReviewAPIURL);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieReviewAPIURL), Line355: " + allJsonData);
            JSONObject movieReviewAllJsonDataObject = new JSONObject(allJsonData);
//            Log.v(LOG_TAG, "movieReviewAllJsonDataObject, Line356: " + movieReviewAllJsonDataObject);
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

        @Override
        protected void onPostExecute(ArrayList<MovieInfoModel> movieModels) {
            super.onPostExecute(movieModels);
            customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), moviesInfoAsArrayList);
            movieInfo = parsingForMovieInfo.getMoviesInfoArrayList();

            updateDatabase(movieInfo);

            if (GeneralHelper.isTablet(MainActivity.this)) {
                Log.v(LOG_TAG, "This is a tablet.");
                setContentView(R.layout.activity_main_tabletux);
                Bundle displayFragmentArgs = new Bundle();
                displayFragmentArgs.putParcelableArrayList(CommonConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER, movieInfo);
                DisplayFragment displayFragment = new DisplayFragment();
                displayFragment.setArguments(displayFragmentArgs);
                getFragmentManager().beginTransaction().
                        replace(R.layout.fragment_display_tabletux, displayFragment).commit();
            } else {
                Log.v(LOG_TAG, "This is a phone.");
                setContentView(R.layout.activity_main);
                gridView = (GridView) findViewById(R.id.gridview_mainactivity);
                customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), movieInfo);
                gridView.setAdapter(customGridViewAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MovieInfoModel clickedMovieInfo = (MovieInfoModel) gridView.getItemAtPosition(position);
                        Intent detailsIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                        detailsIntent.putExtra(CommonConstants.MOVIE_PARCEL, new MovieInfoModel(
                                clickedMovieInfo.getMovieId(),
                                clickedMovieInfo.getMovieOriginalTitle(),
                                clickedMovieInfo.getMovieImageUrl(),
                                clickedMovieInfo.getMoviePlotSynopsis(),
                                clickedMovieInfo.getMovieUserRating(),
                                clickedMovieInfo.getMovieReleaseDate(),
                                clickedMovieInfo.getMovieTrailerUrlArrayList(),
                                clickedMovieInfo.getMovieReviewArrayList()
                        ));
                        startActivity(detailsIntent);
                    }
                });
            }

            Log.v(LOG_TAG, "movieInfo after parsing:" + movieInfo.toString());
            gridView.setAdapter(customGridViewAdapter);
        }

        private void updateDatabase(ArrayList<MovieInfoModel> movieInfoArrayList) {
            for (int i = 0; i < movieInfoArrayList.size(); i++) {
                dbHelper.updateTableData(movieInfo.get(i));
            }
        }
    }
}
