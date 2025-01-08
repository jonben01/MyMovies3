package dk.easv.mymovies3.GUI.Controllers;

import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.MovieModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;

public class NewMovieController {
    @FXML public Button btnFileChooser;
    @FXML public TextField txtFilePath;
    @FXML public TextField txtMovieYear;
    @FXML public TextField txtPersonalRating;
    @FXML public TextField txtIMDBRating;
    @FXML public TextField txtTitle;
    @FXML public Button btnAddMovie;
    @FXML public Button btnCancelMovie;
    @FXML public Button btnEditMovie;

    private MovieModel movieModel;

    public NewMovieController() throws IOException {
        movieModel = new MovieModel();
    }

    public void handleFileChooser(ActionEvent actionEvent) {
    }

    public void handleAddMovie(ActionEvent actionEvent) throws SQLException {
        //TODO make this better later
        if (txtFilePath.getText() == null || txtFilePath.getText().isEmpty()
                || txtMovieYear.getText() == null || txtMovieYear.getText().isEmpty()
                || txtPersonalRating.getText() == null || txtPersonalRating.getText().isEmpty()
                || txtIMDBRating.getText() == null || txtIMDBRating.getText().isEmpty()
                || txtTitle.getText() == null || txtTitle.getText().isEmpty())  {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
            return;
        }

        Integer rating = null;
        if (!txtPersonalRating.getText().isEmpty()) {
            try {
                rating = Integer.parseInt(txtPersonalRating.getText());
                if (rating > 10 || rating < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a whole number rating between 0 and 10");
                    alert.showAndWait();
                    return;
                }

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a numeric value for personal rating");
                alert.showAndWait();
                return;
            }
        }

        String title = txtTitle.getText();
        double imdbRating = Double.parseDouble(txtIMDBRating.getText());
        //TODO not used rn, because FileAlreadyExists isnt handled yet. fix this later
        String path = txtFilePath.getText();
        int year = Integer.parseInt(txtMovieYear.getText());


        /* SAVE FOR LATER.
        String destinationDir = "MyTunesButMovie/movies";
        Path destinationPath = Paths.get(destinationDir, new File(txtFilePath.getText()).getName());
        try {
            Files.copy(Paths.get(txtFilePath.getText()), destinationPath);
        } catch (FileAlreadyExistsException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error");
            alert.setHeaderText("File already exists");
            alert.setContentText("Replace existing file with new file?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() = ButtonType.OK) {
                Movie movieToBeDeleted
            }
        }
         */
        //TODO newFilePath MUST be changed to songDestinationPath, when fileAlreadyExists is handled.
        String newFilePath = txtFilePath.getText();
        Movie newMovie = new Movie(title, imdbRating, rating, newFilePath, year);
        movieModel.createMovie(newMovie);

        Stage stage =(Stage) btnAddMovie.getScene().getWindow();
        stage.close();

    }

    public void handleCancelMovie(ActionEvent actionEvent) {
    }

    public void handleEditMovie(ActionEvent actionEvent) {
    }
}