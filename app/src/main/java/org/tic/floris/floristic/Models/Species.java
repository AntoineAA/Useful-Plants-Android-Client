package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import org.tic.floris.floristic.Interfaces.ImageInterface;

import java.util.ArrayList;
import java.util.List;

public class Species {

    private String idElastic;

    @SerializedName("id")
    private int id;

    @SerializedName("author")
    private String author;

    @SerializedName("name")
    private String name;

    @SerializedName("commons")
    private List<Common> commons;

    @SerializedName("genus")
    private Genus genus;

    @SerializedName("family")
    private Family family;

    @SerializedName("gbif_key")
    private Integer gbifKey;

    @SerializedName("eol_id")
    private Integer eolId;

    @SerializedName("pnet_id")
    private String pnetId;

    @SerializedName("usages")
    private List<Use> uses;

    @SerializedName("traits")
    private List<Trait> traits;

    @SerializedName("media")
    private List<ImageMedia> medias;

    @SerializedName("pnet")
    private Pnet pnet;

    private Boolean isVisible;

    public void setIdElastic(String idElastic) {
        this.idElastic = idElastic;
    }

    public String getIdElastic() {
        return this.idElastic;
    }

    public int getId() {
        return this.id;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getName() {
        return this.name;
    }

    public List<Common> getCommons() {
        return this.commons;
    }

    public Genus getGenus() {
        return this.genus;
    }

    public Family getFamily() {
        return this.family;
    }

    public Integer getGbifKey() {
        return this.gbifKey;
    }

    public Integer getEolId() {
        return this.eolId;
    }

    public String getPnetId() {
        return this.pnetId;
    }

    public List<ImageInterface> getImages() {
        List<ImageInterface> images = new ArrayList<>();

        if (this.pnet != null && this.pnet.getImages() != null) {
            images.addAll(this.pnet.getImages());
        }

        if (this.medias != null) {
            images.addAll(this.medias);
        }
        return images;
    }

    public List<Use> getUses() {
        return this.uses;
    }

    public List<Trait> getTraits() {
        return this.traits;
    }

    public String getFirstCommon(String code) {
        if (this.commons != null) {
            for (Common c : this.commons) {
                if (c.getCode() != null && c.getCode().equals(code)) {
                    return c.getFirstCommon();
                }
            }
        }
        return null;
    }

    public Common getCommon(String code) {
        for (Common c : this.commons) {
            if (c.getCode() != null && c.getCode().equals(code)) {
                return c;
            }
        }
        return null;
    }

    public String getFirstUrlImage() {
        if (this.pnet != null && this.pnet.getImage() != null && this.pnet.getImage().length() > 0) {
            return this.pnet.getImage();
        }
        if (this.medias != null && this.medias.size() > 0) {
            return this.medias.get(0).getUrl();
        }
        return null;
    }

    public String getUsesAsString(String divider) {
        String string = "";
        if (this.uses != null) {
            int count = 0;
            for (Use u : this.uses) {
                count++;
                string += u.getUse();
                if (this.uses.size() != count) {
                    string += divider;
                }
            }
        }
        return string;
    }

    public String getProjectsAsString(String divider) {
        String string = "";
        if (this.pnet != null && this.pnet.getProjects() != null) {
            int count = 0;
            for (Pnet.Project p : this.pnet.getProjects()) {
                count++;
                string += p.getName();
                if (this.pnet.getProjects().size() != count) {
                    string += divider;
                }
            }
        }
        return string;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsVisible() {
        return this.isVisible;
    }
}
