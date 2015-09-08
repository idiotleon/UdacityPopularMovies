package nanodegree.udacity.leon.udacitypopularmovies.display;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.moviedetail.MovieDetailsActivity;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private GridView gridView;

    private CustomGridViewAdapter customGridViewAdapter;

    private ArrayList<MediumMovieInfoModel> mediumMovieInfoArrayList;

    private ParsingJsonForMediumMovieInfo parsingJsonForMediumMovieInfo;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(MainActivity.this);

        if (savedInstanceState != null) {
            mediumMovieInfoArrayList = savedInstanceState.getParcelableArrayList(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_MAIN_ACTIVITY);
            Log.v(LOG_TAG, "movieInfo, fetched from savedInstanceState(): " + mediumMovieInfoArrayList);
            refreshPageView(mediumMovieInfoArrayList);
        } else if (dbHelper.getMovieInfoStoredCount() > 0) {
            mediumMovieInfoArrayList = dbHelper.getAllMediumMovieInfo(GeneralConstants.MOVIE_SORTED_BY_POPULARITY, GeneralConstants.MOVIE_SORTED_DESC);
            refreshPageView(mediumMovieInfoArrayList);
            // For update (database) purpose
            parsingJsonForMediumMovieInfo = new ParsingJsonForMediumMovieInfo();
            parsingJsonForMediumMovieInfo.execute(GeneralConstants.API_KEY, "popularity");
        } else {
            parsingJsonForMediumMovieInfo = new ParsingJsonForMediumMovieInfo();
            parsingJsonForMediumMovieInfo.execute(GeneralConstants.API_KEY, "popularity");
        }
    }

    private void refreshPageView(ArrayList<MediumMovieInfoModel> movieInfo) {
        if (GeneralHelper.isTablet(MainActivity.this)) {
            Log.v(LOG_TAG, "This is a tablet.");
            setContentView(R.layout.activity_main_tabletux);
            Bundle displayFragmentArgs = new Bundle();
            displayFragmentArgs.putParcelableArrayList(GeneralConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER, movieInfo);
            DisplayFragment displayFragment = new DisplayFragment();
            displayFragment.setArguments(displayFragmentArgs);
            getFragmentManager().beginTransaction().
                    replace(R.id.tabletux_container1, displayFragment).commit();
        } else {
            Log.v(LOG_TAG, "This is a phone.");
            setContentView(R.layout.activity_main);
            gridView = (GridView) findViewById(R.id.gridview_mainactivity);
            customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), movieInfo);
            gridView.setAdapter(customGridViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MediumMovieInfoModel clickedMovieInfo = (MediumMovieInfoModel) gridView.getItemAtPosition(position);
                    Intent detailsIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                    detailsIntent.putExtra(GeneralConstants.MOVIE_PARCEL, new MediumMovieInfoModel(
                            clickedMovieInfo.getMovieId(),
                            clickedMovieInfo.getMovieOriginalTitle(),
                            clickedMovieInfo.getMovieImageUrl(),
                            clickedMovieInfo.getMoviePlotSynopsis(),
                            clickedMovieInfo.getMovieUserRating(),
                            clickedMovieInfo.getMovieReleaseDate(),
                            clickedMovieInfo.getMoviePopularity()
                    ));
                    startActivity(detailsIntent);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_MAIN_ACTIVITY, mediumMovieInfoArrayList);
//        Log.v(LOG_TAG, "movieInfo - onSaveInstanceState(), MainActivity: " + movieInfo);
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
            ParsingJsonForMediumMovieInfo parsingJsonForMediumMovieInfo = new ParsingJsonForMediumMovieInfo();
            parsingJsonForMediumMovieInfo.execute(GeneralConstants.API_KEY, "popularity");
            return true;
        }
        if (id == R.id.sort_highest_rating_desc) {
            ParsingJsonForMediumMovieInfo parsingJsonForMediumMovieInfo = new ParsingJsonForMediumMovieInfo();
            parsingJsonForMediumMovieInfo.execute(GeneralConstants.API_KEY, "highestrating");
/*            movieInfo = dbHelper.getAllMovieInfoOrderByUserRating();
            refreshPageView(movieInfo);*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ParsingJsonForMediumMovieInfo extends AsyncTask<String, Void, ArrayList<MediumMovieInfoModel>> {

        private final String LOG_TAG = ParsingJsonForMediumMovieInfo.class.getSimpleName();

        private ArrayList<MediumMovieInfoModel> mediumMovieInfoArrayList = new ArrayList<>();
        private String moviesJsonStr;
        private URL defaultUrl;

        // base API URL to fetch udacity_popular_movie info
        final String BASE_API_MOVIE_INFO_URL = "http://api.themoviedb.org/3/discover/movie?";
        // base URL for poster images
        final String BASE_POSTER_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

        final String QUERY_PARAM = "sort_by";
        final String SETTINGS_PARAM_POPULARITY_DESC = "popularity.desc";
        // parameter options for settings
        final String SETTINGS_PARAM_HIGHESTRATED_DESC = "vote_average.desc";

        /**
         * Method doInBackground() will only call 2 setters, returning null.
         * Method doInBackground()  will set moviesInfoArrayList(as an ArrayList of class CompleteMovieInfoModel), and
         * posterImageURLsArrayList(as an ArrayList of Strings), both of which will be returned by simple getters,
         * within onPostExecute() method.
         *
         * @param params
         * @return
         */
        @Override
        protected ArrayList<MediumMovieInfoModel> doInBackground(String... params) {

            Uri defaultUri;

            if (params[1].toLowerCase() == "highestrating") {
                defaultUri = Uri.parse(BASE_API_MOVIE_INFO_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_HIGHESTRATED_DESC)
                        .appendQueryParameter(GeneralConstants.PARAM_API_KEY, params[0])
                        .build();
            } else {
                defaultUri = Uri.parse(BASE_API_MOVIE_INFO_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SETTINGS_PARAM_POPULARITY_DESC)
                        .appendQueryParameter(GeneralConstants.PARAM_API_KEY, params[0])
                        .build();
            }

            try {
                defaultUrl = new URL(defaultUri.toString());
//                Log.v(LOG_TAG, "defaultUrl is: " + defaultUrl.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            moviesJsonStr = GeneralHelper.getAllJsonDataAsStringFromAPI(defaultUrl);
//            Log.v(LOG_TAG, "moviesJsonStr - all JSON data of movies info: " + moviesJsonStr);

            try {
                try {
                    ArrayList<MediumMovieInfoModel> mediumMovieInfo = parseJsonDataForMediumMovieInfo(moviesJsonStr);
                    setMoviesInfoArrayList(mediumMovieInfo);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mediumMovieInfoArrayList;
        }

        /**
         * Setter method for udacity_popular_movie info
         *
         * @param mediumMoviesInfoAsArrayList
         * @throws JSONException
         */
        public void setMoviesInfoArrayList(ArrayList<MediumMovieInfoModel> mediumMoviesInfoAsArrayList) throws JSONException {
            this.mediumMovieInfoArrayList = mediumMoviesInfoAsArrayList;

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
        public ArrayList<MediumMovieInfoModel> getMoviesInfoArrayList() {
            if (mediumMovieInfoArrayList != null) {
                Log.v(LOG_TAG, "mediumMovieInfoArrayList is returned");
                return mediumMovieInfoArrayList;
            } else {
                Log.e(LOG_TAG, "mediumMovieInfoArrayList is null, please execute(API, 'sort-starndards') first.");
                return null;
            }
        }

        /**
         * JSON parsing method for medium udacity_popular_movie info (without TrailerUrl and MovieReview ArrayLists)
         *
         * @param moviesJsonStr
         * @return
         * @throws JSONException
         */
        public ArrayList<MediumMovieInfoModel> parseJsonDataForMediumMovieInfo(String moviesJsonStr) throws JSONException, MalformedURLException {

            // Keywords for JSON parsing purpose
            final String UPM_RESULTS = "results";
            final String UPM_MOVIE_ID = "id";
            final String UPM_ORIGINAL_TITLE = "original_title";
            final String UPM_MOVIE_PLOT_SYNOPSIS = "overview";
            final String UPM_MOVIE_USER_RATING = "vote_average";
            final String UPM_RELEASE_DATE = "release_date";
            final String UPM_POSTER_PATH = "poster_path";
            final String UPM_POPULAIRY = "popularity";

            // Fields for building models
            long movieId;
            String movieOriginalTitle;
            String moviePlotSynopsis;
            float movieUserRating;
            String movieReleaseDate;
            String moviePosterUrl;
            double moviePopularity;

            JSONObject moviesJsonObject = new JSONObject(moviesJsonStr);
            JSONArray moviesJsonObjectArray = moviesJsonObject.getJSONArray(UPM_RESULTS);

            ArrayList<MediumMovieInfoModel> mediumMoviesInfoAsArrayList = new ArrayList<MediumMovieInfoModel>();
            for (int i = 0; i < moviesJsonObjectArray.length(); i++) {

                JSONObject itemJson = moviesJsonObjectArray.getJSONObject(i);

                movieId = itemJson.getLong(UPM_MOVIE_ID);
//            Log.v(LOG_TAG, "MOVIE_ID, parseJsonDataForMediumMovieInfo(): " + movieId);
                movieOriginalTitle = itemJson.getString(UPM_ORIGINAL_TITLE);
//            Log.v(LOG_TAG, "MOVIE_ORIGINAL_TITLE, parseJsonDataForMediumMovieInfo(): " + movieOriginalTitle);
                moviePlotSynopsis = itemJson.getString(UPM_MOVIE_PLOT_SYNOPSIS);
//            Log.v(LOG_TAG, "MOVIE_PLOT_SYNOPSIS, parseJsonDataForMediumMovieInfo(): " + moviePlotSynopsis);
                movieUserRating = Float.parseFloat(itemJson.getString(UPM_MOVIE_USER_RATING));
//            Log.v(LOG_TAG, "MOVIE_USER_RATING, parseJsonDataForMediumMovieInfo(): " + movieUserRating);
                movieReleaseDate = itemJson.getString(UPM_RELEASE_DATE);
//            Log.v(LOG_TAG, "MOVIE_RELEASE_DATE, parseJsonDataForMediumMovieInfo(): " + movieReleaseDate);
                moviePosterUrl = BASE_POSTER_IMAGE_URL + itemJson.getString(UPM_POSTER_PATH);
//                Log.v(LOG_TAG, "MOVIE_POSTER_IMAGE_URL, parseJsonDataForMediumMovieInfo(): " + moviePosterUrl);
                moviePopularity = itemJson.getDouble(UPM_POPULAIRY);
//                Log.v(LOG_TAG, "MOVIE_POPULAIRY, parseJsonDataForMediumMovieInfo(): " + moviePopularity);

                MediumMovieInfoModel mediumMovieInfoModel = new MediumMovieInfoModel(movieId,
                        movieOriginalTitle, moviePosterUrl, moviePlotSynopsis,
                        movieUserRating, movieReleaseDate, moviePopularity);

                mediumMoviesInfoAsArrayList.add(mediumMovieInfoModel);
            }

            return mediumMoviesInfoAsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<MediumMovieInfoModel> movieModels) {
            super.onPostExecute(movieModels);
            dbHelper.updateDatabaseMovieInfo(mediumMovieInfoArrayList);
            mediumMovieInfoArrayList = parsingJsonForMediumMovieInfo.getMoviesInfoArrayList();
            Log.v(LOG_TAG, "mediumMovieInfoArrayList after parsing:" + mediumMovieInfoArrayList.size());
            customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), mediumMovieInfoArrayList);

            refreshPageView(mediumMovieInfoArrayList);
        }
    }
}
