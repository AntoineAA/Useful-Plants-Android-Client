package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Genus {

    @SerializedName("id")
    private int id;

    @SerializedName("author")
    private String author;

    @SerializedName("name")
    private String name;

    @SerializedName("commons")
    private List<Common> commons;

    public String getAuthor() {
        return this.author;
    }

    public String getName() {
        return this.name;
    }

    public List<Common> getCommons() {
        return this.commons;
    }

    public Common getCommon(String code) {
        if (this.commons != null) {
            for (Common c : this.commons) {
                if (c.getCode() != null && c.getCode().equals(code)) {
                    return c;
                }
            }
        }
        return null;
    }
}
