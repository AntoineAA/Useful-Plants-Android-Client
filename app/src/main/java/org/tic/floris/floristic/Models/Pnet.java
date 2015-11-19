package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import org.tic.floris.floristic.Interfaces.ImageInterface;

import java.util.List;

public class Pnet {

    @SerializedName("image")
    private String image;

    @SerializedName("projects")
    private List<Project> projects;

    @SerializedName("images")
    private List<ImagePnet> images;

    public String getImage() {
        return this.image;
    }

    public List<Project> getProjects() {
        return this.projects;
    }

    public List<ImagePnet> getImages() {
        return this.images;
    }

    public class Project {

        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        public String getName() {
            return this.name;
        }
    }

    public class ImagePnet implements ImageInterface {

        @SerializedName("url")
        private String url;

        @SerializedName("type")
        private String type;

        @SerializedName("author")
        private String author;

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
            return null;
        }

        @Override
        public String getSource() {
            return "Pl@ntNet";
        }
    }
}
