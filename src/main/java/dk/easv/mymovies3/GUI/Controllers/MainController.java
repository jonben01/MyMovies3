package dk.easv.mymovies3.GUI.Controllers;

import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.GUI.Models.MovieModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private MovieModel movieModel;

    @FXML
    private TableView<Movie> tblMovies;

    @FXML
    private TableColumn<Movie, String> colTitle;

    @FXML
    private TableColumn<Movie, Double> colIMDB;

    @FXML
    private TableColumn<Movie, Integer> colPersonalRating;

    @FXML
    private TableColumn<Movie, Integer> colYear;

    @FXML
    private TableColumn<Movie, String> colCategory;












    public void handleAddMovie(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/newMovieView.fxml"));
            Parent root = loader.load();

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
    }

    public void handleDeleteMovie(ActionEvent actionEvent) {
    }

    private void setupTableViews() {


        // TableView columns setup


            colTitle.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
            colIMDB.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
            colPersonalRating.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
            colYear.setCellValueFactory(new PropertyValueFactory<>("movieYear"));
            


            tblMovies.setItems(movieModel.getObservableMovies());






    }



    public MainController() throws Exception {

            movieModel = new MovieModel();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableViews();
    }
}

