package nanodegree.udacity.leon.udacitypopularmovies;


public class MovieModel {

    private String movieId;
    private String movieOriginalTitle;
    private String movieImageUrl;
    private String moviePlotSynopsis;
    private String movieUserRating;
    private String movieReleaseDate;

    public MovieModel(String movieId, String movieOriginalTitle, String movieImageUrl, String moviePlotSynopsis, String movieUserRating, String movieReleaseDate) {
        this.movieId = movieId;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieImageUrl = movieImageUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieUserRating = movieUserRating;
        this.movieReleaseDate = movieReleaseDate;
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
