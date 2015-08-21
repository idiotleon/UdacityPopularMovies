package nanodegree.udacity.leon.udacitypopularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.R;

public class MovieTrailerCustomListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> moviePosterImageUrlArrayList;
    private TextView textViewRowList;

    public MovieTrailerCustomListViewAdapter(Context mContext, ArrayList<String> moviePosterImageUrlArrayList) {
        this.mContext = mContext;
        this.moviePosterImageUrlArrayList = moviePosterImageUrlArrayList;
    }

    @Override
    public int getCount() {
        return moviePosterImageUrlArrayList.size();
    }

    @Override
    public String getItem(int position) {
        return moviePosterImageUrlArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_movie_trailer, parent, false);

        textViewRowList = (TextView) rowView.findViewById(R.id.textview_list_row);

        textViewRowList.setText("Movie Trailer " + (position + 1));

        return rowView;
    }
}
