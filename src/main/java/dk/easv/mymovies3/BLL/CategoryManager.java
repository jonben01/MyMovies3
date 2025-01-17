package dk.easv.mymovies3.BLL;

//Project imports
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.DAL.CategoryDAO;

//Java imports
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryManager {

    private CategoryDAO categoryDAO;

    public CategoryManager() throws IOException {
        categoryDAO = new CategoryDAO();
    }
    public Category CreateCategory(Category category) throws SQLException {
        categoryDAO.createCategory(category);
        return category;
    }

    public ArrayList<Category> getAllCategories() throws SQLException {
        ArrayList<Category> categories = categoryDAO.getAllCategories();
        return categories;
    }


    public void addCategoryToMovie(int movieId, int categoryId) throws SQLException {
        categoryDAO.addCategoryToMovie(movieId, categoryId);
    }

    public Category DeleteCategory(Category category) throws SQLException {
        categoryDAO.deleteCategory(category);
        return category;
    }

    public void clearCategoriesForMovie(int movieId) throws SQLException {
        categoryDAO.clearCategoriesForMovie(movieId);
    }
}
