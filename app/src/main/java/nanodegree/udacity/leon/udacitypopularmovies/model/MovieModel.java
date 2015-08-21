package nanodegree.udacity.leon.udacitypopularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MovieModel implements Parcelable {

    // Movie features
    private String movieId;
    private String movieOriginalTitle;
    private String movieImageUrl;
    private String moviePlotSynopsis;
    private String movieUserRating;
    private String movieReleaseDate;
    private ArrayList<String> movieTrailerUrlArrayList = null;
    private ArrayList<MovieReviewModel> movieReviewArrayList = null;

    public MovieModel(String movieId, String movieOriginalTitle, String movieImageUrl, String moviePlotSynopsis, String movieUserRating, String movieReleaseDate, ArrayList<String> movieTrailerUrlArrayList, ArrayList<MovieReviewModel> movieReviewArrayList) {
        this.movieId = movieId;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieImageUrl = movieImageUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieUserRating = movieUserRating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieTrailerUrlArrayList = movieTrailerUrlArrayList;
        this.movieReviewArrayList = movieReviewArrayList;
    }

    protected MovieModel(Parcel in) {
        movieId = in.readString();
        movieOriginalTitle = in.readString();
        movieImageUrl = in.readString();
        moviePlotSynopsis = in.readString();
        movieUserRating = in.readString();
        movieReleaseDate = in.readString();
        movieTrailerUrlArrayList = in.createStringArrayList();
        movieReviewArrayList = in.readParcelable(MovieReviewModel.class.getClassLoader());
    }

    public void setMovieTrailerUrlArrayList(ArrayList<String> movieTrailerUrlArrayList) {
        this.movieTrailerUrlArrayList = movieTrailerUrlArrayList;
    }

    public ArrayList<MovieReviewModel> getMovieReviewArrayList() {
        return movieReviewArrayList;
    }

    public ArrayList<String> getMovieTrailerUrlArrayList() {
        return movieTrailerUrlArrayList;
    }

    public String getMovieId() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(movieOriginalTitle);
        dest.writeString(movieImageUrl);
        dest.writeString(moviePlotSynopsis);
        dest.writeString(movieUserRating);
        dest.writeString(movieReleaseDate);
        dest.writeStringList(movieTrailerUrlArrayList);
        dest.writeTypedList(movieReviewArrayList);
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };
}
