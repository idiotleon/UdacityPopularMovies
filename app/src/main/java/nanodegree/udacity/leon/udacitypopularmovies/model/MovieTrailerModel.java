package nanodegree.udacity.leon.udacitypopularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leon on 9/10/2015.
 */
public class MovieTrailerModel implements Parcelable {

    private long movieId;
    private String movieTrailerUrl;

    public MovieTrailerModel(long movieId, String movieTrailerUrl) {
        this.movieId = movieId;
        this.movieTrailerUrl = movieTrailerUrl;
    }

    public long getMovieId() {
        return movieId;
    }

    public String getMovieTrailerUrl() {
        return movieTrailerUrl;
    }

    protected MovieTrailerModel(Parcel in) {
        movieId = in.readLong();
        movieTrailerUrl = in.readString();
    }

    public static final Creator<MovieTrailerModel> CREATOR = new Creator<MovieTrailerModel>() {
        @Override
        public MovieTrailerModel createFromParcel(Parcel in) {
            return new MovieTrailerModel(in);
        }

        @Override
        public MovieTrailerModel[] newArray(int size) {
            return new MovieTrailerModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(movieTrailerUrl);
    }
}
