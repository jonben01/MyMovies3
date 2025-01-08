package dk.easv.mymovies3.GUI.Controllers;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class NewCategoryController {
    CategoryModel categoryModel;
    @FXML
    private TextField txtCategory;

    public NewCategoryController() {
        categoryModel = new CategoryModel();
    }

    @FXML
    private void handleAddCategory(ActionEvent actionEvent) throws SQLException {

        if(txtCategory != null) {
            Category newCategory = new Category(txtCategory.getText());
            categoryModel.CreateCategory(newCategory);

        }
    }

    @FXML
    private void handleDeleteCategory(ActionEvent actionEvent) {
    }
}
