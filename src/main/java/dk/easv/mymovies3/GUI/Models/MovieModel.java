package dk.easv.mymovies3.GUI.Models;

import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.BLL.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.sql.SQLException;

public class MovieModel {

    private ObservableList<Movie> moviesToBeViewed;
    private MovieManager movieManager;


    public MovieModel() throws Exception {
        movieManager = new MovieManager();
        moviesToBeViewed = FXCollections.observableArrayList();
        moviesToBeViewed.addAll(movieManager.getAllMovies());

        /*// Adding sample data for debugging
        moviesToBeViewed.add(new Movie("Sample Movie", 7.5, 8, "/path/to/file", 2023));*/

        System.out.println("Movies loaded: " + moviesToBeViewed.size());
        for (Movie movie : moviesToBeViewed) {
            System.out.println(movie.getMovieTitle());
        }
    }


    public ObservableList<Movie> getObservableMovies() {
        return moviesToBeViewed;
    }

    public void createMovie(Movie movie) throws SQLException {
        Movie movieCreated = movieManager.createMovie(movie);
        moviesToBeViewed.add(movieCreated);
    }

    public Movie getMovieByFilePath(String filePath) {
        for (Movie movie : moviesToBeViewed) {
            if (movie.getFilePath().equals(filePath)) {
                return movie;
            }
        }
        return null;
    }

    public void deleteMovie(Movie movieToBeDeleted) throws Exception {
        movieManager.DeleteMovie(movieToBeDeleted);
        UpdateList();
    }

    public void UpdateList() throws Exception {
        moviesToBeViewed.clear();
        moviesToBeViewed.addAll(movieManager.getAllMovies());
    }

    public void updateMovie(Movie movie) throws Exception {
        movieManager.updateMovie(movie);

        for (Movie m : moviesToBeViewed) {
            if (m.getId() == movie.getId()) {
                m.setMovieTitle(movie.getMovieTitle());
                m.setImdbRating(movie.getImdbRating());
                m.setPersonalRating(movie.getPersonalRating());
                m.setMovieYear(movie.getMovieYear());
                m.setCategories(movie.getCategories());
                UpdateList();
                break;

            }
        }
    }


//TODO implement this

/* needed for when fileAlreadyExists needs to be handled in ADD MOVIE method - maincontroller.
    public Movie getMovieByFilePath(String filePath) {

        for (Movie movie : movieManager.getAllMovies()) {

            if (movie.getFilePath().equals(filePath)) {

                return movie;
            }
        }
        return null;
    }

 */
}