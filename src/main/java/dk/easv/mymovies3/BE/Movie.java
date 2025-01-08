package dk.easv.mymovies3.BE;

public class Movie {


    private String movieTitle;
    private Double imdbRating;
    private Integer personalRating;
    private String filePath;
    private int movieYear;

    public String getMovieTitle() {return movieTitle;}
    public void setMovieTitle(String movieTitle) {this.movieTitle = movieTitle;}

    public Double getImdbRating() {return imdbRating;}
    public void setImdbRating(Double imdbRating) {this.imdbRating = imdbRating;}

    public Integer getPersonalRating() {return personalRating;}
    public void setPersonalRating(Integer personalRating) {this.personalRating = personalRating;}

    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}

    public int getMovieYear() {return movieYear;}
    public void setMovieYear(int movieYear) {this.movieYear = movieYear;}

    public Movie(String movieTitle, Double imdbRating, Integer personalRating, String filePath, int movieYear) {
        this.movieTitle = movieTitle;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.filePath = filePath;
        this.movieYear = movieYear;
    }
}