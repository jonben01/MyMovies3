package dk.easv.mymovies3.GUI.Controllers;

//Project Imports
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.MovieModel;

//Java Imports
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            refreshCategoryList();
            allCategories = FXCollections.observableArrayList(categoryModel.getAllCategories());
        } catch (SQLException e) {
            throw new RuntimeException("yikes Scoob - new movie controller, in initialize",e);
        }

        // Set up ListView cell factory for categories
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
                    // Reset the checkbox text and binding
                    checkBox.setText(category.getCategoryName());
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        category.setSelected(newValue); // Update the Category object
                    });

                    // Set the graphic of the cell to the checkbox
                    setGraphic(checkBox);
                }
            }
        });
    }

    public NewMovieController() throws Exception {
        movieModel = new MovieModel();
        categoryModel = new CategoryModel();
        controller = new MainController();
    }

    /**
     * Uses filechooser to grab the file the user wants to add to their collection
     * @param actionEvent button press.
     */
    public void handleFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //file chooser filters
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mpeg4"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());

        }
    }

    /**
     * Creates a new Movie Object based on user input, then goes to database to add it to the database.
     *
     * @param actionEvent button press
     * @throws MovieOperationException in case of errors
     */
    public void handleAddMovie(ActionEvent actionEvent) throws MovieOperationException {

        try {
            if (!validateFields()) {// Validate that all required fields are filled.
                return;
            }

            Double imdbRating = null;
            // Check if the IMDB rating field is not empty
            if (!txtIMDBRating.getText().isEmpty()) {
                try {
                    imdbRating = Double.parseDouble(txtIMDBRating.getText());// Parse personal rating.
                    // Validate that IMDB rating is within the acceptable range.
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
            // Check if the personal rating field is not empty.
            if (!txtPersonalRating.getText().isEmpty()) {
                try {
                    personalRating = Double.parseDouble(txtPersonalRating.getText());
                    // Validate that personal rating is within the acceptable range
                    if (personalRating > 10 || personalRating < 0) {

                        alertMethod("Please enter a valid personal rating between 0 and 10", Alert.AlertType.ERROR);
                        return;
                    }

                } catch (NumberFormatException e) {
                    alertMethod("Please enter a numeric value for personal rating", Alert.AlertType.ERROR);
                    return;
                }
            }

            // Retrieve title and year of the movie from text fields.
            String title = txtTitle.getText();
            int year = Integer.parseInt(txtMovieYear.getText());
            // Handle file copying for the movie's associated file and retrieve the new file path
            String newFilePath = handleFileCopy();

            // Get the categories selected by the user.
            ArrayList<Category> selectedCategories = getSelectedCategories();

            // Check if the user is updating an existing movie or creating a new one.
            if (isUpdateMode) {
                // Update existing movie
                updateMovie(title, imdbRating, personalRating, year, newFilePath, selectedCategories);
                refreshCategoryList();

            } else {
                // Create a new movie with the provided details.
                createMovie(title, imdbRating, personalRating, year, newFilePath, selectedCategories);

            }

            Stage stage = (Stage) btnAddMovie.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            throw new MovieOperationException("Failed while adding movie", e);
        }
    }

    // Validates that all required fields are filled by the user.
    private boolean validateFields() {
        // Check if any of the required fields are null or empty.
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

    // Handles the copying of the movie file to the designated directory.
    private String handleFileCopy() throws MovieOperationException {

        try {
            String destinationDir = "src/main/resources/movies";
            Path destinationPath = Paths.get(destinationDir, new File(txtFilePath.getText()).getName());

            try {
                // Attempt to copy the file to the destination.
                Files.copy(Paths.get(txtFilePath.getText()), destinationPath);
            } catch (FileAlreadyExistsException e) {
                // Handle case where the file already exists in the destination.
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Error");
                alert.setHeaderText("File already exists");
                alert.setContentText("Replace existing file with new file?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // If user confirms replacement, delete the existing movie entry if it exists
                    Movie movieToBeDeleted = movieModel.getMovieByFilePath(destinationPath.toString());
                    if (movieToBeDeleted != null) {
                        movieModel.deleteMovie(movieToBeDeleted);
                    }
                }
            }
            return destinationPath.toString();// Return the new file path
        } catch (Exception e) {
            throw new MovieOperationException("Failed while handling file copy", e);
        }

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

    //Allows update a movie
    private void updateMovie(String title, Double imdbRating, Double personalRating, int year, String filePath, ArrayList<Category> selectedCategories) throws MovieOperationException {

        try {
            // Update movie properties with the new values
            movieToUpdate.setMovieTitle(title);
            movieToUpdate.setImdbRating(imdbRating);
            movieToUpdate.setPersonalRating(personalRating);
            movieToUpdate.setMovieYear(year);
            movieToUpdate.setFilePath(filePath);

            // Update the movie in the model (e.g., database or data storage).
            movieModel.updateMovie(movieToUpdate);

            // Update the movie's associated categories.
            setCategories(movieToUpdate);

        } catch (Exception e) {
            throw new MovieOperationException("Issue in updateMovie method", e);
        }

    }

    // Creates a new movie with the provided details.
    private void createMovie(String title, Double imdbRating, Double personalRating, int year, String filePath, ArrayList<Category> selectedCategories) throws Exception {

        // Instantiate a new Movie object with the provided parameters
        Movie newMovie = new Movie(title, imdbRating, personalRating, filePath, year, selectedCategories);

        // Add the new movie to the model (e.g., database or data storage).
        movieModel.createMovie(newMovie);

        // Assign the selected categories to the newly created movie.
        setCategories(newMovie);
    }

    // Sets or updates the categories associated with a movie.
    private void setCategories(Movie movie) throws MovieOperationException {
try {
    //Clear existing categories
    categoryModel.clearCategoriesForMovie(movie.getId());

    // Create a list to store the selected categories.
    List<Category> selectedCategories = new ArrayList<>();

    // Iterate through all available categories.
    for (Category category : allCategories) {
        // If the category is selected, link it to the movie.
        if (category.isSelected()) {
            categoryModel.addCategoryToMovie(movie.getId(), category.getId()); // Update the database
            selectedCategories.add(category); // Add to the selected categories list.
        }
    }
    // Update the movie's category list with the selected categories.
    movie.setCategories(selectedCategories);

    // Notify the controller to refresh the movie's categories in the UI.
    controller.updateMovieCategories(movie);
} catch (SQLException e) {
    throw new MovieOperationException("Failed while setting a category", e);
}

    }

    public void alertMethod(String alertString, Alert.AlertType alertType) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        alert.showAndWait();
    }

    /**
     * When run, opens the New Category window, while passing on a traceback to this controller.
     * @param actionEvent
     */
    @FXML
    private void onNewCategoryClick(ActionEvent actionEvent) {
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

    public void setMovieModel(MovieModel movieModel) {
        this.movieModel = movieModel;
    }

    //Allows to use the same stage for creating and editing a movie
    public void setMode(boolean isUpdateMode, Movie movieToUpdate) {
        this.isUpdateMode = isUpdateMode;
        this.movieToUpdate = movieToUpdate;

        try {
            refreshCategoryList(); // Ensure the category list is up-to-date.
        } catch (MovieOperationException e) {
            throw new RuntimeException("Failed to refresh categories in setMode", e);
        }

        if (isUpdateMode) {
            // Populate fields with the movie's current details
            txtTitle.setText(movieToUpdate.getMovieTitle());
            txtIMDBRating.setText(String.valueOf(movieToUpdate.getImdbRating()));
            txtPersonalRating.setText(String.valueOf(movieToUpdate.getPersonalRating()));
            txtMovieYear.setText(String.valueOf(movieToUpdate.getMovieYear()));
            txtFilePath.setText(movieToUpdate.getFilePath());

            // Update the checkboxes in the ListView
            lstCategories.refresh();

            btnAddMovie.setText("Edit Movie");
        } else {
            btnAddMovie.setText("Add Movie");
        }
    }

    public void handleCancelMovie(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelMovie.getScene().getWindow();
        stage.close(); }


    //custom exception method
    public class MovieOperationException extends RuntimeException {
        public MovieOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void refreshCategoryList() throws MovieOperationException {
        try {
            allCategories = FXCollections.observableArrayList(categoryModel.getAllCategories());

            // Update ListView items
            lstCategories.setItems(allCategories);
            lstCategories.refresh();
        } catch (SQLException e) {
            throw new MovieOperationException("Failed while refreshing category list", e);
        }
    }


}