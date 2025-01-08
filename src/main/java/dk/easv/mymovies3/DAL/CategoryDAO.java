package dk.easv.mymovies3.DAL;

import dk.easv.mymovies3.BE.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryDAO implements ICategoryDataAccess {
    @Override
    public Category createCategory(Category category) throws SQLException {
        return null;
    }

    @Override
    public List<Category> getAllCategories() throws SQLException {
        return List.of();
    }

    @Override
    public void updateCategory(Category category) throws SQLException {

    }

    @Override
    public void deleteCategory(Category category) throws SQLException {

    }
}
