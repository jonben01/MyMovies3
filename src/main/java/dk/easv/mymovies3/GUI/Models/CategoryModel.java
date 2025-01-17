package dk.easv.mymovies3.GUI.Models;

//Project Imports
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BLL.CategoryManager;

//Java Imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryModel {
        CategoryManager categoryManager;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();

        public CategoryModel() throws IOException, SQLException {
            categoryManager = new CategoryManager();
        }

    public ObservableList<Category> getObservableCategories() {
        return categories;
    }



    public List<String> getAvailableCategories () throws SQLException {
        return categoryManager.getAllCategories().stream().map(Category::getCategoryName).toList();
    }

    public void createCategory(Category category) throws SQLException {
        Category createdCategory = categoryManager.CreateCategory(category);
        categories.add(createdCategory);
    }

    public ArrayList<Category> getAllCategories() throws SQLException {
           ArrayList<Category> categories = categoryManager.getAllCategories();
           return categories;
    }

    public void DeleteCategory(Category category) throws SQLException {
            Category deletedCategory = categoryManager.DeleteCategory(category);
            categories.remove(deletedCategory);
    }

    public void addCategoryToMovie(int movieId, int categoryId) throws SQLException {
        categoryManager.addCategoryToMovie(movieId, categoryId);
    }

    public void clearCategoriesForMovie(int movieId) throws SQLException {
        categoryManager.clearCategoriesForMovie(movieId);
    }
}
