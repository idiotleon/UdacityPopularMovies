package nanodegree.udacity.leon.udacitypopularmovies.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;
import nanodegree.udacity.leon.udacitypopularmovies.moviedetail.DetailFragment;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.CustomGridViewAdapter;

public class DisplayFragment extends Fragment {

    private static final String LOG_TAG = DisplayFragment.class.getSimpleName();

    private GridView gridView;
    private ArrayList<MediumMovieInfoModel> movieModelArrayList;
    private int movieSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View displayFragmentView = inflater.inflate(R.layout.fragment_display_tabletux, null);
        gridView = (GridView) displayFragmentView.findViewById(R.id.gridview_displayfragment);
        if (movieSelectedPosition > -1) {
            Bundle detailsArgs = new Bundle();
            detailsArgs.putParcelable(GeneralConstants.MOVIE_INFO_DETAILFRAGMENT_IDENTIFIER,
                    movieModelArrayList.get(movieSelectedPosition));
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(detailsArgs);
            getFragmentManager().beginTransaction().
                    replace(R.id.tabletux_container2, detailFragment).commit();
        }
        setHasOptionsMenu(true);
        return displayFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        movieModelArrayList = getArguments()
                .getParcelableArrayList(GeneralConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER);
        refreshDisplayFragment(movieModelArrayList);
    }

    // Both onOptionsItemSelected(MenuItem item) of DisplayFragment and MainActivity will be executed.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_favorite_movies:
                Log.v(LOG_TAG, "onOptionsItemSelected(MenuItem item) executed");
                displayFavoriteMovies();
                break;
            case R.id.sort_popularity_desc:
                Log.v(LOG_TAG, "onOptionsItemSelected(MenuItem item) executed");
                break;
            case R.id.sort_highest_rating_desc:
                Log.v(LOG_TAG, "onOptionsItemSelected(MenuItem item) executed");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFavoriteMovies() {
        movieModelArrayList = GeneralHelper.getAllFavoriteMediumMovieInfoAsArrayList(getActivity());
        refreshDisplayFragment(movieModelArrayList);
    }

    private void refreshDisplayFragment(ArrayList<MediumMovieInfoModel> movieModelArrayList) {
        ArrayList<MediumMovieInfoModel> mediumMovieInfoModels = getArguments().getParcelableArrayList(GeneralConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER);
        gridView.setAdapter(new CustomGridViewAdapter(getActivity(), movieModelArrayList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                movieSelectedPosition = position;
                MediumMovieInfoModel clickedMovieInfo = (MediumMovieInfoModel) gridView.getItemAtPosition(position);
                Bundle detailsArgs = new Bundle();
                detailsArgs.putParcelable(GeneralConstants.MOVIE_INFO_DETAILFRAGMENT_IDENTIFIER, clickedMovieInfo);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(detailsArgs);
                // This is committed in DisplayFragment, not in MainActivity
                getFragmentManager().beginTransaction()
                        .replace(R.id.tabletux_container2, detailFragment).commit();
//                Log.v(LOG_TAG, "detailFragment, transaction committed from DisplayFragment");
                MediumMovieInfoModel clickedMovieInfo = (MediumMovieInfoModel) gridView.getItemAtPosition(position);
                ParseJsonForTheCompleteMovieInfo parseJsonForTheCompleteMovieInfo = new ParseJsonForTheCompleteMovieInfo();
                parseJsonForTheCompleteMovieInfo.execute(clickedMovieInfo);
            }
        });
    }

    class ParseJsonForTheCompleteMovieInfo extends AsyncTask<MediumMovieInfoModel, Void, Void> {

        MediumMovieInfoModel completeMovieInfo;

        @Override
        protected Void doInBackground(MediumMovieInfoModel... params) {
            MediumMovieInfoModel movieInfoModelWithoutTrailerAndReviews = params[0];
            Long movieId = movieInfoModelWithoutTrailerAndReviews.getMovieId();
            try {
                ArrayList<String> trailerUrlArrayList = GeneralHelper.parseJsonDataForMovieTrailerUrl(movieId);
                ArrayList<MovieReviewModel> reviewUrlArrayList = GeneralHelper.parseJsonDataForMovieReview(movieId);
                Log.v(LOG_TAG, "GeneralHelper.parseJsonDataForMovieTrailerUrl(movieId), DisplayFragment: " + trailerUrlArrayList);
                Log.v(LOG_TAG, "GeneralHelper.parseJsonDataForMovieReview(movieId), DisplayFragment: " + reviewUrlArrayList);
                completeMovieInfo = new MediumMovieInfoModel(movieInfoModelWithoutTrailerAndReviews, trailerUrlArrayList, reviewUrlArrayList);
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
            Bundle detailsArgs = new Bundle();
            detailsArgs.putParcelable(GeneralConstants.MOVIE_INFO_DETAILFRAGMENT_IDENTIFIER, completeMovieInfo);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(detailsArgs);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.tabletux_container2, detailFragment);
            fragmentTransaction.commit();
        }
    }
}
