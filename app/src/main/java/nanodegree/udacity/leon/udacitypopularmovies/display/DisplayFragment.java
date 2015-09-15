package nanodegree.udacity.leon.udacitypopularmovies.display;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            movieModelArrayList = savedInstanceState.getParcelableArrayList(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_DISPLAY_FRAGMENT);
        }
    }

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
        movieModelArrayList = getArguments().getParcelableArrayList(GeneralConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER);
        gridView.setAdapter(new CustomGridViewAdapter(getActivity(), movieModelArrayList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediumMovieInfoModel clickedMovieInfo = (MediumMovieInfoModel) gridView.getItemAtPosition(position);
                Bundle detailsArgs = new Bundle();
                detailsArgs.putParcelable(GeneralConstants.MOVIE_INFO_DETAIL_FRAGMENT_IDENTIFIER, clickedMovieInfo);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(detailsArgs);
                // This is committed in DisplayFragment, not in MainActivity
                getFragmentManager().beginTransaction()
                        .replace(R.id.tabletux_container2, detailFragment,
                                GeneralConstants.DETAILFRAGMENT_FRAGMENTTRANSACTION_TAG).commit();
                Log.v(LOG_TAG, "detailFragment, transaction committed from DisplayFragment");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(GeneralConstants.MOVIE_SAVED_INSTANCE_STATE_DISPLAY_FRAGMENT, movieModelArrayList);
    }
}
