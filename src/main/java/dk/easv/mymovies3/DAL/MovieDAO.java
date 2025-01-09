package dk.easv.mymovies3.DAL;


import dk.easv.mymovies3.BE.Movie;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO implements IMovieDataAccess {

    private DBConnector connector;

    public MovieDAO() throws IOException {
        connector = new DBConnector();
    }

    @Override
    public Movie createMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO dbo.Movie(Movie_Title, IMDB_Rating, Personal_Rating, File_Path, Movie_Year) " +
                "VALUES (?,?,?,?,?);";
        try (Connection conn = connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, movie.getMovieTitle());
            ps.setDouble(2, movie.getImdbRating());
            if (movie.getPersonalRating() != null) {
                ps.setInt(3, movie.getPersonalRating());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }
            ps.setString(4, movie.getFilePath());
            ps.setInt(5, movie.getMovieYear());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                movie.setId(rs.getInt(1));
            }

            return movie;


        } catch (SQLException e) {
            throw new SQLException("Could not create new Movie", e);
        }
    }

    public List<Movie> getAllMovies() throws Exception {
        ArrayList<Movie> allMovies = new ArrayList<>();

        try (Connection conn = connector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT m.Id, m.Movie_Title, m.IMDB_Rating, m.Personal_Rating, m.File_Path, m.Movie_Year " +
                    "FROM Movie m " +
                    "JOIN CatMov_Junction cmj ON m.Id = cmj.Movie_Id " +
                    "JOIN Category c ON cmj.Category_Id = c.Id";

            ResultSet rs = stmt.executeQuery(sql);

            // Loop through rows from the database result set
            while (rs.next()) {
                int id = rs.getInt("Id");
                String title = rs.getString("Movie_Title");
                BigDecimal imdb = rs.getBigDecimal("IMDB_Rating");
                double imdbRating = imdb.doubleValue();
                int personal = rs.getInt("Personal_Rating");
                String filePath = rs.getString("File_Path");
                int year = rs.getInt("Movie_Year");


                Movie movie = new Movie(id, title, imdbRating, personal, filePath, year);
                allMovies.add(movie);
            }

            // Debugging: print the number of movies loaded
            System.out.println("Movies from database: " + allMovies.size());
            return allMovies;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not get movies from database", ex);
        }
    }


    @Override
    public void updateMovie(Movie movie) throws SQLException {

    }

    @Override
    public void deleteMovie(Movie movie) throws SQLException {

    }
}