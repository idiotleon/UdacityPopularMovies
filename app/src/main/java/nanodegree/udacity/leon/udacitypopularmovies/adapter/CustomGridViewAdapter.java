package nanodegree.udacity.leon.udacitypopularmovies.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.helper.GeneralHelper;
import nanodegree.udacity.leon.udacitypopularmovies.model.MediumMovieInfoModel;

public class CustomGridViewAdapter extends BaseAdapter {

    private static final String LOG_TAG = CustomGridViewAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<MediumMovieInfoModel> moviesInfoAsArrayList;

    public CustomGridViewAdapter(Context context, ArrayList<MediumMovieInfoModel> moviesInfoAsArrayList) {
        this.context = context;
        this.moviesInfoAsArrayList = moviesInfoAsArrayList;
    }

    @Override
    public int getCount() {
        if (moviesInfoAsArrayList.isEmpty())
            return 0;
        else
            return moviesInfoAsArrayList.size();
    }

    @Override
    public MediumMovieInfoModel getItem(int position) {
        return moviesInfoAsArrayList.get(position);
    }

    public String getPosterImageUrl(int position) {
        return getItem(position).getMovieImageUrl();
    }

    public String getOriginalTitle(int position) {
        return getItem(position).getMovieOriginalTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (GeneralHelper.isNetworkAvailable(context)) {
//        Log.v(LOG_TAG, "getView(), CustomGridViewAdapter executed.");
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
            } else {
                imageView = (ImageView) convertView;
            }
            String imageUrl = getPosterImageUrl(position);
//            Log.v(LOG_TAG, "imageUrl: " + imageUrl);
            // todo: Without network, Picasso cannot load images even with imageUrl.
            // todo: Try downloading images and save them somewhere
            Picasso.with(context).load(imageUrl).into(imageView);
//                .memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(imageView);
            return imageView;
        } else {
            TextView textView = new TextView(context);
            textView.setText(getOriginalTitle(position));
            textView.setHeight(100);
            return textView;
        }
    }
}
