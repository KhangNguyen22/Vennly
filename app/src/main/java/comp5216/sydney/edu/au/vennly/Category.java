package comp5216.sydney.edu.au.vennly;

import java.io.Serializable;

public class Category implements Serializable {
    private final String categoryName;
    private final String[] categories;

    public Category(String categoryName, String[] categories) {
        this.categoryName = categoryName;
        this.categories = categories;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public String[] getCategories() {
        return categories;
    }
}
