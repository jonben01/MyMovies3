package dk.easv.mymovies3.GUI.Controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.MovieModel;

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

    public NewMovieController() throws Exception {
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
            alertMethod("Please fill out all fields");
            return;
        }
        Double imdbRating = null;
        if (!txtIMDBRating.getText().isEmpty()) {
            try {
                imdbRating = Double.parseDouble(txtIMDBRating.getText());
                if (imdbRating < 0 || imdbRating > 10) {
                    alertMethod("Please enter a valid IMDB-Rating between 0 and 10");
                    return;
                }
            } catch (NumberFormatException e) {
                alertMethod("Please enter a numeric value for IMDB-Rating");
                return;
            }
        }
        Integer rating = null;
        if (!txtPersonalRating.getText().isEmpty()) {
            try {
                rating = Integer.parseInt(txtPersonalRating.getText());
                if (rating > 10 || rating < 0) {
                    alertMethod("Please enter a whole number between 0-10 for personal rating");
                    return;
                }

            } catch (NumberFormatException e) {
                alertMethod("Please enter a numeric value for personal rating");
                return;
            }
        }
        String title = txtTitle.getText();
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

    public void alertMethod(String alertString) {


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        alert.showAndWait();
    }

    public void handleCancelMovie(ActionEvent actionEvent) {
    }

    public void handleEditMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void OnNewCategoryClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/newCategoryView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("New Category");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

