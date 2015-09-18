package nanodegree.udacity.leon.udacitypopularmovies.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;
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
            detailsArgs.putParcelable(GeneralConstants.MOVIE_INFO_DETAIL_FRAGMENT_IDENTIFIER,
                    movieModelArrayList.get(movieSelectedPosition));
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(detailsArgs);
            getFragmentManager().beginTransaction().
                    replace(R.id.tabletux_container2, detailFragment,
                            GeneralConstants.DETAILFRAGMENT_FRAGMENTTRANSACTION_TAG).commit();
        }
        return displayFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        movieModelArrayList = getArguments().getParcelableArrayList(GeneralConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER);
        gridView.setAdapter(new CustomGridViewAdapter(getActivity(), movieModelArrayList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                movieSelectedPosition = position;
                MediumMovieInfoModel clickedMovieInfo = (MediumMovieInfoModel) gridView.getItemAtPosition(position);
                Bundle detailsArgs = new Bundle();
                detailsArgs.putParcelable(GeneralConstants.MOVIE_INFO_DETAIL_FRAGMENT_IDENTIFIER, clickedMovieInfo);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(detailsArgs);
                // This is committed in DisplayFragment, not in MainActivity
                getFragmentManager().beginTransaction()
                        .replace(R.id.tabletux_container2, detailFragment,
                                GeneralConstants.DETAILFRAGMENT_FRAGMENTTRANSACTION_TAG).commit();
//                Log.v(LOG_TAG, "detailFragment, transaction committed from DisplayFragment");
            }
        });
    }
}
