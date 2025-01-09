package dk.easv.mymovies3.BE;

public class Category {

    private int id;
    private String categoryName;
    private boolean isSelected;

    public String getCategoryName() {return categoryName;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public boolean isSelected() {return isSelected;}
    public void setSelected(boolean selected) {isSelected = selected;}

    public Category(int id,String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}

