package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import org.tic.floris.floristic.Interfaces.ImageInterface;

public class ImageMedia implements ImageInterface {

    @SerializedName("floris_source")
    private String source;

    @SerializedName("floris_media_url")
    private String url;

    @SerializedName("floris_media_type")
    private String type;

    @SerializedName("floris_media_title")
    private String title;

    @SerializedName("floris_media_description")
    private String description;

    @SerializedName("floris_media_author")
    private String author;

    @SerializedName("floris_media_licence")
    private String license;

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSource() {
        return this.source;
    }
}
