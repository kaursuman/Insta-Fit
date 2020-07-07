package com.example.insta_fit.Model;

public class FoodItem {
    private String category;
    private String name;
    private Double calories;
    private int servings;

    public FoodItem() {
    }

    public FoodItem(String category, String name, Double calories, int servings) {
        this.category = category;
        this.name = name;
        this.calories = calories;
        this.servings = servings;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", calories=" + calories +
                ", servings=" + servings +
                '}';
    }
}
