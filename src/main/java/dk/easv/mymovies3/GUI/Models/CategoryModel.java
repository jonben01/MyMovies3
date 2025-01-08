package dk.easv.mymovies3.GUI.Models;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BLL.CategoryManager;

import java.io.IOException;
import java.sql.SQLException;

public class CategoryModel {
        CategoryManager categoryManager;

        public CategoryModel() throws IOException {
            categoryManager = new CategoryManager();
        }


    public void CreateCategory(Category category) throws SQLException {
        Category createdCategory = categoryManager.CreateCategory(category);
    }
}
