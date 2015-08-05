package nanodegree.udacity.leon.udacitypopularmovies;

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
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayList<MovieModel> moviesInfo;
    private ArrayList<String> posterImageUrls = new ArrayList<String>();

    // Variable to store API Key
    final String API_KEY = "74684520f47c025a768d03e231efe89c";

    private GridView gridView;

    private TextView textView;

    private CustomGridViewAdapter customGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridview_mainactivity);

        textView = (TextView) findViewById(R.id.textview_mainactivity);

        BuildConnection buildConnection = new BuildConnection();
        buildConnection.execute(API_KEY, "popularity");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, gridView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                MovieModel tempMovieModel = (MovieModel) gridView.getItemAtPosition(position);
                Intent detailsIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                detailsIntent.putExtra(CommonConstants.movieId, tempMovieModel.getMovieId());
                detailsIntent.putExtra(CommonConstants.movieOriginalTitle, tempMovieModel.getMovieOriginalTitle());
                detailsIntent.putExtra(CommonConstants.moviePosterImageURL, tempMovieModel.getMovieImageUrl());
                Log.v(LOG_TAG, "tempMovieModel.getMovieImageUrl() - MainActivity: " + tempMovieModel.getMovieImageUrl());
                detailsIntent.putExtra(CommonConstants.moviePlotSynopsis, tempMovieModel.getMoviePlotSynopsis());
                detailsIntent.putExtra(CommonConstants.movieUserRating, tempMovieModel.getMovieUserRating());
                detailsIntent.putExtra(CommonConstants.movieReleaseDate, tempMovieModel.getMovieReleaseDate());
                startActivity(detailsIntent);
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_popularity_desc) {
            BuildConnection buildConnection = new BuildConnection();
            buildConnection.execute(API_KEY, "popularity");
            textView.setText("Movies Sorted by Popularity");
            return true;
        }
        if (id == R.id.sort_highest_rating_desc) {
            BuildConnection buildConnection = new BuildConnection();
            buildConnection.execute(API_KEY, "highestrating");
            textView.setText("Movies Sorted by Rating");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class BuildConnection extends AsyncTask<String, Void, ArrayList<MovieModel>> {

        private final String LOG_TAG = BuildConnection.class.getSimpleName();

        private String APIKey;
        private ArrayList<MovieModel> moviesInfoAsArrayList;
        private String moviesJsonStr;
        private URL defaultUrl;

        private String movieId;
        private String movieOriginalTitle;
        private String moviePlotSynopsis;
        private String movieUserRating;
        private String movieReleaseDate;
        private String moviePosterUrl;

        // final Strings to build URL to fetch movie info
        final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String QUERY_PARAM = "sort_by";
        final String SETTINGS_PARAM_POPULARITY_DESC = "popularity.desc";
        // parameter options for settings
        final String SETTINGS_PARAM_HIGHESTRATED_DESC = "vote_average.desc";
        final String SETTINGS_PARAM_POPULARITY_ASC = "popularity.aesc";
        final String SETTINGS_PARAM_HIGHESTRATED_ASC = "vote_average.asc";
        // parameters for parsing data of poster images
        final String BASE_MOVIES_POSTERIMAGE_API_URL = "http://api.themoviedb.org/3/movie/";
        final String IMAGE_PARAM = "/images?";

        // base url for poster images
        final String BASE_POSTERIMAGE_URL = "http://image.tmdb.org/t/p/w500";
        final String LANGUAGE_PARAM = "language";
        final String LANGUAGE = "en";

        // API Parameter for building URL
        final String API_KEY_PARAM = "api_key";


        /**
         * Method doInBackground() will only call 2 setters, returning null.
         * Method doInBackground()  will set moviesInfoArrayList(as an ArrayList of class MovieModel), and
         * posterImageURLsArrayList(as an ArrayList of Strings), both of which will be returned by simple getters,
         * within onPostExecute() method.
         *
         * @param params
         * @return
         */


        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {

            // APIKey for later use (to build poster image URLs), although I did not find a better solution
            APIKey = params[0];

            Uri defaultUri;

            if (params[1].toLowerCase() == "highestrating") {
                defaultUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_HIGHESTRATED_DESC)
                        .appendQueryParameter(API_KEY_PARAM, params[0])
                        .build();
            } else {
                defaultUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_POPULARITY_DESC)
                        .appendQueryParameter(API_KEY_PARAM, params[0])
                        .build();
            }

            try {
                defaultUrl = new URL(defaultUri.toString());
                Log.v(LOG_TAG, "defaultUrl is: " + defaultUrl.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            moviesJsonStr = getAllJsonDataAsStringFromAPI(defaultUrl);
            Log.v(LOG_TAG, "moviesJsonStr - all JSON data of movies info: " + moviesJsonStr);

            try {
                setMoviesInfoArrayList(parseMovieJsonData(moviesJsonStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Setter method for movie info
         *
         * @param moviesInfoAsArrayList
         * @throws JSONException
         */
        public void setMoviesInfoArrayList(ArrayList<MovieModel> moviesInfoAsArrayList) throws JSONException {
            this.moviesInfoAsArrayList = moviesInfoAsArrayList;

            for (int i = 0; i < moviesInfoAsArrayList.size(); i++) {
                Log.v(LOG_TAG, "getMovieId(), setMoviesInfoArrayList() - setMoviesInfoArrayList()" + moviesInfoAsArrayList.get(i).getMovieId().toString());
            }
        }

        /**
         * Method is to get all the json data from the input URL as String.
         * <improvement>: there should be URL check functions
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
                Log.v(LOG_TAG, "inputStream - getAllJsonDataAsStringFromAPI(): " + inputStream.toString());
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
                Log.v(LOG_TAG, ex.toString());
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

            Log.v(LOG_TAG, "moviesJsonStr - getAllJsonDataAsStringFromAPI(): " + moviesJsonStr);
            return moviesJsonStr;
        }

        /**
         * JSON parsing method for movie info
         *
         * @param moviesJsonStr
         * @return
         * @throws JSONException
         */
        public ArrayList<MovieModel> parseMovieJsonData(String moviesJsonStr) throws JSONException {

            final String OWN_RESULTS = "results";
            final String OWN_MOVIE_ID = "id";
            final String OWN_ORIGINAL_TITLE = "original_title";
            final String OWN_MOVIE_PLOT_SYNOPSIS = "overview";
            final String OWN_MOVIE_USER_RATING = "vote_average";
            final String OWN_RELEASE_DATE = "release_date";
            final String OWN_POSTER_PATH = "poster_path";

            JSONObject moviesJsonObject = new JSONObject(moviesJsonStr);
            JSONArray moviesJsonObjectArray = moviesJsonObject.getJSONArray(OWN_RESULTS);

            ArrayList<MovieModel> moviesInfoAsArrayList = new ArrayList<MovieModel>();
            for (int i = 0; i < moviesJsonObjectArray.length(); i++) {

                JSONObject itemJson = moviesJsonObjectArray.getJSONObject(i);

                movieId = itemJson.getString(OWN_MOVIE_ID);
//            Log.v(LOG_TAG, "movieId - parseMovieJsonData(): " + movieId);
                movieOriginalTitle = itemJson.getString(OWN_ORIGINAL_TITLE);
//            Log.v(LOG_TAG, "movieOriginalTitle - parseMovieJsonData(): " + movieOriginalTitle);
                moviePlotSynopsis = itemJson.getString(OWN_MOVIE_PLOT_SYNOPSIS);
//            Log.v(LOG_TAG, "moviePlotSynopsis - parseMovieJsonData(): " + moviePlotSynopsis);
                movieUserRating = itemJson.getString(OWN_MOVIE_USER_RATING);
//            Log.v(LOG_TAG, "movieUserRating - parseMovieJsonData(): " + movieUserRating);
                movieReleaseDate = itemJson.getString(OWN_RELEASE_DATE);
//            Log.v(LOG_TAG, "movieReleaseDate - parseMovieJsonData(): " + movieReleaseDate);
                moviePosterUrl = BASE_POSTERIMAGE_URL + itemJson.getString(OWN_POSTER_PATH);

                MovieModel movieSimple = new MovieModel(movieId, movieOriginalTitle, moviePosterUrl, moviePlotSynopsis, movieUserRating, movieReleaseDate);
                moviesInfoAsArrayList.add(movieSimple);
            }
            Log.v(LOG_TAG, "moviesInfoAsArrayList - parseMovieJsonData(): " + moviesInfoAsArrayList.toString());
            Log.v(LOG_TAG, "moviesInfoAsArrayList.size() - parseMovieJsonData(): " + moviesInfoAsArrayList.size());

            return moviesInfoAsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> movieModels) {
            super.onPostExecute(movieModels);
            customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), moviesInfoAsArrayList);
            gridView.setAdapter(customGridViewAdapter);
        }


        /**
         * Setter method for the URLs of poster images
         *
         * @throws JSONException
         * @throws MalformedURLException
         *//*
        public void setPosterImageUrl() throws JSONException, MalformedURLException {
            String tempMovieId;

            Log.v(LOG_TAG, "moviesInfoAsArrayList - setPosterImageUrl(): " + moviesInfoAsArrayList.toString());

            for (int i = 0; i < moviesInfoAsArrayList.size(); i++) {
                tempMovieId = moviesInfoAsArrayList.get(i).getMovieId();
                String posterImageUrlAsString = BASE_MOVIES_POSTERIMAGE_API_URL + tempMovieId + IMAGE_PARAM + API_KEY_PARAM + "=" + APIKey + "&" + LANGUAGE_PARAM + "=" + LANGUAGE;
                Log.v(LOG_TAG, "posterImageUrlAsString: " + posterImageUrlAsString);
                URL posterImageAPIUrl = new URL(posterImageUrlAsString);
                // parsing JSON data to get a poster image URL arraylist.
                // The element of the arraylist can be the class, or simply strings.
                String posterImageUrl = parsePosterImageUrlsJsonData(getAllJsonDataAsStringFromAPI(posterImageAPIUrl));
                moviesInfoAsArrayList.get(i).setMovieImageUrl(posterImageUrl);
            }
        }

        *//**
         * Parsing JSON data of poster image URLs
         *
         * @param posterImageJsonDataAsString is an ArrayList whose elements consist by final poster image urls (one url for one movieId)
         * @throws JSONException
         *//*
        public String parsePosterImageUrlsJsonData(String posterImageJsonDataAsString) throws JSONException {

            final String OWN_POSTERS = "posters";
            final String OWN_FILE_PATH = "file_path";

            JSONObject posterImageJsonData = new JSONObject(posterImageJsonDataAsString);
            JSONArray posterImageJsonArray = posterImageJsonData.getJSONArray(OWN_POSTERS);
            Log.v(LOG_TAG, "posterImageJsonArray: " + posterImageJsonArray);


            JSONObject itemJson = posterImageJsonArray.getJSONObject(0);
            Log.v(LOG_TAG, "itemJson: " + itemJson);

            String posterImageUrlFilePath = itemJson.getString(OWN_FILE_PATH);

            String posterImageUrl = BASE_POSTERIMAGE_URL + posterImageUrlFilePath;

            Log.v(LOG_TAG, "posterImageUrl: " + posterImageUrl);
            return posterImageUrl;
        }


        public ArrayList<String> getPosterImageUrls(ArrayList<MovieModel> movieInfoArrayList) {
            ArrayList<String> posterImageUrls = new ArrayList<String>();
            for (int i = 0; i < movieInfoArrayList.size(); i++) {
                posterImageUrls.add(movieInfoArrayList.get(i).getMovieImageUrl());
            }
            Log.v(LOG_TAG, "posterImageUrlsAsStringArrayList: " + posterImageUrls.toString());
            return posterImageUrls;
        }*/
    }
}
