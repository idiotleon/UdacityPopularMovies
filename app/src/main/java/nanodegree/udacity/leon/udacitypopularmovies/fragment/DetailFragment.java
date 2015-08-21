package nanodegree.udacity.leon.udacitypopularmovies.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nanodegree.udacity.leon.udacitypopularmovies.R;

public class DetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailFragmentView = inflater.inflate(R.layout.fragment_detail_tabletux, null);
        return detailFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
