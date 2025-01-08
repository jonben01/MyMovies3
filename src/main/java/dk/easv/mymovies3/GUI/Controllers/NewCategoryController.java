package dk.easv.mymovies3.GUI.Controllers;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class NewCategoryController {
    CategoryModel categoryModel;
    @FXML
    private TextField txtCategory;
    @FXML
    private ListView listCategory;

    public NewCategoryController() throws IOException, SQLException {
        categoryModel = new CategoryModel();
    }
    @FXML
    private void initialize() throws SQLException {
        UpdateCategories();
    }

    @FXML
    private void handleAddCategory(ActionEvent actionEvent) throws SQLException {

        if(txtCategory != null) {
            Category newCategory = new Category(txtCategory.getText());
            categoryModel.CreateCategory(newCategory);
            UpdateCategories();

        }
    }

    @FXML
    private void handleDeleteCategory(ActionEvent actionEvent) {

        if(listCategory.getSelectionModel().getSelectedItem() != null) {
            // TODO: Implement this shit..
        }
    }

    private void UpdateCategories() throws SQLException {
        if(listCategory != null) {
            listCategory.getItems().clear();
        }
        for(Category category : categoryModel.GetAllCategories()) {
            listCategory.getItems().add(category.getCategoryName());
        }
    }
}
