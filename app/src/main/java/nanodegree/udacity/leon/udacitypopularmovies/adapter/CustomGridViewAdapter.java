package nanodegree.udacity.leon.udacitypopularmovies.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.CompleteMovieInfoModel;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;

public class CustomGridViewAdapter extends BaseAdapter {

    private static final String LOG_TAG = CustomGridViewAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<MediumMovieInfoModel> moviesInfoAsArrayList;

    public CustomGridViewAdapter(Context context, ArrayList<MediumMovieInfoModel> moviesInfoAsArrayList) {
        mContext = context;
        this.moviesInfoAsArrayList = moviesInfoAsArrayList;
    }

    @Override
    public int getCount() {
        return moviesInfoAsArrayList.size();
    }

    @Override
    public MediumMovieInfoModel getItem(int position) {
        return moviesInfoAsArrayList.get(position);
    }

    public String getPosterImageUrl(int position) {
        return getItem(position).getMovieImageUrl();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.v(LOG_TAG, "getView(), CustomGridViewAdapter executed.");
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        String imageUrl = getPosterImageUrl(position);
//        Log.v(LOG_TAG, "imageUrl, CustomGridViewAdapter: " + imageUrl);
        Picasso.with(mContext).load(imageUrl).into(imageView);
//                .memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(imageView);
        return imageView;
    }
}
