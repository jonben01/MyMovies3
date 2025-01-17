package dk.easv.mymovies3.GUI.Controllers;

//Project Imports
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.CategoryModel;
import dk.easv.mymovies3.GUI.Models.MovieModel;

//Java Imports
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;
import java.util.List;
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
import javafx.scene.image.Image;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.controlsfx.control.CheckTreeView;

public class MainController implements Initializable {
    @FXML public Label lblTitle;
    @FXML public Label lblCategories;
    @FXML public Label lblIMDB;
    @FXML public Label lblPersonal;
    @FXML public Label lblLastOpened;
    @FXML public Label lblHiddenIMDB;
    @FXML public Label lblHiddenPersonal;
    @FXML public Label lblHiddenLastWatched;
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
    @FXML
    private ImageView imgPoster;
    @FXML
    private AnchorPane imgAnchor;
    private static final String API_KEY = "AIzaSyBp5qkHmF0mbmBRcUjjr5krLRoNStZqLHE"; // Our Google images API key
    private static final String CX_ID = "83e7d3530ea0f428e";   // Our Custom Search Engine Key
    private Movie previousMovie = null;

    public MainController() throws SQLException, IOException {
        categoryModel = new CategoryModel();
        movieModel = new MovieModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableViews();


        List<Movie> movies = movieModel.getObservableMovies();
        List<Movie> oldMovies = new ArrayList();
        for (Movie movie : movies) {
            Date lastOpened = movie.getLastOpenedDate();
            if (lastOpened != null) {
                LocalDate date = lastOpened.toLocalDate();
                //if score is less than 6 and movie hasnt been watched in 2 years add to list
                if (movie.getPersonalRating() < 6 && date.isBefore(LocalDate.now().minusYears(2))) {
                    oldMovies.add(movie);
                }
            }
        }
        //if the list isnt empty show what movies are on the list, and ask if the user wants to delete them.
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
                //if user pressed okay, delete all movies on the list.
                for (Movie movie : oldMovies) {
                    try {
                        movieModel.deleteMovie(movie);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        //Populate the checkTreeView "filterBox"
        try {
            populateFilterBox();
        } catch (SQLException e) {
            throw new RuntimeException("problem populating filterbox in initialize", e);
        }

        //listens to filterBox for changes, and applyFiltersAndSearch() method based on the change.
        filterBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<TreeItem<String>>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    applyFiltersAndSearch();
                }
            }
        });
        //hide before the first movie object is shown
        lblHiddenIMDB.setVisible(false);
        lblHiddenPersonal.setVisible(false);
        lblHiddenLastWatched.setVisible(false);

        //listens for selections on the tableView to populate the "side "window"" in the application with info.
        tblMovies.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<Category> movieCategories = newValue.getCategories();
                StringBuilder sb = new StringBuilder();
                //show labels
                lblHiddenIMDB.setVisible(true);
                lblHiddenPersonal.setVisible(true);
                lblHiddenLastWatched.setVisible(true);

                //used to create a new line of categories for movies with more than 3 categories,
                // so it fits on the screen
                for (int i = 0; i < movieCategories.size(); i++) {
                    sb.append(movieCategories.get(i).toString());
                    if ((i + 1) % 3 == 0) {
                        sb.append("\n");
                    } else if (i < movieCategories.size() - 1) {
                        sb.append(", ");
                    }
                    
                }
                //fill labels with info about the selected movie
                lblTitle.setText(newValue.getMovieTitle() + " (" + newValue.getMovieYear() + ") ");
                lblCategories.setText(sb.toString());
                lblIMDB.setText(newValue.getImdbRating().toString());
                lblPersonal.setText(newValue.getPersonalRating().toString());
                if (newValue.getLastOpenedDate() == null) {
                    lblLastOpened.setText("Never watched");
                } else {
                    lblLastOpened.setText(String.valueOf(newValue.getLastOpenedDate()));
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

    public void handleAddMovie(ActionEvent actionEvent) throws MovieOperationException {
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

            alertMethod("An error occurred while adding Movie. Please try again later.", Alert.AlertType.ERROR);
            throw new MovieOperationException("Failed to add movie. ", e);
        }
    }

    //Calls the edit stage when button is pressed
    public void handleEditMovie(ActionEvent actionEvent) throws MovieOperationException {
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

                alertMethod("An error occurred while loading the Edit Movie view. Please try again later.", Alert.AlertType.ERROR);
                throw new MovieOperationException("Failed to load the edit movie view.", e);
            }
        }
    }

    @FXML
    private void onTableViewClick(MouseEvent mouseEvent) throws IOException {
        Movie selectedMovie = tblMovies.getSelectionModel().getSelectedItem();
        if (selectedMovie != null && previousMovie != selectedMovie ) {
            // Fetch and display the movie poster
            getPoster(selectedMovie);

            // Clear existing AnchorPane constraints
            AnchorPane.clearConstraints(imgPoster);

            // Center the ImageView within the AnchorPane
            centerImageView();

            // Add listeners to re-center the ImageView when the AnchorPane resizes
            imgAnchor.widthProperty().addListener((obs, oldVal, newVal) -> centerImageView());
            imgAnchor.heightProperty().addListener((obs, oldVal, newVal) -> centerImageView());
            previousMovie = selectedMovie;
        }
    }

    private void centerImageView() {
        // Clear existing constraints
        AnchorPane.clearConstraints(imgPoster);

        // Recalculate and set new constraints
        AnchorPane.setTopAnchor(imgPoster, (imgAnchor.getHeight() - imgPoster.getFitHeight()) / 2);
        AnchorPane.setBottomAnchor(imgPoster, (imgAnchor.getHeight() - imgPoster.getFitHeight()) / 2);
        AnchorPane.setLeftAnchor(imgPoster, (imgAnchor.getWidth() - imgPoster.getFitWidth()) / 2);
        AnchorPane.setRightAnchor(imgPoster, (imgAnchor.getWidth() - imgPoster.getFitWidth()) / 2);
    }

    //Custom exception method
    public class MovieOperationException extends RuntimeException {
        public MovieOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }



    public void handleDeleteMovie(ActionEvent actionEvent) throws MovieOperationException {

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
                try {
                    movieModel.deleteMovie(movieToDelete);
                    File movieFile = new File(movieToDelete.getFilePath());
                    if(movieFile.exists()) {
                        if (movieFile.delete()) {
                        } else {
                            throw new MovieOperationException("Failed to delete the movie file: " + movieToDelete.getFilePath(), null);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error deleting movie: " + e.getMessage());
                    e.printStackTrace();

                    alertMethod("An error occurred while deleting a Movie. Please try again later" , Alert.AlertType.ERROR );

                    throw new MovieOperationException("Failed to delete movie.", e);
                }
                tblMovies.refresh();
            }
        }
    }

    //Setting view for application
    private void setupTableViews() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        colIMDB.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
        colPersonalRating.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("movieYear"));

        colCategory.setCellValueFactory(data -> {
            Movie movie = data.getValue();
            StringBuilder categories = new StringBuilder(); //A StringBuilder is used to build a comma-separated string of category names by iterating over the getCategories() list of the Movie object
            for (Category category : movie.getCategories()) {
                if (categories.length() > 0) {
                    categories.append(", ");
                }
                categories.append(category.getCategoryName());
            }
            return new ReadOnlyStringWrapper(categories.toString()); //resulting string is wrapped in a ReadOnlyStringWrapper (a JavaFX observable value) so it can be displayed in the table
        });

        tblMovies.setItems(movieModel.getObservableMovies());

    }

    /**
     * Method used to display alerts, to avoid too much repeated code
     *
     * @param alertString String to display on the alert
     * @param alertType sets the alertType of the Alert
     * @return Optional<ButtonType> in case of confirmation type alerts
     */
    public Optional<ButtonType> alertMethod(String alertString, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        return alert.showAndWait();
    }

    //TODO make sure it updates when you add a new category.
    public void populateFilterBox() throws SQLException {
        try {
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
        } catch (SQLException e) {
            throw new SQLException("Failed to populate filter box due to a database error.", e);
        }
    }

    public void applyFiltersAndSearch() throws MovieOperationException {

        try {
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
                filteredList = movieModel.applyFiltersAndSearch(null, selectedCategories, selectedImdbRange, selectedPersonalRating);
            } else if (!txtSearch.getText().isEmpty()) {
                filteredList = movieModel.applyFiltersAndSearch(searchQuery, selectedCategories, selectedImdbRange, selectedPersonalRating);
            }
            tblMovies.setItems(filteredList);

        } catch (Exception e) {
            throw new MovieOperationException("An error occurred while applying filters and searching", e);
        }
    }

    public void handlePlayMovie(ActionEvent actionEvent) throws SQLException {
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
                throw new MovieOperationException("oopsie woopsie while playing movie",e);
            }
        }
    }

    //Allows update categories view at the same time as movie was updated
    public void updateMovieCategories(Movie updatedMovie) {
        try {
            movieModel.updateMovie(updatedMovie); // Update movie in model
        } catch (SQLException e) {
            throw new RuntimeException("error in updateMovieCategories method ",e);
        }
    }

    private String fetchFirstImageUrl(String query) throws IOException {

        String urlString = String.format("https://www.googleapis.com/customsearch/v1?q=%s&cx=%s&key=%s&searchType=image", URLEncoder.encode(query, "UTF-8"), CX_ID, API_KEY);
        System.out.println("Request URL: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = JSONUtil.parseObj(response.toString());
            if (jsonObject.containsKey("items")) {
                JSONArray items = jsonObject.getJSONArray("items");
                if (!items.isEmpty()) {
                    return String.valueOf(items.getJSONObject(0).get("link"));
                }
            }
        } else {
            System.out.println("Error: " + responseCode + " " + conn.getResponseMessage());
        }

        return null;
    }

    private void getPoster(Movie movie) throws IOException {
        String query = movie.getMovieTitle() + " movie poster " + movie.getMovieYear();
        String imageUrl = fetchFirstImageUrl(query);

        if (imageUrl != null) {
            Image image = new Image(imageUrl);
            imgPoster.setImage(image);
        } else {
            System.out.println("No image found for: " + query);
            alertMethod("Failed to find an image for the movie!", Alert.AlertType.ERROR);

        }
    }
}

