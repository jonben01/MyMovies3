module dk.easv.mymovies3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens dk.easv.mymovies3 to javafx.fxml;
    exports dk.easv.mymovies3;
}