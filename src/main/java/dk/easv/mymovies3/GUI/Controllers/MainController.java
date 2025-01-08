package dk.easv.mymovies3.GUI.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private void handleDeleteMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void handleEditMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void handleAddMovie(ActionEvent actionEvent) {
    }
}