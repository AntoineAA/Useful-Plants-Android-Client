package org.tic.floris.floristic.Models;

public class SortBy {

    private String name;
    private String value;
    private Boolean isSelected;

    public SortBy(String name, String value, Boolean isSelected) {
        this.name = name;
        this.value = value;
        this.isSelected = isSelected;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsSelected() {
        return this.isSelected;
    }
}
