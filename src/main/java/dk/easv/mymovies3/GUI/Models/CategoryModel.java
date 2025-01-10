package dk.easv.mymovies3.GUI.Models;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BLL.CategoryManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryModel {
        CategoryManager categoryManager;

   // private ObservableList<Category> categoriesToBeViewed;

        public CategoryModel() throws IOException, SQLException {
            categoryManager = new CategoryManager();
            /*categoriesToBeViewed = FXCollections.observableArrayList();
            categoriesToBeViewed.addAll(categoryManager.getAllCategoriesJun());*/
        }

    /*public ObservableList<Category> getObservableCategories() {
        return categoriesToBeViewed;
    }*/


    public void CreateCategory(Category category) throws SQLException {
        Category createdCategory = categoryManager.CreateCategory(category);
    }

    public ArrayList<Category> GetAllCategories() throws SQLException {
           ArrayList<Category> categories = categoryManager.getAllCategories();
           return categories;
    }

    public void DeleteCategory(Category category) throws SQLException {
            Category deletedCategory = categoryManager.DeleteCategory(category);
    }

    public void addCategoryToMovie(int movieId, int categoryId) throws SQLException {
        categoryManager.addCategoryToMovie(movieId, categoryId);
    }

    public void clearCategoriesForMovie(int movieId) throws SQLException {
        categoryManager.clearCategoriesForMovie(movieId);
    }
}
