package dk.easv.mymovies3.BLL;



import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.DAL.IMovieDataAccess;
import dk.easv.mymovies3.DAL.MovieDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieManager {

    private MovieDAO movieDAO;

    public MovieManager() throws IOException {
        movieDAO = new MovieDAO();
    }

    //TODO IMPLEMENT THIS
    public List<Movie> getAllMovies() throws Exception {
            return movieDAO.getAllMovies();
    }

            //TODO IMPLEMENT THIS
    public Movie createMovie(Movie movie) throws SQLException {
        movieDAO.createMovie(movie);
        return movie;
    }
}