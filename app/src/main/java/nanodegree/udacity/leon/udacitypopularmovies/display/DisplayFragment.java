package nanodegree.udacity.leon.udacitypopularmovies.display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.moviedetail.DetailFragment;
import nanodegree.udacity.leon.udacitypopularmovies.helper.CommonConstants;
import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.adapter.CustomGridViewAdapter;

public class DisplayFragment extends Fragment {

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
        ArrayList<MovieInfoModel> movieModelArrayList = getArguments().getParcelableArrayList(CommonConstants.MOVIE_INFO_DISPLAYFRAGMENT_IDENTIFIER);
        gridView.setAdapter(new CustomGridViewAdapter(getActivity(), movieModelArrayList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfoModel clickedMovieInfo = (MovieInfoModel) gridView.getItemAtPosition(position);
                Bundle detailsArgs = new Bundle();
                detailsArgs.putParcelable(CommonConstants.MOVIE_INFO_DETAILFRAGMENT_IDENTIFIER, clickedMovieInfo);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(detailsArgs);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.tabletux_container2, detailFragment);
                fragmentTransaction.commit();
            }
        });
    }
}
