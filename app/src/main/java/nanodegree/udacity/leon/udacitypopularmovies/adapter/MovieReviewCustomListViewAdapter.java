package nanodegree.udacity.leon.udacitypopularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.R;
import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

/**
 * Created by Leon on 8/5/2015.
 */
public class MovieReviewCustomListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MovieReviewModel> movieReviewsArrayList;

    private TextView textViewReviewAuthor;
    private TextView textViewReviewContent;

    public MovieReviewCustomListViewAdapter(Context mContext, ArrayList<MovieReviewModel> movieReviewsArrayList) {
        this.mContext = mContext;
        this.movieReviewsArrayList = movieReviewsArrayList;
    }

    @Override
    public int getCount() {
        return movieReviewsArrayList.size();
    }

    @Override
    public MovieReviewModel getItem(int position) {
        return movieReviewsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getReviewAuthor(int position) {
        return getItem(position).getReviewAuthor();
    }

    public String getReviewContent(int position) {
        return getItem(position).getReviewContent();
    }

    public String getReviewUrl(int position) {
        return getItem(position).getReviewUrl();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_movie_review, parent, false);

        textViewReviewAuthor = (TextView) rowView.findViewById(R.id.textview_movie_review_author);
        textViewReviewContent = (TextView) rowView.findViewById(R.id.textview_movie_review_content);

        textViewReviewAuthor.setText(getReviewAuthor(position));
        textViewReviewContent.setText(getReviewContent(position));

        return rowView;
    }
}
