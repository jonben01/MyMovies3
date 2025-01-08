package dk.easv.mymovies3.DAL;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BE.Movie;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

                    String sql = "SELECT Category_Name FROM Category";
                    ResultSet rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        allCategories.add(new Category(rs.getString("Category_Name")));
                    }
                    return allCategories;
                } catch (SQLException e) {
                    throw new SQLException("Could not get all categories from database", e);
                }
        }


        @Override
        public void deleteCategory (Category category) throws SQLException {

        }
    }
