package dk.easv.mymovies3.GUI.Models;

import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.BLL.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.sql.SQLException;

public class MovieModel {

    private MovieManager movieManager;
    private ObservableList<Movie> moviesToBeViewed;

    public MovieModel() throws IOException {
        movieManager = new MovieManager();
        moviesToBeViewed = FXCollections.observableArrayList();
        //moviesToBeViewed.addAll(movieManager.getAllMovies());

    }
    public void createMovie(Movie movie) throws SQLException {
        Movie movieCreated = movieManager.createMovie(movie);
        moviesToBeViewed.add(movieCreated);
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