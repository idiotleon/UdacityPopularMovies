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

    public MediumMovieInfoModel(long movieId, String movieOriginalTitle, String movieImageUrl,
                                String moviePlotSynopsis, String movieUserRating, String movieReleaseDate) {
        this.movieId = movieId;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieImageUrl = movieImageUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieUserRating = movieUserRating;
        this.movieReleaseDate = movieReleaseDate;
    }

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

    public long getMovieId() {
        return movieId;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public String getMovieImageUrl() {
        return movieImageUrl;
    }

    public String getMoviePlotSynopsis() {
        return moviePlotSynopsis;
    }

    public String getMovieUserRating() {
        return movieUserRating;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public static Creator<MediumMovieInfoModel> getCREATOR() {
        return CREATOR;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public void setMovieOriginalTitle(String movieOriginalTitle) {
        this.movieOriginalTitle = movieOriginalTitle;
    }

    public void setMovieImageUrl(String movieImageUrl) {
        this.movieImageUrl = movieImageUrl;
    }

    public void setMoviePlotSynopsis(String moviePlotSynopsis) {
        this.moviePlotSynopsis = moviePlotSynopsis;
    }

    public void setMovieUserRating(String movieUserRating) {
        this.movieUserRating = movieUserRating;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }
}
