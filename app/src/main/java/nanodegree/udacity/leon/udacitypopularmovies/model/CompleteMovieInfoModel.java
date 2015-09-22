package nanodegree.udacity.leon.udacitypopularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CompleteMovieInfoModel implements Parcelable {

    // Movie info
    private long movieId;
    private String movieOriginalTitle;
    private String movieImageUrl;
    private String moviePlotSynopsis;
    private float movieUserRating;
    private String movieReleaseDate;
    private double moviePopularity;
    private ArrayList<String> movieTrailerUrlArrayList = new ArrayList<>();
    private ArrayList<MovieReviewModel> movieReviewArrayList = new ArrayList<>();

    public CompleteMovieInfoModel(MediumMovieInfoModel mediumMovieInfoModel,
                                  ArrayList<String> movieTrailerUrlArrayList,
                                  ArrayList<MovieReviewModel> movieReviewModelArrayList) {
        this.movieId = mediumMovieInfoModel.getMovieId();
        this.movieOriginalTitle = mediumMovieInfoModel.getMovieOriginalTitle();
        this.movieImageUrl = mediumMovieInfoModel.getMovieImageUrl();
        this.moviePlotSynopsis = mediumMovieInfoModel.getMoviePlotSynopsis();
        this.movieUserRating = mediumMovieInfoModel.getMovieUserRating();
        this.moviePopularity = mediumMovieInfoModel.getMoviePopularity();
        this.movieReleaseDate = mediumMovieInfoModel.getMovieReleaseDate();
        this.movieTrailerUrlArrayList = movieTrailerUrlArrayList;
        this.movieReviewArrayList = movieReviewModelArrayList;
    }

    public ArrayList<MovieReviewModel> getMovieReviewArrayList() {
        return movieReviewArrayList;
    }

    public ArrayList<String> getMovieTrailerUrlArrayList() {
        return movieTrailerUrlArrayList;
    }

    public Long getMovieId() {
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

    public float getMovieUserRating() {
        return movieUserRating;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected CompleteMovieInfoModel(Parcel in) {
        movieId = in.readLong();
        movieOriginalTitle = in.readString();
        movieImageUrl = in.readString();
        moviePlotSynopsis = in.readString();
        movieUserRating = in.readFloat();
        movieReleaseDate = in.readString();
        moviePopularity = in.readDouble();
        movieTrailerUrlArrayList = in.createStringArrayList();
        in.readTypedList(movieReviewArrayList, MovieReviewModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(movieOriginalTitle);
        dest.writeString(movieImageUrl);
        dest.writeString(moviePlotSynopsis);
        dest.writeFloat(movieUserRating);
        dest.writeString(movieReleaseDate);
        dest.writeDouble(moviePopularity);
        dest.writeStringList(movieTrailerUrlArrayList);
        dest.writeTypedList(movieReviewArrayList);
    }

    public static final Creator<CompleteMovieInfoModel> CREATOR = new Creator<CompleteMovieInfoModel>() {
        @Override
        public CompleteMovieInfoModel createFromParcel(Parcel in) {
            return new CompleteMovieInfoModel(in);
        }

        @Override
        public CompleteMovieInfoModel[] newArray(int size) {
            return new CompleteMovieInfoModel[size];
        }
    };
}
