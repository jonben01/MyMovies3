package dk.easv.mymovies3.GUI.Controllers;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class NewCategoryController {
    CategoryModel categoryModel;
    @FXML
    private TextField txtCategory;
    @FXML
    private ListView<Category> listCategory;

    private NewMovieController newMovieController;

    public NewCategoryController() throws IOException, SQLException {
        categoryModel = new CategoryModel();
    }
    @FXML
    private void initialize() throws SQLException {
        UpdateCategories();
    }

    public void setMovieController(NewMovieController newMovieController) {
        this.newMovieController = newMovieController;
    }

    @FXML
    private void handleAddCategory(ActionEvent actionEvent) throws SQLException {

        if(txtCategory != null) {
            Category newCategory = new Category(txtCategory.getText());
            categoryModel.createCategory(newCategory);
            UpdateCategories();
            newMovieController.updateCategories();

        }
    }

    @FXML
    private void handleDeleteCategory(ActionEvent actionEvent) throws SQLException {

        if (listCategory.getSelectionModel().getSelectedItem() != null) {
            // TODO: Implement this shit..
            Category category = listCategory.getSelectionModel().getSelectedItem();
            categoryModel.DeleteCategory(category);
        }
        UpdateCategories();
    }

    public void UpdateCategories() throws SQLException {
        if(listCategory != null) {
            listCategory.getItems().clear();
        }
        for(Category category : categoryModel.getAllCategories()) {
            listCategory.getItems().add(category);
        }
        listCategory.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCategoryName());
                }
            }
        });
    }
}
