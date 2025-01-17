package dk.easv.mymovies3.BLL;

//Project Imports
import dk.easv.mymovies3.BE.Category;
import dk.easv.mymovies3.BE.Movie;
import dk.easv.mymovies3.DAL.MovieDAO;

//Java Imports
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class MovieFilter {
    private MovieDAO movieDAO;


    public MovieFilter() throws IOException {
        movieDAO = new MovieDAO();
    }

    public ObservableList<Movie> applyFiltersAndSearch(String searchQuery,
                                              Set<String> selectedCategories,
                                              Set<String> selectedImdbRatings,
                                              Set<String> selectedPersonalRatings) throws Exception {

        List<Movie> movieList = movieDAO.getAllMovies();
        ObservableList<Movie> allMovies = FXCollections.observableArrayList(movieList);
        FilteredList<Movie> filteredMovies = new FilteredList<>(allMovies);

        filteredMovies.setPredicate(movie -> {
            if (selectedCategories.isEmpty()
                    && selectedImdbRatings.isEmpty()
                    && selectedPersonalRatings.isEmpty()
                    && searchQuery == null) {
                return true;
            }

            Set<String> movieCategoryNames = movie.getCategories().stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toSet());

            boolean matchesCategory = selectedCategories.isEmpty() ||
                    movieCategoryNames.containsAll(selectedCategories);

            boolean matchesIMDB = selectedImdbRatings.isEmpty() ||
                    selectedImdbRatings.stream().anyMatch(range -> isInRange(range, movie.getImdbRating()));

            boolean matchesPersonal = selectedPersonalRatings.isEmpty() ||
                    selectedPersonalRatings.stream().anyMatch(range -> isInRange(range, movie.getPersonalRating()));

            boolean matchesSearch = (searchQuery == null || searchQuery.isEmpty()) ||
                                    movie.getMovieTitle().toLowerCase().contains(searchQuery.toLowerCase());

            return matchesCategory && matchesIMDB && matchesPersonal && matchesSearch;
        });
        return filteredMovies;
    }


    private boolean isInRange(String range, Double rating) {
        String[] parts = range.split("-");
        if (parts.length == 2) {
            try {
                double min = Double.parseDouble(parts[0]);
                double max = Double.parseDouble(parts[1]);
                return min <= rating && rating <= max;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("bruh");
            }
        }
        return false;
    }
}

