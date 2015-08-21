package nanodegree.udacity.leon.udacitypopularmovies.moviedetail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import nanodegree.udacity.leon.udacitypopularmovies.helper.CommonConstants;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieReviewCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieTrailerCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;

public class MovieDetailsActivity extends Activity {

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private MovieInfoModel movieInfo;

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
            movieInfo = savedInstanceState.getParcelable(CommonConstants.MOVIE_SAVED_INSTANCE_STATE_DETAIL_ACTIVITY);
        } else {
            Bundle data = getIntent().getExtras();
            movieInfo = (MovieInfoModel) data.getParcelable(CommonConstants.MOVIE_PARCEL);
        }

        textViewOriginalTitle = (TextView) findViewById(R.id.textview_original_title_movie_details);
        textViewPlotSynopsis = (TextView) findViewById(R.id.textview_plot_synopsis_movie_details);
        textViewReleaseDate = (TextView) findViewById(R.id.textview_release_date_movie_details);
        imageViewPosterImage = (ImageView) findViewById(R.id.imageview_movie_poster_image);

        ratingBar = (RatingBar) findViewById(R.id.ratingbar_movie_details);

        /**
         * By SharedPreference, I can save the favorite status of a particular movie.
         */
        favoriteStatusCheckBox = (CheckBox) findViewById(R.id.checkbox_favorite_star_button);
        if (1 == GeneralHelper.getFavoriteStatus(MovieDetailsActivity.this, movieInfo.getMovieId(), 0)) {
            favoriteStatusCheckBox.setChecked(true);
        } else {
            favoriteStatusCheckBox.setChecked(false);
        }
        favoriteStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GeneralHelper.markAsFavorite(MovieDetailsActivity.this, movieInfo.getMovieId());
                    Toast.makeText(MovieDetailsActivity.this, "Marked as Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    GeneralHelper.cancelFavoriteStatus(MovieDetailsActivity.this, movieInfo.getMovieId());
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
        ratingBar.setRating(Float.parseFloat(movieInfo.getMovieUserRating()));
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
        outState.putParcelable(CommonConstants.MOVIE_SAVED_INSTANCE_STATE_DETAIL_ACTIVITY, movieInfo);
        super.onSaveInstanceState(outState);

    }
}
