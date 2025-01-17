package dk.easv.mymovies3.GUI.Controllers;

//Project Imports
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.GUI.Models.CategoryModel;

//Java Imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.SQLException;

public class NewCategoryController {
    private CategoryModel categoryModel;
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

    /**
     * This method is responsible for Adding categories based on the text given in the text box.
     * @param actionEvent
     * @throws SQLException
     */
    @FXML
    private void handleAddCategory(ActionEvent actionEvent) throws SQLException {

         //if the text box isn't empty, create a new category with the given name.
        if (!txtCategory.getText().isEmpty()) {
            Category newCategory = new Category(txtCategory.getText());
            categoryModel.createCategory(newCategory);
            
            if(newMovieController != null) {
                newMovieController.refreshCategoryList(); // Update category list in the movie controller
            }
            UpdateCategories();
        }
    }

    /**
     * This method is responsible for deleting categories based on the text given in the text box.
     * @param actionEvent
     * @throws SQLException
     */
    @FXML
    private void handleDeleteCategory(ActionEvent actionEvent) throws SQLException {
     //if there is a selected category, delete it.
    if (listCategory.getSelectionModel().getSelectedItem() != null) {
        Category category = listCategory.getSelectionModel().getSelectedItem();
        categoryModel.DeleteCategory(category);

        if(newMovieController != null) {
            newMovieController.refreshCategoryList(); // Update category list in the movie controller
        }
        UpdateCategories();
       }
    }

    /**
     * This method is responsible for updating the list of available categories.
     * @throws SQLException
     */
    public void UpdateCategories() throws SQLException {


        if (listCategory != null) {
            listCategory.getItems().clear(); //clear the list of categories
        }
        for (Category category : categoryModel.getAllCategories()) {
            listCategory.getItems().add(category); //repopulate the list of categories
        }
        //set the list to only show the category name via Cell Factory.
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
