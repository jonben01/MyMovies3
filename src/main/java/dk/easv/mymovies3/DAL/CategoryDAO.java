package dk.easv.mymovies3.DAL;

import dk.easv.mymovies3.BE.Category;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class CategoryDAO implements ICategoryDataAccess {
    private DBConnector connector;

    public CategoryDAO() throws IOException {
        connector = new DBConnector();
    }

    @Override
    public Category createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO dbo.Category(Category_Name) " + "VALUES(?);";
        try (Connection conn = connector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.executeUpdate();
            return category;
        } catch (SQLException e) {
            throw new SQLException("Could not create new category", e);
        }
    }

    @Override
    public ArrayList<Category> getAllCategories () throws SQLException {
        ArrayList<Category> allCategories = new ArrayList<>();
        try (Connection conn = connector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT Id, Category_Name FROM Category";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Id");
                String categoryName = rs.getString("Category_Name");
                allCategories.add(new Category(id, categoryName));
            }
            return allCategories;
        } catch (SQLException e) {
            throw new SQLException("Could not get all categories from database", e);
        }
    }

    /*@Override
    public ArrayList<Category> getAllCategoriesJun() throws SQLException {
        ArrayList<Category> allCategoriesJun = new ArrayList<>();
        try (Connection conn = connector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT Movie_Id, Category_Id FROM CatMov_Junction";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Movie_Id");
                String categoryName = rs.getString("Category_Id");
                allCategoriesJun.add(new Category(id, categoryName));
            }
            return allCategoriesJun;
        } catch (SQLException e) {
            throw new SQLException("Could not get all categoriesJun from database", e);
        }
    }*/

    @Override
    public void deleteCategory(Category category) throws SQLException {
        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM dbo.Category WHERE Id = ?")) {
            ps.setInt(1, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not delete category: " + category.getCategoryName(), e);
        }
    }


    public void clearCategoriesForMovie(int movieId) throws SQLException {
        String deleteSql = "DELETE FROM dbo.CatMov_Junction WHERE Movie_Id = ?;";
        try (Connection conn = connector.getConnection();
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, movieId);
            deleteStmt.executeUpdate();
        }
    }

    public void addCategoryToMovie(int movieId, int categoryId) throws SQLException {
        String sql = "INSERT INTO dbo.CatMov_Junction (Movie_Id, Category_Id) " + "VALUES(?, ?);";
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ps.setInt(2, categoryId);
            ps.executeUpdate();
        }
    }
}
