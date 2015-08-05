package nanodegree.udacity.leon.udacitypopularmovies;


public class MovieModel {

    private String movieId;
    private String movieTitle;
    private String movieImageUrl;
    private String moviePlotSynopsis;
    private String movieUserRating;
    private String releaseDate;

    public MovieModel(String movieId, String movieTitle, String moviePlotSynopsis, String movieUserRating, String releaseDate) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieUserRating = movieUserRating;
        this.releaseDate = releaseDate;
    }

    public MovieModel(String movieId, String movieTitle, String movieImageUrl, String moviePlotSynopsis, String movieUserRating, String releaseDate) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieImageUrl = movieImageUrl;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieUserRating = movieUserRating;
        this.releaseDate = releaseDate;
    }


    public String getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
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

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
