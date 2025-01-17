package dk.easv.mymovies3.DAL;

//Project Imports
import dk.easv.mymovies3.BE.Movie;

//Java Imports
import java.sql.SQLException;
import java.util.List;

public interface IMovieDataAccess {

    Movie createMovie (Movie movie) throws SQLException;

    List<Movie> getAllMovies () throws SQLException;

    void updateMovie (Movie movie) throws SQLException;

    void deleteMovie (Movie movie) throws SQLException;

}