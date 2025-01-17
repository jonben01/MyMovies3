package dk.easv.mymovies3.BE;

//Java Imports
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.Objects;

public class Category {

    private int id;
    private String categoryName;
    private final BooleanProperty isSelected = new SimpleBooleanProperty();


    public String getCategoryName() {return categoryName;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public boolean isSelected() {
        return isSelected.get();
    }
    public void setSelected(boolean selected) {
        this.isSelected.set(selected);
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public Category(int id,String categoryName) {
        this.id = id;
        this.categoryName = categoryName;

    }
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Category category = (Category) obj;
        return id == category.id && Objects.equals(categoryName, category.getCategoryName());
    }

    @Override
    public String toString() {
        return categoryName;
    }
}

