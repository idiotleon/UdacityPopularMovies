package nanodegree.udacity.leon.udacitypopularmovies;


import java.util.ArrayList;

public class MovieModel {

    private String movieId;
    private String movieOriginalTitle;
    private String movieImageUrl;
    private String moviePlotSynopsis;
    private String movieUserRating;
    private String movieReleaseDate;

    private ArrayList<String> movieTrailerUrlArrayList = null;

    public MovieModel(String movieId, String movieOriginalTitle, String movieImageUrl, String moviePlotSynopsis, String movieUserRating, String movieReleaseDate, ArrayList<String> movieTrailerUrlArrayList) {
        this.movieId = movieId;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieImageUrl = movieImageUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieUserRating = movieUserRating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieTrailerUrlArrayList = movieTrailerUrlArrayList;
    }

    public void setMovieTrailerUrlArrayList(ArrayList<String> movieTrailerUrlArrayList) {
        this.movieTrailerUrlArrayList = movieTrailerUrlArrayList;
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
}
