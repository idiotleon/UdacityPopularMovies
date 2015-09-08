package nanodegree.udacity.leon.udacitypopularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leon on 9/8/2015.
 */
public class MediumMovieInfoModel implements Parcelable {

    private long movieId;
    private String movieOriginalTitle;
    private String movieImageUrl;
    private String moviePlotSynopsis;
    private String movieUserRating;
    private String movieReleaseDate;

    protected MediumMovieInfoModel(Parcel in) {
        movieId = in.readLong();
        movieOriginalTitle = in.readString();
        movieImageUrl = in.readString();
        moviePlotSynopsis = in.readString();
        movieUserRating = in.readString();
        movieReleaseDate = in.readString();
    }

    public static final Creator<MediumMovieInfoModel> CREATOR = new Creator<MediumMovieInfoModel>() {
        @Override
        public MediumMovieInfoModel createFromParcel(Parcel in) {
            return new MediumMovieInfoModel(in);
        }

        @Override
        public MediumMovieInfoModel[] newArray(int size) {
            return new MediumMovieInfoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(movieOriginalTitle);
        dest.writeString(movieImageUrl);
        dest.writeString(moviePlotSynopsis);
        dest.writeString(movieUserRating);
        dest.writeString(movieReleaseDate);
    }
}
