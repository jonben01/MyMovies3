package dk.easv.mymovies3.DAL;

import dk.easv.mymovies3.BE.Category;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        public List<Category> getAllCategories () throws SQLException {
            return List.of();
        }


        @Override
        public void deleteCategory (Category category) throws SQLException {

        }
    }
