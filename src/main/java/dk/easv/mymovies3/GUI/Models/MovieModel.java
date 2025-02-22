package dk.easv.mymovies3.GUI.Models;

//Project Imports
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.BLL.MovieManager;

//Java Imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class MovieModel {

    private ObservableList<Movie> moviesToBeViewed;
    private MovieManager movieManager;


    public MovieModel() throws IOException, SQLException {
        movieManager = new MovieManager();
        moviesToBeViewed = FXCollections.observableArrayList();
        moviesToBeViewed.addAll(movieManager.getAllMovies());

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

    public void deleteMovie(Movie movieToBeDeleted) throws SQLException {
        movieManager.DeleteMovie(movieToBeDeleted);
        UpdateList();
    }

    public void UpdateList() throws SQLException {
        moviesToBeViewed.clear();
        moviesToBeViewed.addAll(movieManager.getAllMovies());
    }

    public void updateMovie(Movie movie) throws SQLException {
        movieManager.updateMovie(movie); // Update movie in the database

        for (int i = 0; i < moviesToBeViewed.size(); i++) {
            if (moviesToBeViewed.get(i).getId() == movie.getId()) {
                // Replace the old movie with the updated one
                moviesToBeViewed.set(i, movie);
                break;
            }
        }
    }

    public ObservableList<Movie> applyFiltersAndSearch(String searchQuery,
                                              Set<String> selectedCategories,
                                              Set<String> selectedImdbRatings,
                                              Set<String> selectedPersonalRatings) throws SQLException {
        return movieManager.applyFiltersAndSearch(searchQuery,selectedCategories, selectedImdbRatings, selectedPersonalRatings);
    }

    public void setLastOpened(Movie movie) throws SQLException {
        movieManager.setLastOpened(movie);
    }
}