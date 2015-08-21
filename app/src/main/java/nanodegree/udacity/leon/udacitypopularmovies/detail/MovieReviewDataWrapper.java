package nanodegree.udacity.leon.udacitypopularmovies.detail;

import java.io.Serializable;
import java.util.ArrayList;

import nanodegree.udacity.leon.udacitypopularmovies.model.MovieReviewModel;

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
