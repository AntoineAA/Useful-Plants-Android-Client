package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

public class Family {

    @SerializedName("id")
    private int id;

    @SerializedName("author")
    private String author;

    @SerializedName("name")
    private String name;

    public String getAuthor() {
        return this.author;
    }

    public String getName() {
        return this.name;
    }
}
