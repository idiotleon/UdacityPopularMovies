package nanodegree.udacity.leon.udacitypopularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leon on 9/8/2015.
 * MovieInfoModel is a slender model, solely for displaying movies in the gridview
 */
public class SimpleMovieInfoModel implements Parcelable {

    private long movieId;
    private String movieImageUrl;

    public SimpleMovieInfoModel(Long movieId, String movieImageUrl) {
        this.movieId = movieId;
        this.movieImageUrl = movieImageUrl;
    }

    protected SimpleMovieInfoModel(Parcel in) {
        movieId = in.readLong();
        movieImageUrl = in.readString();
    }

    public static final Creator<SimpleMovieInfoModel> CREATOR = new Creator<SimpleMovieInfoModel>() {
        @Override
        public SimpleMovieInfoModel createFromParcel(Parcel in) {
            return new SimpleMovieInfoModel(in);
        }

        @Override
        public SimpleMovieInfoModel[] newArray(int size) {
            return new SimpleMovieInfoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(movieImageUrl);
    }
}
