package dk.easv.mymovies3.BLL;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.DAL.CategoryDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryManager {

    private CategoryDAO categoryDAO;

    public CategoryManager() throws IOException {
        categoryDAO = new CategoryDAO();
    }
    public Category CreateCategory(Category category) throws SQLException {
        categoryDAO.createCategory(category);
        return category;
    }

    public ArrayList<Category> GetAllCategories() throws SQLException {
        ArrayList<Category> categories = categoryDAO.getAllCategories();
        return categories;
    }
}
