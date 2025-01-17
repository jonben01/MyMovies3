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

    public List<Movie> getAllMovies() throws SQLException {
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

    public Movie updateMovie(Movie movie) throws SQLException {
        movieDAO.updateMovie(movie);
        return movie;
    }

    /**
     * passes the information from the MovieModel class to the MovieFilter class.
     *
     * @param searchQuery query from search field
     * @param selectedCategories categories selected in checkTreeView "filterBox"
     * @param selectedImdbRatings IMDB rating ranges selected in checkTreeView "filterBox"
     * @param selectedPersonalRatings Personal rating ranges selected in checkTreeView "filterBox"
     * @return returns a filtered observable list to show on the table
     * @throws SQLException in case of db issue
     */
    public ObservableList<Movie> applyFiltersAndSearch(String searchQuery,
                                              Set<String> selectedCategories,
                                              Set<String> selectedImdbRatings,
                                              Set<String> selectedPersonalRatings) throws SQLException {
        return movieFilter.applyFiltersAndSearch(searchQuery,selectedCategories, selectedImdbRatings, selectedPersonalRatings);
    }

    public void setLastOpened(Movie movie) throws SQLException {
        movieDAO.setLastOpened(movie);
    }
}