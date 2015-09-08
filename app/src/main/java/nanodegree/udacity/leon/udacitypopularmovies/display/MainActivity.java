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

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.CustomGridViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.moviedetail.MovieDetailsActivity;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;


public class MainActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    /*
     *  ============================================================
     *  Variable to store API Key
     *  Please type your API key here
     *  ============================================================
     */


    private GridView gridView;

    private CustomGridViewAdapter customGridViewAdapter;

    private ArrayList<MovieInfoModel> movieInfo;

    private ParsingJSONForMovieInfo parsingJSONForMovieInfo;

//    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        dbHelper = new DatabaseHelper(MainActivity.this);

        if (savedInstanceState != null) {
            movieInfo = savedInstanceState.getParcelableArrayList(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_MAIN_ACTIVITY);
            Log.v(LOG_TAG, "movieInfo, fetched from savedInstanceState(): " + movieInfo);
            customSetContentView(movieInfo);
        }
/*        else if ((dbHelper.getAllMovieInfo()).size() > 0) {
            movieInfo = dbHelper.getAllMovieInfo();
            customSetContentView(movieInfo);
            // For update (database) purpose
            parsingJSONForMovieInfo = new ParsingJSONForMovieInfo();
            parsingJSONForMovieInfo.execute(API_KEY, "popularity");
        } */
        else {
            parsingJSONForMovieInfo = new ParsingJSONForMovieInfo();
            parsingJSONForMovieInfo.execute(GeneralConstants.API_KEY, "popularity");
        }
    }

    private void customSetContentView(ArrayList<MovieInfoModel> movieInfo) {
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
                    MovieInfoModel clickedMovieInfo = (MovieInfoModel) gridView.getItemAtPosition(position);
                    Intent detailsIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                    detailsIntent.putExtra(GeneralConstants.MOVIE_PARCEL, new MovieInfoModel(
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
        savedInstanceState.putParcelableArrayList(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_MAIN_ACTIVITY, movieInfo);
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
            ParsingJSONForMovieInfo parsingJSONForMovieInfo = new ParsingJSONForMovieInfo();
            parsingJSONForMovieInfo.execute(GeneralConstants.API_KEY, "popularity");
            return true;
        }
        if (id == R.id.sort_highest_rating_desc) {
            ParsingJSONForMovieInfo parsingJSONForMovieInfo = new ParsingJSONForMovieInfo();
            parsingJSONForMovieInfo.execute(GeneralConstants.API_KEY, "highestrating");
/*            movieInfo = dbHelper.getAllMovieInfoOrderByUserRating();
            customSetContentView(movieInfo);*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ParsingJSONForMovieInfo extends AsyncTask<String, Void, ArrayList<MovieInfoModel>> {

        private final String LOG_TAG = ParsingJSONForMovieInfo.class.getSimpleName();

        private ArrayList<MovieInfoModel> moviesInfoAsArrayList;
        private String moviesJsonStr;
        private URL defaultUrl;

        // base API URL to fetch udacity_popular_movie info
        final String BASE_API_MOVIE_INFO_URL = "http://api.themoviedb.org/3/discover/movie?";

        final String QUERY_PARAM = "sort_by";
        final String SETTINGS_PARAM_POPULARITY_DESC = "popularity.desc";
        // parameter options for settings
        final String SETTINGS_PARAM_HIGHESTRATED_DESC = "vote_average.desc";

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
                    ArrayList<MovieInfoModel> movieInfo = GeneralHelper.parseJsonDataForMovieInfo(moviesJsonStr);
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
         * Setter method for udacity_popular_movie info
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

        @Override
        protected void onPostExecute(ArrayList<MovieInfoModel> movieModels) {
            super.onPostExecute(movieModels);
            customGridViewAdapter = new CustomGridViewAdapter(getApplicationContext(), moviesInfoAsArrayList);
            movieInfo = parsingJSONForMovieInfo.getMoviesInfoArrayList();

//            updateDatabase(movieInfo);
            customSetContentView(movieInfo);

            Log.v(LOG_TAG, "movieInfo after parsing:" + movieInfo.toString());
//            gridView.setAdapter(customGridViewAdapter);
        }

/*        private void updateDatabase(ArrayList<MovieInfoModel> movieInfoArrayList) {
            for (int i = 0; i < movieInfoArrayList.size(); i++) {
                dbHelper.updateTableData(movieInfo.get(i));
            }
        }     */
    }
}
