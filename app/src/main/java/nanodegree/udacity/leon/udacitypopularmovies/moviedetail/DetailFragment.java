package nanodegree.udacity.leon.udacitypopularmovies.moviedetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
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

import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieReviewCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.MovieTrailerCustomListViewAdapter;
import nanodegree.udacity.leon.udacitypopularmovies.helper.CommonConstants;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;

public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private MovieInfoModel movie;

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

    View detailFragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailFragmentView = inflater.inflate(R.layout.fragment_details_tabletux, null);
        return detailFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        movie = getArguments().getParcelable(CommonConstants.MOVIE_INFO_DETAILFRAGMENT_IDENTIFIER);

        textViewOriginalTitle = (TextView) detailFragmentView.findViewById(R.id.textview_original_title_movie_details_tabletux);
        textViewPlotSynopsis = (TextView) detailFragmentView.findViewById(R.id.textview_plot_synopsis_movie_details_tabletux);
        textViewReleaseDate = (TextView) detailFragmentView.findViewById(R.id.textview_release_date_movie_details_tabletux);
        imageViewPosterImage = (ImageView) detailFragmentView.findViewById(R.id.imageview_movie_poster_image_tabletux);

        ratingBar = (RatingBar) detailFragmentView.findViewById(R.id.ratingbar_movie_details_tabletux);

        /**
         * By SharedPreference, I can save the favorite status of a particular movie.
         */
        favoriteStatusCheckBox = (CheckBox) detailFragmentView.findViewById(R.id.checkbox_favorite_star_button_tabletux);
        if (1 == GeneralHelper.getFavoriteStatus(getActivity(), movie.getMovieId().toString(), 0)) {
            favoriteStatusCheckBox.setChecked(true);
        } else {
            favoriteStatusCheckBox.setChecked(false);
        }
        favoriteStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GeneralHelper.markAsFavorite(getActivity(), movie.getMovieId().toString());
                    Toast.makeText(getActivity(), "Marked as Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    GeneralHelper.cancelFavoriteStatus(getActivity(), movie.getMovieId().toString());
                    Toast.makeText(getActivity(), "Favorite Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        movieTrailerListView = (ListView) detailFragmentView.findViewById(R.id.listview_movietrailers);
        movieTrailerListViewAdapter = new MovieTrailerCustomListViewAdapter(getActivity(), movie.getMovieTrailerUrlArrayList());
//        Log.v(LOG_TAG, "movieModel.getMovieTrailerUrlArrayList() - Line59, onCreate(): " + movieModel.getMovieTrailerUrlArrayList().toString());
        movieTrailerListView.setAdapter(movieTrailerListViewAdapter);
        movieTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movie.getMovieTrailerUrlArrayList().get(position);
//                Log.v(LOG_TAG, "Youtube Trailer URL is: " + url);
                Intent implicitVideoPlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitVideoPlayIntent);
            }
        });

        movieReviewListView = (ListView) detailFragmentView.findViewById(R.id.listview_moviereviews);
        movieReviewListViewAdapter = new MovieReviewCustomListViewAdapter(getActivity(), movie.getMovieReviewArrayList());
        movieReviewListView.setAdapter(movieReviewListViewAdapter);
        movieReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = movie.getMovieReviewArrayList().get(position).getReviewUrl();
//                Log.v(LOG_TAG, "Review URL: " + url);
                Intent implicitIntentReviewURLBrowsing = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(implicitIntentReviewURLBrowsing);
            }
        });

        Picasso.with(getActivity()).load(movie.getMovieImageUrl()).into(imageViewPosterImage);
//        Log.v(LOG_TAG, "movieModel.getMovieImageUrl() - Line70, onCreate(): " + movieModel.getMovieImageUrl());
        textViewOriginalTitle.setText("Original Title: " + movie.getMovieOriginalTitle());
        textViewPlotSynopsis.setText("Plot Synopsis: " + movie.getMoviePlotSynopsis());
        ratingBar.setRating(Float.parseFloat(movie.getMovieUserRating()));
        textViewReleaseDate.setText("Release Date: " + movie.getMovieReleaseDate());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getActivity(), "You changed rating to: " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
