package dk.easv.mymovies3.BLL;

import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.DAL.MovieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieFilter {
    private MovieDAO movieDAO;


    public MovieFilter() throws IOException {
        movieDAO = new MovieDAO();
    }


    /**
     * this method creates a filteredList using the setPredicate method,
     * the list is filled with all objects that match any of the boolean checks
     * excluding the categories, as the user should only be shown movies containing all the selected categories.
     *
     * @param searchQuery query from search field
     * @param selectedCategories categories selected in checkTreeView "filterBox"
     * @param selectedImdbRatings IMDB rating ranges selected in checkTreeView "filterBox"
     * @param selectedPersonalRatings Personal rating ranges selected in checkTreeView "filterBox"
     * @return returns a filtered observable list to show on the table
     * @throws SQLException in case of db issue
     */
    public ObservableList<Movie> applyFiltersAndSearch(String searchQuery,
                                              Set<String> selectedCategories,
                                              Set<String> selectedImdbRatings,
                                              Set<String> selectedPersonalRatings) throws SQLException {

        //retrieve all movies from db to compare to.
        List<Movie> movieList = movieDAO.getAllMovies();
        //create an ObservableList to contain all movies, so it can be used to create a FilteredList after.
        ObservableList<Movie> allMovies = FXCollections.observableArrayList(movieList);
        FilteredList<Movie> filteredMovies = new FilteredList<>(allMovies);

        //setPredicate filters the list based on the returns.
        //lambda checks all movie objects to see if they should be filtered out
        filteredMovies.setPredicate(movie -> {
            //if everything is empty, just return the full list
            if (selectedCategories.isEmpty()
                    && selectedImdbRatings.isEmpty()
                    && selectedPersonalRatings.isEmpty()
                    && searchQuery == null) {
                return true;
            }

            //create a set of category names (String) for the current movie object.
            Set<String> movieCategoryNames = movie.getCategories().stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toSet());

            //checks if the current movie objects categories matches all the selected categories.
            boolean matchesCategory = selectedCategories.isEmpty() ||
                    movieCategoryNames.containsAll(selectedCategories);

            //checks if the current movie objects imdb rating is within the selected ranges.
            //calls helper method isInRange() to check.
            boolean matchesIMDB = selectedImdbRatings.isEmpty() ||
                    selectedImdbRatings.stream().anyMatch(range -> isInRange(range, movie.getImdbRating()));

            //checks if the current movie objects personal rating is within the selected ranges.
            //calls helper method isInRange() to check.
            boolean matchesPersonal = selectedPersonalRatings.isEmpty() ||
                    selectedPersonalRatings.stream().anyMatch(range -> isInRange(range, movie.getPersonalRating()));

            //checks if the current movie objects name contains the searchQuery.
            boolean matchesSearch = (searchQuery == null || searchQuery.isEmpty()) ||
                                    movie.getMovieTitle().toLowerCase().contains(searchQuery.toLowerCase());

            //return true where matching, for each movie object. - all boolean checks will return true,
            //if nothing has been selected in the GUI.
            return matchesCategory && matchesIMDB && matchesPersonal && matchesSearch;
        });
        //once all movie objects have been through the filtering process, return the filteredList.
        return filteredMovies;
    }


    /**
     * Helper method to split a "range String", so it can be compared to the actual numeric rating of a movie object.
     *
     * @param range a "range String" contained in a hashset, from the filterBox in the gui layer.
     * @param rating the rating of a specific movie object
     * @return true or false depending on whether the rating is within the range.
     */
    private boolean isInRange(String range, Double rating) {
        String[] parts = range.split("-");
        if (parts.length == 2) {
            try {
                double min = Double.parseDouble(parts[0]);
                double max = Double.parseDouble(parts[1]);
                return min <= rating && rating <= max;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Oopsie in the isInRange method in MovieFilter class");
            }
        }
        return false;
    }
}

