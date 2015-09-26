package nanodegree.udacity.leon.udacitypopularmovies.moviedetail;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieReviewCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieTrailerCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.CompleteMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieTrailerModel;

public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

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

    private View detailFragmentView;

    private CompleteMovieInfoModel completeMovieInfo;
    private MediumMovieInfoModel mediumMovieInfo;
    private long movieId;
    private ArrayList<String> movieTrailerUrlArrayList;
    private ArrayList<MovieReviewModel> movieReviewsArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailFragmentView = inflater.inflate(R.layout.fragment_details_tabletux, null);

        movieTrailerUrlArrayList = new ArrayList<>();
        movieReviewsArrayList = new ArrayList<>();

        Bundle data = getArguments();
        if (data != null) {
            mediumMovieInfo = data.getParcelable(GeneralConstants.MOVIE_INFO_DETAIL_FRAGMENT_IDENTIFIER);
//                Log.v(LOG_TAG, "mediumMovieInfo.getMovieImageUrl(), onCreateView(): " + mediumMovieInfo.getMovieImageUrl());
            movieId = mediumMovieInfo.getMovieId();

            movieTrailerUrlArrayList = GeneralHelper.getMovieTrailerUrls(getActivity(), movieId);
            movieReviewsArrayList = GeneralHelper.getMovieReviews(getActivity(), movieId);
            completeMovieInfo = new CompleteMovieInfoModel(mediumMovieInfo, movieTrailerUrlArrayList, movieReviewsArrayList);
            if (movieTrailerUrlArrayList.isEmpty() || movieReviewsArrayList.isEmpty()) {
                if (GeneralHelper.isNetworkAvailable(getActivity())) {
                    // For update (database) purpose
                    ParseForMovieTrailerAndReviews parseForMovieTrailerAndReviews = new ParseForMovieTrailerAndReviews();
                    parseForMovieTrailerAndReviews.execute(movieId);
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                }
            } else {
                completeMovieInfo = new CompleteMovieInfoModel(mediumMovieInfo, movieTrailerUrlArrayList, movieReviewsArrayList);
//                    Log.v(LOG_TAG, "completeMovieInfo.getMovieImageUrl(), onCreate(): " + completeMovieInfo.getMovieImageUrl());
                // For updating database
                if (GeneralHelper.isNetworkAvailable(getActivity())) {
                    // For update (database) purpose
                    ParseForMovieTrailerAndReviews parseForMovieTrailerAndReviews = new ParseForMovieTrailerAndReviews();
                    parseForMovieTrailerAndReviews.execute(movieId);
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                }
            }
        }

        return detailFragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, movieTrailerUrlArrayList.get(0));
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_intent_chooser_text)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        textViewOriginalTitle = (TextView) detailFragmentView.findViewById(R.id.textview_original_title_movie_details_tabletux);
        textViewPlotSynopsis = (TextView) detailFragmentView.findViewById(R.id.textview_plot_synopsis_movie_details_tabletux);
        textViewReleaseDate = (TextView) detailFragmentView.findViewById(R.id.textview_release_date_movie_details_tabletux);
        imageViewPosterImage = (ImageView) detailFragmentView.findViewById(R.id.imageview_movie_poster_image_tabletux);

        ratingBar = (RatingBar) detailFragmentView.findViewById(R.id.ratingbar_movie_details_tabletux);

        /**
         * By SharedPreference, I can save the favorite status of a particular udacity_popular_movie.
         */
        favoriteStatusCheckBox = (CheckBox) detailFragmentView.findViewById(R.id.checkbox_favorite_star_button_tabletux);
        if (1 == GeneralHelper.getFavoriteStatus(getActivity(), Long.toString(completeMovieInfo.getMovieId()), 0)) {
            favoriteStatusCheckBox.setChecked(true);
        } else {
            favoriteStatusCheckBox.setChecked(false);
        }
        favoriteStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GeneralHelper.markAsFavorite(getActivity(), mediumMovieInfo);
                    Toast.makeText(getActivity(), "Marked as Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    GeneralHelper.cancelFavoriteStatus(getActivity(), mediumMovieInfo);
                    Toast.makeText(getActivity(), "Favorite Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Picasso.with(getActivity()).load(completeMovieInfo.getMovieImageUrl()).into(imageViewPosterImage);
//        Log.v(LOG_TAG, "movieModel.getMovieImageUrl() - Line70, onCreate(): " + movieModel.getMovieImageUrl());
        textViewOriginalTitle.setText("Original Title: " + completeMovieInfo.getMovieOriginalTitle());
        textViewPlotSynopsis.setText("Plot Synopsis: " + completeMovieInfo.getMoviePlotSynopsis());
        ratingBar.setRating(completeMovieInfo.getMovieUserRating());
        textViewReleaseDate.setText("Release Date: " + completeMovieInfo.getMovieReleaseDate());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getActivity(), "You changed rating to: " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ParseForMovieTrailerAndReviews extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            try {
                long movieId = params[0];
                movieTrailerUrlArrayList = parseJsonDataForMovieTrailerUrl(movieId);
                movieReviewsArrayList = parseJsonDataForMovieReview(movieId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            refreshMovieTrailers(movieTrailerUrlArrayList);
            refreshMovieReviews(movieReviewsArrayList);
            completeMovieInfo = new CompleteMovieInfoModel(mediumMovieInfo, movieTrailerUrlArrayList, movieReviewsArrayList);
//            Log.v(LOG_TAG, "completeMovieInfo.getMovieImageUrl(), onPostExecute(): " + completeMovieInfo.getMovieImageUrl());
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
        public ArrayList<String> parseJsonDataForMovieTrailerUrl(long movieId) throws MalformedURLException, JSONException, NullPointerException {

            // base API URL for fetching trailer id
            final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
            // base Youtube URL for displaying trailer
            final String BASE_YOUTUBE_URL = "http://www.youtube.com/v/";
            final String PARAM_VIDEO = "/videos?";
            final String UPM_RESULTS = "results";
            final String UPM_KEY = "key";

            String movieTrailerAPIUrl = BASE_API_TRAILER_URL + movieId + PARAM_VIDEO + GeneralConstants.PARAM_API_KEY + "=" + GeneralConstants.API_KEY;
//            Log.v(LOG_TAG, "movieTrailerAPIUrl, parseJsonDataForMovieTrailerUrl(long movieId): " + movieTrailerAPIUrl);
            URL movieTrailerAPIURL = new URL(movieTrailerAPIUrl);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieTrailerAPIURL), Line325: " + getAllJsonDataAsStringFromAPI(movieTrailerAPIURL));
            JSONObject movieTrailerAllJsonDataObject = new JSONObject(GeneralHelper.getAllJsonDataAsStringFromAPI(movieTrailerAPIURL));

            JSONArray movieTrailerInfoJsonArray = movieTrailerAllJsonDataObject.getJSONArray(UPM_RESULTS);
//            Log.v(LOG_TAG, "movieTrailerInfoJsonArray: " + movieTrailerInfoJsonArray);

            ArrayList<String> movieTrailerUrlArrayList = new ArrayList<>();
            for (int i = 0; i < movieTrailerInfoJsonArray.length(); i++) {
                JSONObject itemJson = movieTrailerInfoJsonArray.getJSONObject(i);
                String key = itemJson.getString(UPM_KEY);
//                Log.v(LOG_TAG, "key, parseJsonDataForMovieTrailerUrl(long movieId): " + key);
                String url = BASE_YOUTUBE_URL + key;
//                Log.v(LOG_TAG, "url, parseJsonDataForMovieTrailerUrl(long movieId): " + url);
                MovieTrailerModel movieTrailerModel = new MovieTrailerModel(movieId, url);
                // todo: how to avoid duplicate record
                GeneralHelper.insertMovieTrailer(getActivity(), movieTrailerModel);
                movieTrailerUrlArrayList.add(url);
            }
            return movieTrailerUrlArrayList;
        }

        public ArrayList<MovieReviewModel> parseJsonDataForMovieReview(long movieId) throws MalformedURLException, JSONException, NullPointerException {

            // base API URL for fetching reviews
            final String BASE_API_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
            final String PARAM_REVIEWS = "/reviews?";

            final String UPM_RESULTS = "results";
            final String UPM_AUTHOR = "author";
            final String UPM_CONTENT = "content";
            final String UPM_URL = "url";

            String movieReviewAPIUrl = BASE_API_TRAILER_URL + Long.toString(movieId) + PARAM_REVIEWS + GeneralConstants.PARAM_API_KEY + "=" + GeneralConstants.API_KEY;
//            Log.v(LOG_TAG, "movieReviewAPIUrl: " + movieReviewAPIUrl);
            URL movieReviewAPIURL = new URL(movieReviewAPIUrl);
            String allJsonData = GeneralHelper.getAllJsonDataAsStringFromAPI(movieReviewAPIURL);
//            Log.v(LOG_TAG, "getAllJsonDataAsStringFromAPI(movieReviewAPIURL): " + allJsonData);
            JSONObject movieReviewAllJsonDataObject = new JSONObject(allJsonData);
//            Log.v(LOG_TAG, "movieReviewAllJsonDataObject: " + movieReviewAllJsonDataObject);
            JSONArray movieTrailerInfoJsonArray = movieReviewAllJsonDataObject.getJSONArray(UPM_RESULTS);
//            Log.v(LOG_TAG, "movieReviewInfoJsonArray: " + movieTrailerInfoJsonArray);

            ArrayList<MovieReviewModel> movieReviewArrayList = new ArrayList<>();
            for (int i = 0; i < movieTrailerInfoJsonArray.length(); i++) {
                JSONObject itemJson = movieTrailerInfoJsonArray.getJSONObject(i);
                String author = itemJson.getString(UPM_AUTHOR);
//                Log.v(LOG_TAG, "author, parseJsonDataForMovieReview(long movieId): " + author);
                String content = itemJson.getString(UPM_CONTENT);
//                Log.v(LOG_TAG, "content, parseJsonDataForMovieReview(long movieId): " + content);
                String url = itemJson.getString(UPM_URL);
//                Log.v(LOG_TAG, "review url, parseJsonDataForMovieReview(long movieId): " + url);
                MovieReviewModel movieReviewModel = new MovieReviewModel(movieId, author, content, url);
                GeneralHelper.insertMovieReviews(getActivity(), movieReviewModel);
                movieReviewArrayList.add(movieReviewModel);
            }
            return movieReviewArrayList;
        }
    }

    private void refreshMovieTrailers(final ArrayList<String> movieTrailerArrayList) {
        movieTrailerListView = (ListView) detailFragmentView.findViewById(R.id.listview_movietrailers_tabletux);
        movieTrailerListViewAdapter = new MovieTrailerCustomListViewAdapter(getActivity(), movieTrailerArrayList);
//        Log.v(LOG_TAG, "movieInfo.getMovieTrailerUrlArrayList() - Line59, onCreate(): " + movieInfo.getMovieTrailerUrlArrayList().toString());
        movieTrailerListView.setAdapter(movieTrailerListViewAdapter);
        movieTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movieTrailerArrayList.get(position);
//                Log.v(LOG_TAG, "Youtube Trailer URL is, refreshMovieTrailers(): " + url);
                Intent implicitVideoPlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitVideoPlayIntent);
            }
        });
    }

    private void refreshMovieReviews(final ArrayList<MovieReviewModel> movieReviewArrayList) {
        movieReviewListView = (ListView) detailFragmentView.findViewById(R.id.listview_moviereviews_tabletux);
        movieReviewListViewAdapter = new MovieReviewCustomListViewAdapter(getActivity(), movieReviewArrayList);
        movieReviewListView.setAdapter(movieReviewListViewAdapter);
        movieReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movieReviewArrayList.get(position).getReviewUrl();
//                Log.v(LOG_TAG, "Review URL, refreshMovieReviews(): " + url);
                Intent implicitIntentReviewURLBrowsing = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitIntentReviewURLBrowsing);
            }
        });
    }
}
