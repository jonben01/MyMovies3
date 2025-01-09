package dk.easv.mymovies3.DAL;


import dk.easv.mymovies3.BE.Category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ICategoryDataAccess {

    Category createCategory (Category category) throws SQLException;

    List<Category> getAllCategories () throws SQLException;

    //ArrayList<Category> getAllCategoriesJun() throws SQLException;

    void deleteCategory (Category category) throws SQLException;
}