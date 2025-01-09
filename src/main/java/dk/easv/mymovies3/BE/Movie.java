package dk.easv.mymovies3.BE;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    private int id;
    private String movieTitle;
    private Double imdbRating;
    private Integer personalRating;
    private String filePath;
    private int movieYear;

    private List<Category> categories;




    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

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

    public List<Category> getCategories() {return categories;}
    public void setCategories(List<Category> categories) {this.categories = categories;}

    public Movie(String movieTitle, Double imdbRating, Integer personalRating, String filePath, int movieYear) {
        this.movieTitle = movieTitle;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.filePath = filePath;
        this.movieYear = movieYear;
        
    }

    public Movie(String movieTitle, Double imdbRating, Integer personalRating, String filePath, int movieYear, List<Category> categories) {
        this.movieTitle = movieTitle;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.filePath = filePath;
        this.movieYear = movieYear;
        this.categories = categories;
    }

    public Movie(int id, String movieTitle, Double imdbRating, Integer personalRating, String filePath, int movieYear, List<Category> categories) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.filePath = filePath;
        this.movieYear = movieYear;
        this.categories = categories;
    }



}