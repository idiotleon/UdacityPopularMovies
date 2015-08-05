package nanodegree.udacity.leon.udacitypopularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends Activity {

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private MovieModel movieModel;

    private TextView textViewOriginalTitle;
    private TextView textViewPlotSynopsis;
    private TextView textViewReleaseDate;
    private ImageView imageViewPosterImage;

    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        textViewOriginalTitle = (TextView) findViewById(R.id.textview_original_title_movie_details);
        textViewPlotSynopsis = (TextView) findViewById(R.id.textview_plot_synopsis_movie_details);
        textViewReleaseDate = (TextView) findViewById(R.id.textview_release_date_movie_details);
        imageViewPosterImage = (ImageView) findViewById(R.id.imageview_movie_posterimage);

        ratingBar = (RatingBar) findViewById(R.id.ratingbar_movie_details);

        Intent detailsIntent = getIntent();
        movieModel = new MovieModel(
                detailsIntent.getStringExtra(CommonConstants.movieId),
                detailsIntent.getStringExtra(CommonConstants.movieOriginalTitle),
                detailsIntent.getStringExtra(CommonConstants.moviePosterImageURL),
                detailsIntent.getStringExtra(CommonConstants.moviePlotSynopsis),
                detailsIntent.getStringExtra(CommonConstants.movieUserRating),
                detailsIntent.getStringExtra(CommonConstants.movieReleaseDate)
        );

        Picasso.with(this).load(movieModel.getMovieImageUrl()).into(imageViewPosterImage);
        Log.v(LOG_TAG, "movieModel.getMovieImageUrl() - MovieDetailsActivity: " + movieModel.getMovieImageUrl());
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
