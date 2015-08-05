package nanodegree.udacity.leon.udacitypopularmovies;

import java.io.Serializable;

/**
 * Created by Leon on 8/5/2015.
 */
public class MovieReviewModel implements Serializable{

    private String reviewAuthor;
    private String reviewContent;
    private String reviewUrl;

    public MovieReviewModel(String reviewAuthor, String reviewContent, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
        this.reviewUrl = reviewUrl;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }
}
