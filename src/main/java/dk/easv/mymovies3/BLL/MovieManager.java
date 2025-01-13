package dk.easv.mymovies3.BLL;

import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.DAL.MovieDAO;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class MovieManager {

    private MovieDAO movieDAO;
    private MovieFilter movieFilter;

    public MovieManager() throws IOException {
        movieDAO = new MovieDAO();
        movieFilter = new MovieFilter();
    }

    public List<Movie> getAllMovies() throws Exception {
            return movieDAO.getAllMovies();
    }

    public Movie createMovie(Movie movie) throws SQLException {
        movieDAO.createMovie(movie);
        return movie;
    }

    public Movie DeleteMovie(Movie movie) throws SQLException {
        movieDAO.deleteMovie(movie);
        return movie;
    }

    public Movie updateMovie(Movie movie) throws Exception {
        movieDAO.updateMovie(movie);
        return movie;
    }

    public ObservableList<Movie> applyFilters(Set<String> selectedCategories,
                                              Set<String> selectedImdbRatings,
                                              Set<String> selectedPersonalRatings) throws Exception {
        return movieFilter.applyFilters(selectedCategories, selectedImdbRatings, selectedPersonalRatings);
    }

}