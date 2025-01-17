package dk.easv.mymovies3.DAL;


import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BE.Movie;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDAO implements IMovieDataAccess {

    private DBConnector connector;

    public MovieDAO() throws IOException {
        connector = new DBConnector();
    }

    /**
     * Inserts an item on a new tuple, representing the created movie object, in the database movie table
     *
     * @param movie made from user input in the gui layer
     * @return the created movie, so it can be added to the observable list and shown on the gui
     * @throws SQLException in case of db error
     */
    @Override
    public Movie createMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO dbo.Movie(Movie_Title, IMDB_Rating, Personal_Rating, File_Path, Movie_Year) " +
                "VALUES (?,?,?,?,?);";
        try (Connection conn = connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, movie.getMovieTitle());
            ps.setDouble(2, movie.getImdbRating());
            if (movie.getPersonalRating() != null) {
                ps.setDouble(3, movie.getPersonalRating());
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

    //Retrieves a list of all movies from the database
    public List<Movie> getAllMovies() throws SQLException {

        //HashMap to avoid duplicating Movie objects when a movie has multiple categories
        Map<Integer, Movie> movieMap = new HashMap<>();

        try (Connection conn = connector.getConnection();
             Statement stmt = conn.createStatement()) {

            //Execute an SQL query to join the Movie, CatMov_Junction, and Category tables
            String sql = "SELECT m.Id, m.Movie_Title, m.IMDB_Rating, m.Personal_Rating, m.File_Path, " +
                    "m.Movie_Year, c.Id as Category_Id, c.Category_Name, m.Last_Opened_Date " +
                    "FROM Movie m " +
                    "LEFT JOIN CatMov_Junction cmj ON m.Id = cmj.Movie_Id " +
                    "LEFT JOIN Category c ON cmj.Category_Id = c.Id";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Id");
                String title = rs.getString("Movie_Title");

                BigDecimal imdb = rs.getBigDecimal("IMDB_Rating");
                double imdbRating = imdb.doubleValue();

                BigDecimal personal = rs.getBigDecimal("Personal_Rating");
                double personalRating = personal.doubleValue();

                String filePath = rs.getString("File_Path");
                int year = rs.getInt("Movie_Year");
                Date lastOpened = rs.getDate("Last_Opened_Date");

                Movie movie = movieMap.get(id); //Each unique movie is keyed by its ID
                if (movie == null) {
                    movie = new Movie(id, title, imdbRating, personalRating, filePath, year, new ArrayList<>());
                    if (lastOpened != null) {
                        movie.setLastOpenedDate(lastOpened);
                    }
                    movieMap.put(id, movie);
                }

                int categoryId = rs.getInt("Category_Id");
                String categoryName = rs.getString("Category_Name");
                if (categoryId > 0 && categoryName != null) {
                    movie.getCategories().add(new Category(categoryId, categoryName));
                }
            }
            //Convert the HashMap values into a List and return it
            return new ArrayList<>(movieMap.values());

        } catch (SQLException ex) {
            throw new SQLException("Could not get movies from database", ex);
        }
    }

    public void setLastOpened(Movie movie) throws SQLException {
        String sql = "UPDATE Movie SET Last_Opened_Date = ? WHERE Id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            Date lastOpened = Date.valueOf(LocalDate.now());
            ps.setDate(1, lastOpened);
            ps.setInt(2, movie.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateMovie(Movie movie) throws SQLException {
        String sql = "UPDATE dbo.Movie SET Movie_Title = ?, IMDB_Rating = ?, Personal_Rating = ?, File_Path = ?, Movie_Year = ? WHERE Id = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getMovieTitle());
            stmt.setDouble(2, movie.getImdbRating());
            stmt.setDouble(3, movie.getPersonalRating());
            stmt.setString(4, movie.getFilePath());
            stmt.setInt(5, movie.getMovieYear());
            stmt.setInt(6, movie.getId());
            stmt.executeUpdate();

            // Update categories in the junction table
            updateMovieCategories(conn, movie);

            // Refresh the categories in the Movie object
            movie.setCategories(getCategoriesForMovie(movie.getId()));
        }
    }

    private List<Category> getCategoriesForMovie(int movieId) throws SQLException {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT c.Id, c.Category_Name FROM Category c " +
                "JOIN CatMov_Junction cmj ON c.Id = cmj.Category_Id WHERE cmj.Movie_Id = ?";
        /*Execute the query and process the results:
         *    - For each row in the ResultSet, create a new `Category` object using the retrieved data.
         *    - Add the `Category` object to the list.*/
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("Id"), rs.getString("Category_Name")));
            }
        }
        return categories;
    }

    public void updateMovieCategories(Connection conn, Movie movie) throws SQLException {
            String deleteSql = "DELETE FROM CatMov_Junction WHERE Movie_Id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, movie.getId());
                deleteStmt.executeUpdate();
            }

            String insertSql = "INSERT INTO CatMov_Junction (Movie_Id, Category_Id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Category category : movie.getCategories()) {
                    insertStmt.setInt(1, movie.getId());
                    insertStmt.setInt(2, category.getId());
                    insertStmt.executeUpdate();
                }
            }
        }

    @Override
    public void deleteMovie(Movie movie) throws SQLException {
        try (Connection conn = connector.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM dbo.Movie WHERE Id = ?")) {
            ps.setInt(1, movie.getId());
            ps.executeUpdate();
        } catch(SQLException ex){
            ex.printStackTrace();
            throw new SQLException("Could not remove movie from database.", ex);
        }
    }
}