package dk.easv.mymovies3.GUI.Controllers;
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.MovieModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class NewMovieController implements Initializable {
    @FXML public Button btnFileChooser;
    @FXML public TextField txtFilePath;
    @FXML public TextField txtMovieYear;
    @FXML public TextField txtPersonalRating;
    @FXML public TextField txtIMDBRating;
    @FXML public TextField txtTitle;
    @FXML public Button btnAddMovie;
    @FXML public Button btnCancelMovie;
    @FXML public Button btnEditMovie;
    @FXML public ListView<Category> lstCategories = new ListView<>();
    private ObservableList<Category> allCategories = null;


    private MovieModel movieModel;
    private CategoryModel categoryModel;

    public NewMovieController() throws Exception {
        movieModel = new MovieModel();
        categoryModel = new CategoryModel();
    }

    public void handleFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mpeg4"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());

        }
    }

    public void handleAddMovie(ActionEvent actionEvent) throws Exception {
        //TODO make this better later
        if (txtFilePath.getText() == null || txtFilePath.getText().isEmpty()
                || txtMovieYear.getText() == null || txtMovieYear.getText().isEmpty()
                || txtPersonalRating.getText() == null || txtPersonalRating.getText().isEmpty()
                || txtIMDBRating.getText() == null || txtIMDBRating.getText().isEmpty()
                || txtTitle.getText() == null || txtTitle.getText().isEmpty())  {
            alertMethod("Please fill out all fields", Alert.AlertType.ERROR);
            return;
        }
        Double imdbRating = null;
        if (!txtIMDBRating.getText().isEmpty()) {
            try {
                imdbRating = Double.parseDouble(txtIMDBRating.getText());
                if (imdbRating < 0 || imdbRating > 10) {
                    alertMethod("Please enter a valid IMDB-Rating between 0 and 10", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                alertMethod("Please enter a numeric value for IMDB-Rating", Alert.AlertType.ERROR);
                return;
            }
        }
        Integer rating = null;
        if (!txtPersonalRating.getText().isEmpty()) {
            try {
                rating = Integer.parseInt(txtPersonalRating.getText());
                if (rating > 10 || rating < 0) {
                    alertMethod("Please enter a whole number between 0-10 for personal rating", Alert.AlertType.ERROR);
                    return;
                }

            } catch (NumberFormatException e) {
                alertMethod("Please enter a numeric value for personal rating", Alert.AlertType.ERROR);
                return;
            }
        }
        //TODO extract below as a method later.
        String title = txtTitle.getText();
        //TODO not used rn, because FileAlreadyExists isnt handled yet. fix this later
        int year = Integer.parseInt(txtMovieYear.getText());
        String destinationDir = "src/main/resources/movies";
        Path destinationPath = Paths.get(destinationDir, new File(txtFilePath.getText()).getName());

        try {
            Files.copy(Paths.get(txtFilePath.getText()), destinationPath);
        } catch (FileAlreadyExistsException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error");
            alert.setHeaderText("File already exists");
            alert.setContentText("Replace existing file with new file?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Movie movieToBeDeleted = movieModel.getMovieByFilePath(destinationPath.toString());
                if (movieToBeDeleted != null) {
                    movieModel.deleteMovie(movieToBeDeleted);
                }
            }
        }

        String newFilePath = destinationPath.toString();
        Movie newMovie = new Movie(title, imdbRating, rating, newFilePath, year);
        movieModel.createMovie(newMovie);

        setCategories(newMovie);

        Stage stage =(Stage) btnAddMovie.getScene().getWindow();
        stage.close();
    }

    private void setCategories(Movie movie) throws SQLException {
        for (Category category : allCategories) {
            if (category.isSelected()) {
                categoryModel.addCategoryToMovie(movie.getId(), category.getId());
            }
        }
    }

    public void alertMethod(String alertString, Alert.AlertType alertType) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            allCategories = FXCollections.observableArrayList(categoryModel.GetAllCategories());
        } catch (SQLException e) {
            throw new RuntimeException("yikes - new movie controller, in initialize",e);
        }
        lstCategories.setItems(allCategories);

        lstCategories.setCellFactory(param -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            protected void updateItem(Category category, boolean empty) {
                //calls the updateIem method from the Cell class (super)
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    checkBox.setText(category.getCategoryName());
                    checkBox.setSelected(false);
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        category.setSelected(newValue);
                    });
                    setGraphic(checkBox);
                }
            }
        });
    }
}