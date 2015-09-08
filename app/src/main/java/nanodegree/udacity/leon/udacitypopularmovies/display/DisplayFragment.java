package nanodegree.udacity.leon.udacitypopularmovies.display;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;
import nanodegree.udacity.leon.udacitypopularmovies.moviedetail.DetailFragment;
import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralConstants;
import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.CustomGridViewAdapter;

public class DisplayFragment extends Fragment {

    private static final String LOG_TAG = DisplayFragment.class.getSimpleName();

    private GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View displayFragmentView = inflater.inflate(R.layout.fragment_display_tabletux, null);
        gridView = (GridView) displayFragmentView.findViewById(R.id.gridview_displayfragment);
        return displayFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayList<MovieInfoModel> movieModelArrayList = getArguments().getParcelableArrayList(GeneralConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER);
        gridView.setAdapter(new CustomGridViewAdapter(getActivity(), movieModelArrayList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfoModel clickedMovieInfo = (MovieInfoModel) gridView.getItemAtPosition(position);
                ParseJsonForTheCompleteMovieInfo parseJsonForTheCompleteMovieInfo = new ParseJsonForTheCompleteMovieInfo();
                parseJsonForTheCompleteMovieInfo.execute(clickedMovieInfo);
            }
        });
    }

    class ParseJsonForTheCompleteMovieInfo extends AsyncTask<MovieInfoModel, Void, Void> {

        MovieInfoModel completeMovieInfo;

        @Override
        protected Void doInBackground(MovieInfoModel... params) {
            MovieInfoModel movieInfoModelWithoutTrailerAndReviews = params[0];
            Long movieId = movieInfoModelWithoutTrailerAndReviews.getMovieId();
            try {
                ArrayList<String> trailerUrlArrayList = GeneralHelper.parseJsonDataForMovieTrailerUrl(movieId);
                ArrayList<MovieReviewModel> reviewUrlArrayList = GeneralHelper.parseJsonDataForMovieReview(movieId);
                Log.v(LOG_TAG, "GeneralHelper.parseJsonDataForMovieTrailerUrl(movieId), DisplayFragment: " + trailerUrlArrayList);
                Log.v(LOG_TAG, "GeneralHelper.parseJsonDataForMovieReview(movieId), DisplayFragment: " + reviewUrlArrayList);
                completeMovieInfo = new MovieInfoModel(movieInfoModelWithoutTrailerAndReviews, trailerUrlArrayList, reviewUrlArrayList);
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
