package dk.easv.mymovies3.GUI.Controllers;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.controlsfx.control.CheckTreeView;
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import dk.easv.mymovies3.GUI.Models.MovieModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.sql.Date;
import java.util.List;

public class MainController implements Initializable {
    @FXML private TextField txtSearch;
    @FXML private CheckTreeView<String> filterBox;
    private MovieModel movieModel;
    private CategoryModel categoryModel;

    @FXML private TableView<Movie> tblMovies;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, Double> colIMDB;
    @FXML private TableColumn<Movie, Integer> colPersonalRating;
    @FXML private TableColumn<Movie, Integer> colYear;
    @FXML private TableColumn<Movie, String> colCategory;

    private PauseTransition searchPause;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableViews();

        List<Movie> movies = movieModel.getObservableMovies();
        List<Movie> oldMovies = new ArrayList();
        for (Movie movie : movies) {
            Date lastOpened = movie.getLastOpenedDate();
            if (lastOpened != null) {
                LocalDate date = lastOpened.toLocalDate();

                if (date.isBefore(LocalDate.now().minusYears(2))) {
                    oldMovies.add(movie);
                }
            }
        }
        if (!oldMovies.isEmpty()) {
            StringBuilder content = new StringBuilder("The following movies are old and disliked: \n\n");
            for (Movie movie : oldMovies) {
                content.append(movie.getMovieTitle()).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete old and disliked movies?");
            alert.setHeaderText(content.toString());
            alert.setContentText("Would you like to delete them?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (Movie movie : oldMovies) {
                    try {
                        movieModel.deleteMovie(movie);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        try {
            populateFilterBox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        filterBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<TreeItem<String>>) c -> {
            while (c.next()) {
                //TODO figure out if this runs well like this, what happens if you uncheck all at the same time?
                //todo make better exception handling jesus christ
                if (c.wasAdded() || c.wasRemoved()) {
                    try {
                        applyFiltersAndSearch();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        //workaround debouncer for smoother searching.
        searchPause = new PauseTransition(Duration.millis(300));
        searchPause.setOnFinished(event -> {
            try {
                applyFiltersAndSearch();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchPause.stop();
            searchPause.playFromStart();
        });
    }

    public void handleAddMovie(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/newMovieView.fxml"));
            Parent root = loader.load();
            NewMovieController controller = loader.getController();
            controller.setMovieModel(movieModel);
            controller.setMode(false, null); // Set to create mode

            Stage stage = new Stage();
            stage.setTitle("New Movie");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleEditMovie(ActionEvent actionEvent) {
        Movie selectedMovie = tblMovies.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            alertMethod("Please select a movie before editing!", Alert.AlertType.INFORMATION);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/newMovieView.fxml"));
                Parent root = loader.load();

                NewMovieController controller = loader.getController();
                controller.setMovieModel(movieModel);
                controller.setMode(true, selectedMovie); // Set to update mode

                Stage stage = new Stage();
                stage.setTitle("Edit Movie");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleDeleteMovie(ActionEvent actionEvent) throws Exception {
        if(tblMovies.getSelectionModel().getSelectedItem() == null)
        {
            alertMethod("Please select a movie before deleting!", Alert.AlertType.WARNING);
        }
        else
        {
            Movie movieToDelete = tblMovies.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Movie");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete: " + movieToDelete.getMovieTitle() + "?" );
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                movieModel.deleteMovie(movieToDelete);
                tblMovies.refresh();
            }
        }
    }

    private void setupTableViews() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        colIMDB.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
        colPersonalRating.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("movieYear"));

        colCategory.setCellValueFactory(data -> {
            Movie movie = data.getValue();
            StringBuilder categories = new StringBuilder();
            for (Category category : movie.getCategories()) {
                if (categories.length() > 0) {
                    categories.append(", ");
                }
                categories.append(category.getCategoryName());
            }
            return new ReadOnlyStringWrapper(categories.toString());
        });

        tblMovies.setItems(movieModel.getObservableMovies());

    }

    public Optional<ButtonType> alertMethod(String alertString, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        return alert.showAndWait();
    }

    public MainController() throws Exception {
            categoryModel = new CategoryModel();
            movieModel = new MovieModel();
    }


    //TODO make sure it updates when you add a new category.
    public void populateFilterBox() throws SQLException {

        List<String> allCategories = categoryModel.getAvailableCategories();

        //root node for treeView, hidden.
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("Root");
        root.setExpanded(true);

        //adds a category node, and creates new checkboxes under it.
        CheckBoxTreeItem<String> categoriesNode = new CheckBoxTreeItem<>("Categories");
        for (String category : allCategories) {
            categoriesNode.getChildren().add(new CheckBoxTreeItem<>(category));
        }
        //TODO make this not shit dude bro homie
        CheckBoxTreeItem<String> imdbNode = new CheckBoxTreeItem<>("IMDB Rating");
        imdbNode.getChildren().addAll(
                new CheckBoxTreeItem<>("0.0 - 0.9"),
                new CheckBoxTreeItem<>("1.0 - 1.9"),
                new CheckBoxTreeItem<>("2.0 - 2.9"),
                new CheckBoxTreeItem<>("3.0 - 3.9"),
                new CheckBoxTreeItem<>("4.0 - 4.9"),
                new CheckBoxTreeItem<>("5.0 - 5.9"),
                new CheckBoxTreeItem<>("6.0 - 6.9"),
                new CheckBoxTreeItem<>("7.0 - 7.9"),
                new CheckBoxTreeItem<>("8.0 - 8.9"),
                new CheckBoxTreeItem<>("9.0 - 10.0")
        );
        //TODO make this not shit dude bro homie
        CheckBoxTreeItem<String> personalRatingNode = new CheckBoxTreeItem<>("Personal Rating");
        personalRatingNode.getChildren().addAll(
                new CheckBoxTreeItem<>("0.0 - 0.9"),
                new CheckBoxTreeItem<>("1.0 - 1.9"),
                new CheckBoxTreeItem<>("2.0 - 2.9"),
                new CheckBoxTreeItem<>("3.0 - 3.9"),
                new CheckBoxTreeItem<>("4.0 - 4.9"),
                new CheckBoxTreeItem<>("5.0 - 5.9"),
                new CheckBoxTreeItem<>("6.0 - 6.9"),
                new CheckBoxTreeItem<>("7.0 - 7.9"),
                new CheckBoxTreeItem<>("8.0 - 8.9"),
                new CheckBoxTreeItem<>("9.0 - 10.0")
        );
        root.getChildren().addAll(categoriesNode, imdbNode, personalRatingNode);
        //sets the root of the treeView and hides it.
        filterBox.setRoot(root);
        filterBox.setShowRoot(false);
    }

    public void applyFiltersAndSearch() throws Exception {
        List<TreeItem<String>> checkedItems = filterBox.getCheckModel().getCheckedItems();

        String searchQuery = txtSearch.getText();
        Set<String> selectedCategories = new HashSet<>();
        Set<String> selectedImdbRange = new HashSet<>();
        Set<String> selectedPersonalRating = new HashSet<>();

        for (TreeItem<String> item : checkedItems) {
            TreeItem<String> parent = item.getParent();

            if (parent != null) {
                String parentValue = parent.getValue();
                if ("Categories".equals(parentValue)) {
                    selectedCategories.add(item.getValue());
                } else if ("IMDB Rating".equals(parentValue)) {
                    selectedImdbRange.add(item.getValue());
                } else if ("Personal Rating".equals(parentValue)) {
                    selectedPersonalRating.add(item.getValue());
                }
            }
        }
        ObservableList<Movie> filteredList = null;
        if (txtSearch.getText().isEmpty()) {
            filteredList = movieModel.applyFiltersAndSearch(null,selectedCategories, selectedImdbRange, selectedPersonalRating);
        } else if (!txtSearch.getText().isEmpty()) {
            filteredList = movieModel.applyFiltersAndSearch(searchQuery,selectedCategories, selectedImdbRange, selectedPersonalRating);
        }
        tblMovies.setItems(filteredList);
    }

    public void handlePlayMovie(ActionEvent actionEvent) throws Exception {
        Movie movieFile = tblMovies.getSelectionModel().getSelectedItem();
        File file = new File (movieFile.getFilePath());
        if (!file.exists()) {
            Optional<ButtonType> result = alertMethod("File doesnt exist, do you want to delete it from the application?", Alert.AlertType.CONFIRMATION);
            if (result.isPresent() && result.get() == ButtonType.OK) {
                movieModel.deleteMovie(movieFile);
            }
            return;
        }

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            try {
                desktop.open(file);
                movieModel.setLastOpened(movieFile);
            } catch (IOException e) {
                throw new RuntimeException("oopsie woopsie",e);
            }
        }
    }

    public void updateMovieCategories(Movie updatedMovie) {
        try {
            movieModel.updateMovie(updatedMovie); // Update movie in model
            tblMovies.refresh(); // Refresh the table view
        } catch (Exception e) {
        }
    }
}

