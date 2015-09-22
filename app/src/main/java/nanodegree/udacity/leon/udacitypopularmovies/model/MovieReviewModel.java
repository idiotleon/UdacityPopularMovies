package nanodegree.udacity.leon.udacitypopularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReviewModel implements Parcelable {

    private long movieId;
    private String reviewAuthor;
    private String reviewContent;
    private String reviewUrl;

    public MovieReviewModel(long movieId, String reviewAuthor, String reviewContent, String reviewUrl) {
        this.movieId = movieId;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
        this.reviewUrl = reviewUrl;
    }

    protected MovieReviewModel(Parcel in) {
        movieId = in.readLong();
        reviewAuthor = in.readString();
        reviewContent = in.readString();
        reviewUrl = in.readString();
    }

    public static final Creator<MovieReviewModel> CREATOR = new Creator<MovieReviewModel>() {
        @Override
        public MovieReviewModel createFromParcel(Parcel in) {
            return new MovieReviewModel(in);
        }

        @Override
        public MovieReviewModel[] newArray(int size) {
            return new MovieReviewModel[size];
        }
    };

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public long getMovieId() {
        return movieId;
    }

    @Override
    public int describeContents() {
        return 4;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
        dest.writeString(reviewUrl);
    }
}
