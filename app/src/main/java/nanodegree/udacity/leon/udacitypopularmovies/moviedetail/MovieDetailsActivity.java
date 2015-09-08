package nanodegree.udacity.leon.udacitypopularmovies.moviedetail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieReviewCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieTrailerCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.CompleteMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

public class MovieDetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private MediumMovieInfoModel movieInfo;

    private TextView textViewOriginalTitle;
    private TextView textViewPlotSynopsis;
    private TextView textViewReleaseDate;
    private ImageView imageViewPosterImage;

    private RatingBar ratingBar;
    private CheckBox favoriteStatusCheckBox;

    private ListView movieTrailerListView;
    private ListView movieReviewListView;

    private MovieTrailerCustomListViewAdapter movieTrailerListViewAdapter;
    private MovieReviewCustomListViewAdapter movieReviewListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        if (savedInstanceState != null) {
            movieInfo = savedInstanceState.getParcelable(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_DETAIL_ACTIVITY);
        } else {
            Bundle data = getIntent().getExtras();
            movieInfo = (MediumMovieInfoModel) data.getParcelable(GeneralConstants.MOVIE_PARCEL);
        }

        textViewOriginalTitle = (TextView) findViewById(R.id.textview_original_title_movie_details);
        textViewPlotSynopsis = (TextView) findViewById(R.id.textview_plot_synopsis_movie_details);
        textViewReleaseDate = (TextView) findViewById(R.id.textview_release_date_movie_details);
        imageViewPosterImage = (ImageView) findViewById(R.id.imageview_movie_poster_image);

        ratingBar = (RatingBar) findViewById(R.id.ratingbar_movie_details);

        /**
         * By SharedPreference, I can save the favorite status of a particular udacity_popular_moive.
         */
        favoriteStatusCheckBox = (CheckBox) findViewById(R.id.checkbox_favorite_star_button);
        if (1 == GeneralHelper.getFavoriteStatus(MovieDetailsActivity.this, Long.toString(movieInfo.getMovieId()), 0)) {
            favoriteStatusCheckBox.setChecked(true);
        } else {
            favoriteStatusCheckBox.setChecked(false);
        }
        favoriteStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GeneralHelper.markAsFavorite(MovieDetailsActivity.this, Long.toString(movieInfo.getMovieId()));
                    Toast.makeText(MovieDetailsActivity.this, "Marked as Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    GeneralHelper.cancelFavoriteStatus(MovieDetailsActivity.this, Long.toString(movieInfo.getMovieId()));
                    Toast.makeText(MovieDetailsActivity.this, "Favorite Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        movieTrailerListView = (ListView) findViewById(R.id.listview_movietrailers);
        movieTrailerListViewAdapter = new MovieTrailerCustomListViewAdapter(MovieDetailsActivity.this, movieInfo.getMovieTrailerUrlArrayList());
//        Log.v(LOG_TAG, "movieInfo.getMovieTrailerUrlArrayList() - Line59, onCreate(): " + movieInfo.getMovieTrailerUrlArrayList().toString());
        movieTrailerListView.setAdapter(movieTrailerListViewAdapter);
        movieTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movieInfo.getMovieTrailerUrlArrayList().get(position);
//                Log.v(LOG_TAG, "Youtube Trailer URL is: " + url);
                Intent implicitVideoPlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitVideoPlayIntent);
            }
        });

        movieReviewListView = (ListView) findViewById(R.id.listview_moviereviews);
        movieReviewListViewAdapter = new MovieReviewCustomListViewAdapter(MovieDetailsActivity.this, movieInfo.getMovieReviewArrayList());
        movieReviewListView.setAdapter(movieReviewListViewAdapter);
        movieReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movieInfo.getMovieReviewArrayList().get(position).getReviewUrl();
//                Log.v(LOG_TAG, "Review URL: " + url);
                Intent implicitIntentReviewURLBrowsing = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitIntentReviewURLBrowsing);
            }
        });

        Picasso.with(this).load(movieInfo.getMovieImageUrl()).into(imageViewPosterImage);
//        Log.v(LOG_TAG, "movieInfo.getMovieImageUrl() - Line70, onCreate(): " + movieInfo.getMovieImageUrl());
        textViewOriginalTitle.setText("Original Title: " + movieInfo.getMovieOriginalTitle());
        textViewPlotSynopsis.setText("Plot Synopsis: " + movieInfo.getMoviePlotSynopsis());
        ratingBar.setRating(movieInfo.getMovieUserRating());
        textViewReleaseDate.setText("Release Date: " + movieInfo.getMovieReleaseDate());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(MovieDetailsActivity.this, "You changed rating to: " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_DETAIL_ACTIVITY, movieInfo);
        super.onSaveInstanceState(outState);
    }

    public class ParseForMovieTralierAndReviews extends AsyncTask<Long, Void, CompleteMovieInfoModel>{

        @Override
        protected CompleteMovieInfoModel doInBackground(Long... params) {
            long MovieId = params[0];

            return null;
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
        public ArrayList<String> parseJsonDataForMovieTrailerUrl(long movieId) throws MalformedURLException, JSONException {

            // base API URL for fetching trailer id
            final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
            // base Youtube URL for displaying trailer
            final String BASE_YOUTUBE_URL = "http://www.youtube.com/v/";
            final String PARAM_VIDEO = "/videos?";
            final String UPM_RESULTS = "results";
            final String UPM_KEY = "key";

            String movieTrailerAPIUrl = BASE_API_TRAILER_URL + movieId + PARAM_VIDEO + GeneralConstants.PARAM_API_KEY + "=" + GeneralConstants.API_KEY;
//            Log.v(LOG_TAG, "movieTrailerAPIUrl - MainActivity: " + movieTrailerAPIUrl);
            URL movieTrailerAPIURL = new URL(movieTrailerAPIUrl);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieTrailerAPIURL), Line325: " + getAllJsonDataAsStringFromAPI(movieTrailerAPIURL));
            JSONObject movieTrailerAllJsonDataObject = new JSONObject(GeneralHelper.getAllJsonDataAsStringFromAPI(movieTrailerAPIURL));

            JSONArray movieTrailerInfoJsonArray = movieTrailerAllJsonDataObject.getJSONArray(UPM_RESULTS);
//            Log.v(LOG_TAG, "movieTrailerInfoJsonArray: " + movieTrailerInfoJsonArray);

            ArrayList<String> movieTrailerUrlArrayList = new ArrayList<>();
            for (int i = 0; i < movieTrailerInfoJsonArray.length(); i++) {
                JSONObject itemJson = movieTrailerInfoJsonArray.getJSONObject(i);
                String key = itemJson.getString(UPM_KEY);
//                Log.v(LOG_TAG, "key: " + key);
                String url = BASE_YOUTUBE_URL + key;
//                Log.v(LOG_TAG, "url: " + url);
                movieTrailerUrlArrayList.add(url);
            }
            return movieTrailerUrlArrayList;
        }

        public ArrayList<MovieReviewModel> parseJsonDataForMovieReview(long movieId) throws MalformedURLException, JSONException {

            // base API URL for fetching reviews
            final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
            final String PARAM_REVIEWS = "/reviews?";

            final String OWN_RESULTS = "results";
            final String OWN_AUTHOR = "author";
            final String OWN_CONTENT = "content";
            final String OWN_URL = "url";

            String movieReviewAPIUrl = BASE_API_TRAILER_URL + Long.toString(movieId) + PARAM_REVIEWS + GeneralConstants.PARAM_API_KEY + "=" + GeneralConstants.API_KEY;
//            Log.v(LOG_TAG, "movieReviewAPIUrl - MainActivity, Line428: " + movieReviewAPIUrl);
            URL movieReviewAPIURL = new URL(movieReviewAPIUrl);
            String allJsonData = GeneralHelper.getAllJsonDataAsStringFromAPI(movieReviewAPIURL);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieReviewAPIURL), Line431: " + allJsonData);
            JSONObject movieReviewAllJsonDataObject = new JSONObject(allJsonData);
            Log.v(LOG_TAG, "movieReviewAllJsonDataObject, Line433: " + movieReviewAllJsonDataObject);
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


}
