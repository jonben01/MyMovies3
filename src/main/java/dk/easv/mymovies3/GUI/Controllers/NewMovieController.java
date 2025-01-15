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
import java.util.ArrayList;
import java.util.List;
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
    private NewCategoryController newCategoryController;


    private MovieModel movieModel;
    private CategoryModel categoryModel;
    private MainController controller;

    private boolean isUpdateMode;
    private Movie movieToUpdate;

    @FXML private TableView<Movie> tblMovies;


    public NewMovieController() throws Exception {
        movieModel = new MovieModel();
        categoryModel = new CategoryModel();
        controller = new MainController();
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

        if(!validateFields()) {
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
        Double personalRating = null;
        if (!txtPersonalRating.getText().isEmpty()) {
            try {
                personalRating = Double.parseDouble(txtPersonalRating.getText());
                if (personalRating > 10 || personalRating < 0) {

                    alertMethod("Please enter a valid personal rating between 0 and 10", Alert.AlertType.ERROR);
                    return;
                }

            } catch (NumberFormatException e) {
                alertMethod("Please enter a numeric value for personal rating", Alert.AlertType.ERROR);
                return;
            }
        }

        String title = txtTitle.getText();
        int year = Integer.parseInt(txtMovieYear.getText());
        String newFilePath = handleFileCopy();

        ArrayList<Category> selectedCategories = getSelectedCategories();


        if (isUpdateMode) {
            // Update existing movie
            updateMovie(title, imdbRating, personalRating, year, newFilePath, selectedCategories);

        } else {
            createMovie(title, imdbRating, personalRating, year, newFilePath, selectedCategories);

        }

        Stage stage =(Stage) btnAddMovie.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {

        if (txtFilePath.getText() == null || txtFilePath.getText().isEmpty()
                || txtMovieYear.getText() == null || txtMovieYear.getText().isEmpty()
                || txtPersonalRating.getText() == null || txtPersonalRating.getText().isEmpty()
                || txtIMDBRating.getText() == null || txtIMDBRating.getText().isEmpty()
                || txtTitle.getText() == null || txtTitle.getText().isEmpty())  {
            alertMethod("Please fill out all fields", Alert.AlertType.ERROR);
            return false;
        }
        return true;

    }


    private String handleFileCopy() throws Exception {

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
        return destinationPath.toString();

    }

    private ArrayList<Category> getSelectedCategories() {

        ArrayList<Category> selectedCategories = new ArrayList<>();

        for (Category category : allCategories) {
            if (category.isSelected()) {
                selectedCategories.add(category);
            }
        }
        return selectedCategories;
    }

    private void updateMovie(String title, Double imdbRating, Double personalRating, int year, String filePath, ArrayList<Category> selectedCategories) throws Exception {
        movieToUpdate.setMovieTitle(title);
        movieToUpdate.setImdbRating(imdbRating);
        movieToUpdate.setPersonalRating(personalRating);
        movieToUpdate.setMovieYear(year);
        movieToUpdate.setFilePath(filePath);

        movieModel.updateMovie(movieToUpdate);
        setCategories(movieToUpdate);
    }

    private void createMovie(String title, Double imdbRating, Double personalRating, int year, String filePath, ArrayList<Category> selectedCategories) throws Exception {
        Movie newMovie = new Movie(title, imdbRating, personalRating, filePath, year, selectedCategories);
        movieModel.createMovie(newMovie);
        setCategories(newMovie);
    }

    private void setCategories(Movie movie) throws Exception {

        //Clear existing categories
        categoryModel.clearCategoriesForMovie(movie.getId());

        List<Category> selectedCategories = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.isSelected()) {
                categoryModel.addCategoryToMovie(movie.getId(), category.getId());
                selectedCategories.add(category);
            }
        }
        // Update the movie's categories in the model
        movie.setCategories(selectedCategories);
        controller.updateMovieCategories(movie); // Notify MainController to refresh
    }

    public void alertMethod(String alertString, Alert.AlertType alertType) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        alert.showAndWait();
    }





    @FXML
    private void OnNewCategoryClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/newCategoryView.fxml"));
            Parent root = loader.load();
            NewCategoryController controller = loader.getController();
            controller.setMovieController(this);

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
            allCategories = FXCollections.observableArrayList(categoryModel.getAllCategories());
        } catch (SQLException e) {
            throw new RuntimeException("yikes - new movie controller, in initialize",e);
        }
        lstCategories.setItems(allCategories);

        lstCategories.setCellFactory(param -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            /**
             * updateItem() defines what should be shown in each cell of the listView. In our case it fills the cells
             * with Category objects or leaves it empty. Using it allows us to customize the cells more, such as using
             * checkboxes in each ListCell.
             *
             * uses the non-overriden updateItem() to make sure proper behavior, like emptying cells before reuse.
             *
             * @param category The new item for the cell.
             * @param empty whether or not this cell represents data from the list. If it
             *        is empty, then it does not represent any domain data, but is a cell
             *        being used to render an "empty" row.
             */
            protected void updateItem(Category category, boolean empty) {
                //calls the updateIem method from the Cell class (super)
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    checkBox.setText(category.getCategoryName());
                    checkBox.setSelected(category.isSelected());
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        category.setSelected(newValue);
                    });
                    setGraphic(checkBox);
                }
            }
        });
    }

    public void setMovieModel(MovieModel movieModel) {
        this.movieModel = movieModel;
    }

    public void setMode(boolean isUpdateMode, Movie movieToUpdate) {
        this.isUpdateMode = isUpdateMode;
        this.movieToUpdate = movieToUpdate;

        if (isUpdateMode) {
            // Populate fields with the movie's current details
            txtTitle.setText(movieToUpdate.getMovieTitle());
            txtIMDBRating.setText(String.valueOf(movieToUpdate.getImdbRating()));
            txtPersonalRating.setText(String.valueOf(movieToUpdate.getPersonalRating()));
            txtMovieYear.setText(String.valueOf(movieToUpdate.getMovieYear()));
            txtFilePath.setText(movieToUpdate.getFilePath());

            // Match categories in the movie to those in the ListView
            for (Category movieCategory : movieToUpdate.getCategories()) {
                for (Category listViewCategory : lstCategories.getItems()) {
                    if (movieCategory.getId() == listViewCategory.getId()) {
                        listViewCategory.setSelected(true); // Mark category as selected
                        break;
                    }
                }
            }

            btnAddMovie.setText("Edit Movie");
        } else {
            btnAddMovie.setText("Add Movie");
        }
    }


    public void handleCancelMovie(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelMovie.getScene().getWindow();
        stage.close(); }

    public void updateCategories() throws SQLException {
        lstCategories.getItems().clear();
        allCategories.clear();
        allCategories = FXCollections.observableArrayList(categoryModel.getAllCategories());
        lstCategories.setItems(allCategories);
    }



}