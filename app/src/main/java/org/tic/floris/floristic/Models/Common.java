package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Common {

    @SerializedName("language")
    private String language;

    @SerializedName("code")
    private String code;

    @SerializedName("names")
    private List<String> names;

    public String getLanguage() {
        return this.language;
    }

    public String getCode() {
        return this.code;
    }

    public List<String> getNames() {
        return this.names;
    }

    public String getFirstCommon() {
        if (this.names.size() > 0) {
            return this.names.get(0);
        }
        return null;
    }

    public String getNamesAsString(String divider) {
        String string = "";
        if (this.names != null) {
            int count = 0;
            for (String s : this.names) {
                count++;
                string += s;
                if (this.names.size() != count) {
                    string += divider;
                }
            }
        }
        return string;
    }
}
