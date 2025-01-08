package dk.easv.mymovies3.BLL;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.DAL.CategoryDAO;

import java.sql.SQLException;

public class CategoryManager {

    private CategoryDAO categoryDAO;

    public CategoryManager() {
        categoryDAO = new CategoryDAO();
    }
    public Category CreateCategory(Category category) throws SQLException {
        categoryDAO.createCategory(category);
        return category;
    }
}
