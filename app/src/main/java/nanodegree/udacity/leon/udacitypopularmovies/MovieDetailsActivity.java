package nanodegree.udacity.leon.udacitypopularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;

public class MovieDetailsActivity extends Activity {

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private MovieModel movieModel;

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

        Intent detailsIntent = getIntent();
        movieModel = new MovieModel(
                detailsIntent.getStringExtra(CommonConstants.movieId),
                detailsIntent.getStringExtra(CommonConstants.movieOriginalTitle),
                detailsIntent.getStringExtra(CommonConstants.moviePosterImageURL),
                detailsIntent.getStringExtra(CommonConstants.moviePlotSynopsis),
                detailsIntent.getStringExtra(CommonConstants.movieUserRating),
                detailsIntent.getStringExtra(CommonConstants.movieReleaseDate),
                detailsIntent.getStringArrayListExtra(CommonConstants.movieTrailersUrlArrayList),
                (ArrayList<MovieReviewModel>) detailsIntent.getSerializableExtra(CommonConstants.movieReviewArrayList)
        );

        textViewOriginalTitle = (TextView) findViewById(R.id.textview_original_title_movie_details);
        textViewPlotSynopsis = (TextView) findViewById(R.id.textview_plot_synopsis_movie_details);
        textViewReleaseDate = (TextView) findViewById(R.id.textview_release_date_movie_details);
        imageViewPosterImage = (ImageView) findViewById(R.id.imageview_movie_poster_image);

        ratingBar = (RatingBar) findViewById(R.id.ratingbar_movie_details);

        /**
         * By SharedPreference, I can save the favorite status of a particular movie.
         */
        favoriteStatusCheckBox = (CheckBox) findViewById(R.id.checkbox_favorite_star_button);
        if (1 == FavoriteStatus.getFavoriteStatus(MovieDetailsActivity.this, movieModel.getMovieId(), 0)) {
            favoriteStatusCheckBox.setChecked(true);
        } else {
            favoriteStatusCheckBox.setChecked(false);
        }
        favoriteStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    FavoriteStatus.markAsFavorite(MovieDetailsActivity.this, movieModel.getMovieId());
                else
                    FavoriteStatus.cancelFavoriteStatus(MovieDetailsActivity.this, movieModel.getMovieId());
            }
        });

        movieTrailerListView = (ListView) findViewById(R.id.listview_movietrailers);
        movieTrailerListViewAdapter = new MovieTrailerCustomListViewAdapter(MovieDetailsActivity.this, movieModel.getMovieTrailerUrlArrayList());
        Log.v(LOG_TAG, "movieModel.getMovieTrailerUrlArrayList() - Line59, onCreate(): " + movieModel.getMovieTrailerUrlArrayList().toString());
        movieTrailerListView.setAdapter(movieTrailerListViewAdapter);
        movieTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movieModel.getMovieTrailerUrlArrayList().get(position);
                Log.v(LOG_TAG, "Youtube Trailer URL is: " + url);
                Intent implicitVideoPlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitVideoPlayIntent);
            }
        });

        movieReviewListView = (ListView) findViewById(R.id.listview_moviereviews);
        movieReviewListViewAdapter = new MovieReviewCustomListViewAdapter(MovieDetailsActivity.this, movieModel.getMovieReviewArrayList());
        movieReviewListView.setAdapter(movieReviewListViewAdapter);
        movieReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movieModel.getMovieReviewArrayList().get(position).getReviewUrl();
                Log.v(LOG_TAG, "Review URL: " + url);
                Intent implicitIntentReviewURLBrowsing = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitIntentReviewURLBrowsing);
            }
        });


        Picasso.with(this).load(movieModel.getMovieImageUrl()).into(imageViewPosterImage);
        Log.v(LOG_TAG, "movieModel.getMovieImageUrl() - Line70, onCreate(): " + movieModel.getMovieImageUrl());
        textViewOriginalTitle.setText("Original Title: " + movieModel.getMovieOriginalTitle());
        textViewPlotSynopsis.setText("Plot Synopsis: " + movieModel.getMoviePlotSynopsis());
        ratingBar.setRating(Float.parseFloat(movieModel.getMovieUserRating()));
        textViewReleaseDate.setText("Release Date: " + movieModel.getMovieReleaseDate());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(MovieDetailsActivity.this, "You changed rating to: " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
