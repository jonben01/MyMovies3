package dk.easv.mymovies3.DAL;

//Project Imports
import dk.easv.mymovies3.BE.Category;

//Java Imports
import java.sql.SQLException;
import java.util.List;

public interface ICategoryDataAccess {

    Category createCategory (Category category) throws SQLException;

    List<Category> getAllCategories () throws SQLException;

    void deleteCategory (Category category) throws SQLException;
}