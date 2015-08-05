package nanodegree.udacity.leon.udacitypopularmovies;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is just a data wrapper class for an ArrayList of objects of type MovieReviewModel
 */

public class MovieReviewDataWrapper implements Serializable{

    private ArrayList<MovieReviewModel> movieReviewModels;

    public MovieReviewDataWrapper(ArrayList<MovieReviewModel> movieReviewModels){
        this.movieReviewModels = movieReviewModels;
    }

    public ArrayList<MovieReviewModel> getMovieReviewData(){
        return this.movieReviewModels;
    }
}
